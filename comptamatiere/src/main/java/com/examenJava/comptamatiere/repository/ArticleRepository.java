package com.examenJava.comptamatiere.repository;

import com.examenJava.comptamatiere.model.Article;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ArticleRepository extends JpaRepository<Article, Long> {
}
