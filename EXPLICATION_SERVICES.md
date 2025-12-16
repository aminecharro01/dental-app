# 📚 Explication Détaillée des Services Créés

## 🎯 Objectif
J'ai créé les services métier pour les modules **Patient** et **Notification** avec leurs interfaces et implémentations.

---

## 📁 Structure des Fichiers Créés

### Module Patient
1. **Interface** : `src/main/java/ma/WhiteLab/service/modules/patient/api/PatientService.java`
2. **Implémentation** : `src/main/java/ma/WhiteLab/service/modules/patient/impl/PatientServiceImpl.java`

### Module Notification
1. **Interface** : `src/main/java/ma/WhiteLab/service/modules/notifications/api/NotificationService.java`
2. **Implémentation** : `src/main/java/ma/WhiteLab/service/modules/notifications/impl/NotificationServiceImpl.java`

---

## 🔍 Explication Détaillée du Service Patient

### 1. **Interface PatientService**

L'interface définit toutes les opérations métier qu'on peut faire sur les patients. Les méthodes sont nommées de manière **verbale** pour être claires :

#### Méthodes de Gestion des Patients :
- `enregistrerPatient(Patient)` : Enregistre un nouveau patient
- `modifierPatient(Patient)` : Modifie les informations d'un patient existant
- `supprimerPatient(Long)` : Supprime un patient
- `consulterPatient(Long)` : Récupère un patient par son ID
- `listerTousLesPatients()` : Récupère tous les patients
- `rechercherPatient(String)` : Recherche par nom/prénom
- `rechercherPatientParEmail(String)` : Recherche par email
- `rechercherPatientParTelephone(String)` : Recherche par téléphone
- `patientExiste(Long)` : Vérifie si un patient existe
- `compterPatients()` : Compte le nombre de patients

#### Méthodes de Gestion des Antécédents :
- `ajouterAntecedent(Long, Long)` : Ajoute un antécédent à un patient
- `retirerAntecedent(Long, Long)` : Retire un antécédent d'un patient
- `consulterAntecedentsPatient(Long)` : Récupère tous les antécédents d'un patient
- `retirerTousLesAntecedents(Long)` : Retire tous les antécédents d'un patient

### 2. **Implémentation PatientServiceImpl**

#### Comment ça fonctionne ?

**a) Injection de Dépendance :**
```java
private final PatientRepository patientRepository;

public PatientServiceImpl(PatientRepository patientRepository) {
    this.patientRepository = patientRepository;
}
```
- Le service reçoit le repository en paramètre (injection de dépendance)
- Cela permet de tester facilement le service avec un mock repository

**b) Méthode `enregistrerPatient` - Exemple détaillé :**

```java
public Patient enregistrerPatient(Patient patient) throws ValidationException, ServiceException {
    // 1. Vérifier que le patient n'est pas null
    if (patient == null) {
        throw new ValidationException("Le patient ne peut pas être null");
    }

    // 2. Valider les données obligatoires
    validerDonneesPatient(patient);

    // 3. Vérifier que l'email n'est pas déjà utilisé
    if (patient.getEmail() != null && !patient.getEmail().trim().isEmpty()) {
        Patient existant = rechercherPatientParEmail(patient.getEmail());
        if (existant != null) {
            throw new ValidationException("Un patient avec cet email existe déjà");
        }
    }

    // 4. Vérifier que le téléphone n'est pas déjà utilisé
    if (patient.getTelephone() != null && !patient.getTelephone().trim().isEmpty()) {
        Patient existant = rechercherPatientParTelephone(patient.getTelephone());
        if (existant != null) {
            throw new ValidationException("Un patient avec ce téléphone existe déjà");
        }
    }

    // 5. Définir la date de création si elle n'est pas définie
    if (patient.getDateCreation() == null) {
        patient.setDateCreation(LocalDateTime.now());
    }

    // 6. Enregistrer le patient dans la base de données
    try {
        patientRepository.create(patient);
        return patient;
    } catch (Exception e) {
        throw new ServiceException("Erreur lors de l'enregistrement du patient : " + e.getMessage(), e);
    }
}
```

