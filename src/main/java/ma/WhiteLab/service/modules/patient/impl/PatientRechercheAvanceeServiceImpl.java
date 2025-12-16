package ma.WhiteLab.service.modules.patient.impl;

import ma.WhiteLab.entities.patient.Patient;
import ma.WhiteLab.entities.patient.Antecedent;
import ma.WhiteLab.entities.enums.Assurance;
import ma.WhiteLab.entities.enums.Sexe;
import ma.WhiteLab.common.exceptions.ServiceException;
import ma.WhiteLab.service.modules.patient.api.PatientService;
import ma.WhiteLab.service.modules.patient.api.PatientRechercheAvanceeService;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implémentation du service métier pour la recherche avancée de patients
 * 
 * Ce service permet de faire des recherches complexes sur les patients
 * en utilisant le PatientService.
 * 
 * Le code est simple et clair pour être facilement compréhensible.
 */
public class PatientRechercheAvanceeServiceImpl implements PatientRechercheAvanceeService {

    private final PatientService patientService;

    /**
     * Constructeur avec injection de dépendance
     * 
     * @param patientService Le service patient pour accéder aux données
     */
    public PatientRechercheAvanceeServiceImpl(PatientService patientService) {
        this.patientService = patientService;
    }

    // ============================================
    //  RECHERCHES PAR CRITÈRES SIMPLES
    // ============================================

    @Override
    public List<Patient> rechercherParSexe(Sexe sexe) throws ServiceException {
        try {
            if (sexe == null) {
                return new ArrayList<>();
            }

            List<Patient> tousLesPatients = patientService.listerTousLesPatients();
            return tousLesPatients.stream()
                    .filter(p -> p.getSexe() == sexe)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la recherche par sexe : " + e.getMessage(), e);
        }
    }

    @Override
    public List<Patient> rechercherParAssurance(Assurance assurance) throws ServiceException {
        try {
            if (assurance == null) {
                return new ArrayList<>();
            }

            List<Patient> tousLesPatients = patientService.listerTousLesPatients();
            return tousLesPatients.stream()
                    .filter(p -> p.getAssurance() == assurance)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la recherche par assurance : " + e.getMessage(), e);
        }
    }

    @Override
    public List<Patient> rechercherParTrancheAge(int ageMin, int ageMax) throws ServiceException {
        try {
            if (ageMin < 0 || ageMax < 0 || ageMin > ageMax) {
                throw new ServiceException("Les âges doivent être valides et ageMin <= ageMax");
            }

            List<Patient> tousLesPatients = patientService.listerTousLesPatients();
            List<Patient> resultats = new ArrayList<>();

            for (Patient patient : tousLesPatients) {
                if (patient.getDateNaissance() != null) {
                    int age = calculerAge(patient.getDateNaissance());
                    if (age >= ageMin && age <= ageMax) {
                        resultats.add(patient);
                    }
                }
            }

            return resultats;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la recherche par tranche d'âge : " + e.getMessage(), e);
        }
    }

    @Override
    public List<Patient> rechercherParDateNaissance(LocalDate dateDebut, LocalDate dateFin) throws ServiceException {
        try {
            if (dateDebut == null || dateFin == null) {
                throw new ServiceException("Les dates de début et de fin sont obligatoires");
            }

            if (dateDebut.isAfter(dateFin)) {
                throw new ServiceException("La date de début doit être antérieure à la date de fin");
            }

            List<Patient> tousLesPatients = patientService.listerTousLesPatients();
            List<Patient> resultats = new ArrayList<>();

            for (Patient patient : tousLesPatients) {
                if (patient.getDateNaissance() != null) {
                    LocalDate dateNaissance = patient.getDateNaissance();
                    if (!dateNaissance.isBefore(dateDebut) && !dateNaissance.isAfter(dateFin)) {
                        resultats.add(patient);
                    }
                }
            }

            return resultats;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la recherche par date de naissance : " + e.getMessage(), e);
        }
    }

    // ============================================
    //  RECHERCHES AVEC CRITÈRES MULTIPLES
    // ============================================

    @Override
    public List<Patient> rechercherAvecCriteres(
            String nom,
            String prenom,
            Sexe sexe,
            Assurance assurance
    ) throws ServiceException {
        try {
            List<Patient> tousLesPatients = patientService.listerTousLesPatients();
            List<Patient> resultats = new ArrayList<>();

            for (Patient patient : tousLesPatients) {
                boolean correspond = true;

                // Vérifier le nom
                if (nom != null && !nom.trim().isEmpty()) {
                    if (patient.getNom() == null || !patient.getNom().toLowerCase().contains(nom.toLowerCase().trim())) {
                        correspond = false;
                    }
                }

                // Vérifier le prénom
                if (correspond && prenom != null && !prenom.trim().isEmpty()) {
                    if (patient.getPrenom() == null || !patient.getPrenom().toLowerCase().contains(prenom.toLowerCase().trim())) {
                        correspond = false;
                    }
                }

                // Vérifier le sexe
                if (correspond && sexe != null) {
                    if (patient.getSexe() != sexe) {
                        correspond = false;
                    }
                }

                // Vérifier l'assurance
                if (correspond && assurance != null) {
                    if (patient.getAssurance() != assurance) {
                        correspond = false;
                    }
                }

                if (correspond) {
                    resultats.add(patient);
                }
            }

            return resultats;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la recherche avec critères : " + e.getMessage(), e);
        }
    }

