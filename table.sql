CREATE DATABASE correction_examen;
use correction_examen;

-- Table des étudiants
CREATE TABLE etudiant (
    id_etudiant INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    numero_etudiant VARCHAR(50) UNIQUE NOT NULL
);

-- Table des correcteurs
CREATE TABLE correcteur (
    id_correcteur INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(100) NOT NULL,
    prenom VARCHAR(100) NOT NULL,
    email VARCHAR(200) UNIQUE
);


-- Table des matières
CREATE TABLE matiere (
    id_matiere INT PRIMARY KEY AUTO_INCREMENT,
    nom_matiere VARCHAR(100) NOT NULL,
    coefficient DECIMAL(3,2) DEFAULT 1.0
);

-- Table des résolutions (règles de décision)
CREATE TABLE resolution (
    id_resolution INT PRIMARY KEY AUTO_INCREMENT,
    nom VARCHAR(50) NOT NULL  -- 'moyenne', 'superieur', 'inferieur'
);

-- Table des opérateurs
CREATE TABLE operateur (
    id_operateur INT PRIMARY KEY AUTO_INCREMENT,
    nom_operateur VARCHAR(50) NOT NULL,  -- '=', '>', '<', '>=', '<=', 'BETWEEN'
    symbole VARCHAR(10) NOT NULL
);


-- Table des paramètres (configuration par matière)
CREATE TABLE parametre (
    id_parametre INT PRIMARY KEY AUTO_INCREMENT,
    id_matiere INT NOT NULL,
    id_resolution INT NOT NULL,
    id_operateur INT NOT NULL,
    seuil_min DECIMAL(5,2),  -- Pour les comparaisons ou borne inférieure
    seuil_max DECIMAL(5,2),  -- Pour BETWEEN ou borne supérieure
    date_application DATE NOT NULL,
    active BOOLEAN DEFAULT TRUE,
    FOREIGN KEY (id_matiere) REFERENCES matiere(id_matiere),
    FOREIGN KEY (id_resolution) REFERENCES resolution(id_resolution),
    FOREIGN KEY (id_operateur) REFERENCES operateur(id_operateur)
);

-- Table des examens (copies/feuilles)
CREATE TABLE examen (
    id_examen INT PRIMARY KEY AUTO_INCREMENT,
    id_etudiant INT NOT NULL,
    id_matiere INT NOT NULL,
    date_examen DATE NOT NULL,
    FOREIGN KEY (id_etudiant) REFERENCES etudiant(id_etudiant),
    FOREIGN KEY (id_matiere) REFERENCES matiere(id_matiere)
);


-- Table des notes (corrections)
CREATE TABLE note (
    id_note INT PRIMARY KEY AUTO_INCREMENT,
    id_examen INT NOT NULL,
    id_correcteur INT NOT NULL,
    valeur_note DECIMAL(5,2) NOT NULL,
    date_correction DATETIME NOT NULL,
    commentaire TEXT,
    FOREIGN KEY (id_examen) REFERENCES examen(id_examen),
    FOREIGN KEY (id_correcteur) REFERENCES correcteur(id_correcteur),
    UNIQUE KEY unique_correction (id_examen, id_correcteur)
);


SELECT 
    e.nom AS etudiant_nom,
    e.prenom AS etudiant_prenom,
    ex.titre AS examen,
    c.id_copie,
    GROUP_CONCAT(CONCAT(corr.note, ' (', corr2.nom, ')') SEPARATOR ' | ') AS toutes_notes,
    COUNT(corr.id_correction) AS nb_correcteurs,
    MIN(corr.note) AS note_min,
    MAX(corr.note) AS note_max,
    ROUND(AVG(corr.note), 2) AS note_moyenne,
    ROUND(MAX(corr.note) - MIN(corr.note), 2) AS ecart_notes
FROM copies c
JOIN etudiants e ON c.id_etudiant = e.id_etudiant
JOIN examens ex ON c.id_examen = ex.id_examen
JOIN corrections corr ON c.id_copie = corr.id_copie
JOIN correcteurs corr2 ON corr.id_correcteur = corr2.id_correcteur
GROUP BY c.id_copie
HAVING COUNT(corr.id_correction) > 1
ORDER BY ecart_notes DESC;



-- Vue pour calculer les notes selon différentes méthodes
CREATE VIEW notes_finales AS
SELECT 
    c.id_copie,
    e.id_etudiant,
    e.nom,
    e.prenom,
    ex.id_examen,
    ex.titre AS examen,
    COUNT(corr.id_correction) AS nb_corrections,
    MIN(corr.note) AS note_min,
    MAX(corr.note) AS note_max,
    ROUND(AVG(corr.note), 2) AS note_moyenne,
    -- Méthode 1: Prendre la moyenne
    ROUND(AVG(corr.note), 2) AS note_finale_moyenne,
    -- Méthode 2: Prendre la note la plus élevée
    MAX(corr.note) AS note_finale_max,
    -- Méthode 3: Prendre la note la plus basse
    MIN(corr.note) AS note_finale_min,
    -- Méthode 4: Prendre la médiane (pour 2 notes, moyenne des deux)
    CASE 
        WHEN COUNT(corr.id_correction) = 1 THEN AVG(corr.note)
        WHEN COUNT(corr.id_correction) = 2 THEN ROUND((MIN(corr.note) + MAX(corr.note)) / 2, 2)
        ELSE ROUND((MIN(corr.note) + MAX(corr.note)) / 2, 2) -- Version simplifiée
    END AS note_finale_mediane
FROM copies c
JOIN etudiants e ON c.id_etudiant = e.id_etudiant
JOIN examens ex ON c.id_examen = ex.id_examen
JOIN corrections corr ON c.id_copie = corr.id_correction
GROUP BY c.id_copie;

-- Table de configuration pour choisir la règle de notation
CREATE TABLE regle_notation (
    id_regle INT PRIMARY KEY AUTO_INCREMENT,
    nom_regle VARCHAR(50) NOT NULL,
    description TEXT,
    active BOOLEAN DEFAULT FALSE
);

INSERT INTO regle_notation (nom_regle, description) VALUES
('moyenne', 'Prendre la moyenne des notes'),
('max', 'Prendre la note la plus élevée'),
('min', 'Prendre la note la plus basse'),
('mediane', 'Prendre la note médiane');

-- Activer une seule règle à la fois
UPDATE regle_notation SET active = TRUE WHERE nom_regle = 'moyenne'; -- Exemple avec la moyenne
UPDATE regle_notation SET active = FALSE WHERE nom_regle != 'moyenne';

SELECT 
    nf.*,
    CASE 
        WHEN rn.nom_regle = 'moyenne' THEN nf.note_finale_moyenne
        WHEN rn.nom_regle = 'max' THEN nf.note_finale_max
        WHEN rn.nom_regle = 'min' THEN nf.note_finale_min
        WHEN rn.nom_regle = 'mediane' THEN nf.note _finale_mediane
    END AS note_retenue,
    rn.nom_regle AS regle_appliquee
FROM notes_finales nf
CROSS JOIN regle_notation rn
WHERE rn.active = TRUE;
