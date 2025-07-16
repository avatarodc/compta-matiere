package com.examenJava.comptamatiere.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Period;
import java.time.temporal.ChronoUnit;

@Entity
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String unite;
    private BigDecimal prixUnitaire;
    private Integer stockActuel;
    private LocalDate dateAcquisition;
    private int dureeExpirationMois;
    private double tauxAmortissementAnnuel;

    @ManyToOne
    private CategorieArticle categorie;

    // Getters & Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getUnite() { return unite; }
    public void setUnite(String unite) { this.unite = unite; }

    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    public Integer getStockActuel() {
        return stockActuel;
    }

    public void setStockActuel(Integer stockActuel) {
        this.stockActuel = stockActuel;
    }


    public LocalDate getDateAcquisition() { return dateAcquisition; }
    public void setDateAcquisition(LocalDate dateAcquisition) { this.dateAcquisition = dateAcquisition; }

    public int getDureeExpirationMois() { return dureeExpirationMois; }
    public void setDureeExpirationMois(int dureeExpirationMois) { this.dureeExpirationMois = dureeExpirationMois; }

    public double getTauxAmortissementAnnuel() { return tauxAmortissementAnnuel; }
    public void setTauxAmortissementAnnuel(double taux) { this.tauxAmortissementAnnuel = taux; }

    public CategorieArticle getCategorie() { return categorie; }
    public void setCategorie(CategorieArticle categorie) { this.categorie = categorie; }

    // Années écoulées depuis l'acquisition
    public int getAnneeAmortissement() {
        if (dateAcquisition == null) return 0;
        return Period.between(dateAcquisition, LocalDate.now()).getYears();
    }

    // Date d'expiration estimée
    public LocalDate getDateExpirationEstimee() {
        if (this.dateAcquisition == null || this.dureeExpirationMois <= 0) {
            return null;
        }
        return this.dateAcquisition.plusMonths(this.dureeExpirationMois);
    }

    // État d'expiration
    public boolean estExpire() {
        LocalDate expiration = getDateExpirationEstimee();
        return expiration != null && LocalDate.now().isAfter(expiration);
    }

    // Valeur amortie
    public BigDecimal getValeurAmortie() {
        if (prixUnitaire == null || dateAcquisition == null) return BigDecimal.ZERO;

        long mois = ChronoUnit.MONTHS.between(dateAcquisition, LocalDate.now());
        double tauxMensuel = tauxAmortissementAnnuel / 12.0 / 100.0;
        BigDecimal amortissement = prixUnitaire.multiply(BigDecimal.valueOf(tauxMensuel * mois));
        BigDecimal valeurActuelle = prixUnitaire.subtract(amortissement);
        return valeurActuelle.max(BigDecimal.ZERO);
    }


    // Valeur de revente estimée
    public BigDecimal getValeurReventeEstimee() {
        return getValeurAmortie().multiply(BigDecimal.valueOf(0.5)); // 50% de la valeur amortie
    }
}
