package ma.WhiteLab.service.modules.notifications.impl;

import ma.WhiteLab.entities.user.Notification;
import ma.WhiteLab.entities.enums.TypeNotification;
import ma.WhiteLab.entities.enums.PrioriteNotification;
import ma.WhiteLab.entities.enums.TitreNotification;
import ma.WhiteLab.common.exceptions.ServiceException;
import ma.WhiteLab.common.exceptions.ValidationException;
import ma.WhiteLab.common.validation.Validators;
import ma.WhiteLab.repository.modules.notifications.api.NotificationRepository;
import ma.WhiteLab.repository.modules.user.impl.mySQL.UtilisateurBaseRepositoryImpl;
import ma.WhiteLab.entities.user.Utilisateur;
import ma.WhiteLab.service.modules.notifications.api.NotificationService;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

/**
 * Implémentation du service métier pour la gestion des notifications
 * 
 * Cette classe contient toute la logique métier pour gérer les notifications.
 * Elle utilise les repositories pour accéder à la base de données.
 * 
 * Les méthodes vérifient les données avant de les enregistrer
 * et lancent des exceptions si quelque chose ne va pas.
 */
public class NotificationServiceImpl implements NotificationService {

    // Les repositories qu'on utilise pour accéder à la base de données
    private final NotificationRepository notificationRepository;
    private final UtilisateurBaseRepositoryImpl utilisateurRepository;

    /**
     * Constructeur : on reçoit les repositories en paramètre
     * (injection de dépendance)
     * 
     * @param notificationRepository Le repository pour accéder aux notifications
     * @param utilisateurRepository Le repository pour accéder aux utilisateurs
     */
    public NotificationServiceImpl(
            NotificationRepository notificationRepository,
            UtilisateurBaseRepositoryImpl utilisateurRepository
    ) {
        this.notificationRepository = notificationRepository;
        this.utilisateurRepository = utilisateurRepository;
    }

    // ============================================
    //  MÉTHODES DE GESTION DES NOTIFICATIONS
    // ============================================

    @Override
    public Notification creerNotification(Notification notification) throws ValidationException, ServiceException {
        // 1. Vérifier que la notification n'est pas null
        if (notification == null) {
            throw new ValidationException("La notification ne peut pas être null");
        }

        // 2. Valider les données obligatoires
        validerDonneesNotification(notification);

        // 3. Définir la date de création si elle n'est pas définie
        if (notification.getDateCreation() == null) {
            notification.setDateCreation(LocalDateTime.now());
        }

        // 4. Définir la date et l'heure si elles ne sont pas définies
        if (notification.getDate() == null) {
            notification.setDate(LocalDate.now());
        }
        if (notification.getTime() == null) {
            notification.setTime(LocalTime.now());
        }

        // 5. Créer la notification dans la base de données
        try {
            notificationRepository.create(notification);
            return notification;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la création de la notification : " + e.getMessage(), e);
        }
    }

    @Override
    public Notification creerNotification(
            TitreNotification titre,
            String message,
            TypeNotification type,
            PrioriteNotification priorite,
            String creePar
    ) throws ValidationException, ServiceException {
        // Créer une nouvelle notification avec les paramètres fournis
        Notification notification = Notification.builder()
                .titre(titre)
                .message(message)
                .type(type)
                .priorite(priorite)
                .creePar(creePar)
                .date(LocalDate.now())
                .time(LocalTime.now())
                .dateCreation(LocalDateTime.now())
                .build();

        // Utiliser la méthode principale pour créer la notification
        return creerNotification(notification);
    }

    @Override
    public Notification modifierNotification(Notification notification) throws ValidationException, ServiceException {
        // 1. Vérifier que la notification n'est pas null
        if (notification == null) {
            throw new ValidationException("La notification ne peut pas être null");
        }

        // 2. Vérifier que la notification a un ID
        if (notification.getId() == null) {
            throw new ValidationException("L'ID de la notification est obligatoire pour la modification");
        }

        // 3. Vérifier que la notification existe
        if (!notificationExiste(notification.getId())) {
            throw new ServiceException("La notification avec l'ID " + notification.getId() + " n'existe pas");
        }

        // 4. Valider les données
        validerDonneesNotification(notification);

        // 5. Mettre à jour la notification
        try {
            notificationRepository.update(notification);
            return notification;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la modification de la notification : " + e.getMessage(), e);
        }
    }

