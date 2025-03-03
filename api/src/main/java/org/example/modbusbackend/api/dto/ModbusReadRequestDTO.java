package org.example.modbusbackend.api.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ModbusReadRequestDTO {
    private int slaveId;
    private int address;
    private int numRegisters;

    //TODO: Change these ints to shorts to better reflect the Modbus data standard?

}
