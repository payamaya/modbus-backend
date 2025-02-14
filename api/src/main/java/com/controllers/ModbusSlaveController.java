package com.controllers;

import com.dto.ModbusWriteSingleRegisterRequestDTO;
import com.services.ModbusSlaveService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/modbus/slave")
public class ModbusSlaveController {

    private final ModbusSlaveService modbusSlaveService;

    public ModbusSlaveController(ModbusSlaveService modbusSlaveService) {
        this.modbusSlaveService = modbusSlaveService;
    }

    @PostMapping("/write-single")
    public ResponseEntity<String> writeRegister(
            @RequestParam int slaveId,
            @RequestParam int startAddress,
            @RequestParam int registerValue
    ) {
        ModbusWriteSingleRegisterRequestDTO modbusWriteSingleRegisterRequestDTO = new ModbusWriteSingleRegisterRequestDTO();
        modbusWriteSingleRegisterRequestDTO.setSlaveId(slaveId);
        modbusWriteSingleRegisterRequestDTO.setAddress(startAddress);
        modbusWriteSingleRegisterRequestDTO.setRegisterValue(registerValue);
        try {
            String result = modbusSlaveService.writeRegister(modbusWriteSingleRegisterRequestDTO);
            return new ResponseEntity<>(result, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>("Error writing to register: " + e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
