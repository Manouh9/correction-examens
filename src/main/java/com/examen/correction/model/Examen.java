package com.examen.correction.model;

import javax.persistence.*;
import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "examen")
public class Examen {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idExamen;
    
    @ManyToOne
    @JoinColumn(name = "id_etudiant", nullable = false)
    private Etudiant etudiant;
    
    @ManyToOne
    @JoinColumn(name = "id_matiere", nullable = false)
    private Matiere matiere;
    
    @Column(nullable = false)
    private LocalDate dateExamen;
    
    @OneToMany(mappedBy = "examen", cascade = CascadeType.ALL)
    private List<Note> notes;
    
    // Constructeurs
    public Examen() {}
    
    public Examen(Etudiant etudiant, Matiere matiere, LocalDate dateExamen) {
        this.etudiant = etudiant;
        this.matiere = matiere;
        this.dateExamen = dateExamen;
    }
    
    // Getters et Setters
    public Long getIdExamen() { return idExamen; }
    public void setIdExamen(Long idExamen) { this.idExamen = idExamen; }
    
    public Etudiant getEtudiant() { return etudiant; }
    public void setEtudiant(Etudiant etudiant) { this.etudiant = etudiant; }
    
    public Matiere getMatiere() { return matiere; }
    public void setMatiere(Matiere matiere) { this.matiere = matiere; }
    
    public LocalDate getDateExamen() { return dateExamen; }
    public void setDateExamen(LocalDate dateExamen) { this.dateExamen = dateExamen; }
    
    public List<Note> getNotes() { return notes; }
    public void setNotes(List<Note> notes) { this.notes = notes; }
}