package com.examenJava.comptamatiere.repository;

import com.examenJava.comptamatiere.model.SortieStock;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SortieStockRepository extends JpaRepository<SortieStock, Long> {
}
