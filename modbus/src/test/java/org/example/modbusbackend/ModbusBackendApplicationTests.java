package org.example.modbusbackend;


import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import org.junit.jupiter.api.Test;

import org.mockito.Mock;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import java.net.InetAddress;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;

@SpringBootTest
class ModbusBackendApplicationTests {



    @Value("${modbus.slave.ip}")
    private String slaveIp;
    @Value("${modbus.slave.port}")
    private int slavePort;
    @Mock
    private TCPMasterConnection connection;



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

}
