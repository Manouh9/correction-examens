package com.examen.correction.model;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "note", uniqueConstraints = {
    @UniqueConstraint(columnNames = {"id_examen", "id_correcteur"})
})
public class Note {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idNote;
    
    @ManyToOne
    @JoinColumn(name = "id_examen", nullable = false)
    private Examen examen;
    
    @ManyToOne
    @JoinColumn(name = "id_correcteur", nullable = false)
    private Correcteur correcteur;
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal valeurNote;
    
    @Column(nullable = false)
    private LocalDateTime dateCorrection;
    
    @Column(columnDefinition = "TEXT")
    private String commentaire;
    
    // Constructeurs
    public Note() {}
    
    public Note(Examen examen, Correcteur correcteur, BigDecimal valeurNote, 
                LocalDateTime dateCorrection, String commentaire) {
        this.examen = examen;
        this.correcteur = correcteur;
        this.valeurNote = valeurNote;
        this.dateCorrection = dateCorrection;
        this.commentaire = commentaire;
    }
    
    // Getters et Setters
    public Long getIdNote() { return idNote; }
    public void setIdNote(Long idNote) { this.idNote = idNote; }
    
    public Examen getExamen() { return examen; }
    public void setExamen(Examen examen) { this.examen = examen; }
    
    public Correcteur getCorrecteur() { return correcteur; }
    public void setCorrecteur(Correcteur correcteur) { this.correcteur = correcteur; }
    
    public BigDecimal getValeurNote() { return valeurNote; }
    public void setValeurNote(BigDecimal valeurNote) { this.valeurNote = valeurNote; }
    
    public LocalDateTime getDateCorrection() { return dateCorrection; }
    public void setDateCorrection(LocalDateTime dateCorrection) { this.dateCorrection = dateCorrection; }
    
    public String getCommentaire() { return commentaire; }
    public void setCommentaire(String commentaire) { this.commentaire = commentaire; }
}