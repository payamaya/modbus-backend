package org.example.modbusbackend.api.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class CoilReadRequestDTO {

    private int slaveId;
    private int startAddress;
    private int count;

}