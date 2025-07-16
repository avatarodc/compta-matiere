package com.examenJava.comptamatiere.controller;

import com.examenJava.comptamatiere.model.CategorieArticle;
import com.examenJava.comptamatiere.repository.CategorieArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/categories")
public class CategorieController {

    @Autowired
    private CategorieArticleRepository categorieRepo;

    @GetMapping
    public String listeCategories(Model model) {
        model.addAttribute("categories", categorieRepo.findAll());
        return "categories";
    }

    @GetMapping("/new")
    public String nouvelleCategorie(Model model) {
        model.addAttribute("categorie", new CategorieArticle());
        return "form_categorie";
    }

    @PostMapping("/save")
    public String enregistrerCategorie(@ModelAttribute CategorieArticle categorie) {
        categorieRepo.save(categorie);
        return "redirect:/categories";
    }

    @GetMapping("/delete/{id}")
    public String supprimerCategorie(@PathVariable Long id) {
        categorieRepo.deleteById(id);
        return "redirect:/categories";
    }
}
