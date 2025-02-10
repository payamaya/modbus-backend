package org.example.modbusbackend.controllers;

import com.ghgande.j2mod.modbus.procimg.Register;
import org.example.modbusbackend.services.ModbusMasterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/modbus")
public class ModbusController {

    private final ModbusMasterService modbusMasterService;

    public ModbusController(ModbusMasterService modbusMasterService) {
        this.modbusMasterService = modbusMasterService;
    }

    // READ HOLDING REGISTER (Function Code 3)
    @GetMapping("/read-holding-register/{slaveId}/{address}")
    public ResponseEntity<String> readHoldingRegister(
            @PathVariable int slaveId,
            @PathVariable int address) {
        return ResponseEntity.ok(modbusMasterService.readHoldingRegister(slaveId, address));
    }

    // READ INPUT REGISTER (Function Code 4)
    @GetMapping("/read-input-register/{slaveId}/{address}")
    public ResponseEntity<String> readInputRegister(
            @PathVariable int slaveId,
            @PathVariable int address) {
        return ResponseEntity.ok(modbusMasterService.readInputRegister(slaveId, address));
    }

    // READ COILS (Function Code 1)
    @GetMapping("/read-coils/{slaveId}/{startAddress}/{count}")
    public ResponseEntity<String> readCoils(
            @PathVariable int slaveId,
            @PathVariable int startAddress,
            @PathVariable int count) {
        return ResponseEntity.ok(modbusMasterService.readCoils(slaveId, startAddress, count));
    }

    // READ DISCRETE INPUTS (Function Code 2)
    @GetMapping("/read-discrete-inputs/{slaveId}/{startAddress}/{count}")
    public ResponseEntity<String> readDiscreteInputs(
            @PathVariable int slaveId,
            @PathVariable int startAddress,
            @PathVariable int count) {
        return ResponseEntity.ok(modbusMasterService.readDiscreteInputs(slaveId, startAddress, count));
    }

    // WRITE TO A SINGLE HOLDING REGISTER (Function Code 6)
    @PostMapping("/write-holding-register/{slaveId}/{address}/{value}")
    public ResponseEntity<String> writeSingleHoldingRegister(
            @PathVariable int slaveId,
            @PathVariable int address,
            @PathVariable int value) {
        return ResponseEntity.ok(modbusMasterService.writeSingleRegister(slaveId, address, value));
    }
}
