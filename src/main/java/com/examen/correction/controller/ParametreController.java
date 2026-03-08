package com.examen.correction.controller;

import com.examen.correction.model.Matiere;
import com.examen.correction.model.Operateur;
import com.examen.correction.model.Parametre;
import com.examen.correction.model.Resolution;
import com.examen.correction.repository.MatiereRepository;
import com.examen.correction.repository.OperateurRepository;
import com.examen.correction.repository.ParametreRepository;
import com.examen.correction.repository.ResolutionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/parametres")
public class ParametreController {

    @Autowired
    private ParametreRepository parametreRepository;
    
    @Autowired
    private MatiereRepository matiereRepository;
    
    @Autowired
    private ResolutionRepository resolutionRepository;
    
    @Autowired
    private OperateurRepository operateurRepository;

    @GetMapping
    public String listeParametres(Model model) {
        model.addAttribute("parametres", parametreRepository.findAll());
        return "parametres/liste";
    }

    @GetMapping("/nouveau")
    public String formulaireParametre(Model model) {
        List<Matiere> matieres = matiereRepository.findAll();
        List<Resolution> resolutions = resolutionRepository.findAll();
        List<Operateur> operateurs = operateurRepository.findAll();
        
        model.addAttribute("parametre", new Parametre());
        model.addAttribute("matieres", matieres);
        model.addAttribute("resolutions", resolutions);
        model.addAttribute("operateurs", operateurs);
        
        return "parametres/formulaire";
    }

    @PostMapping("/save")
    public String sauvegarderParametre(@ModelAttribute Parametre parametre,
                                        @RequestParam Long idMatiere,
                                        @RequestParam Long idResolution,
                                        @RequestParam Long idOperateur) {
        
        Matiere matiere = matiereRepository.findById(idMatiere)
                .orElseThrow(() -> new RuntimeException("Matière non trouvée"));
        Resolution resolution = resolutionRepository.findById(idResolution)
                .orElseThrow(() -> new RuntimeException("Résolution non trouvée"));
        Operateur operateur = operateurRepository.findById(idOperateur)
                .orElseThrow(() -> new RuntimeException("Opérateur non trouvé"));
        
        parametre.setMatiere(matiere);
        parametre.setResolution(resolution);
        parametre.setOperateur(operateur);
        parametre.setDateApplication(LocalDate.now());
        parametre.setActive(true);
        
        parametreRepository.save(parametre);
        
        return "redirect:/parametres";
    }

    @GetMapping("/{id}/desactiver")
    public String desactiverParametre(@PathVariable Long id) {
        Parametre parametre = parametreRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Paramètre non trouvé"));
        parametre.setActive(false);
        parametreRepository.save(parametre);
        return "redirect:/parametres";
    }

    @GetMapping("/matiere/{idMatiere}")
    public String parametresParMatiere(@PathVariable Long idMatiere, Model model) {
        Matiere matiere = matiereRepository.findById(idMatiere)
                .orElseThrow(() -> new RuntimeException("Matière non trouvée"));
        List<Parametre> parametres = parametreRepository.findByMatiereAndActiveTrue(matiere);
        model.addAttribute("parametres", parametres);
        model.addAttribute("matiere", matiere);
        return "parametres/par-matiere";
    }
}