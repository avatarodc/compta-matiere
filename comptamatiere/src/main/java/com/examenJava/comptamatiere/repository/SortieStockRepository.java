package com.examenJava.comptamatiere.repository;

import com.examenJava.comptamatiere.model.Article;
import com.examenJava.comptamatiere.model.SortieStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SortieStockRepository extends JpaRepository<SortieStock, Long> {

    // 🔍 Trouve toutes les sorties liées à un article
    List<SortieStock> findByArticle(Article article);
}
