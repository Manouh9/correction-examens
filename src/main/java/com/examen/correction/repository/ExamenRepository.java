package com.examen.correction.repository;

import com.examen.correction.model.Examen;
import com.examen.correction.model.Etudiant;
import com.examen.correction.model.Matiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ExamenRepository extends JpaRepository<Examen, Long> {
    
    List<Examen> findByEtudiant(Etudiant etudiant);
    List<Examen> findByMatiere(Matiere matiere);
    List<Examen> findByDateExamen(LocalDate date);
    List<Examen> findByDateExamenBetween(LocalDate dateDebut, LocalDate dateFin);
    List<Examen> findByEtudiantAndMatiere(Etudiant etudiant, Matiere matiere);

    Optional<Examen> findByEtudiant_IdEtudiantAndMatiere_IdMatiere(Long etudiantId, Long matiereId);
    
    // Ou avec @Query (plus explicite)
    @Query("SELECT e FROM Examen e WHERE e.etudiant.idEtudiant = :etudiantId AND e.matiere.idMatiere = :matiereId")
    Optional<Examen> trouverParEtudiantEtMatiere(@Param("etudiantId") Long etudiantId, @Param("matiereId") Long matiereId);
    
    @Query("SELECT e FROM Examen e WHERE SIZE(e.notes) >= :nbNotes")
    List<Examen> trouverExamensAvecAuMoinsNNotes(@Param("nbNotes") int nbNotes);
    
    @Query("SELECT e FROM Examen e WHERE SIZE(e.notes) > 1")
    List<Examen> trouverExamensAvecPlusieursCorrections();
    
    Long countByMatiere(Matiere matiere);
    Long countByEtudiant(Etudiant etudiant);
}