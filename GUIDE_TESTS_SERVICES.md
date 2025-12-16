# 🧪 Guide des Tests des Services

## 📋 Vue d'ensemble

Ce guide explique comment utiliser les classes de test créées pour les services **Patient** et **Notification**.

---

## 📁 Fichiers de Test Créés

1. **PatientServiceTest.java** : Tests pour le service Patient
   - Localisation : `src/test/java/ma/WhiteLab/service/test/PatientServiceTest.java`

2. **NotificationServiceTest.java** : Tests pour le service Notification
   - Localisation : `src/test/java/ma/WhiteLab/service/test/NotificationServiceTest.java`

---

## 🚀 Comment Exécuter les Tests

### Méthode 1 : Via l'IDE (IntelliJ IDEA)

1. Ouvrez le fichier de test (ex: `PatientServiceTest.java`)
2. Cliquez droit sur le fichier
3. Sélectionnez **"Run 'PatientServiceTest.main()'"**
4. Les résultats s'affichent dans la console

### Méthode 2 : Via la ligne de commande

```bash
# Compiler le projet
mvn compile

# Exécuter les tests Patient
java -cp "target/classes:target/test-classes:$(mvn dependency:build-classpath -q -DincludeScope=test)" ma.WhiteLab.service.test.PatientServiceTest

# Exécuter les tests Notification
java -cp "target/classes:target/test-classes:$(mvn dependency:build-classpath -q -DincludeScope=test)" ma.WhiteLab.service.test.NotificationServiceTest
```

---

## 📊 Scénarios de Test - Service Patient

### ✅ Scénarios de Succès

#### 1. **Enregistrer un nouveau patient**
- **Objectif** : Vérifier qu'on peut enregistrer un patient avec des données valides
- **Données** : Patient complet (nom, prénom, sexe, assurance, email, téléphone, etc.)
- **Résultat attendu** : Patient enregistré avec un ID généré

#### 2. **Modifier un patient existant**
- **Objectif** : Vérifier qu'on peut modifier les informations d'un patient
- **Données** : Patient existant avec nouvelles valeurs
- **Résultat attendu** : Patient modifié avec date de mise à jour

#### 3. **Rechercher des patients**
- **Objectif** : Vérifier les différentes méthodes de recherche
- **Méthodes testées** :
  - Recherche par mot-clé (nom/prénom)
  - Recherche par email
  - Recherche par téléphone
- **Résultat attendu** : Liste de patients correspondants

#### 4. **Consulter un patient**
- **Objectif** : Vérifier qu'on peut récupérer un patient par son ID
- **Résultat attendu** : Patient complet avec toutes ses informations

#### 5. **Compter les patients**
- **Objectif** : Vérifier le comptage du nombre total de patients
- **Résultat attendu** : Nombre exact de patients en base

#### 6. **Gérer les antécédents**
- **Objectif** : Vérifier la gestion des antécédents médicaux
- **Actions testées** :
  - Créer des antécédents
  - Ajouter des antécédents à un patient
  - Consulter les antécédents d'un patient
  - Retirer un antécédent d'un patient
- **Résultat attendu** : Antécédents correctement liés au patient

#### 7. **Supprimer un patient**
- **Objectif** : Vérifier qu'on peut supprimer un patient
- **Résultat attendu** : Patient supprimé de la base de données

### ❌ Scénarios d'Erreur (Validation)

#### 1. **Patient avec données invalides**
- **Objectif** : Vérifier que la validation fonctionne
- **Données** : Patient sans nom (obligatoire)
- **Résultat attendu** : `ValidationException` levée

#### 2. **Patient avec email existant**
- **Objectif** : Vérifier l'unicité de l'email
- **Données** : Patient avec un email déjà utilisé
- **Résultat attendu** : `ValidationException` levée

