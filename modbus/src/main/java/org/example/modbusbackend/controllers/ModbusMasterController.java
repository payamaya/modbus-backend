package org.example.modbusbackend.controllers;

import org.example.modbusbackend.services.ModbusMasterService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/modbus/master")
public class ModbusMasterController {
    private final ModbusMasterService modbusMasterService;

    public ModbusMasterController(ModbusMasterService modbusMasterService) {
        this.modbusMasterService = modbusMasterService;
    }

    @GetMapping("/read")
    public String readData(@RequestParam int slaveId, @RequestParam int address) {
        return modbusMasterService.readHoldingRegister(slaveId, address);
    }
}
