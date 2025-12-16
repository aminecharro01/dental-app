package ma.WhiteLab.service.modules.notifications.api;

import ma.WhiteLab.entities.user.Notification;
import ma.WhiteLab.entities.enums.TypeNotification;
import ma.WhiteLab.entities.enums.PrioriteNotification;
import ma.WhiteLab.entities.enums.TitreNotification;
import ma.WhiteLab.common.exceptions.ServiceException;
import ma.WhiteLab.common.exceptions.ValidationException;

import java.util.List;

/**
 * Interface du service métier pour la gestion des notifications
 * 
 * Cette interface définit toutes les opérations métier qu'on peut faire
 * sur les notifications : créer, envoyer, consulter, supprimer, etc.
 * 
 * Les méthodes sont nommées de manière verbale pour être claires
 * (ex: creerNotification, envoyerNotification, etc.)
 */
public interface NotificationService {

    // ============================================
    //  MÉTHODES DE GESTION DES NOTIFICATIONS
    // ============================================

    /**
     * Crée une nouvelle notification dans le système
     * 
     * @param notification La notification à créer
     * @return La notification créée avec son ID généré
     * @throws ValidationException Si les données de la notification sont invalides
     * @throws ServiceException Si une erreur survient lors de la création
     */
    Notification creerNotification(Notification notification) throws ValidationException, ServiceException;

    /**
     * Crée une notification avec les paramètres fournis
     * (méthode de convenance pour créer rapidement une notification)
     * 
     * @param titre Le titre de la notification
     * @param message Le message de la notification
     * @param type Le type de notification
     * @param priorite La priorité de la notification
     * @param creePar Le nom de la personne qui crée la notification
     * @return La notification créée
     * @throws ValidationException Si les données sont invalides
     * @throws ServiceException Si une erreur survient
     */
    Notification creerNotification(
            TitreNotification titre,
            String message,
            TypeNotification type,
            PrioriteNotification priorite,
            String creePar
    ) throws ValidationException, ServiceException;

    /**
     * Modifie une notification existante
     * 
     * @param notification La notification avec les nouvelles informations (doit avoir un ID)
     * @return La notification modifiée
     * @throws ValidationException Si les données sont invalides
     * @throws ServiceException Si la notification n'existe pas ou erreur lors de la modification
     */
    Notification modifierNotification(Notification notification) throws ValidationException, ServiceException;

    /**
     * Supprime une notification du système
     * 
     * @param notificationId L'ID de la notification à supprimer
     * @throws ServiceException Si la notification n'existe pas ou erreur lors de la suppression
     */
    void supprimerNotification(Long notificationId) throws ServiceException;

    /**
     * Récupère une notification par son ID
     * 
     * @param notificationId L'ID de la notification
     * @return La notification trouvée, ou null si non trouvée
     */
    Notification consulterNotification(Long notificationId);

    /**
     * Récupère toutes les notifications
     * 
     * @return La liste de toutes les notifications
     */
    List<Notification> listerToutesLesNotifications();

    /**
     * Récupère les notifications d'un type spécifique
     * 
     * @param type Le type de notification
     * @return La liste des notifications du type spécifié
     */
    List<Notification> consulterNotificationsParType(TypeNotification type);

    /**
     * Récupère les notifications d'une priorité spécifique
     * 
     * @param priorite La priorité
     * @return La liste des notifications avec cette priorité
     */
    List<Notification> consulterNotificationsParPriorite(PrioriteNotification priorite);

    /**
     * Récupère les notifications d'un titre spécifique
     * 
     * @param titre Le titre
     * @return La notification trouvée, ou null si non trouvée
     */
    Notification consulterNotificationParTitre(TitreNotification titre);

    /**
     * Vérifie si une notification existe
     * 
     * @param notificationId L'ID de la notification
     * @return true si la notification existe, false sinon
     */
    boolean notificationExiste(Long notificationId);

    /**
     * Compte le nombre total de notifications
     * 
     * @return Le nombre de notifications
     */
    long compterNotifications();

    // ============================================
    //  MÉTHODES DE GESTION UTILISATEUR-NOTIFICATION
    // ============================================

    /**
     * Envoie une notification à un utilisateur
     * (crée la notification et la lie à l'utilisateur)
     * 
     * @param utilisateurId L'ID de l'utilisateur qui recevra la notification
     * @param notification La notification à envoyer
     * @return La notification créée et envoyée
     * @throws ValidationException Si les données sont invalides
     * @throws ServiceException Si l'utilisateur n'existe pas ou erreur
     */
    Notification envoyerNotification(Long utilisateurId, Notification notification) 
            throws ValidationException, ServiceException;

    /**
     * Envoie une notification rapide à un utilisateur
     * (méthode de convenance)
     * 
     * @param utilisateurId L'ID de l'utilisateur
     * @param titre Le titre de la notification
     * @param message Le message
     * @param type Le type
     * @param priorite La priorité
     * @param creePar Le créateur
     * @return La notification créée et envoyée
     * @throws ValidationException Si les données sont invalides
     * @throws ServiceException Si erreur
     */
    Notification envoyerNotification(
            Long utilisateurId,
            TitreNotification titre,
            String message,
            TypeNotification type,
            PrioriteNotification priorite,
            String creePar
    ) throws ValidationException, ServiceException;

    /**
     * Retire une notification d'un utilisateur
     * (la notification reste dans le système mais n'est plus liée à l'utilisateur)
     * 
     * @param utilisateurId L'ID de l'utilisateur
     * @param notificationId L'ID de la notification
     * @throws ServiceException Si erreur
     */
    void retirerNotificationUtilisateur(Long utilisateurId, Long notificationId) throws ServiceException;
}

