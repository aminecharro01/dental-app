package ma.WhiteLab.service.test;

import ma.WhiteLab.conf.ApplicationContext;
import ma.WhiteLab.common.exceptions.ServiceException;
import ma.WhiteLab.common.exceptions.ValidationException;
import ma.WhiteLab.entities.cabinet.CabinetMedicale;
import ma.WhiteLab.entities.dossierMedical.Consultation;
import ma.WhiteLab.entities.dossierMedical.DossierMedical;
import ma.WhiteLab.entities.enums.Assurance;
import ma.WhiteLab.entities.enums.StatusConsultation;
import ma.WhiteLab.entities.patient.Patient;
import ma.WhiteLab.entities.user.Medecin;
import ma.WhiteLab.entities.user.Staff;
import ma.WhiteLab.entities.user.Utilisateur;
import ma.WhiteLab.repository.modules.cabinet.api.CabinetMedicaleRepository;
import ma.WhiteLab.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.WhiteLab.repository.modules.patient.api.PatientRepository;
import ma.WhiteLab.repository.modules.user.api.MedecinRepository;
import ma.WhiteLab.repository.modules.user.api.StaffRepository;
import ma.WhiteLab.repository.modules.user.api.UtilisateurRepository;
import ma.WhiteLab.service.modules.dossierMedicale.api.ConsultationService;

import java.time.LocalDateTime;
import java.util.List;

public class ConsultationServiceTest {

    private static ConsultationService consultationService;
    private static PatientRepository patientRepository;
    private static DossierMedicalRepository dossierMedicalRepository;
    private static CabinetMedicaleRepository cabinetMedicaleRepository;
    private static MedecinRepository medecinRepository;

    private static Long patientId1;
    private static Long cabinetId1;
    private static Long medecinId1;
    private static Long consultationId1;

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("  TESTS DU SERVICE CONSULTATION");
        System.out.println("==========================================\n");

