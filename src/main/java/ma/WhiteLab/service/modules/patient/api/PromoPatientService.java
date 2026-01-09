package ma.WhiteLab.service.modules.patient.api;

import ma.WhiteLab.entities.patient.Patient;

public interface PromoPatientService {
    /**
     * Vérifie si le patient est éligible à une promotion.
     * @param patient Le patient à vérifier
     * @return Message de la promotion ou null si aucune.
     */
    String checkPromoEligibility(Patient patient);

    /**
     * Applique une promotion spéciale (ex: Senior).
     */
    boolean isSeniorDiscountApplicable(Patient patient);
}
