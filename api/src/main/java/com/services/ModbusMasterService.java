package com.services;

import com.dto.*;
import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.*;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.ghgande.j2mod.modbus.util.BitVector;
import com.database.model.ModbusData;
import com.database.repository.ModbusDataRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
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


            System.out.println("Modbus connection established.");
        } catch (Exception e) {
            throw new RuntimeException("Failed to connect to Modbus slave.", e);
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


    private int[] createRegisterArray(ReadMultipleRegistersResponse response){
        int[] registerValues = new int[response.getWordCount()];

        for (int i = 0; i < response.getWordCount(); i++){
            registerValues[i] = response.getRegisterValue(i);
        }
        return registerValues;
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
            saveRegisterData(response.getUnitID(), modbusReadRequestDTO.getAddress(), createRegisterArray(response));

            return ResponseEntity.ok(modbusReadResponseDTO);

        } catch (ModbusException e) {
            throw new RuntimeException("Error reading Modbus registers.", e);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    private void saveRegisterData(int slaveId, int startAddress, int[] values) {
        for (int i = 0; i < values.length; i++) {
            ModbusData data = new ModbusData();
            data.setType("HOLDING_REGISTER");
            data.setSlaveId(slaveId);
            data.setAddress(startAddress + i);
            data.setRegisterValue(values[i]);
            data.setTimestamp(LocalDateTime.now());

            System.out.println("Saving data: " + data);
            repository.save(data);
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
        coilReadResponseDTO.setCoilValues(mapBitVectorToMap(bitVector, coilReadRequestDTO.getStartAddress()));
        return coilReadResponseDTO;

    }


    public ResponseEntity<DiscreteInputReadResponseDTO> readDiscreteInputs(DiscreteInputReadRequestDTO discreteInputReadRequestDTO) {
        try {
            ReadInputDiscretesRequest request = new ReadInputDiscretesRequest(
                    discreteInputReadRequestDTO.getStartAddress(),
                    discreteInputReadRequestDTO.getCount()
            );
            request.setUnitID(discreteInputReadRequestDTO.getSlaveId());

            ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
            transaction.setRequest(request);
            transaction.execute();

            ReadInputDiscretesResponse response = (ReadInputDiscretesResponse) transaction.getResponse();
            BitVector bitVector = response.getDiscretes();

            DiscreteInputReadResponseDTO discreteInputReadResponseDTO = new DiscreteInputReadResponseDTO();
            discreteInputReadResponseDTO.setSlaveId(response.getUnitID());
            discreteInputReadResponseDTO.setStartAddress(discreteInputReadRequestDTO.getStartAddress());
            discreteInputReadResponseDTO.setCount(discreteInputReadRequestDTO.getCount());
            discreteInputReadResponseDTO.setInputValues(mapBitVectorToMap(bitVector, discreteInputReadRequestDTO.getStartAddress()));

            // Store Data in DB
            saveBooleanData("DISCRETE_INPUT", response.getUnitID(), discreteInputReadRequestDTO.getStartAddress(), discreteInputReadResponseDTO.getInputValues());

            return ResponseEntity.ok(discreteInputReadResponseDTO);

        } catch (ModbusException e) {
            throw new RuntimeException("Error reading Modbus discrete inputs.", e);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    private void saveBooleanData(String type, int slaveId, int startAddress, Map<Integer, String> values) {
        values.forEach((address, value) -> {
            ModbusData data = new ModbusData();
            data.setType(type);
            data.setSlaveId(slaveId);
            data.setAddress(address);
            data.setCoilValue(value.equals("ON"));
            data.setTimestamp(LocalDateTime.now());
            repository.save(data);
        });
    }

    private Map<Integer, String> mapBitVectorToMap(BitVector bitVector, int startAddress) {
        Map<Integer, String> valuesMap = new HashMap<>();
        for (int i = 0; i < bitVector.size(); i++) {
            int address = startAddress + i;
            valuesMap.put(address, bitVector.getBit(i) ? "ON" : "OFF");
        }
        return valuesMap;
    }
}
