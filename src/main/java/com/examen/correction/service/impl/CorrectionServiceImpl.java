package com.examen.correction.service.impl;

import com.examen.correction.dto.NoteAvecEcartDTO;
import com.examen.correction.dto.NoteFinaleDTO;
import com.examen.correction.model.*;
import com.examen.correction.repository.*;
import com.examen.correction.service.CorrectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class CorrectionServiceImpl implements CorrectionService {

    @Autowired
    private ExamenRepository examenRepository;
    
    @Autowired
    private NoteRepository noteRepository;
    
    @Autowired
    private ParametreRepository parametreRepository;
    
    @Autowired
    private ResolutionRepository resolutionRepository;
    
    @Autowired
    private OperateurRepository operateurRepository;

    @Override
    public List<NoteAvecEcartDTO> calculerEcartsTousExamens() {
        List<Examen> examens = examenRepository.findAll();
        List<NoteAvecEcartDTO> resultats = new ArrayList<>();
        
        for (Examen examen : examens) {
            if (examen.getNotes() != null && examen.getNotes().size() > 1) {
                resultats.add(calculerEcartParExamen(examen.getIdExamen()));
            }
        }
        
        return resultats;
    }

    @Override
    public NoteAvecEcartDTO calculerEcartParExamen(Long idExamen) {
        Examen examen = examenRepository.findById(idExamen)
                .orElseThrow(() -> new RuntimeException("Examen non trouvé"));
        
        List<Note> notes = examen.getNotes();
        
        // Vérifier s'il y a des notes
        if (notes == null || notes.isEmpty()) {
            // Retourner un DTO vide
            return new NoteAvecEcartDTO(
                examen.getIdExamen(),
                examen.getEtudiant().getNom(),
                examen.getEtudiant().getPrenom(),
                examen.getMatiere().getNomMatiere(),
                new ArrayList<>(),
                new ArrayList<>(),
                0,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO
            );
        }
        
        List<BigDecimal> valeursNotes = notes.stream()
                .map(Note::getValeurNote)
                .collect(Collectors.toList());
        
        List<String> nomsCorrecteurs = notes.stream()
                .map(n -> n.getCorrecteur().getNom() + " " + n.getCorrecteur().getPrenom())
                .collect(Collectors.toList());
        
        BigDecimal noteMin = valeursNotes.stream()
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        
        BigDecimal noteMax = valeursNotes.stream()
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        
        BigDecimal somme = valeursNotes.stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);
        
        // Éviter la division par zéro
        BigDecimal noteMoyenne = BigDecimal.ZERO;
        if (!valeursNotes.isEmpty()) {
            noteMoyenne = somme.divide(new BigDecimal(valeursNotes.size()), 2, RoundingMode.HALF_UP);
        }
        
        BigDecimal ecart = noteMax.subtract(noteMin);
        
        return new NoteAvecEcartDTO(
            examen.getIdExamen(),
            examen.getEtudiant().getNom(),
            examen.getEtudiant().getPrenom(),
            examen.getMatiere().getNomMatiere(),
            valeursNotes,
            nomsCorrecteurs,
            notes.size(),
            noteMin,
            noteMax,
            noteMoyenne,
            ecart
        );
    }

    @Override
    public NoteFinaleDTO determinerNoteFinale(Long idExamen) {
        Examen examen = examenRepository.findById(idExamen)
                .orElseThrow(() -> new RuntimeException("Examen non trouvé"));
        
        // Calculer les statistiques de base
        NoteAvecEcartDTO stats = calculerEcartParExamen(idExamen);
        
        // Trouver le paramètre applicable
        Parametre parametre = trouverParametreApplicable(examen);
        
        if (parametre == null) {
            // Pas de paramètre configuré, utiliser la moyenne par défaut
            return creerNoteFinaleParDefaut(stats, examen);
        }
        
        // Vérifier la condition
        boolean conditionVerifiee = verifierCondition(parametre, stats.getEcartNotes());
        
        // Déterminer la note finale selon la résolution
        BigDecimal noteFinale;
        String resolutionAppliquee;
        
        if (conditionVerifiee) {
            // Appliquer la résolution configurée
            noteFinale = appliquerResolution(
                parametre.getResolution().getNom(), 
                stats.getNotes()
            );
            resolutionAppliquee = parametre.getResolution().getNom();
        } else {
            // Condition non vérifiée, utiliser la moyenne
            noteFinale = stats.getNoteMoyenne();
            resolutionAppliquee = "moyenne (défaut)";
        }
        
        // Créer et retourner le DTO
        NoteFinaleDTO dto = new NoteFinaleDTO();
        dto.setIdExamen(examen.getIdExamen());
        dto.setEtudiantNom(examen.getEtudiant().getNom());
        dto.setEtudiantPrenom(examen.getEtudiant().getPrenom());
        dto.setMatiereNom(examen.getMatiere().getNomMatiere());
        dto.setNoteMin(stats.getNoteMin());
        dto.setNoteMax(stats.getNoteMax());
        dto.setNoteMoyenne(stats.getNoteMoyenne());
        dto.setEcartNotes(stats.getEcartNotes());
        dto.setResolutionAppliquee(resolutionAppliquee);
        
        if (parametre != null) {
            dto.setOperateurApplique(parametre.getOperateur().getSymbole());
            dto.setSeuilApplique(parametre.getSeuilMin());
        }
        
        dto.setNoteFinale(noteFinale);
        dto.setStatutConformite(determinerStatutConformite(noteFinale));
        
        return dto;
    }

    @Override
    public List<NoteFinaleDTO> determinerNotesFinalesTousExamens() {
        List<Examen> examens = examenRepository.findAll();
        List<NoteFinaleDTO> resultats = new ArrayList<>();
        
        for (Examen examen : examens) {
            if (examen.getNotes() != null && !examen.getNotes().isEmpty()) {
                resultats.add(determinerNoteFinale(examen.getIdExamen()));
            }
        }
        
        return resultats;
    }

    @Override
    public boolean verifierCondition(Parametre parametre, BigDecimal ecart) {
        if (parametre == null || ecart == null) return false;
        
        String operateur = parametre.getOperateur().getSymbole();
        BigDecimal seuil = parametre.getSeuilMin();
        
        switch (operateur) {
            case "=":
                return ecart.compareTo(seuil) == 0;
            case ">":
                return ecart.compareTo(seuil) > 0;
            case "<":
                return ecart.compareTo(seuil) < 0;
            case ">=":
                return ecart.compareTo(seuil) >= 0;
            case "<=":
                return ecart.compareTo(seuil) <= 0;
            case "BETWEEN":
                return ecart.compareTo(parametre.getSeuilMin()) >= 0 && 
                       ecart.compareTo(parametre.getSeuilMax()) <= 0;
            default:
                return false;
        }
    }

    @Override
    public BigDecimal appliquerResolution(String resolution, List<BigDecimal> notes) {
        if (notes == null || notes.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        switch (resolution.toLowerCase()) {
            case "superieur":
                return notes.stream()
                        .max(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);
            case "inferieur":
                return notes.stream()
                        .min(BigDecimal::compareTo)
                        .orElse(BigDecimal.ZERO);
            case "moyenne":
            default:
                BigDecimal somme = notes.stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                return somme.divide(new BigDecimal(notes.size()), 2, RoundingMode.HALF_UP);
        }
    }

    @Override
    public Parametre trouverParametreApplicable(Examen examen) {
        List<Parametre> parametres = parametreRepository
                .trouverParametresApplicables(
                    examen.getMatiere().getIdMatiere(), 
                    examen.getDateExamen()
                );
        
        // Retourner le premier paramètre actif trouvé (ou le plus récent)
        return parametres.isEmpty() ? null : parametres.get(0);
    }
    
    private NoteFinaleDTO creerNoteFinaleParDefaut(NoteAvecEcartDTO stats, Examen examen) {
        NoteFinaleDTO dto = new NoteFinaleDTO();
        dto.setIdExamen(examen.getIdExamen());
        dto.setEtudiantNom(examen.getEtudiant().getNom());
        dto.setEtudiantPrenom(examen.getEtudiant().getPrenom());
        dto.setMatiereNom(examen.getMatiere().getNomMatiere());
        dto.setNoteMin(stats.getNoteMin());
        dto.setNoteMax(stats.getNoteMax());
        dto.setNoteMoyenne(stats.getNoteMoyenne());
        dto.setEcartNotes(stats.getEcartNotes());
        dto.setResolutionAppliquee("moyenne (par défaut)");
        dto.setNoteFinale(stats.getNoteMoyenne());
        dto.setStatutConformite(determinerStatutConformite(stats.getNoteMoyenne()));
        
        return dto;
    }

    @Override
    public NoteFinaleDTO chercherNoteParEtudiantEtMatiere(Long etudiantId, Long matiereId) {
        // Utiliser la méthode corrigée
        Optional<Examen> examenOpt = examenRepository.findByEtudiant_IdEtudiantAndMatiere_IdMatiere(etudiantId, matiereId);
        
        if (!examenOpt.isPresent()) {
            NoteFinaleDTO dto = new NoteFinaleDTO();
            dto.setNoteFinale(null);
            dto.setStatutConformite("Aucun examen trouvé pour cet étudiant dans cette matière");
            return dto;
        }
        
        Examen examen = examenOpt.get();
        
        // Vérifier si l'examen a des notes
        if (examen.getNotes() == null || examen.getNotes().isEmpty()) {
            NoteFinaleDTO dto = new NoteFinaleDTO();
            dto.setNoteFinale(null);
            dto.setStatutConformite("L'examen n'a pas encore de notes");
            dto.setIdExamen(examen.getIdExamen());
            dto.setEtudiantNom(examen.getEtudiant().getNom());
            dto.setEtudiantPrenom(examen.getEtudiant().getPrenom());
            dto.setMatiereNom(examen.getMatiere().getNomMatiere());
            return dto;
        }
        
        return determinerNoteFinale(examen.getIdExamen());
    }
    
    private String determinerStatutConformite(BigDecimal note) {
        if (note == null) return "Non défini";
        if (note.compareTo(new BigDecimal("10")) >= 0) {
            return "Admis";
        } else {
            return "Non admis";
        }
    }
}