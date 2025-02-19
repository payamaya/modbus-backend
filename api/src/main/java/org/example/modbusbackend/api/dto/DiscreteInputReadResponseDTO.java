package org.example.modbusbackend.api.dto;

import java.util.Map;

public class DiscreteInputReadResponseDTO {
    private int slaveId;
    private int startAddress;
    private Map<Integer, String> inputValues; // Map<Address, Value>
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

    public Map<Integer, String> getInputValues() {
        return inputValues;
    }

    public int getCount(){
        return count;
    }

    public void setCount(int count){
        this.count = count;
    }

    public void setInputValues(Map<Integer, String> inputValues) {
        this.inputValues = inputValues;
    }
}