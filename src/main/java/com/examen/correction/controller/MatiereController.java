package com.examen.correction.controller;

import com.examen.correction.model.Matiere;
import com.examen.correction.repository.MatiereRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/matieres")
public class MatiereController {

    @Autowired
    private MatiereRepository matiereRepository;

    // READ - Liste toutes les matières
    @GetMapping
    public String listeMatieres(Model model) {
        List<Matiere> matieres = matiereRepository.findAll();
        model.addAttribute("matieres", matieres);
        return "matieres/liste";
    }

    // READ - Détail d'une matière
    @GetMapping("/{id}")
    public String detailMatiere(@PathVariable Long id, Model model) {
        Matiere matiere = matiereRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matière non trouvée avec l'id: " + id));
        model.addAttribute("matiere", matiere);
        return "matieres/detail";
    }

    // CREATE - Afficher le formulaire de création
    @GetMapping("/nouveau")
    public String formulaireNouvelleMatiere(Model model) {
        model.addAttribute("matiere", new Matiere());
        model.addAttribute("mode", "creation");
        return "matieres/formulaire";
    }

    // CREATE - Traiter la création
    @PostMapping("/save")
    public String sauvegarderMatiere(@ModelAttribute Matiere matiere,
                                     RedirectAttributes redirectAttributes) {
        try {
            matiereRepository.save(matiere);
            redirectAttributes.addFlashAttribute("success", "Matière créée avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création : " + e.getMessage());
        }
        return "redirect:/matieres";
    }

    // UPDATE - Afficher le formulaire d'édition
    @GetMapping("/edit/{id}")
    public String formulaireEditMatiere(@PathVariable Long id, Model model) {
        Matiere matiere = matiereRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Matière non trouvée avec l'id: " + id));
        model.addAttribute("matiere", matiere);
        model.addAttribute("mode", "edition");
        return "matieres/formulaire";
    }

    // UPDATE - Traiter la modification
    @PostMapping("/update/{id}")
    public String updateMatiere(@PathVariable Long id,
                                @ModelAttribute Matiere matiere,
                                RedirectAttributes redirectAttributes) {
        try {
            matiere.setIdMatiere(id);
            matiereRepository.save(matiere);
            redirectAttributes.addFlashAttribute("success", "Matière modifiée avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la modification : " + e.getMessage());
        }
        return "redirect:/matieres";
    }

    // DELETE - Supprimer une matière
    @GetMapping("/delete/{id}")
    public String supprimerMatiere(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            matiereRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Matière supprimée avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Impossible de supprimer cette matière car elle est utilisée dans des examens");
        }
        return "redirect:/matieres";
    }

    // RECHERCHE - Rechercher par nom
    @GetMapping("/search")
    public String rechercherMatieres(@RequestParam(required = false) String nom, Model model) {
        if (nom != null && !nom.isEmpty()) {
            model.addAttribute("matieres", matiereRepository.findByNomMatiereContainingIgnoreCase(nom));
            model.addAttribute("recherche", nom);
        } else {
            model.addAttribute("matieres", matiereRepository.findAll());
        }
        return "matieres/liste";
    }
}