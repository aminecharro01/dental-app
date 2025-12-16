package ma.WhiteLab.service.modules.patient.api;

/**
 * Interface du service métier pour la gestion des antécédents
 * 
 * Cette interface sera implémentée plus tard si nécessaire
 */
import ma.WhiteLab.entities.enums.CategorieAntecedent;
import ma.WhiteLab.entities.patient.Antecedent;
import ma.WhiteLab.common.exceptions.ValidationException;
import ma.WhiteLab.common.exceptions.ServiceException;

import java.util.List;

public interface AntecedentService {

    /**
     * Enregistre un nouvel antécédent dans la base de données.
     *
     * @param antecedent l'antécédent à enregistrer
     * @return l'antécédent enregistré avec son ID
     * @throws ValidationException si les données de l'antécédent sont invalides
     * @throws ServiceException    si une erreur métier survient lors de l'enregistrement
     */
    Antecedent enregistrerAntecedent(Antecedent antecedent) throws ValidationException, ServiceException;

    /**
     * Modifie un antécédent existant.
     *
     * @param antecedent l'antécédent avec les informations mises à jour
     * @return l'antécédent modifié
     * @throws ValidationException si les données de l'antécédent sont invalides
     * @throws ServiceException    si l'antécédent n'existe pas ou si une erreur métier survient
     */
    Antecedent modifierAntecedent(Antecedent antecedent) throws ValidationException, ServiceException;

    /**
     * Supprime un antécédent par son ID.
     *
     * @param antecedentId l'ID de l'antécédent à supprimer
     * @throws ServiceException si l'antécédent n'existe pas ou s'il est encore lié à des patients
     */
    void supprimerAntecedent(Long antecedentId) throws ServiceException;

    /**
     * Consulte un antécédent par son ID.
     *
     * @param antecedentId l'ID de l'antécédent à consulter
     * @return l'antécédent trouvé, ou null s'il n'existe pas
     * @throws ServiceException si une erreur survient lors de la récupération
     */
    Antecedent consulterAntecedent(Long antecedentId) throws ServiceException;

    /**
     * Récupère la liste de tous les antécédents.
     *
     * @return une liste de tous les antécédents
     * @throws ServiceException si une erreur survient lors de la récupération
     */
    List<Antecedent> listerTousLesAntecedents() throws ServiceException;

    /**
     * Recherche des antécédents par leur nom.
     *
     * @param nom le nom à rechercher
     * @return une liste d'antécédents correspondant au nom
     * @throws ServiceException si une erreur survient lors de la recherche
     */
    List<Antecedent> rechercherAntecedentParNom(String nom) throws ServiceException;

    /**
     * Récupère la liste des antécédents pour une catégorie donnée.
     *
     * @param categorie la catégorie des antécédents à lister
     * @return une liste d'antécédents de la catégorie spécifiée
     * @throws ServiceException si une erreur survient lors de la récupération
     */
    List<Antecedent> listerAntecedentsParCategorie(CategorieAntecedent categorie) throws ServiceException;
}
