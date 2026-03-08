package com.examen.correction.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "resolution")
public class Resolution {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idResolution;
    
    @Column(nullable = false, length = 50)
    private String nom;  // 'moyenne', 'superieur', 'inferieur'
    
    @OneToMany(mappedBy = "resolution")
    private List<Parametre> parametres;
    
    // Constructeurs
    public Resolution() {}
    
    public Resolution(String nom) {
        this.nom = nom;
    }
    
    // Getters et Setters
    public Long getIdResolution() { return idResolution; }
    public void setIdResolution(Long idResolution) { this.idResolution = idResolution; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public List<Parametre> getParametres() { return parametres; }
    public void setParametres(List<Parametre> parametres) { this.parametres = parametres; }
}