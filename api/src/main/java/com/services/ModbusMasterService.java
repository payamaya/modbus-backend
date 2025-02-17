package com.services;

import com.dto.*;
import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.*;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.ghgande.j2mod.modbus.util.BitVector;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

@Service
public class ModbusMasterService {

    private TCPMasterConnection connection;

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


            return ResponseEntity.ok((modbusReadResponseDTO));

        } catch (ModbusException e) {
            throw new RuntimeException("Error reading Modbus registers.", e);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
            //throw new RuntimeException(e);
        }
    }

    public ResponseEntity<CoilReadResponseDTO> readCoils(CoilReadRequestDTO coilReadRequestDTO) {
        try {
            Map<Integer, String> coilValues = readModbusValues(
                    coilReadRequestDTO.getSlaveId(),
                    coilReadRequestDTO.getStartAddress(),
                    coilReadRequestDTO.getCount(),
                    ReadCoilsRequest::new,
                    response -> ((ReadCoilsResponse) response).getCoils()
            );

            return ResponseEntity.ok(new CoilReadResponseDTO(
                    coilReadRequestDTO.getSlaveId(),
                    coilReadRequestDTO.getStartAddress(),
                    coilValues
            ));

        } catch (ModbusException e) {
            throw new RuntimeException("Error reading Modbus coils.", e);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    public ResponseEntity<DiscreteInputReadResponseDTO> readDiscreteInputs(DiscreteInputReadRequestDTO discreteInputReadRequestDTO) {
        try {
            Map<Integer, String> inputValues = readModbusValues(
                    discreteInputReadRequestDTO.getSlaveId(),
                    discreteInputReadRequestDTO.getStartAddress(),
                    discreteInputReadRequestDTO.getCount(),
                    ReadInputDiscretesRequest::new,
                    response -> ((ReadInputDiscretesResponse) response).getDiscretes()
            );

            return ResponseEntity.ok(new DiscreteInputReadResponseDTO(
                    discreteInputReadRequestDTO.getSlaveId(),
                    discreteInputReadRequestDTO.getStartAddress(),
                    inputValues
            ));

        } catch (ModbusException e) {
            throw new RuntimeException("Error reading Modbus discrete inputs.", e);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
    }

    private Map<Integer, String> readModbusValues(int slaveId, int startAddress, int count,
                                                  BiFunction<Integer, Integer, ModbusRequest> requestSupplier,
                                                  Function<ModbusResponse, BitVector> responseExtractor) throws ModbusException {
        return executeModbusTransaction(slaveId, startAddress, count, requestSupplier, response -> {
            BitVector bitVector = responseExtractor.apply(response);
            Map<Integer, String> values = new HashMap<>();
            for (int i = 0; i < bitVector.size(); i++) {
                values.put(startAddress + i, bitVector.getBit(i) ? "ON" : "OFF");
            }
            return values;
        });
    }


    private <T extends ModbusResponse> Map<Integer, String> executeModbusTransaction(
            int slaveId,
            int startAddress,
            int count,
            BiFunction<Integer, Integer, ModbusRequest> requestCreator,
            Function<T, Map<Integer, String>> responseMapper
    ) throws ModbusException {
        ModbusRequest request = requestCreator.apply(startAddress, count);
        request.setUnitID(slaveId);

        ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
        transaction.setRequest(request);
        transaction.execute();

        T response = (T) transaction.getResponse();
        return responseMapper.apply(response);
    }
}
