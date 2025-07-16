package com.examenJava.comptamatiere.dto;

public class ArticleDTO {
    private String nom;
    private String categorieNom;
    private int stockActuel;
    private double prixUnitaire;

    public ArticleDTO(String nom, String categorieNom, int stockActuel, double prixUnitaire) {
        this.nom = nom;
        this.categorieNom = categorieNom;
        this.stockActuel = stockActuel;
        this.prixUnitaire = prixUnitaire;
    }

    // Getters
    public String getNom() { return nom; }
    public String getCategorieNom() { return categorieNom; }
    public int getStockActuel() { return stockActuel; }
    public double getPrixUnitaire() { return prixUnitaire; }
}
