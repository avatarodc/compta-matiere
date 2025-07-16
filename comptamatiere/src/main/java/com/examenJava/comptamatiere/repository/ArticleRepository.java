package com.examenJava.comptamatiere.repository;

import com.examenJava.comptamatiere.model.Article;
import com.examenJava.comptamatiere.model.CategorieArticle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Map;

public interface ArticleRepository extends JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {


    List<Article> findByCategorie(CategorieArticle categorie);

}
