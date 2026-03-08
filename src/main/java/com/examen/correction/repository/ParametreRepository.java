package com.examen.correction.repository;

import com.examen.correction.model.Parametre;
import com.examen.correction.model.Matiere;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface ParametreRepository extends JpaRepository<Parametre, Long> {
    List<Parametre> findByMatiereAndActiveTrue(Matiere matiere);
    
    @Query("SELECT p FROM Parametre p WHERE p.matiere.idMatiere = :idMatiere AND p.active = true")
    List<Parametre> trouverParametresActifsParMatiere(@Param("idMatiere") Long idMatiere);
    
    @Query("SELECT p FROM Parametre p WHERE p.matiere.idMatiere = :idMatiere " +
           "AND p.dateApplication <= :date AND p.active = true")
    List<Parametre> trouverParametresApplicables(@Param("idMatiere") Long idMatiere, 
                                                 @Param("date") LocalDate date);
    
    Optional<Parametre> findByMatiereAndActiveTrueAndDateApplication(Matiere matiere, LocalDate date);
}