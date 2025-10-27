# DentalTech - Gestion de Cabinet Dentaire

## Description

`DentalTech` est une application de bureau complète pour la gestion d'un cabinet dentaire. Elle a été conçue pour simplifier les tâches administratives et médicales quotidiennes, permettant aux professionnels de la santé de se concentrer sur les soins aux patients.

L'application couvre les aspects suivants :
-   Gestion des dossiers patients
-   Prise de rendez-vous et gestion de l'agenda
-   Suivi des consultations et des actes médicaux
-   Facturation et suivi des paiements
-   Gestion du personnel
-   Statistiques et rapports d'activité

## Objectifs Pédagogiques

Ce projet a également été développé dans un but pédagogique pour illustrer plusieurs concepts clés de l'ingénierie logicielle avec Java :
-   Architecture en couches (MVC)
-   Injection de dépendances manuelle
-   Utilisation de Maven pour la gestion de projet
-   Configuration d'un build reproductible
-   Génération d'un JAR exécutable ("fat-jar")
-   Intégration de tests unitaires avec JUnit

## Technologies Utilisées

-   **Langage** : Java 23
-   **Gestion de projet** : Apache Maven
-   **Base de données** : MySQL (via connecteur JDBC)
-   **Utilitaires** :
    -   `Lombok` : Pour réduire le code boilerplate (getters, setters, etc.).
    -   `jBCrypt` : Pour le hachage sécurisé des mots de passe.
-   **Tests** : JUnit

## Structure du Projet

Le projet est organisé en plusieurs modules pour une meilleure séparation des préoccupations :

-   `conf` : Configuration de l'application, y compris le contexte d'injection de dépendances.
-   `entities` : Modèles de données (Patient, Rendez-vous, etc.).
-   `repository` : Couche d'accès aux données (Data Access Layer).
-   `service` : Couche de logique métier (Business Logic Layer).
-   `mvc` : Couche de présentation (Model-View-Controller), contenant les contrôleurs, les DTOs et les vues (UI).

## Comment Lancer l'Application

1.  **Prérequis** :
    -   JDK 23 ou supérieur.
    -   Apache Maven.
    -   Une base de données MySQL configurée (les scripts de création de la base se trouvent dans `src/main/resources/dataBase`).

2.  **Compiler le projet** :
    Ouvrez un terminal à la racine du projet et exécutez la commande Maven suivante pour compiler le code et créer un JAR exécutable :
    ```bash
    mvn clean package
    ```

3.  **Exécuter l'application** :
    Une fois la compilation terminée, vous trouverez le JAR exécutable dans le dossier `target/`. Exécutez-le avec la commande suivante :
    ```bash
    java -jar target/DentalTech-1.0-SNAPSHOT.jar
    ```

