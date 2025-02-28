package org.example.modbusbackend.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class DiscreteInputReadResponseDTO {
    private int slaveId;
    private int startAddress;
    private boolean[] discreteValues; // Map<Address, Value>
    private Map<Integer, String> inputValues; // Map<Address, Value>
    private int count;

}