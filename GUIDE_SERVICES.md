# Guide de Création des Services et des Tests

Ce guide a pour but de détailler la structure et la logique derrière la création des services et de leurs tests unitaires dans l'application WhiteLab.

## 1. Services

L'architecture des services est basée sur le principe d'inversion de contrôle (IoC) et d'injection de dépendances (DI). Chaque module de service est séparé en une interface (API) et une implémentation.

-   **Interfaces (API)** : Situées dans les packages `api`, elles définissent le contrat du service, c'est-à-dire les méthodes publiques qui seront exposées au reste de l'application (contrôleurs, autres services, etc.).
-   **Implémentations** : Situées dans les packages `impl`, elles contiennent la logique métier concrète pour chaque méthode définie dans l'interface. Elles interagissent avec les *repositories* pour accéder à la base de données.

### 1.1. Services du dossier `patient`

Ce module gère toute la logique liée aux patients et à leurs antécédents.

-   `AntecedentService` :
    -   **Objectif** : Gérer les opérations CRUD (Créer, Lire, Mettre à jour, Supprimer) pour les antécédents médicaux (ex: Asthme, Diabète).
    -   **Méthodes clés** : `enregistrerAntecedent`, `modifierAntecedent`, `supprimerAntecedent`, `rechercherAntecedentParNom`.
    -   **Logique de validation** : S'assure qu'un antécédent a un nom et que ce nom est unique avant de l'enregistrer.

-   `PatientService` :
    -   **Objectif** : Gérer les informations de base des patients.
    -   **Méthodes clés** : `enregistrerNouveauPatient`, `mettreAJourInfosPatient`, `rechercherPatientParId`, `listerTousLesPatients`.
    -   **Logique de validation** : Valide les informations essentielles comme le nom, le prénom, la date de naissance, etc.

-   `PatientRechercheAvanceeService` :
    -   **Objectif** : Fournir des fonctionnalités de recherche complexes pour les patients, basées sur plusieurs critères (âge, sexe, antécédents, etc.).
    -   **Méthodes clés** : `rechercherPatients(criteres)`.

-   `PatientStatistiquesService` :
    -   **Objectif** : Calculer et fournir des statistiques sur les patients (ex: répartition par âge, par sexe).
    -   **Méthodes clés** : `getStatistiquesAge()`, `getStatistiquesSexe()`.

### 1.2. Services du dossier `notifications`

Ce module est responsable de la gestion des notifications pour les utilisateurs de l'application.

-   `NotificationService` :
    -   **Objectif** : Créer, lire et gérer le cycle de vie des notifications.
    -   **Méthodes clés** : `creerNotification`, `marquerCommeLu`, `getNotificationsPourUtilisateur`, `supprimerNotification`.
    -   **Logique** : Associe les notifications à des utilisateurs spécifiques et gère leur état (lu/non lu).

### 1.3. Services du dossier `dossierMedicale`

Ce module gère les aspects médicaux du dossier d'un patient, comme les consultations et les certificats.

-   `ConsultationService` :
    -   **Objectif** : Gérer les consultations médicales.
    -   **Méthodes clés** : `planifierConsultation`, `demarrerConsultation`, `terminerConsultation`, `annulerConsultation`, `getConsultationParId`.
    -   **Logique de validation** : S'assure qu'une consultation est liée à un patient et à un médecin, et gère les statuts (planifiée, en cours, terminée, annulée).

-   `CertificatService` :
    -   **Objectif** : Créer et gérer les certificats médicaux générés lors d'une consultation.
    -   **Méthodes clés** : `genererCertificat`, `getCertificatParId`, `listerCertificatsParPatient`.
    -   **Logique de validation** : Vérifie qu'un certificat est bien associé à une consultation existante.

## 2. Tests des Services (`service/test`)

Les tests unitaires pour les services sont cruciaux pour garantir que la logique métier fonctionne comme prévu, qu'elle gère correctement les cas d'erreur et qu'elle est robuste.

### Approche Générale des Tests

1.  **Initialisation** : Avant chaque scénario de test, on initialise les *repositories* et les services nécessaires. Souvent, on utilise des *mocks* (simulacres) pour les dépendances (comme les repositories) afin d'isoler le service testé. Cependant, dans ce projet, les tests interagissent avec une base de données de test réelle pour garantir une intégration correcte.
2.  **Préparation des données** (`preparerDonneesDeTest`) : On insère des données de test dans la base de données (ex: un patient, un médecin) pour créer un contexte de test réaliste.
3.  **Exécution des scénarios** : Chaque méthode publique du service est testée à travers plusieurs scénarios :
    -   **Cas nominal** : Le fonctionnement attendu (ex: enregistrer un patient avec des données valides).
    -   **Cas d'erreur** : Comportement en cas de données invalides (ex: enregistrer un patient sans nom). On vérifie que les bonnes exceptions (`ValidationException`, `ServiceException`) sont levées.
    -   **Cas limites** : Comportement avec des données "étranges" (ex: chaînes vides, `null`).
4.  **Nettoyage** (`nettoyerDonneesDeTest`) : Après l'exécution des tests, toutes les données créées sont supprimées de la base de données pour ne pas polluer les tests suivants.

### Exemples de fichiers de test

-   `AntecedentServiceTest.java` :
    -   **Scénario 1** : Teste l'enregistrement réussi d'un nouvel antécédent.
    -   **Scénario 2** : Vérifie qu'une exception est levée si on essaie d'enregistrer un antécédent avec un nom `null` ou vide.
    -   **Scénario 3** : Vérifie qu'il est impossible d'ajouter un antécédent qui existe déjà.
    -   **Scénario 4 & 5** : Teste la modification et la suppression d'un antécédent.

-   `CertificatServiceTest.java` :
    -   **Pré-requis** : Ce test est plus complexe car la création d'un certificat dépend de l'existence d'un patient, d'un médecin et d'une consultation. La méthode `preparerDonneesDeTest` est donc plus longue.
    -   **Scénario 1** : Teste la génération réussie d'un certificat pour une consultation valide.
    -   **Scénario 2** : Vérifie qu'une exception est levée si on essaie de créer un certificat sans description.
    -   **Scénario 3** : Tente de générer un certificat pour une consultation qui n'existe pas.

-   `ConsultationServiceTest.java` :
    -   **Scénarios** : Teste la création, la mise à jour du statut (en cours, terminée), et l'annulation d'une consultation, en vérifiant que les contraintes (patient et médecin existants) sont respectées.
