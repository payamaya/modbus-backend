package org.example.modbusbackend.api.services;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.*;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.ghgande.j2mod.modbus.util.BitVector;
import org.example.modbusbackend.api.dto.*;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.example.modbusbackend.database.model.ModbusData;
import org.example.modbusbackend.database.repository.ModbusDataRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.net.InetAddress;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class ModbusMasterService {

    private TCPMasterConnection connection;
    private final DataSource dataSource;

    private final ModbusDataRepository repository;

    @Value("${modbus.slave.ip}")
    private String slaveIp;

    @Value("${modbus.slave.port}")
    private int slavePort;

    @PostConstruct
    public void initialize() {
        try {
            InetAddress address = InetAddress.getByName(slaveIp);
            connection = new TCPMasterConnection(address);
            connection.setPort(slavePort);
            connection.connect();

            if (!connection.isConnected()) {
                System.err.println("Modbus connection failed. Exiting application...");
                System.exit(1); // Force shutdown
            }

            System.out.println("Modbus connection established.");
        } catch (Exception e) {
            System.err.println("Failed to connect to Modbus slave: " + e.getMessage());
            System.exit(1); // Force shutdown
        }
    }

    public ModbusMasterService(ModbusDataRepository repository, DataSource dataSource) {
        this.repository = repository;
        this.dataSource = dataSource;
    }

    @PostConstruct
    public void logDatabasePath() {
        try (Connection conn = dataSource.getConnection()) {
            System.out.println("Connected to SQLite database at: " + conn.getMetaData().getURL());
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @PreDestroy
    public void cleanup() {
        try {
            if (connection != null) {
                connection.close();
                System.out.println("Modbus connection closed.");
            }
        } catch (Exception e) {
            System.err.println("Error closing Modbus connection: " + e.getMessage());
        }
    }

    public ResponseEntity<ModbusReadResponseDTO> readRegisters(ModbusReadRequestDTO modbusReadRequestDTO) {
        try {
            ReadMultipleRegistersRequest request = new ReadMultipleRegistersRequest(modbusReadRequestDTO.getAddress(), modbusReadRequestDTO.getNumRegisters());
            request.setUnitID(modbusReadRequestDTO.getSlaveId());

            ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
            transaction.setRequest(request);
            transaction.execute();

            ReadMultipleRegistersResponse response = (ReadMultipleRegistersResponse) transaction.getResponse();
            ModbusReadResponseDTO modbusReadResponseDTO = new ModbusReadResponseDTO();

            modbusReadResponseDTO.setSlaveId(response.getUnitID());
            modbusReadResponseDTO.setAddress(modbusReadRequestDTO.getAddress());
            modbusReadResponseDTO.setNumRegisters(response.getWordCount());
            modbusReadResponseDTO.setRegisterValues(createRegisterArray(response));

            // Store Data in DB
            saveRegisterData(response.getUnitID(), modbusReadRequestDTO.getAddress(), createRegisterArray(response), modbusReadRequestDTO.getNumRegisters());

            return ResponseEntity.ok((modbusReadResponseDTO));

        } catch (ModbusException e) {
            throw new RuntimeException("Error reading Modbus registers.", e);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    private void saveRegisterData(int slaveId, int startAddress, int[] values, int count) {
        for (int i = 0; i < count; i++) {
            int currentAddress = startAddress + i;
            Optional<ModbusData> existingDataOpt = repository.findByTypeAndAddress("HOLDING_REGISTER", currentAddress);

            ModbusData modbusData = existingDataOpt.orElseGet(ModbusData::new);
            modbusData.setType("HOLDING_REGISTER");
            modbusData.setSlaveId(slaveId);
            modbusData.setAddress(currentAddress);
            modbusData.setRegisterValue(values[i]);
            modbusData.setTimestamp(LocalDateTime.now());

            repository.save(modbusData);
            System.out.println(existingDataOpt.isPresent() ? "Updated register data: " + modbusData : "Inserted new register data: " + modbusData);
        }
    }


    public ResponseEntity<CoilReadResponseDTO> readCoils(CoilReadRequestDTO coilReadRequestDTO) {
        try {
            ReadCoilsRequest request = new ReadCoilsRequest(coilReadRequestDTO.getStartAddress(), coilReadRequestDTO.getCount());
            request.setUnitID(coilReadRequestDTO.getSlaveId());

            ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
            transaction.setRequest(request);
            transaction.execute();

            CoilReadResponseDTO coilReadResponseDTO = getCoilReadResponseDTO(coilReadRequestDTO, transaction);

            // Store Data in DB
            saveBooleanData("COIL", coilReadResponseDTO.getSlaveId(), coilReadResponseDTO.getStartAddress(), coilReadResponseDTO.getCoilValues());

            return ResponseEntity.ok(coilReadResponseDTO);

        } catch (ModbusException e) {
            throw new RuntimeException("Error reading Modbus coils.", e);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    private CoilReadResponseDTO getCoilReadResponseDTO(CoilReadRequestDTO coilReadRequestDTO, ModbusTCPTransaction transaction) {
        ReadCoilsResponse response = (ReadCoilsResponse) transaction.getResponse();
        BitVector bitVector = response.getCoils();

        CoilReadResponseDTO coilReadResponseDTO = new CoilReadResponseDTO();
        coilReadResponseDTO.setSlaveId(response.getUnitID());
        coilReadResponseDTO.setStartAddress(coilReadRequestDTO.getStartAddress());
        coilReadResponseDTO.setCoilValues(mapBitVectorToMap(bitVector, coilReadRequestDTO.getStartAddress(), coilReadRequestDTO.getCount()));
        return coilReadResponseDTO;

    }

    public ResponseEntity<DiscreteInputReadResponseDTO> readDiscreteInputs(DiscreteInputReadRequestDTO discreteInputReadRequestDTO) {
        try {
            // Read the discrete inputs from Modbus
            ReadInputDiscretesRequest request = new ReadInputDiscretesRequest(
                    discreteInputReadRequestDTO.getStartAddress(),
                    discreteInputReadRequestDTO.getCount()
            );
            request.setUnitID(discreteInputReadRequestDTO.getSlaveId());

            ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
            transaction.setRequest(request);
            transaction.execute();

            // Get response from transaction
            ReadInputDiscretesResponse response = (ReadInputDiscretesResponse) transaction.getResponse();
            BitVector bitVector = response.getDiscretes();

            // Create response DTO
            DiscreteInputReadResponseDTO discreteInputReadResponseDTO = new DiscreteInputReadResponseDTO();
            discreteInputReadResponseDTO.setSlaveId(response.getUnitID());
            discreteInputReadResponseDTO.setStartAddress(discreteInputReadRequestDTO.getStartAddress());
            discreteInputReadResponseDTO.setCount(discreteInputReadRequestDTO.getCount());
            discreteInputReadResponseDTO.setInputValues(mapBitVectorToMap(bitVector, discreteInputReadRequestDTO.getStartAddress(), discreteInputReadRequestDTO.getCount()));

            // ✅ Store data in DB
            saveBooleanData("DISCRETE_INPUT", discreteInputReadResponseDTO.getSlaveId(),
                    discreteInputReadResponseDTO.getStartAddress(), discreteInputReadResponseDTO.getInputValues());

            return ResponseEntity.ok(discreteInputReadResponseDTO);

        } catch (ModbusException e) {
            throw new RuntimeException("Error reading Modbus discrete inputs.", e);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }


    private void saveBooleanData(String type, int slaveId, int startAddress, Map<Integer, String> values) {
        values.forEach((address, value) -> {
            Optional<ModbusData> existingDataOpt = repository.findByTypeAndAddress(type, address);

            ModbusData modbusData = existingDataOpt.orElseGet(ModbusData::new);
            modbusData.setType(type);
            modbusData.setSlaveId(slaveId);
            modbusData.setAddress(address);
            modbusData.setTimestamp(LocalDateTime.now());

            boolean booleanValue = value.equalsIgnoreCase("ON");

            if ("COIL".equals(type)) {
                modbusData.setCoilValue(booleanValue);
            } else if ("DISCRETE_INPUT".equals(type)) {
                modbusData.setDiscreteValue(booleanValue);
            }

            repository.save(modbusData);
            System.out.println(existingDataOpt.isPresent() ? "Updated boolean data: " + modbusData : "Inserted new boolean data: " + modbusData);
        });
    }




    private ReadInputDiscretesResponse getReadInputDiscretesResponse(DiscreteInputReadRequestDTO discreteInputReadRequestDTO) throws ModbusException {
        ReadInputDiscretesRequest request = new ReadInputDiscretesRequest(
                discreteInputReadRequestDTO.getStartAddress(),
                discreteInputReadRequestDTO.getCount()
        );
        request.setUnitID(discreteInputReadRequestDTO.getSlaveId());

        ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
        transaction.setRequest(request);
        transaction.execute();

        return (ReadInputDiscretesResponse) transaction.getResponse();
    }

    private int[] createRegisterArray(ReadMultipleRegistersResponse response){
        int[] registerValues = new int[response.getWordCount()];

        for (int i = 0; i < response.getWordCount(); i++){
            registerValues[i] = response.getRegisterValue(i);
        }
        return registerValues;
    }

    private Map<Integer, String> mapBitVectorToMap(BitVector bitVector, int startAddress, int count) {
        Map<Integer, String> valuesMap = new HashMap<>();
        for (int i = 0; i < count; i++) {
            int address = startAddress + i;
            valuesMap.put(address, bitVector.getBit(i) ? "ON" : "OFF");
        }
        return valuesMap;
    }

    private boolean[] createDiscreteValuesArray(BitVector bitVector, int count){
        boolean[] discreteValues = new boolean[count];

        for (int i = 0; i < count; i++){
            discreteValues[i] = bitVector.getBit(i);
        }
        return discreteValues;
    }
}
