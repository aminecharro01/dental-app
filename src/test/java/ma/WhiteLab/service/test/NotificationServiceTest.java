package ma.WhiteLab.service.test;

import ma.WhiteLab.conf.ApplicationContext;
import ma.WhiteLab.common.exceptions.ServiceException;
import ma.WhiteLab.common.exceptions.ValidationException;
import ma.WhiteLab.entities.user.Notification;
import ma.WhiteLab.entities.enums.TitreNotification;
import ma.WhiteLab.entities.enums.TypeNotification;
import ma.WhiteLab.entities.enums.PrioriteNotification;
import ma.WhiteLab.repository.modules.notifications.api.NotificationRepository;
import ma.WhiteLab.repository.modules.user.impl.mySQL.UtilisateurBaseRepositoryImpl;
import ma.WhiteLab.service.modules.notifications.api.NotificationService;
import ma.WhiteLab.service.modules.notifications.impl.NotificationServiceImpl;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

/**
 * Classe de test pour le service Notification
 * 
 * Cette classe contient plusieurs scénarios de test pour vérifier
 * que le service Notification fonctionne correctement.
 * 
 * Pour exécuter : Run -> NotificationServiceTest.main()
 */
public class NotificationServiceTest {

    private static NotificationService notificationService;
    private static NotificationRepository notificationRepository;
    private static UtilisateurBaseRepositoryImpl utilisateurRepository;

    // IDs pour les tests
    private static Long notificationId1, notificationId2, notificationId3;
    private static Long utilisateurId;

