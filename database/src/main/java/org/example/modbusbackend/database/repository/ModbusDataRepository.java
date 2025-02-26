package org.example.modbusbackend.database.repository;

import org.example.modbusbackend.database.model.ModbusData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ModbusDataRepository extends JpaRepository<ModbusData, Long> {
    Optional<ModbusData> findByTypeAndAddress(String type, int address);
}
