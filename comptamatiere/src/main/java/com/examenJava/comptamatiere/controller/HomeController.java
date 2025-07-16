package com.examenJava.comptamatiere.controller;

import com.examenJava.comptamatiere.model.Article;
import com.examenJava.comptamatiere.service.StockService;
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
    public String dashboard(Model model) {
        List<Article> articles = stockService.getAllArticles();
        model.addAttribute("articles", articles);
        model.addAttribute("stats", stockService.getStats());
        model.addAttribute("alertes", stockService.getAlertes());
        return "dashboard";
    }
}