    public static void main(String[] args) {
        System.out.println("==========================================");
        System.out.println("  TESTS DU SERVICE NOTIFICATION");
        System.out.println("==========================================\n");

        try {
            // Initialiser les repositories et le service
            initialiser();

            // Exécuter tous les scénarios de test
            scenario1_CreerNotification();
            scenario2_CreerNotificationAvecDonneesInvalides();
            scenario3_CreerNotificationRapide();
            scenario4_ModifierNotification();
            scenario5_ModifierNotificationInexistante();
            scenario6_ConsulterNotification();
            scenario7_ConsulterNotificationsParType();
            scenario8_ConsulterNotificationsParPriorite();
            scenario9_ConsulterNotificationParTitre();
            scenario10_CompterNotifications();
            scenario11_EnvoyerNotificationAUtilisateur();
            scenario12_ConsulterNotificationsUtilisateur();
            scenario13_RetirerNotificationUtilisateur();
            scenario14_SupprimerNotification();
            scenario15_SupprimerNotificationInexistante();

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
        notificationRepository = context.getBean(NotificationRepository.class);
        utilisateurRepository = context.getBean(UtilisateurBaseRepositoryImpl.class);

        // Créer le service avec injection de dépendance
        notificationService = new NotificationServiceImpl(notificationRepository, utilisateurRepository);

        // Créer ou récupérer un utilisateur de test
        creerUtilisateurTest();

        System.out.println("✅ Initialisation terminée\n");
    }

    /**
     * Crée un utilisateur de test pour les scénarios
     */
    private static void creerUtilisateurTest() {
        try {
            // Chercher un utilisateur existant ou en créer un
            // Utiliser findById avec un ID connu ou findAll() qui retourne une liste
            // Pour simplifier, on essaie de trouver un utilisateur par ID 1
            var utilisateur = utilisateurRepository.findById(1L);
            if (utilisateur != null) {
                utilisateurId = utilisateur.getId();
                System.out.println("   Utilisateur de test trouvé (ID: " + utilisateurId + ")");
            } else {
                // Essayer avec findAll
                var utilisateurs = utilisateurRepository.findAll();
                if (!utilisateurs.isEmpty()) {
                    utilisateurId = utilisateurs.get(0).getId();
                    System.out.println("   Utilisateur de test trouvé (ID: " + utilisateurId + ")");
                } else {
                    System.out.println("   ⚠️  Aucun utilisateur trouvé. Les tests d'envoi nécessitent un utilisateur.");
                    System.out.println("   Créez d'abord un utilisateur dans la base de données.");
                }
            }
        } catch (Exception e) {
            System.out.println("   ⚠️  Erreur lors de la recherche d'utilisateur : " + e.getMessage());
        }
    }

    // ============================================
    //  SCÉNARIOS DE TEST
    // ============================================

    /**
     * SCÉNARIO 1 : Créer une notification avec succès
     */
    private static void scenario1_CreerNotification() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 1 : Créer une notification");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            // Créer une notification
            Notification notification = Notification.builder()
                    .titre(TitreNotification.RAPPEL)
                    .message("Vous avez un rendez-vous demain à 10h00")
                    .type(TypeNotification.RENDEZVOUS)
                    .priorite(PrioriteNotification.HAUTE)
                    .date(LocalDate.now())
                    .time(LocalTime.now())
                    .creePar("Système")
                    .build();

            System.out.println("📝 Tentative de création d'une notification...");

            // Créer la notification
            Notification notificationCreee = notificationService.creerNotification(notification);
            notificationId1 = notificationCreee.getId();

            System.out.println("✅ SUCCÈS : Notification créée avec l'ID : " + notificationId1);
            System.out.println("   - Titre : " + notificationCreee.getTitre());
            System.out.println("   - Message : " + notificationCreee.getMessage());
            System.out.println("   - Type : " + notificationCreee.getType());
            System.out.println("   - Priorité : " + notificationCreee.getPriorite());
            System.out.println("   - Date de création : " + notificationCreee.getDateCreation());

        } catch (ValidationException e) {
            System.err.println("❌ ERREUR DE VALIDATION : " + e.getMessage());
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR DE SERVICE : " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * SCÉNARIO 2 : Tenter de créer une notification avec des données invalides
     */
    private static void scenario2_CreerNotificationAvecDonneesInvalides() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 2 : Notification avec données invalides");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            // Notification sans message (invalide)
            Notification notificationInvalide = Notification.builder()
                    .titre(TitreNotification.INFO)
                    .type(TypeNotification.INFO)
                    .priorite(PrioriteNotification.BASSE)
                    .creePar("Système")
                    .build();

            System.out.println("📝 Tentative de création d'une notification sans message...");

            notificationService.creerNotification(notificationInvalide);

            System.err.println("❌ ÉCHEC : La notification a été créée alors qu'elle ne devrait pas l'être !");

        } catch (ValidationException e) {
            System.out.println("✅ SUCCÈS : ValidationException correctement levée");
            System.out.println("   Message : " + e.getMessage());
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * SCÉNARIO 3 : Créer une notification rapidement (méthode de convenance)
     */
    private static void scenario3_CreerNotificationRapide() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 3 : Créer une notification rapidement");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            System.out.println("📝 Création rapide d'une notification...");

            Notification notification = notificationService.creerNotification(
                    TitreNotification.ALERTE,
                    "Attention : Facture impayée depuis 30 jours",
                    TypeNotification.FACTURE,
                    PrioriteNotification.HAUTE,
                    "Système"
            );

            notificationId2 = notification.getId();

            System.out.println("✅ SUCCÈS : Notification créée rapidement avec l'ID : " + notificationId2);
            System.out.println("   - Titre : " + notification.getTitre());
            System.out.println("   - Message : " + notification.getMessage());

        } catch (ValidationException e) {
            System.err.println("❌ ERREUR DE VALIDATION : " + e.getMessage());
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR DE SERVICE : " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * SCÉNARIO 4 : Modifier une notification existante
     */
    private static void scenario4_ModifierNotification() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 4 : Modifier une notification");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            // Récupérer la notification
            Notification notification = notificationService.consulterNotification(notificationId1);
            if (notification == null) {
                System.err.println("❌ Notification non trouvée");
                return;
            }

            System.out.println("📝 Notification avant modification :");
            System.out.println("   - Message : " + notification.getMessage());
            System.out.println("   - Priorité : " + notification.getPriorite());

            // Modifier le message et la priorité
            notification.setMessage("Vous avez un rendez-vous demain à 10h00 - RAPPEL URGENT");
            notification.setPriorite(PrioriteNotification.HAUTE);

            // Enregistrer les modifications
            Notification notificationModifiee = notificationService.modifierNotification(notification);

            System.out.println("✅ SUCCÈS : Notification modifiée");
            System.out.println("   - Nouveau message : " + notificationModifiee.getMessage());
            System.out.println("   - Nouvelle priorité : " + notificationModifiee.getPriorite());

        } catch (ValidationException e) {
            System.err.println("❌ ERREUR DE VALIDATION : " + e.getMessage());
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR DE SERVICE : " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * SCÉNARIO 5 : Tenter de modifier une notification inexistante
     */
    private static void scenario5_ModifierNotificationInexistante() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 5 : Modifier une notification inexistante");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            // Créer une notification avec un ID inexistant
            Notification notificationInexistante = Notification.builder()
                    .id(99999L) // ID qui n'existe pas
                    .titre(TitreNotification.INFO)
                    .message("Test")
                    .type(TypeNotification.INFO)
                    .priorite(PrioriteNotification.BASSE)
                    .creePar("Test")
                    .build();

            System.out.println("📝 Tentative de modification d'une notification inexistante (ID: 99999)...");

            notificationService.modifierNotification(notificationInexistante);

            System.err.println("❌ ÉCHEC : La notification a été modifiée alors qu'elle n'existe pas !");

        } catch (ValidationException e) {
            System.err.println("❌ ERREUR DE VALIDATION : " + e.getMessage());
        } catch (ServiceException e) {
            System.out.println("✅ SUCCÈS : ServiceException correctement levée");
            System.out.println("   Message : " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * SCÉNARIO 6 : Consulter une notification par ID
     */
    private static void scenario6_ConsulterNotification() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 6 : Consulter une notification");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            System.out.println("📝 Consultation de la notification avec l'ID : " + notificationId1);

            Notification notification = notificationService.consulterNotification(notificationId1);

            if (notification != null) {
                System.out.println("✅ SUCCÈS : Notification trouvée");
                System.out.println("   - ID : " + notification.getId());
                System.out.println("   - Titre : " + notification.getTitre());
                System.out.println("   - Message : " + notification.getMessage());
                System.out.println("   - Type : " + notification.getType());
                System.out.println("   - Priorité : " + notification.getPriorite());
                System.out.println("   - Date : " + notification.getDate());
                System.out.println("   - Heure : " + notification.getTime());
            } else {
                System.out.println("⚠️  Notification non trouvée");
            }

        } catch (Exception e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * SCÉNARIO 7 : Consulter les notifications par type
     */
    private static void scenario7_ConsulterNotificationsParType() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 7 : Consulter les notifications par type");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            System.out.println("📝 Recherche des notifications de type RENDEZVOUS...");

            List<Notification> notifications = notificationService.consulterNotificationsParType(TypeNotification.RENDEZVOUS);

            System.out.println("✅ SUCCÈS : " + notifications.size() + " notification(s) trouvée(s)");
            for (Notification n : notifications) {
                System.out.println("   - " + n.getTitre() + " : " + n.getMessage() + " (ID: " + n.getId() + ")");
            }

        } catch (Exception e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * SCÉNARIO 8 : Consulter les notifications par priorité
     */
    private static void scenario8_ConsulterNotificationsParPriorite() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 8 : Consulter les notifications par priorité");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            System.out.println("📝 Recherche des notifications de priorité HAUTE...");

            List<Notification> notifications = notificationService.consulterNotificationsParPriorite(PrioriteNotification.HAUTE);

            System.out.println("✅ SUCCÈS : " + notifications.size() + " notification(s) trouvée(s)");
            for (Notification n : notifications) {
                System.out.println("   - " + n.getTitre() + " : " + n.getMessage() + " (Priorité: " + n.getPriorite() + ")");
            }

        } catch (Exception e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * SCÉNARIO 9 : Consulter une notification par titre
     */
    private static void scenario9_ConsulterNotificationParTitre() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 9 : Consulter une notification par titre");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            System.out.println("📝 Recherche d'une notification avec le titre ALERTE...");

            Notification notification = notificationService.consulterNotificationParTitre(TitreNotification.ALERTE);

            if (notification != null) {
                System.out.println("✅ SUCCÈS : Notification trouvée");
                System.out.println("   - ID : " + notification.getId());
                System.out.println("   - Message : " + notification.getMessage());
            } else {
                System.out.println("⚠️  Aucune notification trouvée avec ce titre");
            }

        } catch (Exception e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * SCÉNARIO 10 : Compter le nombre de notifications
     */
    private static void scenario10_CompterNotifications() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 10 : Compter les notifications");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            System.out.println("📝 Comptage du nombre total de notifications...");

            long nombre = notificationService.compterNotifications();

            System.out.println("✅ SUCCÈS : Nombre total de notifications = " + nombre);

        } catch (Exception e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * SCÉNARIO 11 : Envoyer une notification à un utilisateur
     */
    private static void scenario11_EnvoyerNotificationAUtilisateur() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 11 : Envoyer une notification à un utilisateur");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        if (utilisateurId == null) {
            System.out.println("⚠️  SKIP : Aucun utilisateur disponible pour ce test");
            System.out.println();
            return;
        }

        try {
            System.out.println("📝 Envoi d'une notification à l'utilisateur (ID: " + utilisateurId + ")...");

            Notification notification = notificationService.envoyerNotification(
                    utilisateurId,
                    TitreNotification.INFO,
                    "Bienvenue dans le système WhiteLab !",
                    TypeNotification.SUCCES,
                    PrioriteNotification.MOYENNE,
                    "Système"
            );

            notificationId3 = notification.getId();

            System.out.println("✅ SUCCÈS : Notification envoyée avec l'ID : " + notificationId3);
            System.out.println("   - Titre : " + notification.getTitre());
            System.out.println("   - Message : " + notification.getMessage());
            System.out.println("   - Type : " + notification.getType());

        } catch (ValidationException e) {
            System.err.println("❌ ERREUR DE VALIDATION : " + e.getMessage());
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR DE SERVICE : " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * SCÉNARIO 12 : Consulter les notifications d'un utilisateur
     */
    private static void scenario12_ConsulterNotificationsUtilisateur() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 12 : Consulter les notifications d'un utilisateur");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        if (utilisateurId == null) {
            System.out.println("⚠️  SKIP : Aucun utilisateur disponible pour ce test");
            System.out.println();
            return;
        }

        try {
            System.out.println("📝 Consultation des notifications de l'utilisateur (ID: " + utilisateurId + ")...");

            // Utiliser le repository directement car le service n'a pas cette méthode
            List<Notification> notifications = utilisateurRepository.getNotificationsOfUtilisateur(utilisateurId);

            System.out.println("✅ SUCCÈS : " + notifications.size() + " notification(s) trouvée(s)");
            for (Notification n : notifications) {
                System.out.println("   - " + n.getTitre() + " : " + n.getMessage() + " (ID: " + n.getId() + ")");
            }

        } catch (Exception e) {
            System.err.println("❌ ERREUR : " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * SCÉNARIO 13 : Retirer une notification d'un utilisateur
     */
    private static void scenario13_RetirerNotificationUtilisateur() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 13 : Retirer une notification d'un utilisateur");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        if (utilisateurId == null || notificationId3 == null) {
            System.out.println("⚠️  SKIP : Données insuffisantes pour ce test");
            System.out.println();
            return;
        }

        try {
            System.out.println("📝 Retrait de la notification (ID: " + notificationId3 + ") de l'utilisateur (ID: " + utilisateurId + ")...");

            notificationService.retirerNotificationUtilisateur(utilisateurId, notificationId3);

            System.out.println("✅ SUCCÈS : Notification retirée de l'utilisateur");

            // Vérifier qu'elle a bien été retirée
            List<Notification> notifications = utilisateurRepository.getNotificationsOfUtilisateur(utilisateurId);
            boolean existeEncore = notifications.stream()
                    .anyMatch(n -> n.getId().equals(notificationId3));

            if (!existeEncore) {
                System.out.println("   ✅ Vérification : Notification bien retirée");
            } else {
                System.err.println("   ❌ ERREUR : La notification existe encore !");
            }

        } catch (ServiceException e) {
            System.err.println("❌ ERREUR DE SERVICE : " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * SCÉNARIO 14 : Supprimer une notification
     */
    private static void scenario14_SupprimerNotification() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 14 : Supprimer une notification");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            // Créer une notification pour la supprimer
            Notification notificationASupprimer = notificationService.creerNotification(
                    TitreNotification.INFO,
                    "Notification de test à supprimer",
                    TypeNotification.INFO,
                    PrioriteNotification.BASSE,
                    "Test"
            );

            Long idASupprimer = notificationASupprimer.getId();

            System.out.println("📝 Suppression de la notification (ID: " + idASupprimer + ")...");

            notificationService.supprimerNotification(idASupprimer);

            System.out.println("✅ SUCCÈS : Notification supprimée");

            // Vérifier qu'elle a bien été supprimée
            Notification notification = notificationService.consulterNotification(idASupprimer);
            if (notification == null) {
                System.out.println("   ✅ Vérification : Notification bien supprimée (retourne null)");
            } else {
                System.err.println("   ❌ ERREUR : La notification existe encore !");
            }

        } catch (ValidationException e) {
            System.err.println("❌ ERREUR DE VALIDATION : " + e.getMessage());
        } catch (ServiceException e) {
            System.err.println("❌ ERREUR DE SERVICE : " + e.getMessage());
        }

        System.out.println();
    }

    /**
     * SCÉNARIO 15 : Tenter de supprimer une notification inexistante
     */
    private static void scenario15_SupprimerNotificationInexistante() {
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("SCÉNARIO 15 : Supprimer une notification inexistante");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");

        try {
            System.out.println("📝 Tentative de suppression d'une notification inexistante (ID: 99999)...");

            notificationService.supprimerNotification(99999L);

            System.err.println("❌ ÉCHEC : La notification a été supprimée alors qu'elle n'existe pas !");

        } catch (ServiceException e) {
            System.out.println("✅ SUCCÈS : ServiceException correctement levée");
            System.out.println("   Message : " + e.getMessage());
        }

        System.out.println();
    }
}

