package com.examen.correction.repository;

import com.examen.correction.model.Operateur;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface OperateurRepository extends JpaRepository<Operateur, Long> {
    Optional<Operateur> findBySymbole(String symbole);
}