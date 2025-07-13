package com.examenJava.comptamatiere.model;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
public class SortieStock {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    private Article article;

    private int quantite;
    private LocalDate dateSortie;
    private String motif; // vente, casse, don...

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public Article getArticle() { return article; }
    public void setArticle(Article article) { this.article = article; }

    public int getQuantite() { return quantite; }
    public void setQuantite(int quantite) { this.quantite = quantite; }

    public LocalDate getDateSortie() { return dateSortie; }
    public void setDateSortie(LocalDate dateSortie) { this.dateSortie = dateSortie; }

    public String getMotif() { return motif; }
    public void setMotif(String motif) { this.motif = motif; }
}
