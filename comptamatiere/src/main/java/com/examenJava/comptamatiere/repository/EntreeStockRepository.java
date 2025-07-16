package com.examenJava.comptamatiere.repository;

import com.examenJava.comptamatiere.model.Article;
import com.examenJava.comptamatiere.model.EntreeStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface EntreeStockRepository extends JpaRepository<EntreeStock, Long> {

    // 🔍 Permet de retrouver toutes les entrées liées à un article
    List<EntreeStock> findByArticle(Article article);
}
