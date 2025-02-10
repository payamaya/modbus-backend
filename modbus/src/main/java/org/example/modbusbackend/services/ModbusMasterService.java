package org.example.modbusbackend.services;

import com.ghgande.j2mod.modbus.facade.ModbusTCPMaster;
import com.ghgande.j2mod.modbus.procimg.Register;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.Map;

@Service
public class ModbusMasterService {
    private final ModbusTCPMaster modbusMaster;

    public ModbusMasterService() {
        this.modbusMaster = new ModbusTCPMaster("127.0.0.1", 502);
    }

    // Read multiple registers (Holding Registers)
    public ResponseEntity<Map<String, Integer[]>> readHoldingRegister(int slaveId, int address, int numRegisters) {
        try {
            if (!modbusMaster.isConnected()) {
                modbusMaster.connect();
            }

            // Read multiple registers from the slave
            Register[] registers = modbusMaster.readMultipleRegisters(slaveId, address, numRegisters);
            Integer[] values = new Integer[numRegisters];

            // Convert the registers to their respective values
            for (int i = 0; i < numRegisters; i++) {
                values[i] = registers[i].getValue();
            }

            modbusMaster.disconnect();

            // Return the values in a Map
            Map<String, Integer[]> response = Collections.singletonMap("values", values);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", new Integer[] {-1}));
        }
    }

}
