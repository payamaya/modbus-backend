package com.controllers;

import com.dto.*;
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
    @GetMapping("/read-registers")
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

    @GetMapping("/read-coils")
    public ResponseEntity<CoilReadResponseDTO> readCoils(
            @RequestParam int slaveId,
            @RequestParam int startAddress,
            @RequestParam int count) {

        CoilReadRequestDTO requestDTO = new CoilReadRequestDTO();
        requestDTO.setSlaveId(slaveId);
        requestDTO.setStartAddress(startAddress);
        requestDTO.setCount(count);

        return modbusMasterService.readCoils(requestDTO);
    }

    @GetMapping("/read-discrete-inputs")
    public ResponseEntity<DiscreteInputReadResponseDTO> readDiscreteInputs(
            @RequestParam int slaveId,
            @RequestParam int startAddress,
            @RequestParam int count) {

        DiscreteInputReadRequestDTO requestDTO = new DiscreteInputReadRequestDTO();
        requestDTO.setSlaveId(slaveId);
        requestDTO.setStartAddress(startAddress);
        requestDTO.setCount(count);

        return modbusMasterService.readDiscreteInputs(requestDTO);
    }


}
