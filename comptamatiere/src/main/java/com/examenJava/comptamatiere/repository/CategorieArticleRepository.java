package com.examenJava.comptamatiere.repository;

import com.examenJava.comptamatiere.model.CategorieArticle;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategorieArticleRepository extends JpaRepository<CategorieArticle, Long> {
}
