package com.controllers;

import com.dto.ModbusReadRequestDTO;
import com.dto.ModbusReadResponseDTO;
import com.services.ModbusMasterService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/modbus/master")
public class ModbusMasterController {
    private final ModbusMasterService modbusMasterService;

    public ModbusMasterController(ModbusMasterService modbusMasterService) {
        this.modbusMasterService = modbusMasterService;
    }

    /*@GetMapping("/read-inputs")
    public List<ModbusReadResponseDTO> readInputs(
            @RequestParam(defaultValue = "0") int startAddress,
            @RequestParam(defaultValue = "9") int count
    ) {
        boolean[] values = modbusService.readDiscreteInputs(startAddress, count);
        List<ModbusReadResponseDTO> responseList = new ArrayList<>();

        for (int i = 0; i < count; i++) {
            responseList.add(new ModbusReadResponseDTO(startAddress + i, values[i]));
        }

        return responseList;
    }*/

    /*
    *
    @GetMapping("/read")
    public ResponseEntity<ReadMultipleRegistersResponse> readRegisters(
            @RequestParam int slaveId,
            @RequestParam int startAddress,
            @RequestParam int registerQuantity
    ) {
        ModbusDTO modbusDTO = new ModbusDTO();
        modbusDTO.setSlaveId(slaveId);
        modbusDTO.setStartAddress(startAddress);
        modbusDTO.setRegisterQuantity(registerQuantity);
        try {
            ReadMultipleRegistersResponse response = modbusService.readRegisters(modbusDTO);
            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    *
    *
    *
    * */

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
