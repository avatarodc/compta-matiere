package com.examenJava.comptamatiere.controller;

import com.examenJava.comptamatiere.model.Article;
import com.examenJava.comptamatiere.model.CategorieArticle;
import com.examenJava.comptamatiere.model.EntreeStock;
import com.examenJava.comptamatiere.model.SortieStock;
import com.examenJava.comptamatiere.repository.CategorieArticleRepository;
import com.examenJava.comptamatiere.service.StockService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/stock")
public class StockController {

    @Autowired
    private StockService stockService;

    private static final Logger log = LoggerFactory.getLogger(StockController.class);


    @Autowired
    private CategorieArticleRepository categorieArticleRepository;

    @GetMapping("/articles")
    public String afficherArticles(Model model) {
        model.addAttribute("articles", stockService.getAllArticles());
        return "articles";
    }

    @GetMapping("/articles/new")
    public String nouveauArticle(Model model) {
        model.addAttribute("article", new Article());
        model.addAttribute("categories", categorieArticleRepository.findAll());
        return "form_article";
    }

    @PostMapping("/articles/save")
    public String enregistrerArticle(@Valid @ModelAttribute("article") Article article, BindingResult result, Model model) {
        log.info("📥 Données reçues depuis le formulaire :");
        log.info("Nom : {}", article.getNom());
        log.info("Unité : {}", article.getUnite());
        log.info("Prix unitaire : {}", article.getPrixUnitaire());
        log.info("Date acquisition : {}", article.getDateAcquisition());
        log.info("Durée expiration (mois) : {}", article.getDureeExpirationMois());
        log.info("Taux amortissement annuel : {}", article.getTauxAmortissementAnnuel());
        log.info("Catégorie ID : {}", article.getCategorie() != null ? article.getCategorie().getId() : null);

        if (result.hasErrors()) {
            model.addAttribute("categories", categorieArticleRepository.findAll());
            return "form_article";
        }

        if (article.getCategorie() != null && article.getCategorie().getId() != null) {
            CategorieArticle cat = categorieArticleRepository.findById(article.getCategorie().getId()).orElse(null);
            article.setCategorie(cat);
        }

        stockService.saveArticle(article);
        return "redirect:/stock/articles";
    }


    @GetMapping("/articles/delete/{id}")
    public String supprimerArticle(@PathVariable Long id) {
        stockService.deleteArticle(id);
        return "redirect:/stock/articles";
    }

    @GetMapping("/inventaire")
    public String afficherInventaire(Model model) {
        List<Article> articles = stockService.getAllArticles();
        Map<String, Object> stats = stockService.getStats();

        // Calcul du nombre d’articles en stock faible
        long stockFaible = articles.stream()
                .filter(a -> a.getStockActuel() != null && a.getStockActuel() < 5)
                .count();

        model.addAttribute("articles", articles);
        model.addAttribute("stats", stats);
        model.addAttribute("stockFaible", stockFaible); // 👈 nouvelle variable

        return "inventaire";
    }



    @GetMapping("/inventaire/excel")
    public void exportExcel(HttpServletResponse response) throws Exception {
        response.setContentType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet");
        response.setHeader("Content-Disposition", "attachment; filename=\"inventaire.xlsx\"");
        stockService.exportInventaireExcel(response.getOutputStream());
    }

    @GetMapping("/inventaire/pdf")
    public void exportPdf(HttpServletResponse response) throws Exception {
        response.setContentType("application/pdf");
        response.setHeader("Content-Disposition", "attachment; filename=\"inventaire.pdf\"");
        stockService.exportInventairePDF(response.getOutputStream());
    }

    @GetMapping("/entrees/new")
    public String nouvelleEntree(Model model) {
        model.addAttribute("entree", new EntreeStock());
        model.addAttribute("articles", stockService.getAllArticles());
        return "form_entree";
    }

    @PostMapping("/entrees/save")
    public String enregistrerEntree(@ModelAttribute EntreeStock entree) {
        stockService.enregistrerEntree(entree);
        return "redirect:/stock/articles";
    }

    @GetMapping("/sorties/new")
    public String nouvelleSortie(Model model) {
        model.addAttribute("sortie", new SortieStock());
        model.addAttribute("articles", stockService.getAllArticles());
        return "form_sortie";
    }

    @PostMapping("/sorties/save")
    public String enregistrerSortie(@ModelAttribute SortieStock sortie) {
        stockService.enregistrerSortie(sortie);
        return "redirect:/stock/articles";
    }

    @ExceptionHandler(IllegalStateException.class)
    public String handleIllegalState(Model model, IllegalStateException ex) {
        model.addAttribute("errorMessage", ex.getMessage());
        return "erreur-suppression";
    }
}
