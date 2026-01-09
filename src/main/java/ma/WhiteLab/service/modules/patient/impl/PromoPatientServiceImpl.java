package ma.WhiteLab.service.modules.patient.impl;

import ma.WhiteLab.entities.patient.Patient;
import ma.WhiteLab.service.modules.patient.api.PromoPatientService;

import java.time.LocalDate;
import java.time.Period;

public class PromoPatientServiceImpl implements PromoPatientService {

    @Override
    public String checkPromoEligibility(Patient patient) {
        if (patient == null || patient.getDateNaissance() == null) return null;

        int age = Period.between(patient.getDateNaissance(), LocalDate.now()).getYears();

        if (age >= 60) {
            return "PROMO_SENIOR: -20% sur les consultations";
        }
        if (age <= 12) {
            return "PROMO_ENFANT: 1ère consultation gratuite";
        }
        return null;
    }

    @Override
    public boolean isSeniorDiscountApplicable(Patient patient) {
        if (patient == null || patient.getDateNaissance() == null) return false;
        return Period.between(patient.getDateNaissance(), LocalDate.now()).getYears() >= 60;
    }
}
