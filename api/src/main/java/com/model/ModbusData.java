package com.model;

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
    private Boolean coilValue; // Coils & Discrete Inputs

    private LocalDateTime timestamp;
}
