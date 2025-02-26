package org.example.modbusbackend;


import com.controllers.ModbusMasterController;
import com.dto.ModbusReadRequestDTO;
import com.dto.ModbusReadResponseDTO;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.services.ModbusMasterService;
import org.junit.jupiter.api.Test;

import org.mockito.InjectMocks;
import org.mockito.Mock;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;


import java.net.InetAddress;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;


@SpringBootTest
class ModbusBackendApplicationTests {


    @Value("${modbus.slave.ip}")
    private String slaveIp;
    @Value("${modbus.slave.port}")
    private int slavePort;
    @Mock
    private TCPMasterConnection connection;

    @InjectMocks
    private ModbusMasterController modbusMasterController;

    @Mock
    private ModbusMasterService modbusMasterService;


    @Test
    void testShouldReturnSuccessfulConnectionToSpecificModbusSlave() throws Exception {
        //Arrange
        InetAddress address = InetAddress.getByName(slaveIp);
        connection = new TCPMasterConnection(address);
        connection.setPort(slavePort);

        //Act
        connection.connect();

        //Assert
        assertTrue(connection.isConnected());
        connection.close();

    }

    @Test
    public void testReadRegisters() {
        // Arrange
        int slaveId = 1;
        int address = 1;
        int numRegisters = 5;

        ModbusReadRequestDTO requestDTO = new ModbusReadRequestDTO();
        requestDTO.setSlaveId(slaveId);
        requestDTO.setAddress(address);
        requestDTO.setNumRegisters(numRegisters);

        ModbusReadResponseDTO responseDTO = new ModbusReadResponseDTO();
        responseDTO.setSlaveId(slaveId);
        responseDTO.setAddress(address);
        responseDTO.setNumRegisters(numRegisters);
        responseDTO.setRegisterValues(new int[]{10, 20});

        when(modbusMasterService.readRegisters(any(ModbusReadRequestDTO.class))).thenReturn(ResponseEntity.ok(responseDTO));

        // Act
        ResponseEntity<ModbusReadResponseDTO> responseEntity = modbusMasterController.readData(slaveId, address, numRegisters);

        // Assert
        assertEquals(200, responseEntity.getStatusCode().value());
        assertEquals(slaveId, Objects.requireNonNull(responseEntity.getBody()).getSlaveId());
        assertEquals(address, responseEntity.getBody().getAddress());
        assertEquals(numRegisters, responseEntity.getBody().getNumRegisters());
    }

}
