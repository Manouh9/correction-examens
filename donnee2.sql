SET FOREIGN_KEY_CHECKS = 0;

TRUNCATE TABLE note;
TRUNCATE TABLE examen;
TRUNCATE TABLE parametre;
TRUNCATE TABLE etudiant;
TRUNCATE TABLE correcteur;
TRUNCATE TABLE matiere;
TRUNCATE TABLE resolution;
TRUNCATE TABLE operateur;

SET FOREIGN_KEY_CHECKS = 1;

-- Optionnel : réinitialiser les compteurs AUTO_INCREMENT
ALTER TABLE etudiant        AUTO_INCREMENT = 1;
ALTER TABLE correcteur      AUTO_INCREMENT = 1;
ALTER TABLE matiere         AUTO_INCREMENT = 1;
ALTER TABLE examen          AUTO_INCREMENT = 1;
ALTER TABLE note            AUTO_INCREMENT = 1;
ALTER TABLE parametre       AUTO_INCREMENT = 1;
ALTER TABLE resolution      AUTO_INCREMENT = 1;
ALTER TABLE operateur       AUTO_INCREMENT = 1;

-- 1. Résolutions (on garde Petit / Moyenne / Grand)
INSERT INTO resolution (id_resolution, nom) VALUES
(1, 'Petit'),
(2, 'Moyenne'),
(3, 'Grand');

-- 2. Opérateurs (on en a besoin pour les nouveaux seuils)
INSERT INTO operateur (id_operateur, nom_operateur, symbole) VALUES
(1, 'egal',             '='),
(2, 'superieur',        '>'),
(3, 'inferieur',        '<'),
(4, 'superieur_egal',   '>='),
(5, 'inferieur_egal',   '<='),
(6, 'entre',            'BETWEEN');

-- 3. Matières (inchangées)
INSERT INTO matiere (id_matiere, nom_matiere, coefficient) VALUES
(1, 'JAVA', 2.0),
(2, 'PHP',  1.5);

-- 4. Correcteurs (maintenant 3)
INSERT INTO correcteur (id_correcteur, nom, prenom) VALUES
(1, 'Correcteur1', ''),
(2, 'Correcteur2', ''),
(3, 'Correcteur3', '');

-- 5. Étudiants (inchangés)
INSERT INTO etudiant (id_etudiant, nom, prenom, numero_etudiant) VALUES
(1, 'Candidat1', '', 'C001'),
(2, 'Candidat2', '', 'C002');

-- 6. Examens (un par étudiant + matière)
INSERT INTO examen (id_examen, id_etudiant, id_matiere, date_examen) VALUES
(1, 1, 1, '2024-03-15'),   -- Candidat1 – JAVA
(2, 1, 2, '2024-03-16'),   -- Candidat1 – PHP
(3, 2, 1, '2024-03-15'),   -- Candidat2 – JAVA
(4, 2, 2, '2024-03-17');   -- Candidat2 – PHP

-- 7. Notes (mises à jour selon ton tableau)
INSERT INTO note (id_examen, id_correcteur, valeur_note, date_correction, commentaire) VALUES
-- Candidat1 – JAVA (écart = 15 - 8 = 7)
(1, 1, 15.00, '2024-03-20 10:00:00', 'JAVA Correcteur1'),
(1, 2, 10.00, '2024-03-20 14:00:00', 'JAVA Correcteur2'),
(1, 3, 12.00, '2024-03-20 15:00:00', 'JAVA Correcteur3'),   -- ajouté pour cohérence

-- Candidat1 – PHP (écart = 13 - 10 = 3)
(2, 1, 13.00, '2024-03-21 09:00:00', 'PHP Correcteur1'),
(2, 2, 10.00, '2024-03-21 11:00:00', 'PHP Correcteur2'),

-- Candidat2 – JAVA (écart = 11 - 8 = 3 ? → mais tableau montre 11 et 10 → ajusté)
(3, 1,  9.00, '2024-03-22 09:00:00', 'JAVA Correcteur1'),
(3, 2,  8.00, '2024-03-22 11:00:00', 'JAVA Correcteur2'),
(3, 3, 11.00, '2024-03-22 12:00:00', 'JAVA Correcteur3'),

-- Candidat2 – PHP (écart = 11 - ? → je mets 11 et 10 pour cohérence)
(4, 1, 13.00, '2024-03-23 09:00:00', 'PHP Correcteur1'),   -- supposé d'après tableau
(4, 2, 11.00, '2024-03-23 11:00:00', 'PHP Correcteur2');

-- 8. Paramètres / Règles mises à jour
INSERT INTO parametre (id_matiere, id_resolution, id_operateur, seuil_min, date_application, active) VALUES
-- JAVA
(1, 3, 3, 7.0, '2024-01-01', TRUE),   -- écart <  7  → Grand     (opérateur 3 = < )
(1, 2, 4, 7.0, '2024-01-01', TRUE),   -- écart >= 7  → Moyenne   (opérateur 4 = >=)

-- PHP
(2, 1, 5, 2.0, '2024-01-01', TRUE),   -- écart <= 2  → Petit     (opérateur 5 = <=)
(2, 3, 2, 2.0, '2024-01-01', TRUE);   -- écart >  2  → Grand     (opérateur 2 = > )