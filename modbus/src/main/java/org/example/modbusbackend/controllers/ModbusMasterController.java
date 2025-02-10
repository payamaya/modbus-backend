package org.example.modbusbackend.controllers;

import org.example.modbusbackend.services.ModbusMasterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public ResponseEntity<Map<String, Integer[]>> readData(
            @RequestParam int slaveId,
            @RequestParam int address,
            @RequestParam int numRegisters) {
        return modbusMasterService.readHoldingRegister(slaveId, address, numRegisters);
    }


}
