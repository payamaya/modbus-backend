package org.example.modbusbackend.api.dto;

public class ModbusReadRequestDTO {
    private int slaveId;
    private int address;
    private int numRegisters;

    //TODO: Change these ints to shorts to better reflect the Modbus data standard?

    public int getSlaveId() {
        return slaveId;
    }

    public int getAddress() {
        return address;
    }

    public int getNumRegisters() {
        return numRegisters;
    }

    public void setSlaveId(int slaveId) {
        this.slaveId = slaveId;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public void setNumRegisters(int numRegisters) {
        this.numRegisters = numRegisters;
    }
}
