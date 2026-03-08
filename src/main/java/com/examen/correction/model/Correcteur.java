package com.examen.correction.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "correcteur")
public class Correcteur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idCorrecteur;
    
    @Column(nullable = false, length = 100)
    private String nom;
    
    @Column(nullable = false, length = 100)
    private String prenom;
    
    @Column(unique = true, length = 200)
    private String email;
    
    @OneToMany(mappedBy = "correcteur")
    private List<Note> notes;
    
    // Constructeurs
    public Correcteur() {}
    
    public Correcteur(String nom, String prenom, String email) {
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
    }
    
    // Getters et Setters
    public Long getIdCorrecteur() { return idCorrecteur; }
    public void setIdCorrecteur(Long idCorrecteur) { this.idCorrecteur = idCorrecteur; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getPrenom() { return prenom; }
    public void setPrenom(String prenom) { this.prenom = prenom; }
    
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    
    public List<Note> getNotes() { return notes; }
    public void setNotes(List<Note> notes) { this.notes = notes; }
}