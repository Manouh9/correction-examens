package com.examen.correction.controller;

import com.examen.correction.model.Correcteur;
import com.examen.correction.repository.CorrecteurRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/correcteurs")
public class CorrecteurController {

    @Autowired
    private CorrecteurRepository correcteurRepository;

    // READ - Liste tous les correcteurs
    @GetMapping
    public String listeCorrecteurs(Model model) {
        List<Correcteur> correcteurs = correcteurRepository.findAll();
        model.addAttribute("correcteurs", correcteurs);
        return "correcteurs/liste";
    }

    // READ - Détail d'un correcteur
    @GetMapping("/{id}")
    public String detailCorrecteur(@PathVariable Long id, Model model) {
        Correcteur correcteur = correcteurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Correcteur non trouvé avec l'id: " + id));
        model.addAttribute("correcteur", correcteur);
        return "correcteurs/detail";
    }

    // CREATE - Afficher le formulaire de création
    @GetMapping("/nouveau")
    public String formulaireNouveauCorrecteur(Model model) {
        model.addAttribute("correcteur", new Correcteur());
        return "correcteurs/formulaire";
    }

    // CREATE - Traiter la création
    @PostMapping("/save")
    public String sauvegarderCorrecteur(@ModelAttribute Correcteur correcteur,
                                        RedirectAttributes redirectAttributes) {
        try {
            correcteurRepository.save(correcteur);
            redirectAttributes.addFlashAttribute("success", "Correcteur créé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création : " + e.getMessage());
        }
        return "redirect:/correcteurs";
    }

    // UPDATE - Afficher le formulaire d'édition
    @GetMapping("/edit/{id}")
    public String formulaireEditCorrecteur(@PathVariable Long id, Model model) {
        Correcteur correcteur = correcteurRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Correcteur non trouvé avec l'id: " + id));
        model.addAttribute("correcteur", correcteur);
        return "correcteurs/formulaire";
    }

    // UPDATE - Traiter la modification
    @PostMapping("/update/{id}")
    public String updateCorrecteur(@PathVariable Long id,
                                   @ModelAttribute Correcteur correcteur,
                                   RedirectAttributes redirectAttributes) {
        try {
            correcteur.setIdCorrecteur(id);
            correcteurRepository.save(correcteur);
            redirectAttributes.addFlashAttribute("success", "Correcteur modifié avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la modification : " + e.getMessage());
        }
        return "redirect:/correcteurs";
    }

    // DELETE - Supprimer un correcteur
    @GetMapping("/delete/{id}")
    public String supprimerCorrecteur(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            correcteurRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Correcteur supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Impossible de supprimer ce correcteur car il a des notes associées");
        }
        return "redirect:/correcteurs";
    }

    // RECHERCHE - Rechercher par nom
    @GetMapping("/search")
    public String rechercherCorrecteurs(@RequestParam(required = false) String nom, Model model) {
        if (nom != null && !nom.isEmpty()) {
            model.addAttribute("recherche", nom);
        } else {
            model.addAttribute("correcteurs", correcteurRepository.findAll());
        }
        return "correcteurs/liste";
    }
}