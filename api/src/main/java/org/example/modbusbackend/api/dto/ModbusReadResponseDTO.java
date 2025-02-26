package org.example.modbusbackend.api.dto;

public class ModbusReadResponseDTO {
    private int slaveId;
    private int address;
    private int numRegisters;
    private int[] registerValues;

    //TODO: Change these ints to shorts to better reflect the Modbus data standard?

    public int getSlaveId() {
        return slaveId;
    }

    public void setSlaveId(int slaveId) {
        this.slaveId = slaveId;
    }

    // Getters and Setters
    public int getAddress() {
        return address;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getNumRegisters() {
        return numRegisters;
    }

    public void setNumRegisters(int numRegisters) {
        this.numRegisters = numRegisters;
    }

    public int[] getRegisterValues() {
        return registerValues;
    }

    public void setRegisterValues(int[] registerValues) {
        this.registerValues = registerValues;
    }
}
