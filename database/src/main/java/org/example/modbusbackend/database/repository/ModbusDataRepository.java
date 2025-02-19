package org.example.modbusbackend.database.repository;

import org.example.modbusbackend.database.model.ModbusData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ModbusDataRepository extends JpaRepository<ModbusData, Long> {
}