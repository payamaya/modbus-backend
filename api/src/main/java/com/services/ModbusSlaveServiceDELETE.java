/*package com.services;

import com.ghgande.j2mod.modbus.facade.ModbusTCPMaster;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import org.springframework.stereotype.Service;

@Service
public class ModbusSlaveServiceDELETE {
    private final ModbusTCPMaster modbusMaster;

    public ModbusSlaveServiceDELETE() {
        this.modbusMaster = new ModbusTCPMaster("127.0.0.1", 502);
    }

    // Write a single register to the slave
    public String writeRegister(int slaveId, int address, int value) {
        try {
            if (!modbusMaster.isConnected()) {
                modbusMaster.connect();
            }

            // Write value to register
            modbusMaster.writeSingleRegister(slaveId, address, new SimpleRegister(value));

            modbusMaster.disconnect();
            return "Data written to Slave " + slaveId + " at address " + address + " with value " + value;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error writing Modbus register: " + e.getMessage();
        }
    }

    // Read multiple registers from the slave
    public Integer[] readRegisters(int slaveId, int address, int numRegisters) {
        try {
            if (!modbusMaster.isConnected()) {
                modbusMaster.connect();
            }

            // Read multiple registers
            Register[] registers = modbusMaster.readMultipleRegisters(slaveId, address, numRegisters);
            Integer[] values = new Integer[numRegisters];

            // Extract the values from the registers
            for (int i = 0; i < numRegisters; i++) {
                values[i] = registers[i].getValue();
            }

            modbusMaster.disconnect();
            return values;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}*/
