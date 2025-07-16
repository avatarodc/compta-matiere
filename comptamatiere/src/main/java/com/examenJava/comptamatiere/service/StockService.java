package com.examenJava.comptamatiere.service;

import com.examenJava.comptamatiere.model.Article;
import com.examenJava.comptamatiere.model.EntreeStock;
import com.examenJava.comptamatiere.model.SortieStock;
import com.examenJava.comptamatiere.repository.ArticleRepository;
import com.examenJava.comptamatiere.repository.EntreeStockRepository;
import com.examenJava.comptamatiere.repository.SortieStockRepository;
import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.OutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class StockService {

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private EntreeStockRepository entreeStockRepository;

    @Autowired
    private SortieStockRepository sortieStockRepository;

    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    public Article saveArticle(Article article) {
        System.out.println("➡️ Enregistrement de l'article :");
        System.out.println("Nom = " + article.getNom());
        System.out.println("Unité = " + article.getUnite());
        System.out.println("PU = " + article.getPrixUnitaire());
        System.out.println("Date = " + article.getDateAcquisition());
        System.out.println("Durée = " + article.getDureeExpirationMois());
        System.out.println("Taux amortissement = " + article.getTauxAmortissementAnnuel());
        System.out.println("Catégorie = " + (article.getCategorie() != null ? article.getCategorie().getId() : "null"));

        return articleRepository.save(article);
    }


    public void deleteArticle(Long id) {
        Article article = articleRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Article introuvable"));

        if (!entreeStockRepository.findByArticle(article).isEmpty()
                || !sortieStockRepository.findByArticle(article).isEmpty()) {
            throw new IllegalStateException("Impossible de supprimer un article qui a des entrées ou sorties liées.");
        }

        articleRepository.delete(article);
    }


    public boolean articleEstLie(Article article) {
        return !entreeStockRepository.findByArticle(article).isEmpty()
                || !sortieStockRepository.findByArticle(article).isEmpty();
    }


    public void enregistrerEntree(EntreeStock entree) {
        Long articleId = entree.getArticle().getId();

        // ⚠️ On récupère l'article complet depuis la base
        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article introuvable pour l'entrée"));

        // ✅ Mise à jour du stock uniquement
        article.setStockActuel(article.getStockActuel() + entree.getQuantite());

        articleRepository.save(article);         // ✅ met à jour uniquement le stock
        entree.setArticle(article);              // pour avoir un lien cohérent dans l'entrée
        entreeStockRepository.save(entree);      // sauvegarde l'entrée
    }


    public void enregistrerSortie(SortieStock sortie) {
        Long articleId = sortie.getArticle().getId();

        Article article = articleRepository.findById(articleId)
                .orElseThrow(() -> new IllegalArgumentException("Article introuvable pour la sortie"));

        if (article.getStockActuel() < sortie.getQuantite()) {
            throw new IllegalArgumentException("Stock insuffisant pour cette sortie.");
        }

        article.setStockActuel(article.getStockActuel() - sortie.getQuantite());

        articleRepository.save(article);
        sortie.setArticle(article); // garder la liaison propre
        sortieStockRepository.save(sortie);
    }


    public List<EntreeStock> getEntrees() {
        return entreeStockRepository.findAll();
    }

    public List<SortieStock> getSorties() {
        return sortieStockRepository.findAll();
    }

    public void exportInventairePDF(OutputStream out) throws Exception {

        List<Article> articles = articleRepository.findAll();

        Document doc = new Document(PageSize.A4);
        PdfWriter.getInstance(doc, out);
        doc.open();

        doc.add(new Paragraph("Fiche d'inventaire - " + LocalDate.now()));
        doc.add(new Paragraph(" ")); // Espace

        PdfPTable table = new PdfPTable(6);
        table.setWidthPercentage(100);
        table.addCell("Nom");
        table.addCell("Catégorie");
        table.addCell("Stock");
        table.addCell("PU (F CFA)");
        table.addCell("Valeur nette (F CFA)");
        table.addCell("Expiration");

        for (Article a : articles) {
            table.addCell(Optional.ofNullable(a.getNom()).orElse("-"));
            table.addCell(a.getCategorie() != null ? a.getCategorie().getNom() : "-");
            table.addCell(String.valueOf(a.getStockActuel()));
            table.addCell(a.getPrixUnitaire() != null ? a.getPrixUnitaire().toString() : "-");
            table.addCell(a.getValeurAmortie() != null ? a.getValeurAmortie().toString() : "-");
            table.addCell(a.getDateExpirationEstimee() != null ? a.getDateExpirationEstimee().toString() : "-");
        }

        doc.add(table);
        doc.close();
    }

    public Map<String, Object> getStats() {
        List<Article> all = articleRepository.findAll();

        List<Article> valides = all.stream()
                .filter(a -> a.getPrixUnitaire() != null)
                .collect(Collectors.toList());

        int total = valides.size();
        int expirés = (int) valides.stream()
                .filter(Article::estExpire)
                .count();

        BigDecimal valeurBrute = valides.stream()
                .map(a -> a.getPrixUnitaire().multiply(BigDecimal.valueOf(a.getStockActuel())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal valeurNette = valides.stream()
                .map(a -> a.getValeurAmortie().multiply(BigDecimal.valueOf(a.getStockActuel())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        double tauxExpirés = total == 0 ? 0 : (100.0 * expirés / total);

        Map<String, Object> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("expirés", expirés);
        stats.put("valeurBrute", valeurBrute);
        stats.put("valeurNette", valeurNette);
        stats.put("tauxExpirés", Math.round(tauxExpirés * 100.0) / 100.0);

        return stats;
    }

    public void exportInventaireExcel(OutputStream out) throws Exception {
        List<Article> articles = articleRepository.findAll();
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Inventaire");

        Row header = sheet.createRow(0);
        String[] cols = {"Nom", "Catégorie", "Stock", "PU", "Valeur nette", "Expiration", "Amortissement (ans)"};
        for (int i = 0; i < cols.length; i++) {
            header.createCell(i).setCellValue(cols[i]);
        }

        int rowIdx = 1;
        for (Article a : articles) {
            Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(Optional.ofNullable(a.getNom()).orElse("-"));
            row.createCell(1).setCellValue(a.getCategorie() != null ? a.getCategorie().getNom() : "-");
            row.createCell(2).setCellValue(a.getStockActuel());
            row.createCell(3).setCellValue(a.getPrixUnitaire() != null ? a.getPrixUnitaire().doubleValue() : 0);
            row.createCell(4).setCellValue(a.getValeurAmortie() != null ? a.getValeurAmortie().doubleValue() : 0);
            row.createCell(5).setCellValue(a.getDateExpirationEstimee() != null ? a.getDateExpirationEstimee().toString() : "-");
            row.createCell(6).setCellValue(a.getAnneeAmortissement());
        }

        workbook.write(out);
        workbook.close();
    }

    public Map<String, List<Article>> getAlertes() {
        List<Article> articles = articleRepository.findAll();

        List<Article> expirés = articles.stream()
                .filter(Article::estExpire)
                .collect(Collectors.toList());

        List<Article> stockCritique = articles.stream()
                .filter(a -> a.getStockActuel() < 5)
                .collect(Collectors.toList());

        List<Article> totalementAmortis = articles.stream()
                .filter(a -> a.getValeurAmortie() != null &&
                        a.getValeurAmortie().compareTo(BigDecimal.ZERO) == 0)
                .collect(Collectors.toList());

        Map<String, List<Article>> alertes = new HashMap<>();
        alertes.put("expirés", expirés);
        alertes.put("stock", stockCritique);
        alertes.put("amortis", totalementAmortis);

        return alertes;
    }
}
