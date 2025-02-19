package com.repository;

import com.model.ModbusData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import javax.crypto.Cipher;

@Repository
public interface ModbusDataRepository extends JpaRepository<ModbusData, Long> {
}
