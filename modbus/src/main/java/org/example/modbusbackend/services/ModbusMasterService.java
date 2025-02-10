package org.example.modbusbackend.services;

import com.ghgande.j2mod.modbus.facade.ModbusTCPMaster;
import com.ghgande.j2mod.modbus.procimg.InputRegister;
import com.ghgande.j2mod.modbus.procimg.Register;
import com.ghgande.j2mod.modbus.procimg.SimpleRegister;
import com.ghgande.j2mod.modbus.util.BitVector;
import org.springframework.stereotype.Service;

@Service
public class ModbusMasterService {
    private final ModbusTCPMaster modbusMaster;

    public ModbusMasterService() {
        this.modbusMaster = new ModbusTCPMaster("127.0.0.1", 502);
    }

    /**
     * Read a single holding register (Function Code 3).
     */
    public String readHoldingRegister(int slaveId, int address) {
        return readRegister(slaveId, address);
    }

    /**
     * Read a single input register (Function Code 4).
     */
    public String readInputRegister(int slaveId, int address) {
        return readInputRegisterInternal(slaveId, address);
    }

    /**
     * Read multiple coils (Function Code 1).
     */
    public String readCoils(int slaveId, int startAddress, int count) {
        try {
            if (!modbusMaster.isConnected()) {
                modbusMaster.connect();
            }

            BitVector coils = modbusMaster.readCoils(slaveId, startAddress, count);
            modbusMaster.disconnect();

            StringBuilder result = new StringBuilder("Coil values: ");
            for (int i = 0; i < coils.size(); i++) {
                result.append("Coil ").append(startAddress + i).append(": ").append(coils.getBit(i) ? "ON" : "OFF").append(", ");
            }

            return result.toString();
        } catch (Exception e) {
            return "Error reading coils: " + e.getMessage();
        }
    }

    /**
     * Read multiple discrete inputs (Function Code 2).
     */
    public String readDiscreteInputs(int slaveId, int startAddress, int count) {
        try {
            if (!modbusMaster.isConnected()) {
                modbusMaster.connect();
            }

            BitVector inputs = modbusMaster.readInputDiscretes(slaveId, startAddress, count);
            modbusMaster.disconnect();

            StringBuilder result = new StringBuilder("Discrete input values: ");
            for (int i = 0; i < inputs.size(); i++) {
                result.append("Input ").append(startAddress + i).append(": ").append(inputs.getBit(i) ? "ON" : "OFF").append(", ");
            }

            return result.toString();
        } catch (Exception e) {
            return "Error reading discrete inputs: " + e.getMessage();
        }
    }

    /**
     * Write a single holding register (Function Code 6).
     */
    public String writeSingleRegister(int slaveId, int address, int value) {
        try {
            if (!modbusMaster.isConnected()) {
                modbusMaster.connect();
            }
            Register registerValue = new SimpleRegister(value);
            modbusMaster.writeSingleRegister(slaveId, address, registerValue);
            modbusMaster.disconnect();

            return "Successfully wrote value " + value + " to register " + address;
        } catch (Exception e) {
            return "Error writing to register: " + e.getMessage();
        }
    }

    /**
     * Generic method to read a single register and handle exceptions.
     */
    private String readRegister(int slaveId, int address) {
        try {
            if (!modbusMaster.isConnected()) {
                modbusMaster.connect();
            }

            Register register = modbusMaster.readMultipleRegisters(slaveId, address, 1)[0];
            modbusMaster.disconnect();
            return "Holding Register" + " value at " + address + ": " + register.getValue();
        } catch (Exception e) {
            return "Error reading " + "Holding Register" + ": " + e.getMessage();
        }
    }

    /**
     * Internal method for reading input registers with exception handling.
     */
    private String readInputRegisterInternal(int slaveId, int address) {
        try {
            if (!modbusMaster.isConnected()) {
                modbusMaster.connect();
            }

            InputRegister register = modbusMaster.readInputRegisters(slaveId, address, 1)[0];
            modbusMaster.disconnect();
            return "Input Register value at " + address + ": " + register.getValue();
        } catch (Exception e) {
            return "Error reading Input Register: " + e.getMessage();
        }
    }
}
