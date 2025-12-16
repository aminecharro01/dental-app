package ma.WhiteLab.service.test;

import ma.WhiteLab.conf.ApplicationContext;
import ma.WhiteLab.common.exceptions.ServiceException;
import ma.WhiteLab.common.exceptions.ValidationException;
import ma.WhiteLab.entities.dossierMedical.Certificat;
import ma.WhiteLab.entities.dossierMedical.Consultation;
import ma.WhiteLab.repository.modules.dossierMedical.api.CertificatRepository;
import ma.WhiteLab.repository.modules.dossierMedical.api.ConsultationRepository;
import ma.WhiteLab.service.modules.dossierMedicale.api.CertificatService;   
import ma.WhiteLab.entities.dossierMedical.DossierMedical;
import ma.WhiteLab.entities.patient.Patient;
import ma.WhiteLab.repository.modules.dossierMedical.api.DossierMedicalRepository;
import ma.WhiteLab.repository.modules.patient.api.PatientRepository;
import ma.WhiteLab.service.modules.dossierMedicale.impl.CertificatServiceImpl;

import ma.WhiteLab.entities.user.Medecin;
import ma.WhiteLab.repository.modules.user.api.UtilisateurRepository;
import java.time.LocalDate;

public class CertificatServiceTest {

    private static CertificatService certificatService;
    private static CertificatRepository certificatRepository;
    private static ConsultationRepository consultationRepository;
    private static PatientRepository patientRepository;
    private static DossierMedicalRepository dossierMedicalRepository;
    private static UtilisateurRepository utilisateurRepository;


    private static Long certificatId1;
    private static Long consultationId1;
    private static Long patientId1;
    private static Long medecinId1;


    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("  TESTS DU SERVICE CERTIFICAT");
        System.out.println("==========================================\n");

        try {
            initialiser();
            preparerDonneesDeTest();

            scenario1_GenererCertificat();
            scenario2_GenererCertificatAvecDonneesInvalides();
            scenario3_ModifierCertificat();
            scenario4_ModifierCertificatInexistant();
            scenario5_ConsulterCertificat();
            scenario6_SupprimerCertificat();
            scenario7_SupprimerCertificatInexistant();

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
        System.out.println("📋 Initialisation des repositories et services...\n");
        ApplicationContext context = new ApplicationContext("config/beans.properties");
        certificatRepository = context.getBean(CertificatRepository.class);
        consultationRepository = context.getBean(ConsultationRepository.class);
        patientRepository = context.getBean(PatientRepository.class);
        dossierMedicalRepository = context.getBean(DossierMedicalRepository.class);
        utilisateurRepository = context.getBean(UtilisateurRepository.class);
        certificatService = new CertificatServiceImpl(certificatRepository, consultationRepository);
        System.out.println("✅ Initialisation terminée\n");
    }

    private static void preparerDonneesDeTest() throws Exception {
        System.out.println("📋 Préparation des données de test...");
        
        Medecin medecin = Medecin.builder().nom("Test").prenom("Medecin").build();
        utilisateurRepository.create(medecin);
        medecinId1 = medecin.getId();

        Patient patient = Patient.builder().nom("TestCert").prenom("PatientCert").sexe(ma.WhiteLab.entities.enums.Sexe.Femme).assurance(ma.WhiteLab.entities.enums.Assurance.CNOPS).build();
        patientRepository.create(patient);
        patientId1 = patient.getId();

        DossierMedical dossier = DossierMedical.builder().pat(patient).medcine(medecin).build();
        dossierMedicalRepository.create(dossier);

        Consultation consultation = Consultation.builder().dossierMedical(dossier).motif("Consultation pour certificat").date(java.time.LocalDateTime.now()).status(ma.WhiteLab.entities.enums.StatusConsultation.PLANIFIEE).build();
        consultationRepository.create(consultation);
        consultationId1 = consultation.getId();
        System.out.println("   - Consultation de test créée avec l'ID : " + consultationId1);
        System.out.println("✅ Préparation terminée\n");
    }

