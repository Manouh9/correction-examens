package com.examen.correction.controller;

import com.examen.correction.model.Etudiant;
import com.examen.correction.repository.EtudiantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/etudiants")
public class EtudiantController {

    @Autowired
    private EtudiantRepository etudiantRepository;

    // READ - Liste tous les étudiants
    @GetMapping
    public String listeEtudiants(Model model) {
        List<Etudiant> etudiants = etudiantRepository.findAll();
        System.out.println("Nombre d'étudiants trouvés : " + etudiants.size()); // Debug
        model.addAttribute("etudiants", etudiants);
        return "etudiants/liste";
    }

    // READ - Détail d'un étudiant
    @GetMapping("/{id}")
    public String detailEtudiant(@PathVariable Long id, Model model) {
        Etudiant etudiant = etudiantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé avec l'id: " + id));
        model.addAttribute("etudiant", etudiant);
        return "etudiants/detail";
    }

    // CREATE - Afficher le formulaire de création
    @GetMapping("/nouveau")
    public String formulaireNouveauEtudiant(Model model) {
        model.addAttribute("etudiant", new Etudiant());
        return "etudiants/formulaire";
    }

    // CREATE - Traiter la création (POST)
    @PostMapping("/save")
    public String sauvegarderEtudiant(@ModelAttribute Etudiant etudiant, 
                                      RedirectAttributes redirectAttributes) {
        try {
            System.out.println("Sauvegarde étudiant : " + etudiant.getNom()); // Debug
            etudiantRepository.save(etudiant);
            redirectAttributes.addFlashAttribute("success", "Étudiant créé avec succès !");
        } catch (Exception e) {
            e.printStackTrace(); // Debug
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la création : " + e.getMessage());
        }
        return "redirect:/etudiants";
    }

    // UPDATE - Afficher le formulaire d'édition
    @GetMapping("/edit/{id}")
    public String formulaireEditEtudiant(@PathVariable Long id, Model model) {
        Etudiant etudiant = etudiantRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Étudiant non trouvé avec l'id: " + id));
        model.addAttribute("etudiant", etudiant);
        return "etudiants/formulaire";
    }

    // UPDATE - Traiter la modification (POST)
    @PostMapping("/update/{id}")
    public String updateEtudiant(@PathVariable Long id,
                                 @ModelAttribute Etudiant etudiant,
                                 RedirectAttributes redirectAttributes) {
        try {
            etudiant.setIdEtudiant(id);
            etudiantRepository.save(etudiant);
            redirectAttributes.addFlashAttribute("success", "Étudiant modifié avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la modification : " + e.getMessage());
        }
        return "redirect:/etudiants";
    }

    // DELETE - Supprimer un étudiant
    @GetMapping("/delete/{id}")
    public String supprimerEtudiant(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            etudiantRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("success", "Étudiant supprimé avec succès !");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Erreur lors de la suppression : " + e.getMessage());
        }
        return "redirect:/etudiants";
    }

    // RECHERCHE - Rechercher par nom
    @GetMapping("/search")
    public String rechercherEtudiants(@RequestParam(required = false) String nom, Model model) {
        if (nom != null && !nom.isEmpty()) {
            model.addAttribute("etudiants", etudiantRepository.findByNomContainingIgnoreCase(nom));
            model.addAttribute("recherche", nom);
        } else {
            model.addAttribute("etudiants", etudiantRepository.findAll());
        }
        return "etudiants/liste";
    }
}