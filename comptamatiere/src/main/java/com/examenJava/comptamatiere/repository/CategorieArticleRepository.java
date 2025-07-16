package com.examenJava.comptamatiere.repository;

import com.examenJava.comptamatiere.model.CategorieArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategorieArticleRepository extends JpaRepository<CategorieArticle, Long> {
    @Query("SELECT c FROM CategorieArticle c LEFT JOIN FETCH c.articles")
    List<CategorieArticle> findAllWithArticles();
}
