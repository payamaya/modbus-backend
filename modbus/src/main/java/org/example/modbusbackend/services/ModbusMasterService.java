package org.example.modbusbackend.services;

import com.ghgande.j2mod.modbus.facade.ModbusTCPMaster;
import com.ghgande.j2mod.modbus.procimg.Register;
import org.springframework.stereotype.Service;

@Service
public class ModbusMasterService {
    private final ModbusTCPMaster modbusMaster;

    public ModbusMasterService() {
        this.modbusMaster = new ModbusTCPMaster("127.0.0.1", 502);
    }

    public String readHoldingRegister(int slaveId, int address) {
        try {
            // ✅ Open connection before reading
            if (!modbusMaster.isConnected()) {
                modbusMaster.connect();
            }

            // ✅ Read register value
            Register register = modbusMaster.readMultipleRegisters(slaveId, address, 1)[0];

            // ✅ Close connection after reading
            modbusMaster.disconnect();

            return "Register value: " + register.getValue();
        } catch (Exception e) {
            return "Error reading Modbus register: " + e.getMessage();
        }
    }
}
