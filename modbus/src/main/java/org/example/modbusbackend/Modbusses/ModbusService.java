package org.example.modbusbackend.Modbusses;

import com.ghgande.j2mod.modbus.ModbusException;
import com.ghgande.j2mod.modbus.io.ModbusTCPTransaction;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersRequest;
import com.ghgande.j2mod.modbus.msg.ReadMultipleRegistersResponse;
import com.ghgande.j2mod.modbus.msg.WriteSingleRegisterRequest;
import com.ghgande.j2mod.modbus.net.TCPMasterConnection;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.InetAddress;


@Service
public class ModbusService {

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

    public ReadMultipleRegistersResponse readRegisters(ModbusDTO modbusDTO) {
        try {
            ReadMultipleRegistersRequest request = new ReadMultipleRegistersRequest(modbusDTO.getStartAddress(), modbusDTO.getRegisterQuantity());
            request.setUnitID(modbusDTO.getSlaveId());

            ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
            transaction.setRequest(request);
            transaction.execute();

            return (ReadMultipleRegistersResponse) transaction.getResponse();
        } catch (ModbusException e) {
            throw new RuntimeException("Error reading Modbus registers.", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public String writeRegister(ModbusDTO modbusDTO) {
        try {
            WriteSingleRegisterRequest request = new WriteSingleRegisterRequest();
            request.setUnitID(modbusDTO.getSlaveId());
            request.setReference(modbusDTO.getStartAddress());
            SimpleRegister register = new SimpleRegister(modbusDTO.getRegisterValue());
            request.setRegister(register);

            ModbusTCPTransaction transaction = new ModbusTCPTransaction(connection);
            transaction.setRequest(request);
            transaction.execute();

            return "Wrote value: " + modbusDTO.getRegisterValue() + " to register at address: " + modbusDTO.getStartAddress();
        } catch (Exception e) {
            throw new RuntimeException("Error writing to Modbus register.", e);
        }
    }

}