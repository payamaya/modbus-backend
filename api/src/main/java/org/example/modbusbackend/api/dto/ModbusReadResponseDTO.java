package org.example.modbusbackend.api.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ModbusReadResponseDTO {
    private int slaveId;
    private int address;
    private int numRegisters;
    private int[] registerValues;

    //TODO: Change these ints to shorts to better reflect the Modbus data standard?

}
