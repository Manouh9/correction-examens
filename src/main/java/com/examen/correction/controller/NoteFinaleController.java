package com.examen.correction.controller;

import com.examen.correction.dto.NoteFinaleDTO;
import com.examen.correction.model.Etudiant;
import com.examen.correction.model.Matiere;
import com.examen.correction.repository.EtudiantRepository;
import com.examen.correction.repository.MatiereRepository;
import com.examen.correction.service.CorrectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/recherche-note")
public class NoteFinaleController {

    @Autowired
    private EtudiantRepository etudiantRepository;
    
    @Autowired
    private MatiereRepository matiereRepository;
    
    @Autowired
    private CorrectionService correctionService;

    @GetMapping
    public String afficherFormulaire(Model model) {
        List<Etudiant> etudiants = etudiantRepository.findAll();
        List<Matiere> matieres = matiereRepository.findAll();
        
        model.addAttribute("etudiants", etudiants);
        model.addAttribute("matieres", matieres);
        model.addAttribute("noteFinale", null);
        
        return "recherche-note";
    }

    @PostMapping("/rechercher")
    public String rechercherNote(@RequestParam(required = false) Long etudiantId,
                                 @RequestParam(required = false) Long matiereId,
                                 Model model) {
        
        List<Etudiant> etudiants = etudiantRepository.findAll();
        List<Matiere> matieres = matiereRepository.findAll();
        
        model.addAttribute("etudiants", etudiants);
        model.addAttribute("matieres", matieres);
        
        if (etudiantId != null) {
            etudiantRepository.findById(etudiantId).ifPresent(e -> 
                model.addAttribute("etudiantNom", e.getPrenom() + " " + e.getNom())
            );
        }
        if (matiereId != null) {
            matiereRepository.findById(matiereId).ifPresent(m -> 
                model.addAttribute("matiereNom", m.getNomMatiere())
            );
        }
        
        if (etudiantId != null && matiereId != null) {
            NoteFinaleDTO noteFinale = correctionService.chercherNoteParEtudiantEtMatiere(etudiantId, matiereId);
            model.addAttribute("noteFinale", noteFinale);
        }
        
        return "recherche-note";
    }
}