**Étapes expliquées :**
1. **Vérification null** : On vérifie que le patient n'est pas null
2. **Validation** : On appelle `validerDonneesPatient()` qui vérifie :
   - Le nom n'est pas vide
   - Le prénom n'est pas vide
   - Le sexe est défini
   - L'assurance est définie
   - L'email est valide (format)
   - Le téléphone est valide (format)
3. **Vérification unicité** : On vérifie que l'email et le téléphone ne sont pas déjà utilisés
4. **Date de création** : On définit automatiquement la date de création si elle n'est pas définie
5. **Enregistrement** : On appelle le repository pour enregistrer en base
6. **Gestion d'erreur** : Si une erreur survient, on lance une `ServiceException`

**c) Méthode privée `validerDonneesPatient` :**

```java
private void validerDonneesPatient(Patient patient) throws ValidationException {
    // Le nom est obligatoire
    Validators.notBlank(patient.getNom(), "Le nom");

    // Le prénom est obligatoire
    Validators.notBlank(patient.getPrenom(), "Le prénom");

    // Le sexe est obligatoire
    if (patient.getSexe() == null) {
        throw new ValidationException("Le sexe est obligatoire");
    }

    // L'assurance est obligatoire
    if (patient.getAssurance() == null) {
        throw new ValidationException("L'assurance est obligatoire");
    }

    // Valider l'email s'il est fourni
    if (patient.getEmail() != null && !patient.getEmail().trim().isEmpty()) {
        Validators.email(patient.getEmail());
    }

    // Valider le téléphone s'il est fourni
    if (patient.getTelephone() != null && !patient.getTelephone().trim().isEmpty()) {
        Validators.phone(patient.getTelephone());
    }
}
```

Cette méthode utilise la classe `Validators` qui contient des méthodes de validation réutilisables.

---

## 🔔 Explication Détaillée du Service Notification

### 1. **Interface NotificationService**

L'interface définit toutes les opérations métier sur les notifications :

#### Méthodes de Gestion des Notifications :
- `creerNotification(Notification)` : Crée une nouvelle notification
- `creerNotification(titre, message, type, priorite, creePar)` : Crée une notification rapidement (méthode de convenance)
- `modifierNotification(Notification)` : Modifie une notification
- `supprimerNotification(Long)` : Supprime une notification
- `consulterNotification(Long)` : Récupère une notification par ID
- `listerToutesLesNotifications()` : Récupère toutes les notifications
- `consulterNotificationsParType(TypeNotification)` : Filtre par type
- `consulterNotificationsParPriorite(PrioriteNotification)` : Filtre par priorité
- `consulterNotificationParTitre(TitreNotification)` : Recherche par titre
- `notificationExiste(Long)` : Vérifie si une notification existe
- `compterNotifications()` : Compte le nombre de notifications

#### Méthodes de Gestion Utilisateur-Notification :
- `envoyerNotification(Long, Notification)` : Crée et envoie une notification à un utilisateur
- `envoyerNotification(Long, titre, message, type, priorite, creePar)` : Envoie rapidement
- `retirerNotificationUtilisateur(Long, Long)` : Retire une notification d'un utilisateur

### 2. **Implémentation NotificationServiceImpl**

#### Comment ça fonctionne ?

**a) Injection de Dépendances :**
```java
private final NotificationRepository notificationRepository;
private final UtilisateurBaseRepositoryImpl utilisateurRepository;

public NotificationServiceImpl(
        NotificationRepository notificationRepository,
        UtilisateurBaseRepositoryImpl utilisateurRepository
) {
    this.notificationRepository = notificationRepository;
    this.utilisateurRepository = utilisateurRepository;
}
```

Le service utilise **deux repositories** :
- `NotificationRepository` : Pour gérer les notifications
- `UtilisateurBaseRepositoryImpl` : Pour gérer les utilisateurs et les liens notification-utilisateur

**b) Méthode `envoyerNotification` - Exemple détaillé :**

