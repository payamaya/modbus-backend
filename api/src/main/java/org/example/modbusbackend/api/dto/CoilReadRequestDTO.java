package org.example.modbusbackend.api.dto;

public class CoilReadRequestDTO {
    private int slaveId;
    private int startAddress;
    private int count;

    // Getters and Setters
    public int getSlaveId() {
        return slaveId;
    }

    public void setSlaveId(int slaveId) {
        this.slaveId = slaveId;
    }

    public int getStartAddress() {
        return startAddress;
    }

    public void setStartAddress(int startAddress) {
        this.startAddress = startAddress;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }
}