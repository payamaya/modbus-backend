package org.example.modbusbackend.api.dto;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class ModbusWriteSingleRegisterRequestDTO {
    private int slaveId;
    private int address;
    private int registerValue;

    //TODO: Change these ints to shorts to better reflect the Modbus data standard?

}