    @Override
    public List<Patient> rechercherParPresenceAntecedents(boolean avecAntecedents) throws ServiceException {
        try {
            List<Patient> tousLesPatients = patientService.listerTousLesPatients();
            List<Patient> resultats = new ArrayList<>();

            for (Patient patient : tousLesPatients) {
                List<Antecedent> antecedents = patientService.consulterAntecedentsPatient(patient.getId());
                boolean aDesAntecedents = antecedents != null && !antecedents.isEmpty();

                if (avecAntecedents && aDesAntecedents) {
                    resultats.add(patient);
                } else if (!avecAntecedents && !aDesAntecedents) {
                    resultats.add(patient);
                }
            }

            return resultats;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la recherche par présence d'antécédents : " + e.getMessage(), e);
        }
    }

    @Override
    public List<Patient> rechercherParDateEnregistrement(LocalDate dateDebut, LocalDate dateFin) throws ServiceException {
        try {
            if (dateDebut == null || dateFin == null) {
                throw new ServiceException("Les dates de début et de fin sont obligatoires");
            }

            if (dateDebut.isAfter(dateFin)) {
                throw new ServiceException("La date de début doit être antérieure à la date de fin");
            }

            List<Patient> tousLesPatients = patientService.listerTousLesPatients();
            List<Patient> resultats = new ArrayList<>();

            for (Patient patient : tousLesPatients) {
                if (patient.getDateCreation() != null) {
                    LocalDate dateCreation = patient.getDateCreation().toLocalDate();
                    if (!dateCreation.isBefore(dateDebut) && !dateCreation.isAfter(dateFin)) {
                        resultats.add(patient);
                    }
                }
            }

            return resultats;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la recherche par date d'enregistrement : " + e.getMessage(), e);
        }
    }

    // ============================================
    //  RECHERCHES SPÉCIALISÉES
    // ============================================

    @Override
    public List<Patient> rechercherParPresenceEmail(boolean avecEmail) throws ServiceException {
        try {
            List<Patient> tousLesPatients = patientService.listerTousLesPatients();
            List<Patient> resultats = new ArrayList<>();

            for (Patient patient : tousLesPatients) {
                boolean aEmail = patient.getEmail() != null && !patient.getEmail().trim().isEmpty();

                if (avecEmail && aEmail) {
                    resultats.add(patient);
                } else if (!avecEmail && !aEmail) {
                    resultats.add(patient);
                }
            }

            return resultats;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la recherche par présence d'email : " + e.getMessage(), e);
        }
    }

    @Override
    public List<Patient> rechercherParPresenceTelephone(boolean avecTelephone) throws ServiceException {
        try {
            List<Patient> tousLesPatients = patientService.listerTousLesPatients();
            List<Patient> resultats = new ArrayList<>();

            for (Patient patient : tousLesPatients) {
                boolean aTelephone = patient.getTelephone() != null && !patient.getTelephone().trim().isEmpty();

                if (avecTelephone && aTelephone) {
                    resultats.add(patient);
                } else if (!avecTelephone && !aTelephone) {
                    resultats.add(patient);
                }
            }

            return resultats;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la recherche par présence de téléphone : " + e.getMessage(), e);
        }
    }

    @Override
    public List<Patient> rechercherParVille(String ville) throws ServiceException {
        try {
            if (ville == null || ville.trim().isEmpty()) {
                return new ArrayList<>();
            }

            List<Patient> tousLesPatients = patientService.listerTousLesPatients();
            List<Patient> resultats = new ArrayList<>();
            String villeLower = ville.toLowerCase().trim();

            for (Patient patient : tousLesPatients) {
                if (patient.getAdresse() != null && patient.getAdresse().toLowerCase().contains(villeLower)) {
                    resultats.add(patient);
                }
            }

            return resultats;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la recherche par ville : " + e.getMessage(), e);
        }
    }

    // ============================================
    //  MÉTHODES PRIVÉES (UTILITAIRES)
    // ============================================

    /**
     * Calcule l'âge d'une personne à partir de sa date de naissance
     * 
     * @param dateNaissance La date de naissance
     * @return L'âge en années
     */
    private int calculerAge(LocalDate dateNaissance) {
        if (dateNaissance == null) {
            return 0;
        }
        return Period.between(dateNaissance, LocalDate.now()).getYears();
    }
}



