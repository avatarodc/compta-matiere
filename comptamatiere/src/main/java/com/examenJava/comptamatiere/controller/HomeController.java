package com.examenJava.comptamatiere.controller;

import com.examenJava.comptamatiere.dto.ArticleDTO;
import com.examenJava.comptamatiere.model.Article;
import com.examenJava.comptamatiere.service.StockService;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final StockService stockService;

    @Autowired
    public HomeController(StockService stockService) {
        this.stockService = stockService;
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/dashboard")
    @Transactional
    public String dashboard(Model model) {
        List<Article> articles = stockService.getAllArticles();

        List<ArticleDTO> dtoList = articles.stream().map(a ->
                new ArticleDTO(
                        a.getNom(),
                        a.getCategorie() != null ? a.getCategorie().getNom() : "Sans catégorie",
                        a.getStockActuel(),
                        a.getPrixUnitaire().doubleValue()
                )
        ).toList();


        model.addAttribute("articles", dtoList);
        model.addAttribute("stats", stockService.getStats());
        model.addAttribute("alertes", stockService.getAlertes());

        return "dashboard";
    }


}
