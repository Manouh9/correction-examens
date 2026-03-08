package com.examen.correction.repository;

import com.examen.correction.model.Note;
import com.examen.correction.model.Examen;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

@Repository
public interface NoteRepository extends JpaRepository<Note, Long> {
    List<Note> findByExamen(Examen examen);
    
    Optional<Note> findByExamenAndCorrecteurIdCorrecteur(Examen examen, Long idCorrecteur);
    
    @Query("SELECT AVG(n.valeurNote) FROM Note n WHERE n.examen.idExamen = :idExamen")
    BigDecimal calculerMoyenneParExamen(@Param("idExamen") Long idExamen);
    
    @Query("SELECT MIN(n.valeurNote) FROM Note n WHERE n.examen.idExamen = :idExamen")
    BigDecimal trouverNoteMinParExamen(@Param("idExamen") Long idExamen);
    
    @Query("SELECT MAX(n.valeurNote) FROM Note n WHERE n.examen.idExamen = :idExamen")
    BigDecimal trouverNoteMaxParExamen(@Param("idExamen") Long idExamen);
    
    @Query("SELECT COUNT(n) FROM Note n WHERE n.examen.idExamen = :idExamen")
    int compterNotesParExamen(@Param("idExamen") Long idExamen);
}