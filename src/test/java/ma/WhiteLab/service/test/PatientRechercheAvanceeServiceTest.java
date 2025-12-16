package ma.WhiteLab.service.test;

import ma.WhiteLab.conf.ApplicationContext;
import ma.WhiteLab.common.exceptions.ServiceException;
import ma.WhiteLab.entities.patient.Patient;
import ma.WhiteLab.entities.enums.Assurance;
import ma.WhiteLab.entities.enums.Sexe;
import ma.WhiteLab.service.modules.patient.api.PatientRechercheAvanceeService;
import ma.WhiteLab.service.modules.patient.api.PatientService;

import java.time.LocalDate;
import java.util.List;

public class PatientRechercheAvanceeServiceTest {

    private static PatientRechercheAvanceeService rechercheService;
    private static PatientService patientService;

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("  TESTS DU SERVICE RECHERCHE AVANCÉE PATIENT");
        System.out.println("==========================================\n");

        try {
            initialiser();
            preparerDonneesDeTest();

            scenario1_RechercherParSexe();
            scenario2_RechercherParAssurance();
            scenario3_RechercherParTrancheAge();
            scenario4_RechercherParDateNaissance();
            scenario5_RechercherAvecCriteres();
            scenario6_RechercherParPresenceAntecedents();
            scenario7_RechercherParDateEnregistrement();
            scenario8_RechercherParPresenceContacts();
            scenario9_RechercherParVille();

            System.out.println("\n==========================================");
            System.out.println("  TOUS LES TESTS SONT TERMINÉS");
            System.out.println("==========================================");

        } catch (Exception e) {
            System.err.println("ERREUR CRITIQUE : " + e.getMessage());
            e.printStackTrace();
        }
    }

    private static void initialiser() {
        System.out.println("📋 Initialisation des services...\n");
        ApplicationContext context = new ApplicationContext("config/beans.properties");
        patientService = context.getBean(PatientService.class);
        rechercheService = context.getBean(PatientRechercheAvanceeService.class);
        System.out.println("✅ Initialisation terminée\n");
    }

    private static void preparerDonneesDeTest() throws Exception {
        System.out.println("📋 Préparation des données de test...\n");
        // Assuming some patients exist in the database for testing
    }

    private static void scenario1_RechercherParSexe() throws ServiceException {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 1 : Rechercher par sexe");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        try {
            List<Patient> patients = rechercheService.rechercherParSexe(Sexe.Femme);
            System.out.println("✅ SUCCÈS : " + patients.size() + " patients de sexe féminin trouvés.");
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario2_RechercherParAssurance() throws ServiceException {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 2 : Rechercher par assurance");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        try {
            List<Patient> patients = rechercheService.rechercherParAssurance(Assurance.CNOPS);
            System.out.println("✅ SUCCÈS : " + patients.size() + " patients avec assurance CNOPS trouvés.");
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario3_RechercherParTrancheAge() throws ServiceException {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 3 : Rechercher par tranche d'âge");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        try {
            List<Patient> patients = rechercheService.rechercherParTrancheAge(30, 40);
            System.out.println("✅ SUCCÈS : " + patients.size() + " patients âgés de 30 à 40 ans trouvés.");
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario4_RechercherParDateNaissance() throws ServiceException {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 4 : Rechercher par date de naissance");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        try {
            LocalDate debut = LocalDate.of(1980, 1, 1);
            LocalDate fin = LocalDate.of(1990, 12, 31);
            List<Patient> patients = rechercheService.rechercherParDateNaissance(debut, fin);
            System.out.println("✅ SUCCÈS : " + patients.size() + " patients nés entre " + debut + " et " + fin + " trouvés.");
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario5_RechercherAvecCriteres() throws ServiceException {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 5 : Rechercher avec critères multiples");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        try {
            List<Patient> patients = rechercheService.rechercherAvecCriteres("Alami", null, Sexe.Femme, null);
            System.out.println("✅ SUCCÈS : " + patients.size() + " patients trouvés pour les critères donnés.");
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario6_RechercherParPresenceAntecedents() throws ServiceException {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 6 : Rechercher par présence d'antécédents");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        try {
            List<Patient> patients = rechercheService.rechercherParPresenceAntecedents(true);
            System.out.println("✅ SUCCÈS : " + patients.size() + " patients avec antécédents trouvés.");
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario7_RechercherParDateEnregistrement() throws ServiceException {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 7 : Rechercher par date d'enregistrement");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        try {
            LocalDate debut = LocalDate.now().minusMonths(6);
            LocalDate fin = LocalDate.now();
            List<Patient> patients = rechercheService.rechercherParDateEnregistrement(debut, fin);
            System.out.println("✅ SUCCÈS : " + patients.size() + " patients enregistrés depuis 6 mois trouvés.");
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario8_RechercherParPresenceContacts() throws ServiceException {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 8 : Rechercher par présence de contacts");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        try {
            List<Patient> avecEmail = rechercheService.rechercherParPresenceEmail(true);
            List<Patient> sansTelephone = rechercheService.rechercherParPresenceTelephone(false);
            System.out.println("✅ SUCCÈS : " + avecEmail.size() + " patients avec email, " + sansTelephone.size() + " patients sans téléphone.");
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario9_RechercherParVille() throws ServiceException {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 9 : Rechercher par ville");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        try {
            List<Patient> patients = rechercheService.rechercherParVille("Rabat");
            System.out.println("✅ SUCCÈS : " + patients.size() + " patients trouvés à Rabat.");
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }
}
