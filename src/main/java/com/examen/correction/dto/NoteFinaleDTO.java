package com.examen.correction.dto;

import java.math.BigDecimal;
import java.util.List;

public class NoteFinaleDTO {
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
    private BigDecimal mediane;
    private BigDecimal ecartNotes;
    private BigDecimal ecartType;
    private String resolutionAppliquee;
    private String operateurApplique;
    private BigDecimal seuilApplique;
    private BigDecimal noteFinale;
    private String statutConformite;
    
    // Constructeurs
    public NoteFinaleDTO() {}
    
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
    
    public BigDecimal getMediane() { return mediane; }
    public void setMediane(BigDecimal mediane) { this.mediane = mediane; }
    
    public BigDecimal getEcartNotes() { return ecartNotes; }
    public void setEcartNotes(BigDecimal ecartNotes) { this.ecartNotes = ecartNotes; }
    
    public BigDecimal getEcartType() { return ecartType; }
    public void setEcartType(BigDecimal ecartType) { this.ecartType = ecartType; }
    
    public String getResolutionAppliquee() { return resolutionAppliquee; }
    public void setResolutionAppliquee(String resolutionAppliquee) { this.resolutionAppliquee = resolutionAppliquee; }
    
    public String getOperateurApplique() { return operateurApplique; }
    public void setOperateurApplique(String operateurApplique) { this.operateurApplique = operateurApplique; }
    
    public BigDecimal getSeuilApplique() { return seuilApplique; }
    public void setSeuilApplique(BigDecimal seuilApplique) { this.seuilApplique = seuilApplique; }
    
    public BigDecimal getNoteFinale() { return noteFinale; }
    public void setNoteFinale(BigDecimal noteFinale) { this.noteFinale = noteFinale; }
    
    public String getStatutConformite() { return statutConformite; }
    public void setStatutConformite(String statutConformite) { this.statutConformite = statutConformite; }
}