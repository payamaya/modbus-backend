package org.example.modbusbackend.database.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "modbus_data")
public class ModbusData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String type; // COIL, DISCRETE_INPUT, HOLDING_REGISTER, INPUT_REGISTER

    private int slaveId;
    private int address;

    private Integer registerValue; // Holding & Input Registers
    private Boolean coilValue; // Coils
    private Boolean discreteValue; // Discrete Inputs

    private LocalDateTime timestamp;

    public void setType(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }

    public void setSlaveId(int slaveId) {
        this.slaveId = slaveId;
    }

    public int getSlaveId() {
        return slaveId;
    }

    public void setAddress(int address) {
        this.address = address;
    }

    public int getAddress() {
        return address;
    }

    public void setRegisterValue(int registerValue) {
        this.registerValue = registerValue;
    }

    public int getRegisterValue() {
        return registerValue;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setCoilValue(boolean coilValue) {
        this.coilValue = coilValue;
    }

    public boolean isCoilValue() {
        return coilValue;
    }

    public void setDiscreteValue(boolean discreteValue) {
        this.discreteValue = discreteValue;
    }

    public boolean isDiscreteValue() {
        return discreteValue;
    }
}
