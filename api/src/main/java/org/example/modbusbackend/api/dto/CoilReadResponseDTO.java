package org.example.modbusbackend.api.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.Map;

@Setter
@Getter
public class CoilReadResponseDTO {
    private int slaveId;
    private int startAddress;
    private Map<Integer, String> coilValues; // Map<Address, Value>

}