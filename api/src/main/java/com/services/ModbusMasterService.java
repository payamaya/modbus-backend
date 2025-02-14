package com.services;

import com.dto.ModbusReadRequestDTO;
import com.dto.ModbusReadResponseDTO;
import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.net.InetAddress;

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
}
