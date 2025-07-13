package com.examenJava.comptamatiere.repository;

import com.examenJava.comptamatiere.model.EntreeStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntreeStockRepository extends JpaRepository<EntreeStock, Long> {
}
