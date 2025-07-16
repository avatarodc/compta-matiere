package com.examenJava.comptamatiere.controller;

import com.examenJava.comptamatiere.model.CategorieArticle;
import com.examenJava.comptamatiere.repository.ArticleRepository;
import com.examenJava.comptamatiere.repository.CategorieArticleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/categories")
public class CategorieController {

    @Autowired
    private CategorieArticleRepository categorieRepo;

    @GetMapping
    public String listeCategories(Model model) {
        List<CategorieArticle> categories = categorieRepo.findAllWithArticles(); // 👈 Utilise le fetch join
        model.addAttribute("categories", categories);
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

    @Autowired
    private ArticleRepository articleRepo;

    @GetMapping("/edit/{id}")
    public String modifierCategorie(@PathVariable Long id, Model model) {
        CategorieArticle cat = categorieRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Catégorie non trouvée"));

        boolean estLiee = !articleRepo.findByCategorie(cat).isEmpty();
        if (estLiee) {
            // Redirection vers liste avec message d'erreur
            model.addAttribute("erreur", "Impossible de modifier cette catégorie : des articles y sont rattachés.");
            return listeCategories(model);
        }

        model.addAttribute("categorie", cat);
        return "form_categorie";
    }

    @GetMapping("/delete/{id}")
    public String supprimerCategorie(@PathVariable Long id, Model model) {
        CategorieArticle cat = categorieRepo.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Catégorie non trouvée"));

        boolean estLiee = !articleRepo.findByCategorie(cat).isEmpty();
        if (estLiee) {
            model.addAttribute("erreur", "Impossible de supprimer cette catégorie : des articles y sont rattachés.");
            return listeCategories(model);
        }

        categorieRepo.delete(cat);
        return "redirect:/categories";
    }

}
