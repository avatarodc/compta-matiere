package com.examenJava.comptamatiere.model;

import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
public class Article {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String nom;
    private String unite;
    private BigDecimal prixUnitaire;
    private int stockActuel;
    private LocalDate dateAcquisition;
    private int dureeExpirationMois;
    private double tauxAmortissementAnnuel;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }

    public String getUnite() { return unite; }
    public void setUnite(String unite) { this.unite = unite; }

    public BigDecimal getPrixUnitaire() { return prixUnitaire; }
    public void setPrixUnitaire(BigDecimal prixUnitaire) { this.prixUnitaire = prixUnitaire; }

    public int getStockActuel() { return stockActuel; }
    public void setStockActuel(int stockActuel) { this.stockActuel = stockActuel; }

    public LocalDate getDateAcquisition() { return dateAcquisition; }
    public void setDateAcquisition(LocalDate dateAcquisition) { this.dateAcquisition = dateAcquisition; }

    public int getDureeExpirationMois() { return dureeExpirationMois; }
    public void setDureeExpirationMois(int dureeExpirationMois) { this.dureeExpirationMois = dureeExpirationMois; }

    public double getTauxAmortissementAnnuel() { return tauxAmortissementAnnuel; }
    public void setTauxAmortissementAnnuel(double taux) { this.tauxAmortissementAnnuel = taux; }

    // Calculs dynamiques

    public LocalDate getDateExpirationEstimee() {
        return dateAcquisition.plusMonths(dureeExpirationMois);
    }

    public boolean estExpire() {
        return LocalDate.now().isAfter(getDateExpirationEstimee());
    }

    public BigDecimal getValeurAmortie() {
        int mois = Math.max(0, (int) java.time.temporal.ChronoUnit.MONTHS.between(dateAcquisition, LocalDate.now()));
        double tauxMensuel = tauxAmortissementAnnuel / 12 / 100;
        BigDecimal valeurActuelle = prixUnitaire.subtract(prixUnitaire.multiply(BigDecimal.valueOf(tauxMensuel * mois)));
        return valeurActuelle.max(BigDecimal.ZERO);
    }

    public BigDecimal getValeurReventeEstimee() {
        return getValeurAmortie().multiply(BigDecimal.valueOf(0.5)); // valeur résiduelle estimée à 50%
    }
}