    private static void nettoyerDonneesDeTest() {
        System.out.println("\n🧹 Nettoyage des données de test...");
        if (patientId1 != null) {
            try {
                // The deletion of patient should cascade and delete dossier, consultation, and certificat
                patientRepository.deleteById(patientId1);
                System.out.println("   - Patient de test (Certificat) supprimé.");
            } catch (Exception e) {
                System.err.println("   - Erreur lors de la suppression du patient de test : " + e.getMessage());
            }
        }
        if (medecinId1 != null) {
            try {
                utilisateurRepository.deleteById(medecinId1);
                System.out.println("   - Medecin de test (Certificat) supprimé.");
            } catch (Exception e) {
                System.err.println("   - Erreur lors de la suppression du medecin de test : " + e.getMessage());
            }
        }
    }

    private static void scenario1_GenererCertificat() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 1 : Générer un nouveau certificat");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            Certificat nouveauCertificat = Certificat.builder()
                    .contenu("Certificat de repos médical")
                    .dateDebut(LocalDate.now())
                    .dureeRepos(5)
                    .build();

            System.out.println("📝 Tentative de génération du certificat...");

            Certificat certificatGenere = certificatService.genererCertificatPourConsultation(consultationId1, nouveauCertificat);
            certificatId1 = certificatGenere.getId();

            System.out.println("✅ SUCCÈS : Certificat généré avec l'ID : " + certificatId1);
        } catch (ValidationException | ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario2_GenererCertificatAvecDonneesInvalides() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 2 : Certificat avec données invalides");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            Certificat certificatInvalide = Certificat.builder().build();
            System.out.println("📝 Tentative de génération d'un certificat invalide...");
            certificatService.genererCertificatPourConsultation(consultationId1, certificatInvalide);
            System.err.println("❌ ÉCHEC : Le certificat a été généré avec des données invalides !");
        } catch (ValidationException e) {
            System.out.println("✅ SUCCÈS : ValidationException correctement levée");
            System.out.println("   Message : " + e.getMessage());
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario3_ModifierCertificat() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 3 : Modifier un certificat existant");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        if (certificatId1 == null) {
            System.out.println("⏩ SCÉNARIO IGNORÉ : Aucun certificat n'a été créé dans le scénario 1.");
            return;
        }
        try {
            Certificat certificat = certificatService.consulterCertificat(certificatId1);
            certificat.setContenu("Contenu mis à jour");
            certificatService.modifierCertificat(certificat);
            System.out.println("✅ SUCCÈS : Certificat modifié.");
        } catch (ValidationException | ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }
    
    private static void scenario4_ModifierCertificatInexistant() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 4 : Modifier un certificat inexistant");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    
        try {
            Certificat certificat = Certificat.builder()
                .id(999L)
                .contenu("Contenu")
                .dateDebut(LocalDate.now())
                .dureeRepos(1)
                .build();
            certificatService.modifierCertificat(certificat);
            System.err.println("❌ ÉCHEC : Le certificat a été modifié alors qu'il n'existe pas !");
        } catch (ServiceException e) {
            System.out.println("✅ SUCCÈS : ServiceException correctement levée");
        } catch (ValidationException e) {
            System.err.println("❌ ERREUR DE VALIDATION : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario5_ConsulterCertificat() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 5 : Consulter un certificat");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        if (certificatId1 == null) {
            System.out.println("⏩ SCÉNARIO IGNORÉ : Aucun certificat n'a été créé dans le scénario 1.");
            return;
        }
        try {
            Certificat certificat = certificatService.consulterCertificat(certificatId1);
            if (certificat != null) {
                System.out.println("✅ SUCCÈS : Certificat trouvé : ID " + certificat.getId());
            } else {
                System.err.println("❌ ERREUR : Certificat non trouvé !");
            }
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario6_SupprimerCertificat() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 6 : Supprimer un certificat");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        if (certificatId1 == null) {
            System.out.println("⏩ SCÉNARIO IGNORÉ : Aucun certificat n'a été créé dans le scénario 1.");
            return;
        }
        try {
            certificatService.supprimerCertificat(certificatId1);
            if (certificatService.consulterCertificat(certificatId1) == null) {
                System.out.println("✅ SUCCÈS : Certificat supprimé.");
            }
            else {
                System.err.println("❌ ERREUR : Le certificat n'a pas été supprimé !");
            }
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }
    
    private static void scenario7_SupprimerCertificatInexistant() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 7 : Supprimer un certificat inexistant");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    
        try {
            certificatService.supprimerCertificat(999L);
            System.err.println("❌ ÉCHEC : La suppression aurait dû lever une exception !");
        } catch (ServiceException e) {
            System.out.println("✅ SUCCÈS : ServiceException correctement levée.");
        }
        System.out.println();
    }
}
