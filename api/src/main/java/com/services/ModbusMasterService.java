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
            ReadCoilsRequest request = new ReadCoilsRequest(coilReadRequestDTO.getStartAddress(), coilReadRequestDTO.getCount());
            request.setUnitID(coilReadRequestDTO.getSlaveId());

            ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
            transaction.setRequest(request);
            transaction.execute();

            CoilReadResponseDTO coilReadResponseDTO = getCoilReadResponseDTO(coilReadRequestDTO, transaction);

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
            BitVector bitVector = response.getDiscretes(); // ✅ Returns a BitVector

            DiscreteInputReadResponseDTO discreteInputReadResponseDTO = new DiscreteInputReadResponseDTO();
            discreteInputReadResponseDTO.setSlaveId(response.getUnitID());
            discreteInputReadResponseDTO.setStartAddress(discreteInputReadRequestDTO.getStartAddress());
            discreteInputReadResponseDTO.setCount(discreteInputReadRequestDTO.getCount());
            discreteInputReadResponseDTO.setInputValues(mapBitVectorToMap(bitVector, discreteInputReadRequestDTO.getStartAddress())); // ✅ Now it's a Map<Integer, String>

            return ResponseEntity.ok(discreteInputReadResponseDTO);

        } catch (ModbusException e) {
            throw new RuntimeException("Error reading Modbus discrete inputs.", e);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(null);
        }
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