    @Override
    public void supprimerNotification(Long notificationId) throws ServiceException {
        // 1. Vérifier que l'ID n'est pas null
        if (notificationId == null) {
            throw new ServiceException("L'ID de la notification ne peut pas être null");
        }

        // 2. Vérifier que la notification existe
        if (!notificationExiste(notificationId)) {
            throw new ServiceException("La notification avec l'ID " + notificationId + " n'existe pas");
        }

        // 3. Supprimer la notification
        try {
            notificationRepository.deleteById(notificationId);
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la suppression de la notification : " + e.getMessage(), e);
        }
    }

    @Override
    public Notification consulterNotification(Long notificationId) {
        if (notificationId == null) {
            return null;
        }
        return notificationRepository.findById(notificationId);
    }

    @Override
    public List<Notification> listerToutesLesNotifications() {
        return notificationRepository.findAll();
    }

    @Override
    public List<Notification> consulterNotificationsParType(TypeNotification type) {
        if (type == null) {
            return listerToutesLesNotifications();
        }
        return notificationRepository.findByType(type.name());
    }

    @Override
    public List<Notification> consulterNotificationsParPriorite(PrioriteNotification priorite) {
        if (priorite == null) {
            return listerToutesLesNotifications();
        }
        return notificationRepository.findByPriorite(priorite.name());
    }

    @Override
    public Notification consulterNotificationParTitre(TitreNotification titre) {
        if (titre == null) {
            return null;
        }
        return notificationRepository.findByTitre(titre.name()).orElse(null);
    }

    @Override
    public boolean notificationExiste(Long notificationId) {
        if (notificationId == null) {
            return false;
        }
        Notification notif = notificationRepository.findById(notificationId);
        return notif != null;
    }

    @Override
    public long compterNotifications() {
        return notificationRepository.count();
    }

    // ============================================
    //  MÉTHODES DE GESTION UTILISATEUR-NOTIFICATION
    // ============================================

    @Override
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

    @Override
    public Notification envoyerNotification(
            Long utilisateurId,
            TitreNotification titre,
            String message,
            TypeNotification type,
            PrioriteNotification priorite,
            String creePar
    ) throws ValidationException, ServiceException {
        // Créer une notification avec les paramètres fournis
        Notification notification = Notification.builder()
                .titre(titre)
                .message(message)
                .type(type)
                .priorite(priorite)
                .creePar(creePar)
                .date(LocalDate.now())
                .time(LocalTime.now())
                .dateCreation(LocalDateTime.now())
                .build();

        // Utiliser la méthode principale pour envoyer la notification
        return envoyerNotification(utilisateurId, notification);
    }

    @Override
    public void retirerNotificationUtilisateur(Long utilisateurId, Long notificationId) throws ServiceException {
        // 1. Vérifier que les IDs ne sont pas null
        if (utilisateurId == null) {
            throw new ServiceException("L'ID de l'utilisateur ne peut pas être null");
        }
        if (notificationId == null) {
            throw new ServiceException("L'ID de la notification ne peut pas être null");
        }

        // 2. Vérifier que l'utilisateur existe
        Utilisateur utilisateur = utilisateurRepository.findById(utilisateurId);
        if (utilisateur == null) {
            throw new ServiceException("L'utilisateur avec l'ID " + utilisateurId + " n'existe pas");
        }

        // 3. Vérifier que la notification existe
        if (!notificationExiste(notificationId)) {
            throw new ServiceException("La notification avec l'ID " + notificationId + " n'existe pas");
        }

        // 4. Retirer la notification de l'utilisateur
        try {
            utilisateurRepository.removeNotificationFromUtilisateur(utilisateurId, notificationId);
        } catch (Exception e) {
            throw new ServiceException("Erreur lors du retrait de la notification : " + e.getMessage(), e);
        }
    }

    // ============================================
    //  MÉTHODES PRIVÉES (UTILITAIRES)
    // ============================================

    /**
     * Valide les données d'une notification avant création/modification
     * 
     * @param notification La notification à valider
     * @throws ValidationException Si les données sont invalides
     */
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
}

