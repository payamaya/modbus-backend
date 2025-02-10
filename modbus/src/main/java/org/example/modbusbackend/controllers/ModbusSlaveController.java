package org.example.modbusbackend.controllers;

import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/modbus/slave")
public class ModbusSlaveController {

    @PostMapping("/write")
    public String writeData(@RequestParam int slaveId, @RequestParam int address, @RequestParam int value) {
        return "Data written to Slave " + slaveId + " at address " + address + " with value " + value;
    }
}