#### 3. **Modifier un patient inexistant**
- **Objectif** : Vérifier la gestion des erreurs
- **Données** : Patient avec ID inexistant
- **Résultat attendu** : `ServiceException` levée

#### 4. **Supprimer un patient inexistant**
- **Objectif** : Vérifier la gestion des erreurs
- **Données** : ID de patient inexistant
- **Résultat attendu** : `ServiceException` levée

---

## 📊 Scénarios de Test - Service Notification

### ✅ Scénarios de Succès

#### 1. **Créer une notification**
- **Objectif** : Vérifier qu'on peut créer une notification
- **Données** : Notification complète (titre, message, type, priorité, etc.)
- **Résultat attendu** : Notification créée avec un ID généré

#### 2. **Créer une notification rapidement**
- **Objectif** : Tester la méthode de convenance
- **Méthode** : `creerNotification(titre, message, type, priorite, creePar)`
- **Résultat attendu** : Notification créée sans créer l'objet manuellement

#### 3. **Modifier une notification**
- **Objectif** : Vérifier qu'on peut modifier une notification
- **Résultat attendu** : Notification modifiée avec nouvelles valeurs

#### 4. **Consulter des notifications**
- **Objectif** : Vérifier les différentes méthodes de consultation
- **Méthodes testées** :
  - Consulter par ID
  - Consulter par type
  - Consulter par priorité
  - Consulter par titre
- **Résultat attendu** : Notifications correspondantes

#### 5. **Compter les notifications**
- **Objectif** : Vérifier le comptage
- **Résultat attendu** : Nombre exact de notifications

#### 6. **Envoyer une notification à un utilisateur**
- **Objectif** : Vérifier l'envoi et la liaison notification-utilisateur
- **Actions** :
  - Créer une notification
  - Lier la notification à un utilisateur
- **Résultat attendu** : Notification créée et liée à l'utilisateur

#### 7. **Consulter les notifications d'un utilisateur**
- **Objectif** : Vérifier qu'on peut récupérer les notifications d'un utilisateur
- **Résultat attendu** : Liste des notifications de l'utilisateur

#### 8. **Retirer une notification d'un utilisateur**
- **Objectif** : Vérifier qu'on peut retirer une notification
- **Résultat attendu** : Notification retirée (mais reste dans le système)

#### 9. **Supprimer une notification**
- **Objectif** : Vérifier qu'on peut supprimer une notification
- **Résultat attendu** : Notification supprimée de la base

### ❌ Scénarios d'Erreur (Validation)

#### 1. **Notification avec données invalides**
- **Objectif** : Vérifier la validation
- **Données** : Notification sans message (obligatoire)
- **Résultat attendu** : `ValidationException` levée

#### 2. **Modifier une notification inexistante**
- **Objectif** : Vérifier la gestion des erreurs
- **Données** : Notification avec ID inexistant
- **Résultat attendu** : `ServiceException` levée

#### 3. **Supprimer une notification inexistante**
- **Objectif** : Vérifier la gestion des erreurs
- **Données** : ID de notification inexistant
- **Résultat attendu** : `ServiceException` levée

---

## 📝 Structure d'un Scénario de Test

Chaque scénario suit cette structure :

```java
/**
 * SCÉNARIO X : Description du scénario
 */
private static void scenarioX_NomDuScenario() {
    System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    System.out.println("SCÉNARIO X : Description");
    System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

    try {
        // 1. Préparer les données
        // 2. Exécuter l'action
        // 3. Vérifier le résultat
        System.out.println("✅ SUCCÈS : ...");

    } catch (ValidationException e) {
        // Gérer les erreurs de validation
        System.out.println("✅ SUCCÈS : ValidationException correctement levée");
    } catch (ServiceException e) {
        // Gérer les erreurs de service
        System.out.println("✅ SUCCÈS : ServiceException correctement levée");
    }

    System.out.println();
}
```

---

## 🎯 Points Importants

### 1. **Initialisation**

