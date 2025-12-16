package ma.WhiteLab.service.test;

import ma.WhiteLab.conf.ApplicationContext;
import ma.WhiteLab.common.exceptions.ServiceException;
import ma.WhiteLab.common.exceptions.ValidationException;
import ma.WhiteLab.entities.patient.Patient;
import ma.WhiteLab.entities.patient.Antecedent;
import ma.WhiteLab.entities.enums.Sexe;
import ma.WhiteLab.entities.enums.Assurance;
import ma.WhiteLab.entities.enums.CategorieAntecedent;
import ma.WhiteLab.entities.enums.NiveauDeRisk;
import ma.WhiteLab.repository.modules.patient.api.PatientRepository;
import ma.WhiteLab.repository.modules.patient.api.AntecedentRepository;
import ma.WhiteLab.service.modules.patient.api.PatientService;
import ma.WhiteLab.service.modules.patient.impl.PatientServiceImpl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Classe de test pour le service Patient
 * 
 * Cette classe contient plusieurs scénarios de test pour vérifier
 * que le service Patient fonctionne correctement.
 * 
 * Pour exécuter : Run -> PatientServiceTest.main()
 */
public class PatientServiceTest {

    private static PatientService patientService;
    private static PatientRepository patientRepository;
    private static AntecedentRepository antecedentRepository;

    // IDs pour les tests
    private static Long patientId1;
    private static Long antecedentId1, antecedentId2;

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("  TESTS DU SERVICE PATIENT");
        System.out.println("==========================================\n");

