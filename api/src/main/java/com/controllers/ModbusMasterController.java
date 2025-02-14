package com.controllers;

import com.dto.ModbusReadRequestDTO;
import com.dto.ModbusReadResponseDTO;
import com.services.ModbusMasterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/modbus/master")
public class ModbusMasterController {
    private final ModbusMasterService modbusMasterService;

    public ModbusMasterController(ModbusMasterService modbusMasterService) {
        this.modbusMasterService = modbusMasterService;
    }

    // Endpoint to read multiple registers from the master
    @GetMapping("/read")
    public ResponseEntity<ModbusReadResponseDTO> readData(
            @RequestParam int slaveId,
            @RequestParam int address,
            @RequestParam int numRegisters) {

        ModbusReadRequestDTO modbusReadRequestDTO = new ModbusReadRequestDTO();
        modbusReadRequestDTO.setSlaveId(slaveId);
        modbusReadRequestDTO.setAddress(address);
        modbusReadRequestDTO.setNumRegisters(numRegisters);

        return modbusMasterService.readRegisters(modbusReadRequestDTO);
    }


}
