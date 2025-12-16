-- Base de données WhiteLabDB
CREATE DATABASE IF NOT EXISTS WhiteLabDB;
USE WhiteLabDB;

-- =========================
-- Table Acte_Medical
-- =========================
CREATE TABLE IF NOT EXISTS Acte_Medical (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dateCreation DATETIME,
    dateMiseAJour DATETIME,
    creePar VARCHAR(255),
    modifierPar VARCHAR(255),
    libelle VARCHAR(255) NOT NULL,
    categorie VARCHAR(100),
    prixDeBase FLOAT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- Table CabinetMedicale
-- =========================
CREATE TABLE IF NOT EXISTS CabinetMedicale (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dateCreation DATETIME,
    dateMiseAJour DATETIME,
    creePar VARCHAR(255),
    modifierPar VARCHAR(255),
    nom VARCHAR(255) NOT NULL,
    email VARCHAR(255),
    logo VARCHAR(255),
    categorie VARCHAR(100),
    tel1 VARCHAR(50),
    tel2 VARCHAR(50),
    siteWeb VARCHAR(255),
    instagram VARCHAR(255),
    facebook VARCHAR(255),
    description TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- Table Utilisateur
-- =========================
CREATE TABLE IF NOT EXISTS Utilisateur (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dateCreation DATETIME,
    dateMiseAJour DATETIME,
    creePar VARCHAR(255),
    modifierPar VARCHAR(255),
    nom VARCHAR(255),
    prenom VARCHAR(255),
    email VARCHAR(255),
    adresse VARCHAR(255),
    cin VARCHAR(50),
    telephone VARCHAR(50),
    dateNaissance DATE,
    sexe VARCHAR(10),
    lastLoginDate DATETIME,
    motDePasse VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- Table Patient
-- =========================
CREATE TABLE IF NOT EXISTS Patient (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dateCreation DATETIME,
    dateMiseAJour DATETIME,
    creePar VARCHAR(255),
    modifierPar VARCHAR(255),
    nom VARCHAR(255) NOT NULL,
    prenom VARCHAR(255),
    sexe VARCHAR(10),
    email VARCHAR(255),
    dateNaissance DATE,
    adresse VARCHAR(255),
    telephone VARCHAR(50),
    assurance VARCHAR(50)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- Tables héritage/liaison basées sur Utilisateur / Patient / Cabinet
-- =========================
CREATE TABLE IF NOT EXISTS Admin (
    id BIGINT PRIMARY KEY,
    FOREIGN KEY (id) REFERENCES Utilisateur(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Staff (
    id BIGINT PRIMARY KEY,
    salaire DOUBLE,
    prime DOUBLE,
    dateRecrutement DATE,
    soldeConge INT,
    cabinetMedicale_id BIGINT,
    FOREIGN KEY (id) REFERENCES Utilisateur(id) ON DELETE CASCADE,
    FOREIGN KEY (cabinetMedicale_id) REFERENCES CabinetMedicale(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Medecin (
    id BIGINT PRIMARY KEY,
    specialite VARCHAR(255),
    FOREIGN KEY (id) REFERENCES Staff(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Secretaire (
    id BIGINT PRIMARY KEY,
    numCNS VARCHAR(50),
    commission DOUBLE,
    FOREIGN KEY (id) REFERENCES Staff(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- Table DossierMedical
-- =========================
CREATE TABLE IF NOT EXISTS DossierMedical (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dateCreation DATETIME,
    dateMiseAJour DATETIME,
    creePar VARCHAR(255),
    modifierPar VARCHAR(255),
    historique TEXT,
    pat_id BIGINT,
    medcine_id BIGINT,
    CONSTRAINT fk_dossier_patient FOREIGN KEY (pat_id) REFERENCES Patient(id) ON DELETE SET NULL,
    CONSTRAINT fk_dossier_medecin FOREIGN KEY (medcine_id) REFERENCES Utilisateur(id) ON DELETE SET NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- Antecedent & liaison Patient <-> Antecedent
-- =========================
CREATE TABLE IF NOT EXISTS Antecedent (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    nom VARCHAR(255) NOT NULL,
    description TEXT,
    categorie VARCHAR(50),
    niveauRisque VARCHAR(50),
    dateCreation DATETIME,
    dateMiseAJour DATETIME,
    creePar VARCHAR(255),
    modifierPar VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS Patient_Antecedent (
    patient_id BIGINT NOT NULL,
    antecedent_id BIGINT NOT NULL,
    PRIMARY KEY (patient_id, antecedent_id),
    CONSTRAINT fk_pa_patient FOREIGN KEY (patient_id) REFERENCES Patient(id) ON DELETE CASCADE,
    CONSTRAINT fk_pa_antecedent FOREIGN KEY (antecedent_id) REFERENCES Antecedent(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- Table Consultation
-- (doit venir après DossierMedical si Consultation référence Dossier)
-- =========================
CREATE TABLE IF NOT EXISTS Consultation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dateCreation DATETIME DEFAULT CURRENT_TIMESTAMP,
    dateMiseAJour DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    creePar VARCHAR(255),
    modifierPar VARCHAR(255),
    date DATETIME NOT NULL,
    status VARCHAR(50) NOT NULL,
    notes TEXT,
    observations_medecin TEXT,
    dossier_medical_id BIGINT,
    CONSTRAINT fk_consultation_dossier FOREIGN KEY (dossier_medical_id) REFERENCES DossierMedical(id) ON DELETE SET NULL ON UPDATE CASCADE,
    INDEX idx_dossier_medical (dossier_medical_id),
    INDEX idx_date (date),
    INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- InterventionMedecin (référence Consultation et Acte_Medical)
-- =========================
CREATE TABLE IF NOT EXISTS InterventionMedecin (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dateCreation DATETIME,
    dateMiseAJour DATETIME,
    creePar VARCHAR(255),
    modifierPar VARCHAR(255),
    prixDePatient DOUBLE,
    numDent INT,
    consultation_id BIGINT,
    acteMedical_id BIGINT,
    FOREIGN KEY (consultation_id) REFERENCES Consultation(id) ON DELETE CASCADE,
    FOREIGN KEY (acteMedical_id) REFERENCES Acte_Medical(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- Statistiques
-- =========================
CREATE TABLE IF NOT EXISTS Statistiques (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dateCreation DATETIME,
    dateMiseAJour DATETIME,
    creePar VARCHAR(255),
    modifierPar VARCHAR(255),
    nom VARCHAR(255) NOT NULL,
    categorie VARCHAR(50),
    chiffre DOUBLE,
    dateCalcul DATE,
    cabinet_id BIGINT,
    FOREIGN KEY (cabinet_id) REFERENCES CabinetMedicale(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- Certificat
-- =========================
CREATE TABLE IF NOT EXISTS Certificat (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dateCreation DATETIME,
    dateMiseAJour DATETIME,
    creePar VARCHAR(255),
    modifierPar VARCHAR(255),
    dateDebut DATE,
    dateFin DATE,
    dureeRepos INT,
    contenu TEXT,
    consultation_id BIGINT,
    dossier_med_id BIGINT,
    FOREIGN KEY (consultation_id) REFERENCES Consultation(id) ON DELETE CASCADE,
    FOREIGN KEY (dossier_med_id) REFERENCES DossierMedical(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- AgendaMensuel & AgendaMensuel_Jour
-- =========================
CREATE TABLE IF NOT EXISTS AgendaMensuel (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dateCreation DATETIME DEFAULT CURRENT_TIMESTAMP,
    dateMiseAJour DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    creePar VARCHAR(255),
    modifierPar VARCHAR(255),
    mois VARCHAR(20),
    medecin_id BIGINT,
    CONSTRAINT fk_agenda_medecin FOREIGN KEY (medecin_id) REFERENCES Utilisateur(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

CREATE TABLE IF NOT EXISTS AgendaMensuel_Jour (
    agenda_id BIGINT NOT NULL,
    jour_nom VARCHAR(20) NOT NULL,
    PRIMARY KEY (agenda_id, jour_nom),
    CONSTRAINT fk_agenda_jours FOREIGN KEY (agenda_id) REFERENCES AgendaMensuel(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- RendezVous
-- =========================
CREATE TABLE IF NOT EXISTS RendezVous (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dateCreation DATETIME,
    dateMiseAJour DATETIME,
    creePar VARCHAR(255),
    modifierPar VARCHAR(255),
    dateRDV DATETIME,
    heure_rdv TIME,
    motif VARCHAR(255),
    status VARCHAR(50),
    note_medecin TEXT,
    dossier_med_id BIGINT,
    consultation_id BIGINT,
    FOREIGN KEY (dossier_med_id) REFERENCES DossierMedical(id) ON DELETE CASCADE,
    FOREIGN KEY (consultation_id) REFERENCES Consultation(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- Medicament
-- =========================
CREATE TABLE IF NOT EXISTS Medicament (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dateCreation DATETIME,
    dateMiseAJour DATETIME,
    creePar VARCHAR(255),
    modifierPar VARCHAR(255),
    nom VARCHAR(255),
    labo VARCHAR(255),
    type VARCHAR(50),
    forme VARCHAR(50),
    remboursable BOOLEAN,
    prix_unitaire DOUBLE,
    description TEXT
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- Ordonnance
-- =========================
CREATE TABLE IF NOT EXISTS Ordonnance (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dateCreation DATETIME,
    dateMiseAJour DATETIME,
    creePar VARCHAR(255),
    modifierPar VARCHAR(255),
    date_ordonnance DATE,
    consultation_id BIGINT,
    dossier_med_id BIGINT,
    FOREIGN KEY (consultation_id) REFERENCES Consultation(id) ON DELETE CASCADE,
    FOREIGN KEY (dossier_med_id) REFERENCES DossierMedical(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- Prescription
-- =========================
CREATE TABLE IF NOT EXISTS Prescription (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    dateCreation DATETIME,
    dateMiseAJour DATETIME,
    creePar VARCHAR(255),
    modifierPar VARCHAR(255),
    qte INT,
    frequence VARCHAR(50),
    duree INT,
    ordonnance_id BIGINT,
    medicament_id BIGINT,
    FOREIGN KEY (ordonnance_id) REFERENCES Ordonnance(id) ON DELETE CASCADE,
    FOREIGN KEY (medicament_id) REFERENCES Medicament(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- Role
-- =========================
CREATE TABLE IF NOT EXISTS Role (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    libelle VARCHAR(50) NOT NULL,
    privileges TEXT,
    dateCreation DATETIME DEFAULT CURRENT_TIMESTAMP,
    dateMiseAJour DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    creePar VARCHAR(255),
    modifierPar VARCHAR(255)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- Notification
-- =========================
CREATE TABLE IF NOT EXISTS Notification (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    titre VARCHAR(100) NOT NULL,
    message TEXT NOT NULL,
    date DATE,
    time TIME,
    type VARCHAR(50) NOT NULL,
    priorite VARCHAR(50) NOT NULL,
    dateCreation DATETIME DEFAULT CURRENT_TIMESTAMP,
    dateMiseAJour DATETIME,
    creePar VARCHAR(100),
    modifierPar VARCHAR(100)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- Liaison Utilisateur <-> Role
-- =========================
CREATE TABLE IF NOT EXISTS Utilisateur_Role (
    utilisateur_id BIGINT,
    role_id BIGINT,
    PRIMARY KEY(utilisateur_id, role_id),
    FOREIGN KEY (utilisateur_id) REFERENCES Utilisateur(id) ON DELETE CASCADE,
    FOREIGN KEY (role_id) REFERENCES Role(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- =========================
-- Liaison Utilisateur <-> Notification
-- =========================
CREATE TABLE IF NOT EXISTS Utilisateur_Notification (
    utilisateur_id BIGINT,
    notification_id BIGINT,
    PRIMARY KEY(utilisateur_id, notification_id),
    FOREIGN KEY (utilisateur_id) REFERENCES Utilisateur(id) ON DELETE CASCADE,
    FOREIGN KEY (notification_id) REFERENCES Notification(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE : charges
-- ============================================================
CREATE TABLE IF NOT EXISTS charges (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    titre VARCHAR(255),
    description TEXT,
    montant DOUBLE,
    date DATETIME,
    cabinet_id BIGINT,
    dateCreation DATETIME,
    dateMiseAJour DATETIME,
    creePar VARCHAR(255),
    modifierPar VARCHAR(255),
    CONSTRAINT fk_charges_cabinet FOREIGN KEY (cabinet_id) REFERENCES CabinetMedicale(id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE : revenus
-- ============================================================
CREATE TABLE IF NOT EXISTS revenus (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    titre VARCHAR(255),
    description TEXT,
    montant DOUBLE,
    date DATETIME,
    cabinet_id BIGINT,
    dateCreation DATETIME,
    dateMiseAJour DATETIME,
    creePar VARCHAR(255),
    modifierPar VARCHAR(255),
    CONSTRAINT fk_revenus_cabinet FOREIGN KEY (cabinet_id) REFERENCES CabinetMedicale(id) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE : SituationFinanciere (suivi_financier)
-- ============================================================
CREATE TABLE IF NOT EXISTS SituationFinanciere (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    totalDesActes FLOAT,
    totalPaye FLOAT,
    credit FLOAT,
    enPromo VARCHAR(50),
    status VARCHAR(50),
    dossierMedical_id BIGINT,
    dateCreation DATETIME,
    dateMiseAJour DATETIME,
    creePar VARCHAR(255),
    modifierPar VARCHAR(255),
    CONSTRAINT fk_situation_dossier FOREIGN KEY (dossierMedical_id) REFERENCES DossierMedical(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- ============================================================
-- TABLE : facture
-- ============================================================
CREATE TABLE IF NOT EXISTS facture (
    id BIGINT PRIMARY KEY AUTO_INCREMENT,
    totalFact FLOAT,
    totalPaye FLOAT,
    reste FLOAT,
    date DATE,
    statut VARCHAR(50),
    consultation_id BIGINT,
    sf_id BIGINT,
    dateCreation DATETIME,
    dateMiseAJour DATETIME,
    creePar VARCHAR(255),
    modifierPar VARCHAR(255),
    CONSTRAINT fk_facture_consultation FOREIGN KEY (consultation_id) REFERENCES Consultation(id) ON DELETE SET NULL,
    CONSTRAINT fk_facture_situation_financiere FOREIGN KEY (sf_id) REFERENCES SituationFinanciere(id) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;
