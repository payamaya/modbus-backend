package com.dto;

public class ModbusWriteSingleRegisterRequestDTO {
    private int slaveId;
    private int address;
    private int registerValue;

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

    public int getRegisterValue() {
        return registerValue;
    }

    public void setRegisterValue(int registerValue) {
        this.registerValue = registerValue;
    }
}
