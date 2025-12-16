package ma.WhiteLab.service.test;

import ma.WhiteLab.conf.ApplicationContext;
import ma.WhiteLab.common.exceptions.ServiceException;
import ma.WhiteLab.common.exceptions.ValidationException;
import ma.WhiteLab.entities.patient.Antecedent;
import ma.WhiteLab.entities.enums.CategorieAntecedent;
import ma.WhiteLab.entities.enums.NiveauDeRisk;
import ma.WhiteLab.repository.modules.patient.api.AntecedentRepository;
import ma.WhiteLab.service.modules.patient.api.AntecedentService;

import java.time.LocalDateTime;
import java.util.List;

public class AntecedentServiceTest {

    private static AntecedentService antecedentService;
    private static AntecedentRepository antecedentRepository;

    private static Long antecedentId1;

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("  TESTS DU SERVICE ANTECEDENT");
        System.out.println("==========================================\n");

        try {
            initialiser();
            preparerDonneesDeTest(); // Ensure a clean state

            scenario1_EnregistrerNouvelAntecedent();
            scenario2_EnregistrerAntecedentAvecDonneesInvalides();
            scenario3_EnregistrerAntecedentAvecNomExistant();
            scenario4_ModifierAntecedent();
            scenario5_ModifierAntecedentInexistant();
            scenario6_ConsulterAntecedent();
            scenario7_ListerTousLesAntecedents();
            scenario8_RechercherAntecedentParNom();
            scenario9_ListerAntecedentsParCategorie();
            scenario10_SupprimerAntecedent();
            scenario11_SupprimerAntecedentInexistant();

            System.out.println("\n==========================================");
            System.out.println("  TOUS LES TESTS SONT TERMINÉS");
            System.out.println("==========================================");

        } catch (Exception e) {
            System.err.println("ERREUR CRITIQUE : " + e.getMessage());
            e.printStackTrace();
        } finally {
            nettoyerDonneesDeTest(); // Cleanup after all scenarios
        }
    }

    private static void initialiser() {
        System.out.println("📋 Initialisation des repositories et services...\n");
        ApplicationContext context = new ApplicationContext("config/beans.properties");
        antecedentRepository = context.getBean(AntecedentRepository.class);
        antecedentService = context.getBean(AntecedentService.class);
        System.out.println("✅ Initialisation terminée\n");
    }

    private static void preparerDonneesDeTest() {
        System.out.println("📋 Préparation des données de test (nettoyage)...");
        try {
            antecedentRepository.findByNom("Asthme").ifPresent(antecedent -> {
                try {
                    antecedentService.supprimerAntecedent(antecedent.getId());
                    System.out.println("   - Ancien antécédent 'Asthme' supprimé.");
                } catch (ServiceException e) {
                    // Ignore if patient is linked, main scenario will fail and that's ok
                }
            });
             antecedentRepository.findByNom("Temp Antecedent").ifPresent(antecedent -> {
                try {
                    antecedentService.supprimerAntecedent(antecedent.getId());
                    System.out.println("   - Ancien antécédent 'Temp Antecedent' supprimé.");
                } catch (ServiceException e) {
                     // Ignore if patient is linked, main scenario will fail and that's ok
                }
            });
        } catch (Exception e) {
            System.err.println("   - Erreur lors du nettoyage initial : " + e.getMessage());
        }
        System.out.println("✅ Préparation terminée\n");
    }
    
    private static void nettoyerDonneesDeTest() {
        System.out.println("\n🧹 Nettoyage final des données de test...");
        if (antecedentId1 != null) {
            try {
                antecedentService.supprimerAntecedent(antecedentId1);
                System.out.println("   - Antécédent de test (Asthme) supprimé.");
            } catch (ServiceException e) {
                // It might have been deleted by a scenario, which is fine
            }
        }
    }

    private static void scenario1_EnregistrerNouvelAntecedent() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 1 : Enregistrer un nouvel antécédent");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            Antecedent nouvelAntecedent = Antecedent.builder()
                    .nom("Asthme")
                    .description("Asthme bronchique depuis l'enfance")
                    .categorie(CategorieAntecedent.MALADIE_CHRONIQUE)
                    .niveauDeRisk(NiveauDeRisk.MOYEN)
                    .build();

            System.out.println("📝 Tentative d'enregistrement de l'antécédent : " + nouvelAntecedent.getNom());

            Antecedent antecedentEnregistre = antecedentService.enregistrerAntecedent(nouvelAntecedent);
            antecedentId1 = antecedentEnregistre.getId();

            System.out.println("✅ SUCCÈS : Antécédent enregistré avec l'ID : " + antecedentId1);
            System.out.println("   - Nom : " + antecedentEnregistre.getNom());
            System.out.println("   - Catégorie : " + antecedentEnregistre.getCategorie());
            System.out.println("   - Date de création : " + antecedentEnregistre.getDateCreation());

        } catch (ValidationException | ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario2_EnregistrerAntecedentAvecDonneesInvalides() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 2 : Antécédent avec données invalides");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            Antecedent antecedentInvalide = Antecedent.builder().nom("").description("d").categorie(CategorieAntecedent.ALLERGIE).niveauDeRisk(NiveauDeRisk.FAIBLE).build();
            System.out.println("📝 Tentative d'enregistrement d'un antécédent sans nom...");
            antecedentService.enregistrerAntecedent(antecedentInvalide);
            System.err.println("❌ ÉCHEC : L'antécédent a été enregistré alors qu'il ne devrait pas l'être !");
        } catch (ValidationException e) {
            System.out.println("✅ SUCCÈS : ValidationException correctement levée");
            System.out.println("   Message : " + e.getMessage());
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario3_EnregistrerAntecedentAvecNomExistant() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 3 : Antécédent avec nom existant");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            Antecedent antecedentDuplique = Antecedent.builder()
                    .nom("Asthme")
                    .description("Description test")
                    .categorie(CategorieAntecedent.ALLERGIE)
                    .niveauDeRisk(NiveauDeRisk.FAIBLE)
                    .build();
            System.out.println("📝 Tentative d'enregistrement avec un nom déjà utilisé...");
            antecedentService.enregistrerAntecedent(antecedentDuplique);
            System.err.println("❌ ÉCHEC : L'antécédent a été enregistré avec un nom dupliqué !");
        } catch (ValidationException e) {
            System.out.println("✅ SUCCÈS : ValidationException correctement levée");
            System.out.println("   Message : " + e.getMessage());
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario4_ModifierAntecedent() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 4 : Modifier un antécédent existant");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        if (antecedentId1 == null) {
            System.out.println("⏩ SCÉNARIO IGNORÉ : Aucun antécédent n'a été créé dans le scénario 1.");
            return;
        }

        try {
            Antecedent antecedent = antecedentService.consulterAntecedent(antecedentId1);
            System.out.println("📝 Antécédent avant modification :");
            System.out.println("   - Description : " + antecedent.getDescription());
            System.out.println("   - Niveau de risque : " + antecedent.getNiveauDeRisk());

            antecedent.setDescription("Description modifiée");
            antecedent.setNiveauDeRisk(NiveauDeRisk.ELEVE);

            Antecedent antecedentModifie = antecedentService.modifierAntecedent(antecedent);

            System.out.println("✅ SUCCÈS : Antécédent modifié");
            System.out.println("   - Nouvelle description : " + antecedentModifie.getDescription());
            System.out.println("   - Nouveau niveau de risque : " + antecedentModifie.getNiveauDeRisk());
        } catch (ValidationException | ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario5_ModifierAntecedentInexistant() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 5 : Modifier un antécédent inexistant");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            Antecedent antecedentInexistant = Antecedent.builder()
                    .id(99999L)
                    .nom("Test")
                    .description("Test")
                    .categorie(CategorieAntecedent.AUTRE)
                    .niveauDeRisk(NiveauDeRisk.FAIBLE)
                    .build();
            System.out.println("📝 Tentative de modification d'un antécédent inexistant (ID: 99999)...");
            antecedentService.modifierAntecedent(antecedentInexistant);
            System.err.println("❌ ÉCHEC : L'antécédent a été modifié alors qu'il n'existe pas !");
        } catch (ServiceException e) {
            System.out.println("✅ SUCCÈS : ServiceException correctement levée");
            System.out.println("   Message : " + e.getMessage());
        } catch (ValidationException e) {
            System.err.println("❌ ERREUR DE VALIDATION : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario6_ConsulterAntecedent() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 6 : Consulter un antécédent");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            System.out.println("📝 Consultation de l'antécédent avec l'ID : " + antecedentId1);
            Antecedent antecedent = antecedentService.consulterAntecedent(antecedentId1);
            if (antecedent != null) {
                System.out.println("✅ SUCCÈS : Antécédent trouvé");
                System.out.println("   - ID : " + antecedent.getId());
                System.out.println("   - Nom : " + antecedent.getNom());
            } else {
                System.out.println("⚠️  Antécédent non trouvé");
            }
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario7_ListerTousLesAntecedents() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 7 : Lister tous les antécédents");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            System.out.println("📝 Récupération de tous les antécédents...");
            List<Antecedent> antecedents = antecedentService.listerTousLesAntecedents();
            System.out.println("✅ SUCCÈS : " + antecedents.size() + " antécédent(s) trouvé(s)");
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario8_RechercherAntecedentParNom() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 8 : Rechercher un antécédent par nom");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            System.out.println("📝 Recherche par mot-clé 'Asthme'...");
            List<Antecedent> resultats = antecedentService.rechercherAntecedentParNom("Asthme");
            System.out.println("✅ SUCCÈS : " + resultats.size() + " antécédent(s) trouvé(s)");
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario9_ListerAntecedentsParCategorie() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 9 : Lister les antécédents par catégorie");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            System.out.println("📝 Liste des antécédents de catégorie 'MALADIE_CHRONIQUE'...");
            List<Antecedent> resultats = antecedentService.listerAntecedentsParCategorie(CategorieAntecedent.MALADIE_CHRONIQUE);
            System.out.println("✅ SUCCÈS : " + resultats.size() + " antécédent(s) trouvé(s)");
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario10_SupprimerAntecedent() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 10 : Supprimer un antécédent");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            Antecedent tempAntecedent = Antecedent.builder()
                .nom("Temp Antecedent")
                .description("A supprimer")
                .categorie(CategorieAntecedent.AUTRE)
                .niveauDeRisk(NiveauDeRisk.FAIBLE)
                .build();
            tempAntecedent = antecedentService.enregistrerAntecedent(tempAntecedent);
            
            System.out.println("📝 Suppression de l'antécédent (ID: " + tempAntecedent.getId() + ")...");
            antecedentService.supprimerAntecedent(tempAntecedent.getId());
            System.out.println("✅ SUCCÈS : Antécédent supprimé");

            Antecedent antecedent = antecedentService.consulterAntecedent(tempAntecedent.getId());
            if (antecedent == null) {
                System.out.println("   ✅ Vérification : Antécédent bien supprimé (retourne null)");
            } else {
                System.err.println("   ❌ ERREUR : L'antécédent existe encore !");
            }
        } catch (ServiceException | ValidationException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }

    private static void scenario11_SupprimerAntecedentInexistant() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 11 : Supprimer un antécédent inexistant");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            System.out.println("📝 Tentative de suppression d'un antécédent inexistant (ID: 99999)...");
            antecedentService.supprimerAntecedent(99999L);
            System.out.println("✅ SUCCÈS : Aucune erreur levée pour un ID inexistant (comportement attendu)");
        } catch (ServiceException e) {
             System.err.println("❌ ERREUR : " + e.getMessage());
        }
        System.out.println();
    }
}