```java
public Notification envoyerNotification(Long utilisateurId, Notification notification) 
        throws ValidationException, ServiceException {
    // 1. Vérifier que l'ID utilisateur n'est pas null
    if (utilisateurId == null) {
        throw new ServiceException("L'ID de l'utilisateur ne peut pas être null");
    }

    // 2. Vérifier que l'utilisateur existe
    Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId);
    if (utilisateur == null) {
        throw new ServiceException("L'utilisateur avec l'ID " + utilisateurId + " n'existe pas");
    }

    // 3. Créer la notification
    Notification notificationCreee = creerNotification(notification);

    // 4. Lier la notification à l'utilisateur
    try {
        utilisateurRepository.addNotificationToUtilisateur(utilisateurId, notificationCreee.getId());
    } catch (Exception e) {
        // Si l'ajout échoue, on supprime la notification créée pour éviter les orphelins
        try {
            notificationRepository.deleteById(notificationCreee.getId());
        } catch (Exception ignored) {}
        throw new ServiceException("Erreur lors de l'envoi de la notification : " + e.getMessage(), e);
    }

    return notificationCreee;
}
```

**Étapes expliquées :**
1. **Vérification ID** : On vérifie que l'ID utilisateur n'est pas null
2. **Vérification utilisateur** : On vérifie que l'utilisateur existe en base
3. **Création notification** : On crée la notification (avec validation)
4. **Liaison** : On lie la notification à l'utilisateur dans la table de liaison `Utilisateur_Notification`
5. **Gestion d'erreur** : Si la liaison échoue, on supprime la notification créée pour éviter les "orphelins" (notifications sans utilisateur)

**c) Méthode privée `validerDonneesNotification` :**

```java
private void validerDonneesNotification(Notification notification) throws ValidationException {
    // Le titre est obligatoire
    if (notification.getTitre() == null) {
        throw new ValidationException("Le titre de la notification est obligatoire");
    }

    // Le message est obligatoire et ne doit pas être vide
    Validators.notBlank(notification.getMessage(), "Le message");

    // Le type est obligatoire
    if (notification.getType() == null) {
        throw new ValidationException("Le type de la notification est obligatoire");
    }

    // La priorité est obligatoire
    if (notification.getPriorite() == null) {
        throw new ValidationException("La priorité de la notification est obligatoire");
    }

    // Le créateur est obligatoire
    Validators.notBlank(notification.getCreePar(), "Le créateur");
}
```

---

## 🎓 Concepts Importants Expliqués

### 1. **Architecture en Couches**

```
┌─────────────────┐
│   Controllers   │  ← Interface utilisateur (Swing)
└────────┬────────┘
         │
┌────────▼────────┐
│    Services     │  ← Logique métier (ce qu'on vient de créer)
└────────┬────────┘
         │
┌────────▼────────┐
│   Repositories  │  ← Accès aux données (JDBC)
└────────┬────────┘
         │
┌────────▼────────┐
│  Base de données│
└─────────────────┘
```

**Pourquoi cette architecture ?**
- **Séparation des responsabilités** : Chaque couche a un rôle précis
- **Réutilisabilité** : Le service peut être utilisé par plusieurs contrôleurs
- **Testabilité** : On peut tester le service avec un mock repository
- **Maintenabilité** : Si on change la base de données, on modifie seulement le repository

### 2. **Injection de Dépendance**

Au lieu de créer le repository dans le service :
```java
// ❌ MAUVAIS : Le service crée lui-même le repository
public PatientServiceImpl() {
    this.patientRepository = new PatientRepositoryImpl(); // Mauvaise pratique
}
```

On le reçoit en paramètre :
```java
// ✅ BON : Le repository est injecté
public PatientServiceImpl(PatientRepository patientRepository) {
    this.patientRepository = patientRepository; // Bonne pratique
}
```

**Avantages :**
- On peut tester avec un mock
- On peut changer d'implémentation facilement
- Le service ne dépend pas directement de l'implémentation

### 3. **Exceptions Métier**

On utilise deux types d'exceptions :

**a) ValidationException** : Pour les erreurs de validation (données invalides)
```java
throw new ValidationException("Le nom est obligatoire");
```

