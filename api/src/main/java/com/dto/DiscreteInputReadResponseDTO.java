package com.dto;

import java.util.Map;

public class DiscreteInputReadResponseDTO {
    private int slaveId;
    private int startAddress;
    private Map<Integer, String> inputValues; // Map<Address, Value>

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

    public Map<Integer, String> getInputValues() {
        return inputValues;
    }

    public void setInputValues(Map<Integer, String> inputValues) {
        this.inputValues = inputValues;
    }
}