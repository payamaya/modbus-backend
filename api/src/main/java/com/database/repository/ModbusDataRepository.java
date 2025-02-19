package com.database.repository;

import com.database.model.ModbusData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModbusDataRepository extends JpaRepository<ModbusData, Long> {
}