**b) ServiceException** : Pour les erreurs métier (patient n'existe pas, erreur base de données)
```java
throw new ServiceException("Le patient avec l'ID " + id + " n'existe pas");
```

**Pourquoi deux types ?**
- On peut gérer différemment les erreurs dans l'interface utilisateur
- Les erreurs de validation peuvent afficher un message à l'utilisateur
- Les erreurs de service peuvent nécessiter un log

### 4. **Méthodes de Convenance**

On a créé des méthodes "rapides" pour faciliter l'utilisation :

```java
// Méthode complète
Notification creerNotification(Notification notification);

// Méthode de convenance (plus rapide à utiliser)
Notification creerNotification(
    TitreNotification titre,
    String message,
    TypeNotification type,
    PrioriteNotification priorite,
    String creePar
);
```

**Avantage :** On peut créer une notification sans créer l'objet `Notification` manuellement.

---

## 💡 Exemples d'Utilisation

### Exemple 1 : Enregistrer un nouveau patient

```java
// Créer un patient
Patient nouveauPatient = Patient.builder()
    .nom("Dupont")
    .prenom("Jean")
    .sexe(Sexe.Homme)
    .assurance(Assurance.CNOPS)
    .email("jean.dupont@email.com")
    .telephone("0612345678")
    .dateNaissance(LocalDate.of(1990, 5, 15))
    .adresse("123 Rue Example, Rabat")
    .build();

// Enregistrer via le service
try {
    Patient patientEnregistre = patientService.enregistrerPatient(nouveauPatient);
    System.out.println("Patient enregistré avec l'ID : " + patientEnregistre.getId());
} catch (ValidationException e) {
    System.out.println("Erreur de validation : " + e.getMessage());
} catch (ServiceException e) {
    System.out.println("Erreur service : " + e.getMessage());
}
```

### Exemple 2 : Envoyer une notification

```java
// Envoyer une notification rapidement
try {
    Notification notif = notificationService.envoyerNotification(
        utilisateurId,                    // ID de l'utilisateur
        TitreNotification.RAPPEL,         // Titre
        "Vous avez un rendez-vous demain", // Message
        TypeNotification.RENDEZVOUS,      // Type
        PrioriteNotification.HAUTE,      // Priorité
        "Système"                         // Créateur
    );
    System.out.println("Notification envoyée avec l'ID : " + notif.getId());
} catch (ValidationException e) {
    System.out.println("Erreur de validation : " + e.getMessage());
} catch (ServiceException e) {
    System.out.println("Erreur service : " + e.getMessage());
}
```

### Exemple 3 : Rechercher des patients

```java
// Rechercher par mot-clé
List<Patient> patients = patientService.rechercherPatient("Dupont");
System.out.println("Trouvé " + patients.size() + " patient(s)");

// Rechercher par email
Patient patient = patientService.rechercherPatientParEmail("jean.dupont@email.com");
if (patient != null) {
    System.out.println("Patient trouvé : " + patient.getNom() + " " + patient.getPrenom());
}
```

---

## ✅ Points Importants à Retenir

1. **Les services contiennent la logique métier** : Validation, vérifications, règles métier
2. **Les repositories accèdent aux données** : Le service ne fait jamais de SQL directement
3. **Les méthodes sont verbales** : `enregistrerPatient`, `envoyerNotification`, etc.
4. **Les exceptions sont gérées** : ValidationException pour validation, ServiceException pour erreurs métier
5. **Le code est commenté** : Chaque méthode a une JavaDoc expliquant ce qu'elle fait
6. **Le code est clair** : Pas de code trop complexe, facile à comprendre pour un étudiant

---

## 🔧 Prochaines Étapes

Pour utiliser ces services dans votre application :

1. **Ajouter les services dans beans.properties** :
```properties
# Services
patientService = ma.WhiteLab.service.modules.patient.impl.PatientServiceImpl
notificationService = ma.WhiteLab.service.modules.notifications.impl.NotificationServiceImpl
```

2. **Utiliser dans les contrôleurs** :
```java
ApplicationContext context = new ApplicationContext("config/beans.properties");
PatientService patientService = context.getBean(PatientService.class);
```

3. **Tester les services** : Créer des tests unitaires pour vérifier que tout fonctionne

---

## 📝 Résumé

J'ai créé :
- ✅ **2 interfaces** (PatientService, NotificationService)
- ✅ **2 implémentations** (PatientServiceImpl, NotificationServiceImpl)
- ✅ **Méthodes métier verbales** (enregistrer, modifier, supprimer, etc.)
- ✅ **Validation des données** (avec Validators)
- ✅ **Gestion des exceptions** (ValidationException, ServiceException)
- ✅ **Code clair et commenté** (niveau étudiant)
- ✅ **Injection de dépendance** (repositories en paramètre)

Le code est prêt à être utilisé ! 🎉

