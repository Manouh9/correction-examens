package com.examen.correction.controller;

import com.examen.correction.dto.NoteAvecEcartDTO;
import com.examen.correction.dto.NoteFinaleDTO;
import com.examen.correction.model.Examen;
import com.examen.correction.repository.ExamenRepository;
import com.examen.correction.service.CorrectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/examens")
public class ExamenController {

    @Autowired
    private ExamenRepository examenRepository;
    
    @Autowired
    private CorrectionService correctionService;

    @GetMapping
    public String listeExamens(Model model) {
        model.addAttribute("examens", examenRepository.findAll());
        return "examens/liste";
    }

    @GetMapping("/{id}")
    public String detailExamen(@PathVariable Long id, Model model) {
        Examen examen = examenRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Examen non trouvé"));
        model.addAttribute("examen", examen);
        return "examens/detail";
    }

    @GetMapping("/{id}/corrections")
    public String voirCorrections(@PathVariable Long id, Model model) {
        NoteAvecEcartDTO stats = correctionService.calculerEcartParExamen(id);
        model.addAttribute("stats", stats);
        return "examens/corrections";
    }

    @GetMapping("/{id}/note-finale")
    public String voirNoteFinale(@PathVariable Long id, Model model) {
        NoteFinaleDTO noteFinale = correctionService.determinerNoteFinale(id);
        model.addAttribute("noteFinale", noteFinale);
        return "examens/note-finale";
    }

    @GetMapping("/ecarts")
    public String voirTousEcarts(Model model) {
        List<NoteAvecEcartDTO> ecarts = correctionService.calculerEcartsTousExamens();
        model.addAttribute("ecarts", ecarts);
        return "examens/ecarts";
    }

    @GetMapping("/notes-finales")
    public String voirToutesNotesFinales(Model model) {
        List<NoteFinaleDTO> notesFinales = correctionService.determinerNotesFinalesTousExamens();
        model.addAttribute("notesFinales", notesFinales);
        return "examens/notes-finales";
    }
}