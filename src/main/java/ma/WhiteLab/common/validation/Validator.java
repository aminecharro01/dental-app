package ma.WhiteLab.common.validation;

import java.util.Map;

/**
 * Interface générique pour la validation des objets métier/DTO.
 * @param <T> Le type d'objet à valider
 */
public interface Validator<T> {
    /**
     * Valide un objet et retourne une map d'erreurs.
     * @param object L'objet à valider
     * @return Map contenant les erreurs (clé = champ, valeur = message d'erreur), vide si valide.
     */
    Map<String, String> validate(T object);
}
