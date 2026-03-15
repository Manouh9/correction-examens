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
import java.util.Collections;
import java.util.Comparator;
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
        
        if (notes == null || notes.isEmpty()) {
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
        
        NoteAvecEcartDTO stats = calculerEcartParExamen(idExamen);
        
        BigDecimal mediane = calculerMediane(stats.getNotes());
        BigDecimal ecartType = calculerEcartType(stats.getNotes(), stats.getNoteMoyenne());
        
        // Trouver le paramètre applicable avec la nouvelle logique
        Parametre parametre = trouverParametreApplicable(examen);
        
        // Déterminer la note finale
        BigDecimal noteFinale;
        String resolutionAppliquee;
        
        if (parametre == null) {
            noteFinale = stats.getNoteMoyenne();
            resolutionAppliquee = "moyenne (par défaut)";
        } else {
            boolean conditionVerifiee = verifierCondition(parametre, stats.getEcartNotes());
            
            if (conditionVerifiee) {
                noteFinale = appliquerResolution(
                    parametre.getResolution().getNom(), 
                    stats.getNotes()
                );
                resolutionAppliquee = parametre.getResolution().getNom() + " (condition vérifiée)";
            } else {
                noteFinale = stats.getNoteMoyenne();
                resolutionAppliquee = "moyenne (condition non vérifiée)";
            }
        }
        
        NoteFinaleDTO dto = new NoteFinaleDTO();
        dto.setIdExamen(examen.getIdExamen());
        dto.setEtudiantNom(examen.getEtudiant().getNom());
        dto.setEtudiantPrenom(examen.getEtudiant().getPrenom());
        dto.setMatiereNom(examen.getMatiere().getNomMatiere());
        dto.setNotes(stats.getNotes());
        dto.setCorrecteurs(stats.getCorrecteurs());
        dto.setNombreCorrecteurs(stats.getNombreCorrecteurs());
        dto.setNoteMin(stats.getNoteMin());
        dto.setNoteMax(stats.getNoteMax());
        dto.setNoteMoyenne(stats.getNoteMoyenne());
        dto.setMediane(mediane);
        dto.setEcartNotes(stats.getEcartNotes());
        dto.setEcartType(ecartType);
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
        
        List<BigDecimal> notesTriees = new ArrayList<>(notes);
        Collections.sort(notesTriees);
        
        switch (resolution.toLowerCase()) {
            case "superieur":
                return notesTriees.get(notesTriees.size() - 1);
            case "inferieur":
                return notesTriees.get(0);
            case "mediane":
                return calculerMediane(notesTriees);
            case "moyenne":
            default:
                BigDecimal somme = notesTriees.stream()
                        .reduce(BigDecimal.ZERO, BigDecimal::add);
                return somme.divide(new BigDecimal(notesTriees.size()), 2, RoundingMode.HALF_UP);
        }
    }

    @Override
    public BigDecimal calculerMediane(List<BigDecimal> notes) {
        if (notes == null || notes.isEmpty()) return BigDecimal.ZERO;
        
        List<BigDecimal> notesTriees = new ArrayList<>(notes);
        Collections.sort(notesTriees);
        
        int taille = notesTriees.size();
        int milieu = taille / 2;
        
        if (taille % 2 == 0) {
            return notesTriees.get(milieu - 1)
                    .add(notesTriees.get(milieu))
                    .divide(new BigDecimal(2), 2, RoundingMode.HALF_UP);
        } else {
            return notesTriees.get(milieu);
        }
    }

    @Override
    public BigDecimal calculerEcartType(List<BigDecimal> notes, BigDecimal moyenne) {
        if (notes == null || notes.size() < 2) return BigDecimal.ZERO;
        
        BigDecimal sommeCarres = BigDecimal.ZERO;
        for (BigDecimal note : notes) {
            BigDecimal diff = note.subtract(moyenne);
            sommeCarres = sommeCarres.add(diff.multiply(diff));
        }
        
        BigDecimal variance = sommeCarres.divide(new BigDecimal(notes.size()), 10, RoundingMode.HALF_UP);
        return new BigDecimal(Math.sqrt(variance.doubleValue())).setScale(2, RoundingMode.HALF_UP);
    }

    @Override
    public BigDecimal calculerEcartPourExamen(Long idExamen) {
        Examen examen = examenRepository.findById(idExamen)
                .orElseThrow(() -> new RuntimeException("Examen non trouvé"));
        
        List<Note> notes = examen.getNotes();
        if (notes == null || notes.isEmpty()) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal noteMin = notes.stream()
                .map(Note::getValeurNote)
                .min(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        
        BigDecimal noteMax = notes.stream()
                .map(Note::getValeurNote)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        
        return noteMax.subtract(noteMin);
    }

    @Override
    public Parametre trouverParametreApplicable(Examen examen) {
        List<Parametre> parametres = parametreRepository
                .trouverParametresApplicables(
                    examen.getMatiere().getIdMatiere(), 
                    examen.getDateExamen()
                );
        
        if (parametres.isEmpty()) {
            return null;
        }
        
        if (parametres.size() == 1) {
            return parametres.get(0);
        }
        
        BigDecimal ecart = calculerEcartPourExamen(examen.getIdExamen());
        System.out.println("=== RECHERCHE DU PARAMÈTRE LE PLUS PROCHE ===");
        System.out.println("Matière: " + examen.getMatiere().getNomMatiere());
        System.out.println("Écart calculé: " + ecart);
        System.out.println("Nombre de paramètres: " + parametres.size());
        
        Parametre resultat = trouverParametreLePlusProche(parametres, ecart);
        
        System.out.println("Paramètre choisi - Seuil: " + resultat.getSeuilMin() + 
                          ", Résolution: " + resultat.getResolution().getNom());
        System.out.println("=============================================");
        
        return resultat;
    }

    @Override
    public Parametre trouverParametreLePlusProche(List<Parametre> parametres, BigDecimal ecart) {
        // Trier les paramètres par seuil croissant
        parametres.sort(Comparator.comparing(Parametre::getSeuilMin));
        
        System.out.println("Paramètres triés par seuil:");
        for (Parametre p : parametres) {
            System.out.println("  - Seuil: " + p.getSeuilMin() + 
                             ", Opérateur: " + p.getOperateur().getSymbole() +
                             ", Résolution: " + p.getResolution().getNom());
        }
        
        // Vérifier d'abord si un paramètre satisfait déjà la condition
        for (Parametre p : parametres) {
            if (verifierCondition(p, ecart)) {
                System.out.println("→ Condition déjà satisfaite avec seuil " + p.getSeuilMin());
                return p;
            }
        }
        
        // Sinon, trouver le seuil le plus proche
        Parametre meilleur = null;
        BigDecimal plusPetiteDifference = null;
        
        for (Parametre p : parametres) {
            BigDecimal difference = ecart.subtract(p.getSeuilMin()).abs();
            System.out.println("Seuil: " + p.getSeuilMin() + ", Distance: " + difference);
            
            if (plusPetiteDifference == null || difference.compareTo(plusPetiteDifference) < 0) {
                plusPetiteDifference = difference;
                meilleur = p;
                System.out.println("  → Nouveau meilleur");
            } else if (difference.compareTo(plusPetiteDifference) == 0) {
                // En cas d'égalité, prendre le seuil le plus petit
                System.out.println("  → Égalité de distance");
                if (p.getSeuilMin().compareTo(meilleur.getSeuilMin()) < 0) {
                    meilleur = p;
                    System.out.println("    → Prise du seuil le plus petit");
                }
            }
        }
        
        System.out.println("→ Meilleur seuil: " + meilleur.getSeuilMin() + 
                         " (distance: " + plusPetiteDifference + ")");
        
        return meilleur;
    }

    @Override
    public NoteFinaleDTO chercherNoteParEtudiantEtMatiere(Long etudiantId, Long matiereId) {
        Optional<Examen> examenOpt = examenRepository.findByEtudiant_IdEtudiantAndMatiere_IdMatiere(etudiantId, matiereId);
        
        if (!examenOpt.isPresent()) {
            NoteFinaleDTO dto = new NoteFinaleDTO();
            dto.setNoteFinale(null);
            dto.setStatutConformite("Aucun examen trouvé pour cet étudiant dans cette matière");
            return dto;
        }
        
        Examen examen = examenOpt.get();
        
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