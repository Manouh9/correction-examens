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

-- réinitialiser les compteurs AUTO_INCREMENT
ALTER TABLE etudiant        AUTO_INCREMENT = 1;
ALTER TABLE correcteur      AUTO_INCREMENT = 1;
ALTER TABLE matiere         AUTO_INCREMENT = 1;
ALTER TABLE examen          AUTO_INCREMENT = 1;
ALTER TABLE note            AUTO_INCREMENT = 1;
ALTER TABLE parametre       AUTO_INCREMENT = 1;
ALTER TABLE resolution      AUTO_INCREMENT = 1;
ALTER TABLE operateur       AUTO_INCREMENT = 1;

-- 1. Résolutions (Grand / Moyen / Petit)
INSERT INTO resolution (id_resolution, nom) VALUES
(1, 'Petit'),
(2, 'Moyenne'),
(3, 'Grand');

-- 2. Opérateurs
INSERT INTO operateur (id_operateur, nom_operateur, symbole) VALUES
(1, 'egal',             '='),
(2, 'superieur',        '>'),
(3, 'inferieur',        '<'),
(4, 'superieur_egal',   '>='),
(5, 'inferieur_egal',   '<='),
(6, 'entre',            'BETWEEN');

-- 3. Matières
INSERT INTO matiere (id_matiere, nom_matiere, coefficient) VALUES
(1, 'JAVA', 2.0),
(2, 'PHP',  1.5);

-- 4. Correcteurs (seulement ceux qui apparaissent dans les tableaux)
INSERT INTO correcteur (id_correcteur, nom, prenom) VALUES
(1, 'Correcteur1', ''),
(2, 'Correcteur2', '');

-- 5. Étudiants
INSERT INTO etudiant (id_etudiant, nom, prenom, numero_etudiant) VALUES
(1, 'Candidat1', '', 'C001'),
(2, 'Candidat2', '', 'C002');

-- 6. Examens (on crée une ligne par étudiant + matière)
INSERT INTO examen (id_examen, id_etudiant, id_matiere, date_examen) VALUES
(1, 1, 1, '2024-03-15'),   -- Candidat1 – JAVA
(2, 1, 2, '2024-03-16'),   -- Candidat1 – PHP
(3, 2, 1, '2024-03-15'),   -- Candidat2 – JAVA
(4, 2, 2, '2024-03-17');   -- Candidat2 – PHP

INSERT INTO note (id_examen, id_correcteur, valeur_note, date_correction, commentaire) VALUES

-- Candidat1 – JAVA
(1, 1, 12.00, '2024-03-20 10:00:00', 'JAVA Correcteur1'),
(1, 2, 11.00, '2024-03-20 14:00:00', 'JAVA Correcteur2'),

-- Candidat1 – PHP
(2, 1,  7.00, '2024-03-21 09:00:00', 'PHP Correcteur1'),
(2, 2, 11.00, '2024-03-21 11:00:00', 'PHP Correcteur2'),

-- Candidat2 – JAVA
(3, 1, 13.00, '2024-03-22 09:00:00', 'JAVA Correcteur1'),
(3, 2, 10.00, '2024-03-22 11:00:00', 'JAVA Correcteur2'),

-- Candidat2 – PHP
(4, 1, 14.00, '2024-03-23 09:00:00', 'PHP Correcteur1'),
(4, 2, 16.00, '2024-03-23 11:00:00', 'PHP Correcteur2');

INSERT INTO parametre (id_matiere, id_resolution, id_operateur, seuil_min, date_application, active) VALUES

-- JAVA
(1, 3, 3, 3.0, '2024-01-01', TRUE),   -- écart < 3    → Grand    (supérieur)
(1, 2, 4, 3.0, '2024-01-01', TRUE),   -- écart >= 3   → Moyenne

-- PHP
(2, 1, 5, 2.0, '2024-01-01', TRUE),   -- écart <= 2   → Petit    (inférieur)
(2, 3, 2, 2.0, '2024-01-01', TRUE);   -- écart > 2    → Grand    (supérieur)

-- Vérifier les notes par étudiant et matière
SELECT 
    e.nom AS Candidat,
    m.nom_matiere AS Matiere,
    c.nom AS Correcteur,
    n.valeur_note AS Note
FROM note n
JOIN examen ex  ON n.id_examen    = ex.id_examen
JOIN etudiant e ON ex.id_etudiant = e.id_etudiant
JOIN matiere  m ON ex.id_matiere  = m.id_matiere
JOIN correcteur c ON n.id_correcteur = c.id_correcteur
ORDER BY e.nom, m.nom_matiere, c.id_correcteur;

-- Calculer les écarts et proposer la résolution (version simple)
SELECT 
    e.nom AS Candidat,
    m.nom_matiere AS Matiere,
    ROUND(MAX(n.valeur_note) - MIN(n.valeur_note), 2) AS ecart,
    MIN(n.valeur_note) AS min_note,
    MAX(n.valeur_note) AS max_note,
    ROUND(AVG(n.valeur_note), 2) AS moyenne,
    CASE 
        WHEN m.nom_matiere = 'JAVA' AND (MAX(n.valeur_note) - MIN(n.valeur_note)) < 3  THEN 'Grand'
        WHEN m.nom_matiere = 'JAVA' AND (MAX(n.valeur_note) - MIN(n.valeur_note)) >= 3 THEN 'Moyenne'
        WHEN m.nom_matiere = 'PHP'  AND (MAX(n.valeur_note) - MIN(n.valeur_note)) <= 2 THEN 'Petit'
        WHEN m.nom_matiere = 'PHP'  AND (MAX(n.valeur_note) - MIN(n.valeur_note)) > 2  THEN 'Grand'
        ELSE 'Règle non définie'
    END AS Resolution_proposee
FROM note n
JOIN examen ex  ON n.id_examen = ex.id_examen
JOIN etudiant e ON ex.id_etudiant = e.id_etudiant
JOIN matiere m  ON ex.id_matiere  = m.id_matiere
GROUP BY e.id_etudiant, m.id_matiere;