Les tests initialisent automatiquement :
- Les repositories via `ApplicationContext`
- Les services avec injection de dépendance
- Les données de test nécessaires

### 2. **Isolation des Tests**

⚠️ **Attention** : Les tests utilisent la **vraie base de données**. 
- Les données créées restent en base après les tests
- Pour des tests isolés, il faudrait utiliser une base de test ou des mocks

### 3. **Ordre d'Exécution**

Les scénarios s'exécutent dans un ordre logique :
1. Création d'entités
2. Consultation
3. Modification
4. Suppression

### 4. **Gestion des Erreurs**

Les tests vérifient que :
- Les exceptions sont correctement levées
- Les messages d'erreur sont pertinents
- Les validations fonctionnent

---

## 📊 Résultats Attendus

### Exemple de Sortie Console

```
==========================================
  TESTS DU SERVICE PATIENT
==========================================

📋 Initialisation des repositories et services...

✅ Initialisation terminée

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
SCÉNARIO 1 : Enregistrer un nouveau patient
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📝 Tentative d'enregistrement du patient : Alami Fatima
✅ SUCCÈS : Patient enregistré avec l'ID : 1
   - Nom : Alami
   - Prénom : Fatima
   - Email : fatima.alami@email.com
   - Date de création : 2025-01-15T10:30:00

━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
SCÉNARIO 2 : Patient avec données invalides
━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━
📝 Tentative d'enregistrement d'un patient sans nom...
✅ SUCCÈS : ValidationException correctement levée
   Message : Le nom est obligatoire

...
```

---

## 🔧 Personnalisation des Tests

### Ajouter un Nouveau Scénario

1. Créer une nouvelle méthode `scenarioX_NomDuScenario()`
2. Suivre la structure standard
3. L'appeler dans la méthode `main()`

Exemple :

```java
private static void scenario16_NouveauScenario() {
    System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    System.out.println("SCÉNARIO 16 : Nouveau scénario");
    System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

    try {
        // Votre code de test ici
        System.out.println("✅ SUCCÈS : ...");
    } catch (Exception e) {
        System.err.println("❌ ERREUR : " + e.getMessage());
    }

    System.out.println();
}
```

Puis dans `main()` :

```java
scenario16_NouveauScenario();
```

---

## ⚠️ Prérequis

Avant d'exécuter les tests, assurez-vous que :

1. ✅ La base de données MySQL est démarrée
2. ✅ Le schéma de base de données est créé (`schema.sql`)
3. ✅ Le fichier `db.properties` est correctement configuré
4. ✅ Les repositories sont correctement configurés dans `beans.properties`
5. ✅ Pour les tests de notification avec utilisateur : au moins un utilisateur existe en base

---

## 🐛 Dépannage

### Erreur : "Connection refused"
- **Cause** : MySQL n'est pas démarré ou mauvais paramètres dans `db.properties`
- **Solution** : Vérifier la connexion à la base de données

### Erreur : "Table doesn't exist"
- **Cause** : Le schéma de base de données n'est pas créé
- **Solution** : Exécuter le script `schema.sql`

### Erreur : "Bean not found"
- **Cause** : Repository non configuré dans `beans.properties`
- **Solution** : Vérifier la configuration des beans

### Tests de notification avec utilisateur échouent
- **Cause** : Aucun utilisateur en base
- **Solution** : Créer d'abord un utilisateur via les tests de repository ou `seed.sql`

---

## 📚 Résumé

Les tests créés permettent de :

✅ **Vérifier** que les services fonctionnent correctement  
✅ **Tester** les cas de succès (création, modification, consultation, suppression)  
✅ **Valider** la gestion des erreurs (exceptions, validations)  
✅ **Documenter** l'utilisation des services avec des exemples concrets  
✅ **Détecter** les bugs potentiels avant l'intégration dans l'application  

**Les tests sont prêts à être exécutés !** 🎉

