package com.examenJava.comptamatiere.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class EntreeStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Article article;

    private int quantite;
    private LocalDate dateEntree;
    private String source; // fournisseur, retour...

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Article getArticle() { return article; }
    public void setArticle(Article article) { this.article = article; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }

    public LocalDate getDateEntree() { return dateEntree; }
    public void setDateEntree(LocalDate dateEntree) { this.dateEntree = dateEntree; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }
}
