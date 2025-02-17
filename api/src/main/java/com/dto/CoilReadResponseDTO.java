package com.dto;

import java.util.Map;

public class CoilReadResponseDTO {
    private int slaveId;
    private int startAddress;
    private Map<Integer, String> coilValues; // Map<Address, Value>

    public CoilReadResponseDTO(int slaveId, int startAddress, Map<Integer, String> coilValues) {
        this.slaveId = slaveId;
        this.startAddress = startAddress;
        this.coilValues = coilValues;
    }

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

    public Map<Integer, String> getCoilValues() {
        return coilValues;
    }

    public void setCoilValues(Map<Integer, String> coilValues) {
        this.coilValues = coilValues;
    }
}