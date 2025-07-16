package com.examenJava.comptamatiere.model;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
public class CategorieArticle {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;

    @OneToMany(mappedBy = "categorie", fetch = FetchType.LAZY)
    private List<Article> articles = new ArrayList<>();

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public List<Article> getArticles() {
        return articles;
    }

    public void setArticles(List<Article> articles) {
        this.articles = articles;
    }

    @Override
    public String toString() {
        return nom;
    }
}
