package org.example.modbusbackend.api.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class DiscreteInputReadRequestDTO {
    private int slaveId;
    private int startAddress;
    private int count;

}