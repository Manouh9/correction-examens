package com.examen.correction.dto;

import java.math.BigDecimal;
import java.util.List;

public class NoteAvecEcartDTO {
    private Long idExamen;
    private String etudiantNom;
    private String etudiantPrenom;
    private String matiereNom;
    private List<BigDecimal> notes;
    private List<String> correcteurs;
    private Integer nombreCorrecteurs;
    private BigDecimal noteMin;
    private BigDecimal noteMax;
    private BigDecimal noteMoyenne;
    private BigDecimal ecartNotes;
    
    // Constructeurs
    public NoteAvecEcartDTO() {}
    
    public NoteAvecEcartDTO(Long idExamen, String etudiantNom, String etudiantPrenom, 
                           String matiereNom, List<BigDecimal> notes, List<String> correcteurs,
                           Integer nombreCorrecteurs, BigDecimal noteMin, BigDecimal noteMax, 
                           BigDecimal noteMoyenne, BigDecimal ecartNotes) {
        this.idExamen = idExamen;
        this.etudiantNom = etudiantNom;
        this.etudiantPrenom = etudiantPrenom;
        this.matiereNom = matiereNom;
        this.notes = notes;
        this.correcteurs = correcteurs;
        this.nombreCorrecteurs = nombreCorrecteurs;
        this.noteMin = noteMin;
        this.noteMax = noteMax;
        this.noteMoyenne = noteMoyenne;
        this.ecartNotes = ecartNotes;
    }
    
    // Getters et Setters
    public Long getIdExamen() { return idExamen; }
    public void setIdExamen(Long idExamen) { this.idExamen = idExamen; }
    
    public String getEtudiantNom() { return etudiantNom; }
    public void setEtudiantNom(String etudiantNom) { this.etudiantNom = etudiantNom; }
    
    public String getEtudiantPrenom() { return etudiantPrenom; }
    public void setEtudiantPrenom(String etudiantPrenom) { this.etudiantPrenom = etudiantPrenom; }
    
    public String getMatiereNom() { return matiereNom; }
    public void setMatiereNom(String matiereNom) { this.matiereNom = matiereNom; }
    
    public List<BigDecimal> getNotes() { return notes; }
    public void setNotes(List<BigDecimal> notes) { this.notes = notes; }
    
    public List<String> getCorrecteurs() { return correcteurs; }
    public void setCorrecteurs(List<String> correcteurs) { this.correcteurs = correcteurs; }
    
    public Integer getNombreCorrecteurs() { return nombreCorrecteurs; }
    public void setNombreCorrecteurs(Integer nombreCorrecteurs) { this.nombreCorrecteurs = nombreCorrecteurs; }
    
    public BigDecimal getNoteMin() { return noteMin; }
    public void setNoteMin(BigDecimal noteMin) { this.noteMin = noteMin; }
    
    public BigDecimal getNoteMax() { return noteMax; }
    public void setNoteMax(BigDecimal noteMax) { this.noteMax = noteMax; }
    
    public BigDecimal getNoteMoyenne() { return noteMoyenne; }
    public void setNoteMoyenne(BigDecimal noteMoyenne) { this.noteMoyenne = noteMoyenne; }
    
    public BigDecimal getEcartNotes() { return ecartNotes; }
    public void setEcartNotes(BigDecimal ecartNotes) { this.ecartNotes = ecartNotes; }
}