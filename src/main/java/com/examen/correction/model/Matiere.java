package com.examen.correction.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "matiere")
public class Matiere {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idMatiere;
    
    @Column(nullable = false, length = 100)
    private String nomMatiere;
    
    @Column(precision = 3, scale = 2)
    private BigDecimal coefficient = BigDecimal.ONE;
    
    @OneToMany(mappedBy = "matiere")
    private List<Examen> examens;
    
    @OneToMany(mappedBy = "matiere")
    private List<Parametre> parametres;
    
    // Constructeurs
    public Matiere() {}
    
    public Matiere(String nomMatiere, BigDecimal coefficient) {
        this.nomMatiere = nomMatiere;
        this.coefficient = coefficient;
    }
    
    // Getters et Setters
    public Long getIdMatiere() { return idMatiere; }
    public void setIdMatiere(Long idMatiere) { this.idMatiere = idMatiere; }
    
    public String getNomMatiere() { return nomMatiere; }
    public void setNomMatiere(String nomMatiere) { this.nomMatiere = nomMatiere; }
    
    public BigDecimal getCoefficient() { return coefficient; }
    public void setCoefficient(BigDecimal coefficient) { this.coefficient = coefficient; }
    
    public List<Examen> getExamens() { return examens; }
    public void setExamens(List<Examen> examens) { this.examens = examens; }
    
    public List<Parametre> getParametres() { return parametres; }
    public void setParametres(List<Parametre> parametres) { this.parametres = parametres; }
}