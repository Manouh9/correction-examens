package com.examen.correction.model;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "operateur")
public class Operateur {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idOperateur;
    
    @Column(nullable = false, length = 50)
    private String nomOperateur;  // 'egal', 'superieur', etc.
    
    @Column(nullable = false, length = 10)
    private String symbole;  // '=', '>', '<', etc.
    
    @OneToMany(mappedBy = "operateur")
    private List<Parametre> parametres;
    
    // Constructeurs
    public Operateur() {}
    
    public Operateur(String nomOperateur, String symbole) {
        this.nomOperateur = nomOperateur;
        this.symbole = symbole;
    }
    
    // Getters et Setters
    public Long getIdOperateur() { return idOperateur; }
    public void setIdOperateur(Long idOperateur) { this.idOperateur = idOperateur; }
    
    public String getNomOperateur() { return nomOperateur; }
    public void setNomOperateur(String nomOperateur) { this.nomOperateur = nomOperateur; }
    
    public String getSymbole() { return symbole; }
    public void setSymbole(String symbole) { this.symbole = symbole; }
    
    public List<Parametre> getParametres() { return parametres; }
    public void setParametres(List<Parametre> parametres) { this.parametres = parametres; }
}