        try {
            initialiser();
            preparerDonneesDeTest();

            scenario1_CreerConsultation();
            scenario2_CreerConsultationAvecDonneesInvalides();
            scenario3_ModifierConsultation();
            scenario4_ModifierConsultationInexistante();
            scenario5_ChangerStatusConsultation();
            scenario6_ConsulterConsultation();
            scenario7_ListerConsultationsParPatient();

            System.out.println("\n==========================================");
            System.out.println("  TOUS LES TESTS SONT TERMINÉS");
            System.out.println("==========================================");

        } catch (Exception e) {
            System.err.println("ERREUR CRITIQUE : " + e.getMessage());
            e.printStackTrace();
        } finally {
            nettoyerDonneesDeTest();
        }
    }

    private static void initialiser() {
        System.out.println("📋 Initialisation des services...\n");
        ApplicationContext context = new ApplicationContext("config/beans.properties");
        consultationService = context.getBean(ConsultationService.class);
        patientRepository = context.getBean(PatientRepository.class);
        dossierMedicalRepository = context.getBean(DossierMedicalRepository.class);
        cabinetMedicaleRepository = context.getBean(CabinetMedicaleRepository.class);
        medecinRepository = context.getBean(MedecinRepository.class);
        System.out.println("✅ Initialisation terminée\n");
    }

    private static void preparerDonneesDeTest() throws ServiceException {
        System.out.println("📋 Préparation des données de test...\n");

        // 1. Create Cabinet
        CabinetMedicale cabinet = CabinetMedicale.builder().nom("Cabinet Test").build();
        cabinetMedicaleRepository.create(cabinet);
        cabinetId1 = cabinet.getId();

        // 2. Create Medecin (which also creates Utilisateur and Staff)
        Medecin medecin = Medecin.builder()
                .nom("Docteur")
                .prenom("Test")
                .email("doc@test.com")
                .specialite("Généraliste")
                .cabinetMedicale(cabinet)
                .build();
        medecinRepository.create(medecin);
        medecinId1 = medecin.getId();

        // 3. Create Patient
        Patient patient = Patient.builder().nom("Test").prenom("Patient").sexe(ma.WhiteLab.entities.enums.Sexe.Homme).assurance(Assurance.CNOPS).build();
        patientRepository.create(patient);
        patientId1 = patient.getId();

        // 4. Create DossierMedical with Patient and Medecin
        DossierMedical dossier = DossierMedical.builder().pat(patient).medcine(medecin).build();
        dossierMedicalRepository.create(dossier);
    }

    private static void nettoyerDonneesDeTest() {
        System.out.println("\n🧹 Nettoyage des données de test...");
        // Deleting the medecin will cascade to staff and utilisateur
        if (medecinId1 != null) {
            try {
                medecinRepository.deleteById(medecinId1);
                System.out.println("   - Médecin de test supprimé.");
            } catch (Exception e) {
                System.err.println("   - Erreur lors de la suppression du médecin de test : " + e.getMessage());
            }
        }
        if (patientId1 != null) {
            try {
                patientRepository.deleteById(patientId1);
                System.out.println("   - Patient de test supprimé.");
            } catch (Exception e) {
                System.err.println("   - Erreur lors de la suppression du patient de test : " + e.getMessage());
            }
        }
        if (cabinetId1 != null) {
            try {
                cabinetMedicaleRepository.deleteById(cabinetId1);
                System.out.println("   - Cabinet de test supprimé.");
            } catch (Exception e) {
                System.err.println("   - Erreur lors de la suppression du cabinet de test : " + e.getMessage());
            }
        }
    }

    private static void scenario1_CreerConsultation() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 1 : Créer une nouvelle consultation");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        try {
            Consultation nouvelleConsultation = Consultation.builder()
                    .motif("Examen de routine")
                    .build();
            Consultation consultationCreee = consultationService.creerConsultationPourPatient(patientId1, nouvelleConsultation);
            consultationId1 = consultationCreee.getId();
            System.out.println("✅ SUCCÈS : Consultation créée avec l'ID : " + consultationId1);
        } catch (ValidationException | ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario2_CreerConsultationAvecDonneesInvalides() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 2 : Consultation avec données invalides");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        try {
            consultationService.creerConsultationPourPatient(patientId1, null);
            System.err.println("❌ ÉCHEC : La consultation a été créée avec des données invalides !");
        } catch (ValidationException e) {
            System.out.println("✅ SUCCÈS : ValidationException correctement levée");
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario3_ModifierConsultation() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 3 : Modifier une consultation existante");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        try {
            Consultation consultation = consultationService.consulterConsultation(consultationId1);
            consultation.setMotif("Suivi annuel");
            consultationService.modifierConsultation(consultation);
            System.out.println("✅ SUCCÈS : Consultation modifiée.");
        } catch (ValidationException | ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario4_ModifierConsultationInexistante() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 4 : Modifier une consultation inexistante");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        try {
            Consultation consultation = Consultation.builder().id(999L).motif("Inexistant").date(LocalDateTime.now()).status(StatusConsultation.PLANIFIEE).build();
            consultationService.modifierConsultation(consultation);
            System.err.println("❌ ÉCHEC : La consultation a été modifiée alors qu'elle n'existe pas !");
        } catch (ServiceException e) {
            System.out.println("✅ SUCCÈS : ServiceException correctement levée");
        } catch (ValidationException e) {
            System.err.println("❌ ERREUR DE VALIDATION : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario5_ChangerStatusConsultation() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 5 : Changer le statut d'une consultation");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        try {
            consultationService.changerStatusConsultation(consultationId1, StatusConsultation.TERMINEE);
            Consultation consultation = consultationService.consulterConsultation(consultationId1);
            if (consultation.getStatus() == StatusConsultation.TERMINEE) {
                System.out.println("✅ SUCCÈS : Statut de la consultation mis à jour.");
            } else {
                System.err.println("❌ ERREUR : Le statut n'a pas été mis à jour.");
            }
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario6_ConsulterConsultation() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 6 : Consulter une consultation");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        try {
            Consultation consultation = consultationService.consulterConsultation(consultationId1);
            if (consultation != null) {
                System.out.println("✅ SUCCÈS : Consultation trouvée : ID " + consultation.getId());
            } else {
                System.err.println("❌ ERREUR : Consultation non trouvée !");
            }
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario7_ListerConsultationsParPatient() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 7 : Lister les consultations par patient");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        try {
            List<Consultation> consultations = consultationService.listerConsultationsParPatient(patientId1);
            System.out.println("✅ SUCCÈS : " + consultations.size() + " consultation(s) trouvée(s) pour le patient.");
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }
}
