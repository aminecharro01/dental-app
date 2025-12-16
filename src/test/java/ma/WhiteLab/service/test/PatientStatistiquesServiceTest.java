package ma.WhiteLab.service.test;

import ma.WhiteLab.conf.ApplicationContext;
import ma.WhiteLab.common.exceptions.ServiceException;
import ma.WhiteLab.entities.enums.Assurance;
import ma.WhiteLab.entities.enums.Sexe;
import ma.WhiteLab.service.modules.patient.api.PatientStatistiquesService;
import ma.WhiteLab.service.modules.patient.api.PatientService;
import ma.WhiteLab.entities.patient.Patient;

import java.time.LocalDate;
import java.util.Map;

public class PatientStatistiquesServiceTest {

    private static PatientStatistiquesService statistiquesService;
    private static PatientService patientService;

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("  TESTS DU SERVICE STATISTIQUES PATIENT");
        System.out.println("==========================================\n");

        try {
            initialiser();
            preparerDonneesDeTest();

            scenario1_CompterTotalPatients();
            scenario2_CompterPatientsParSexe();
            scenario3_CompterPatientsParAssurance();
            scenario4_CompterNouveauxPatients();
            scenario5_CompterPatientsParTrancheAge();
            scenario6_CalculerAgeMoyen();
            scenario7_TrouverAgeMinMax();
            scenario8_CompterPatientsAvecSansAntecedents();
            scenario9_CalculerPourcentageContacts();

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
        statistiquesService = context.getBean(PatientStatistiquesService.class);
        System.out.println("✅ Initialisation terminée\n");
    }

    private static void preparerDonneesDeTest() throws Exception {
        System.out.println("📋 Préparation des données de test...\n");
        // You may want to add some patients for testing
        // For now, we assume there are some patients in the database
    }

    private static void scenario1_CompterTotalPatients() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 1 : Compter le total des patients");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        try {
            long total = statistiquesService.compterTotalPatients();
            System.out.println("✅ SUCCÈS : Nombre total de patients : " + total);
        } catch (Exception e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario2_CompterPatientsParSexe() throws ServiceException {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 2 : Compter les patients par sexe");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        try {
            Map<Sexe, Long> stats = statistiquesService.compterPatientsParSexe();
            System.out.println("✅ SUCCÈS : Statistiques par sexe : " + stats);
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario3_CompterPatientsParAssurance() throws ServiceException {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 3 : Compter les patients par assurance");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        try {
            Map<Assurance, Long> stats = statistiquesService.compterPatientsParAssurance();
            System.out.println("✅ SUCCÈS : Statistiques par assurance : " + stats);
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario4_CompterNouveauxPatients() throws ServiceException {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 4 : Compter les nouveaux patients");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        try {
            LocalDate debut = LocalDate.now().minusMonths(1);
            LocalDate fin = LocalDate.now();
            long count = statistiquesService.compterNouveauxPatients(debut, fin);
            System.out.println("✅ SUCCÈS : Nouveaux patients depuis " + debut + " : " + count);
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario5_CompterPatientsParTrancheAge() throws ServiceException {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 5 : Compter les patients par tranche d'âge");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        try {
            Map<String, Long> stats = statistiquesService.compterPatientsParTrancheAge();
            System.out.println("✅ SUCCÈS : Statistiques par tranche d'âge : " + stats);
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario6_CalculerAgeMoyen() throws ServiceException {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 6 : Calculer l'âge moyen des patients");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        try {
            double ageMoyen = statistiquesService.calculerAgeMoyen();
            System.out.println("✅ SUCCÈS : Âge moyen des patients : " + ageMoyen);
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario7_TrouverAgeMinMax() throws ServiceException {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 7 : Trouver l'âge minimum et maximum");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        try {
            int ageMin = statistiquesService.trouverAgeMinimum();
            int ageMax = statistiquesService.trouverAgeMaximum();
            System.out.println("✅ SUCCÈS : Âge minimum : " + ageMin + ", Âge maximum : " + ageMax);
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario8_CompterPatientsAvecSansAntecedents() throws ServiceException {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 8 : Compter les patients avec/sans antécédents");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        try {
            long avec = statistiquesService.compterPatientsAvecAntecedents();
            long sans = statistiquesService.compterPatientsSansAntecedents();
            System.out.println("✅ SUCCÈS : Patients avec antécédents : " + avec + ", sans antécédents : " + sans);
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario9_CalculerPourcentageContacts() throws ServiceException {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 9 : Calculer le pourcentage de contacts");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        try {
            double avecEmail = statistiquesService.calculerPourcentagePatientsAvecEmail();
            double avecTel = statistiquesService.calculerPourcentagePatientsAvecTelephone();
            System.out.println("✅ SUCCÈS : % avec email : " + avecEmail + "%, % avec téléphone : " + avecTel + "%");
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }
}
