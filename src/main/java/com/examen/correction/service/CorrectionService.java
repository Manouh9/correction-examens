package com.examen.correction.service;

import com.examen.correction.dto.NoteAvecEcartDTO;
import com.examen.correction.dto.NoteFinaleDTO;
import com.examen.correction.model.Examen;
import com.examen.correction.model.Parametre;
import java.math.BigDecimal;
import java.util.List;

public interface CorrectionService {
    // Calcul des écarts
    List<NoteAvecEcartDTO> calculerEcartsTousExamens();
    NoteAvecEcartDTO calculerEcartParExamen(Long idExamen);
    
    // Détermination de la note finale
    NoteFinaleDTO determinerNoteFinale(Long idExamen);
    List<NoteFinaleDTO> determinerNotesFinalesTousExamens();
    
    // Validation selon les paramètres
    boolean verifierCondition(Parametre parametre, BigDecimal ecart);
    BigDecimal appliquerResolution(String resolution, List<BigDecimal> notes);
    
    // Récupération des paramètres applicables
    Parametre trouverParametreApplicable(Examen examen);
    NoteFinaleDTO chercherNoteParEtudiantEtMatiere(Long etudiantId, Long matiereId);
}