        try {
            // Initialiser les repositories et le service
            initialiser();

            // Exécuter tous les scénarios de test
            scenario1_EnregistrerNouveauPatient();
            scenario2_EnregistrerPatientAvecDonneesInvalides();
            scenario3_EnregistrerPatientAvecEmailExistant();
            scenario4_ModifierPatient();
            scenario5_ModifierPatientInexistant();
            scenario6_RechercherPatient();
            scenario7_RechercherPatientParEmail();
            scenario8_RechercherPatientParTelephone();
            scenario9_ConsulterPatient();
            scenario10_CompterPatients();
            scenario11_CreerEtAjouterAntecedents();
            scenario12_ConsulterAntecedentsPatient();
            scenario13_RetirerAntecedent();
            scenario14_SupprimerPatient();
            scenario15_SupprimerPatientInexistant();

            System.out.println("\n==========================================");
            System.out.println("  TOUS LES TESTS SONT TERMINÉS");
            System.out.println("==========================================");

        } catch (Exception e) {
            System.err.println("ERREUR CRITIQUE : " + e.getMessage());
            e.printStackTrace();
        }
    }

    /**
     * Initialise les repositories et le service
     */
    private static void initialiser() {
        System.out.println("📋 Initialisation des repositories et services...\n");

        ApplicationContext context = new ApplicationContext("config/beans.properties");

        // Récupérer les repositories
        patientRepository = context.getBean(PatientRepository.class);
        antecedentRepository = context.getBean(AntecedentRepository.class);

        // Créer le service avec injection de dépendance
        patientService = new PatientServiceImpl(patientRepository);

        System.out.println("✅ Initialisation terminée\n");
    }

    // ============================================
    //  SCÉNARIOS DE TEST
    // ============================================

    /**
     * SCÉNARIO 1 : Enregistrer un nouveau patient avec succès
     */
    private static void scenario1_EnregistrerNouveauPatient() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 1 : Enregistrer un nouveau patient");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            // Créer un nouveau patient
            Patient nouveauPatient = Patient.builder()
                    .nom("Alami")
                    .prenom("Fatima")
                    .sexe(Sexe.Femme)
                    .assurance(Assurance.CNOPS)
                    .email("fatima.alami@email.com")
                    .telephone("0612345678")
                    .dateNaissance(LocalDate.of(1985, 3, 15))
                    .adresse("123 Avenue Mohammed V, Rabat")
                    .build();

            System.out.println("📝 Tentative d'enregistrement du patient : " + nouveauPatient.getNom() + " " + nouveauPatient.getPrenom());

            // Enregistrer le patient
            Patient patientEnregistre = patientService.enregistrerPatient(nouveauPatient);
            patientId1 = patientEnregistre.getId();

            System.out.println("✅ SUCCÈS : Patient enregistré avec l'ID : " + patientId1);
            System.out.println("   - Nom : " + patientEnregistre.getNom());
            System.out.println("   - Prénom : " + patientEnregistre.getPrenom());
            System.out.println("   - Email : " + patientEnregistre.getEmail());
            System.out.println("   - Date de création : " + patientEnregistre.getDateCreation());

        } catch (ValidationException e) {
            System.err.println("❌ ERREUR DE VALIDATION : " + e.getMessage());
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR DE SERVICE : " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * SCÉNARIO 2 : Tenter d'enregistrer un patient avec des données invalides
     */
    private static void scenario2_EnregistrerPatientAvecDonneesInvalides() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 2 : Patient avec données invalides");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            // Patient sans nom (invalide)
            Patient patientInvalide = Patient.builder()
                    .prenom("Ahmed")
                    .sexe(Sexe.Homme)
                    .assurance(Assurance.CNSS)
                    .email("ahmed@email.com")
                    .build();

            System.out.println("📝 Tentative d'enregistrement d'un patient sans nom...");

            patientService.enregistrerPatient(patientInvalide);

            System.err.println("❌ ÉCHEC : Le patient a été enregistré alors qu'il ne devrait pas l'être !");

        } catch (ValidationException e) {
            System.out.println("✅ SUCCÈS : ValidationException correctement levée");
            System.out.println("   Message : " + e.getMessage());
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * SCÉNARIO 3 : Tenter d'enregistrer un patient avec un email déjà utilisé
     */
    private static void scenario3_EnregistrerPatientAvecEmailExistant() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 3 : Patient avec email existant");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            // Créer un deuxième patient avec le même email
            Patient patientDuplique = Patient.builder()
                    .nom("Benali")
                    .prenom("Karim")
                    .sexe(Sexe.Homme)
                    .assurance(Assurance.CNOPS)
                    .email("fatima.alami@email.com") // Même email que le patient 1
                    .telephone("0623456789")
                    .dateNaissance(LocalDate.of(1990, 7, 20))
                    .build();

            System.out.println("📝 Tentative d'enregistrement avec un email déjà utilisé...");

            patientService.enregistrerPatient(patientDuplique);

            System.err.println("❌ ÉCHEC : Le patient a été enregistré avec un email dupliqué !");

        } catch (ValidationException e) {
            System.out.println("✅ SUCCÈS : ValidationException correctement levée");
            System.out.println("   Message : " + e.getMessage());
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * SCÉNARIO 4 : Modifier un patient existant
     */
    private static void scenario4_ModifierPatient() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 4 : Modifier un patient existant");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            // Récupérer le patient
            Patient patient = patientService.consulterPatient(patientId1);
            if (patient == null) {
                System.err.println("❌ Patient non trouvé");
                return;
            }

            System.out.println("📝 Patient avant modification :");
            System.out.println("   - Adresse : " + patient.getAdresse());
            System.out.println("   - Téléphone : " + patient.getTelephone());

            // Modifier l'adresse et le téléphone
            patient.setAdresse("456 Boulevard Hassan II, Casablanca");
            patient.setTelephone("0698765432");

            // Enregistrer les modifications
            Patient patientModifie = patientService.modifierPatient(patient);

            System.out.println("✅ SUCCÈS : Patient modifié");
            System.out.println("   - Nouvelle adresse : " + patientModifie.getAdresse());
            System.out.println("   - Nouveau téléphone : " + patientModifie.getTelephone());

        } catch (ValidationException e) {
            System.err.println("❌ ERREUR DE VALIDATION : " + e.getMessage());
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR DE SERVICE : " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * SCÉNARIO 5 : Tenter de modifier un patient inexistant
     */
    private static void scenario5_ModifierPatientInexistant() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 5 : Modifier un patient inexistant");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            // Créer un patient avec un ID inexistant
            Patient patientInexistant = Patient.builder()
                    .id(99999L) // ID qui n'existe pas
                    .nom("Test")
                    .prenom("Test")
                    .sexe(Sexe.Homme)
                    .assurance(Assurance.Aucune)
                    .build();

            System.out.println("📝 Tentative de modification d'un patient inexistant (ID: 99999)...");

            patientService.modifierPatient(patientInexistant);

            System.err.println("❌ ÉCHEC : Le patient a été modifié alors qu'il n'existe pas !");

        } catch (ValidationException e) {
            System.err.println("❌ ERREUR DE VALIDATION : " + e.getMessage());
        } catch (ServiceException e) {
            System.out.println("✅ SUCCÈS : ServiceException correctement levée");
            System.out.println("   Message : " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * SCÉNARIO 6 : Rechercher des patients par nom/prénom
     */
    private static void scenario6_RechercherPatient() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 6 : Rechercher des patients");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            // Rechercher par nom
            System.out.println("📝 Recherche par mot-clé 'Alami'...");
            List<Patient> resultats = patientService.rechercherPatient("Alami");

            System.out.println("✅ SUCCÈS : " + resultats.size() + " patient(s) trouvé(s)");
            for (Patient p : resultats) {
                System.out.println("   - " + p.getNom() + " " + p.getPrenom() + " (ID: " + p.getId() + ")");
            }

        } catch (Exception e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * SCÉNARIO 7 : Rechercher un patient par email
     */
    private static void scenario7_RechercherPatientParEmail() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 7 : Rechercher par email");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            System.out.println("📝 Recherche du patient avec l'email 'fatima.alami@email.com'...");

            Patient patient = patientService.rechercherPatientParEmail("fatima.alami@email.com");

            if (patient != null) {
                System.out.println("✅ SUCCÈS : Patient trouvé");
                System.out.println("   - Nom : " + patient.getNom() + " " + patient.getPrenom());
                System.out.println("   - ID : " + patient.getId());
            } else {
                System.out.println("⚠️  Aucun patient trouvé avec cet email");
            }

        } catch (Exception e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * SCÉNARIO 8 : Rechercher un patient par téléphone
     */
    private static void scenario8_RechercherPatientParTelephone() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 8 : Rechercher par téléphone");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            System.out.println("📝 Recherche du patient avec le téléphone '0698765432'...");

            Patient patient = patientService.rechercherPatientParTelephone("0698765432");

            if (patient != null) {
                System.out.println("✅ SUCCÈS : Patient trouvé");
                System.out.println("   - Nom : " + patient.getNom() + " " + patient.getPrenom());
                System.out.println("   - Téléphone : " + patient.getTelephone());
            } else {
                System.out.println("⚠️  Aucun patient trouvé avec ce téléphone");
            }

        } catch (Exception e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * SCÉNARIO 9 : Consulter un patient par ID
     */
    private static void scenario9_ConsulterPatient() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 9 : Consulter un patient");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            System.out.println("📝 Consultation du patient avec l'ID : " + patientId1);

            Patient patient = patientService.consulterPatient(patientId1);

            if (patient != null) {
                System.out.println("✅ SUCCÈS : Patient trouvé");
                System.out.println("   - ID : " + patient.getId());
                System.out.println("   - Nom complet : " + patient.getNom() + " " + patient.getPrenom());
                System.out.println("   - Sexe : " + patient.getSexe());
                System.out.println("   - Assurance : " + patient.getAssurance());
                System.out.println("   - Email : " + patient.getEmail());
                System.out.println("   - Téléphone : " + patient.getTelephone());
                System.out.println("   - Date de naissance : " + patient.getDateNaissance());
                System.out.println("   - Adresse : " + patient.getAdresse());
            } else {
                System.out.println("⚠️  Patient non trouvé");
            }

        } catch (Exception e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * SCÉNARIO 10 : Compter le nombre de patients
     */
    private static void scenario10_CompterPatients() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 10 : Compter les patients");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            System.out.println("📝 Comptage du nombre total de patients...");

            long nombre = patientService.compterPatients();

            System.out.println("✅ SUCCÈS : Nombre total de patients = " + nombre);

        } catch (Exception e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * SCÉNARIO 11 : Créer des antécédents et les ajouter à un patient
     */
    private static void scenario11_CreerEtAjouterAntecedents() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 11 : Créer et ajouter des antécédents");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            // Créer un premier antécédent
            Antecedent antecedent1 = Antecedent.builder()
                    .nom("Diabète")
                    .description("Diabète de type 2")
                    .categorie(CategorieAntecedent.MALADIE_CHRONIQUE)
                    .niveauDeRisk(NiveauDeRisk.MOYEN)
                    .dateCreation(LocalDateTime.now())
                    .creePar("Système")
                    .build();

            antecedentRepository.create(antecedent1);
            antecedentId1 = antecedent1.getId();
            System.out.println("✅ Antécédent 1 créé (ID: " + antecedentId1 + ") : " + antecedent1.getNom());

            // Créer un deuxième antécédent
            Antecedent antecedent2 = Antecedent.builder()
                    .nom("Allergie aux antibiotiques")
                    .description("Allergie à la pénicilline")
                    .categorie(CategorieAntecedent.ALLERGIE)
                    .niveauDeRisk(NiveauDeRisk.ELEVE)
                    .dateCreation(LocalDateTime.now())
                    .creePar("Système")
                    .build();

            antecedentRepository.create(antecedent2);
            antecedentId2 = antecedent2.getId();
            System.out.println("✅ Antécédent 2 créé (ID: " + antecedentId2 + ") : " + antecedent2.getNom());

            // Ajouter les antécédents au patient
            System.out.println("\n📝 Ajout des antécédents au patient (ID: " + patientId1 + ")...");

            patientService.ajouterAntecedent(patientId1, antecedentId1);
            System.out.println("✅ Antécédent 1 ajouté");

            patientService.ajouterAntecedent(patientId1, antecedentId2);
            System.out.println("✅ Antécédent 2 ajouté");

        } catch (ServiceException e) {
            System.err.println("❌ ERREUR DE SERVICE : " + e.getMessage());
        } catch (Exception e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * SCÉNARIO 12 : Consulter les antécédents d'un patient
     */
    private static void scenario12_ConsulterAntecedentsPatient() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 12 : Consulter les antécédents d'un patient");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            System.out.println("📝 Consultation des antécédents du patient (ID: " + patientId1 + ")...");

            List<Antecedent> antecedents = patientService.consulterAntecedentsPatient(patientId1);

            System.out.println("✅ SUCCÈS : " + antecedents.size() + " antécédent(s) trouvé(s)");
            for (Antecedent a : antecedents) {
                System.out.println("   - " + a.getNom() + " (" + a.getCategorie() + ", Risque: " + a.getNiveauDeRisk() + ")");
                System.out.println("     Description : " + a.getDescription());
            }

        } catch (ServiceException e) {
            System.err.println("❌ ERREUR DE SERVICE : " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * SCÉNARIO 13 : Retirer un antécédent d'un patient
     */
    private static void scenario13_RetirerAntecedent() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 13 : Retirer un antécédent");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            System.out.println("📝 Retrait de l'antécédent (ID: " + antecedentId1 + ") du patient (ID: " + patientId1 + ")...");

            patientService.retirerAntecedent(patientId1, antecedentId1);

            System.out.println("✅ SUCCÈS : Antécédent retiré");

            // Vérifier qu'il a bien été retiré
            List<Antecedent> antecedents = patientService.consulterAntecedentsPatient(patientId1);
            System.out.println("   Nombre d'antécédents restants : " + antecedents.size());

        } catch (ServiceException e) {
            System.err.println("❌ ERREUR DE SERVICE : " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * SCÉNARIO 14 : Supprimer un patient
     */
    private static void scenario14_SupprimerPatient() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 14 : Supprimer un patient");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            // Créer un patient pour le supprimer
            Patient patientASupprimer = Patient.builder()
                    .nom("Test")
                    .prenom("Suppression")
                    .sexe(Sexe.Homme)
                    .assurance(Assurance.Aucune)
                    .build();

            patientService.enregistrerPatient(patientASupprimer);
            Long idASupprimer = patientASupprimer.getId();

            System.out.println("📝 Suppression du patient (ID: " + idASupprimer + ")...");

            patientService.supprimerPatient(idASupprimer);

            System.out.println("✅ SUCCÈS : Patient supprimé");

            // Vérifier qu'il a bien été supprimé
            Patient patient = patientService.consulterPatient(idASupprimer);
            if (patient == null) {
                System.out.println("   ✅ Vérification : Patient bien supprimé (retourne null)");
            } else {
                System.err.println("   ❌ ERREUR : Le patient existe encore !");
            }

        } catch (ValidationException e) {
            System.err.println("❌ ERREUR DE VALIDATION : " + e.getMessage());
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR DE SERVICE : " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * SCÉNARIO 15 : Tenter de supprimer un patient inexistant
     */
    private static void scenario15_SupprimerPatientInexistant() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 15 : Supprimer un patient inexistant");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            System.out.println("📝 Tentative de suppression d'un patient inexistant (ID: 99999)...");

            patientService.supprimerPatient(99999L);

            System.err.println("❌ ÉCHEC : Le patient a été supprimé alors qu'il n'existe pas !");

        } catch (ServiceException e) {
            System.out.println("✅ SUCCÈS : ServiceException correctement levée");
            System.out.println("   Message : " + e.getMessage());
        }

        System.out.println();
    }
}

