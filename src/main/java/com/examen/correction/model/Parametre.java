package com.examen.correction.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "parametre")
public class Parametre {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idParametre;
    
    @ManyToOne
    @JoinColumn(name = "id_matiere", nullable = false)
    private Matiere matiere;
    
    @ManyToOne
    @JoinColumn(name = "id_resolution", nullable = false)
    private Resolution resolution;
    
    @ManyToOne
    @JoinColumn(name = "id_operateur", nullable = false)
    private Operateur operateur;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal seuilMin;
    
    @Column(precision = 5, scale = 2)
    private BigDecimal seuilMax;
    
    @Column(nullable = false)
    private LocalDate dateApplication;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    // Constructeurs
    public Parametre() {}
    
    // Getters et Setters
    public Long getIdParametre() { return idParametre; }
    public void setIdParametre(Long idParametre) { this.idParametre = idParametre; }
    
    public Matiere getMatiere() { return matiere; }
    public void setMatiere(Matiere matiere) { this.matiere = matiere; }
    
    public Resolution getResolution() { return resolution; }
    public void setResolution(Resolution resolution) { this.resolution = resolution; }
    
    public Operateur getOperateur() { return operateur; }
    public void setOperateur(Operateur operateur) { this.operateur = operateur; }
    
    public BigDecimal getSeuilMin() { return seuilMin; }
    public void setSeuilMin(BigDecimal seuilMin) { this.seuilMin = seuilMin; }
    
    public BigDecimal getSeuilMax() { return seuilMax; }
    public void setSeuilMax(BigDecimal seuilMax) { this.seuilMax = seuilMax; }
    
    public LocalDate getDateApplication() { return dateApplication; }
    public void setDateApplication(LocalDate dateApplication) { this.dateApplication = dateApplication; }
    
    public Boolean getActive() { return active; }
    public void setActive(Boolean active) { this.active = active; }
}