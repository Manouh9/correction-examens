package com.examen.correction.repository;

import com.examen.correction.model.Correcteur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface CorrecteurRepository extends JpaRepository<Correcteur, Long> {
    Optional<Correcteur> findByEmail(String email);
}