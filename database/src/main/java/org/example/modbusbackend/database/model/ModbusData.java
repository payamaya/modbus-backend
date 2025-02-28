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

    @Getter
    @Setter
    private String type; // COIL, DISCRETE_INPUT, HOLDING_REGISTER, INPUT_REGISTER

    @Getter
    @Setter
    private int slaveId;
    @Getter
    @Setter
    private int address;

    private Integer registerValue; // Holding & Input Registers
    private Boolean coilValue; // Coils
    private Boolean discreteValue; // Discrete Inputs

    @Getter
    @Setter
    private LocalDateTime timestamp;

    public void setRegisterValue(int registerValue) {
        this.registerValue = registerValue;
    }

    public int getRegisterValue() {
        return registerValue;
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
