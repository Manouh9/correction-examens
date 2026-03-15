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
    
    // Recherche par étudiant et matière
    NoteFinaleDTO chercherNoteParEtudiantEtMatiere(Long etudiantId, Long matiereId);
    
    // Validation selon les paramètres
    boolean verifierCondition(Parametre parametre, BigDecimal ecart);
    BigDecimal appliquerResolution(String resolution, List<BigDecimal> notes);
    
    // Calculs statistiques
    BigDecimal calculerMediane(List<BigDecimal> notes);
    BigDecimal calculerEcartType(List<BigDecimal> notes, BigDecimal moyenne);
    BigDecimal calculerEcartPourExamen(Long idExamen);
    
    // Récupération des paramètres applicables avec logique de proximité
    Parametre trouverParametreApplicable(Examen examen);
    Parametre trouverParametreLePlusProche(List<Parametre> parametres, BigDecimal ecart);
}