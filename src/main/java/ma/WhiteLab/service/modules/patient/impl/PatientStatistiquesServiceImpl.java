package ma.WhiteLab.service.modules.patient.impl;

import ma.WhiteLab.entities.enums.Assurance;
import ma.WhiteLab.entities.enums.Sexe;
import ma.WhiteLab.entities.patient.Patient;
import ma.WhiteLab.entities.patient.Antecedent;
import ma.WhiteLab.common.exceptions.ServiceException;
import ma.WhiteLab.service.modules.patient.api.PatientService;
import ma.WhiteLab.service.modules.patient.api.PatientStatistiquesService;

import java.time.LocalDate;
import java.time.Period;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Implémentation du service métier pour les statistiques sur les patients
 * 
 * Ce service calcule des statistiques sur les patients en utilisant
 * le PatientService et le PatientRepository.
 * 
 * Le code est simple et clair pour être facilement compréhensible.
 */
public class PatientStatistiquesServiceImpl implements PatientStatistiquesService {

    private final PatientService patientService;

    /**
     * Constructeur avec injection de dépendance
     * 
     * @param patientService Le service patient pour accéder aux données
     */
    public PatientStatistiquesServiceImpl(PatientService patientService) {
        this.patientService = patientService;
    }

    // ============================================
    //  STATISTIQUES GÉNÉRALES
    // ============================================

    @Override
    public long compterTotalPatients() {
        return patientService.compterPatients();
    }

    @Override
    public Map<Sexe, Long> compterPatientsParSexe() throws ServiceException {
        try {
            List<Patient> patients = patientService.listerTousLesPatients();
            Map<Sexe, Long> statistiques = new HashMap<>();

            // Initialiser toutes les valeurs à 0
            for (Sexe sexe : Sexe.values()) {
                statistiques.put(sexe, 0L);
            }

            // Compter les patients par sexe
            for (Patient patient : patients) {
                if (patient.getSexe() != null) {
                    Sexe sexe = patient.getSexe();
                    statistiques.put(sexe, statistiques.get(sexe) + 1);
                }
            }

            return statistiques;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors du comptage des patients par sexe : " + e.getMessage(), e);
        }
    }

    @Override
    public Map<Assurance, Long> compterPatientsParAssurance() throws ServiceException {
        try {
            List<Patient> patients = patientService.listerTousLesPatients();
            Map<Assurance, Long> statistiques = new HashMap<>();

            // Initialiser toutes les valeurs à 0
            for (Assurance assurance : Assurance.values()) {
                statistiques.put(assurance, 0L);
            }

            // Compter les patients par assurance
            for (Patient patient : patients) {
                if (patient.getAssurance() != null) {
                    Assurance assurance = patient.getAssurance();
                    statistiques.put(assurance, statistiques.get(assurance) + 1);
                }
            }

            return statistiques;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors du comptage des patients par assurance : " + e.getMessage(), e);
        }
    }

    @Override
    public long compterNouveauxPatients(LocalDate dateDebut, LocalDate dateFin) throws ServiceException {
        try {
            if (dateDebut == null || dateFin == null) {
                throw new ServiceException("Les dates de début et de fin sont obligatoires");
            }

            if (dateDebut.isAfter(dateFin)) {
                throw new ServiceException("La date de début doit être antérieure à la date de fin");
            }

            List<Patient> patients = patientService.listerTousLesPatients();
            long compteur = 0;

            for (Patient patient : patients) {
                if (patient.getDateCreation() != null) {
                    LocalDate dateCreation = patient.getDateCreation().toLocalDate();
                    if (!dateCreation.isBefore(dateDebut) && !dateCreation.isAfter(dateFin)) {
                        compteur++;
                    }
                }
            }

            return compteur;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors du comptage des nouveaux patients : " + e.getMessage(), e);
        }
    }

    // ============================================
    //  STATISTIQUES PAR ÂGE
    // ============================================

    @Override
    public Map<String, Long> compterPatientsParTrancheAge() throws ServiceException {
        try {
            List<Patient> patients = patientService.listerTousLesPatients();
            Map<String, Long> statistiques = new HashMap<>();

            // Initialiser les tranches d'âge
            statistiques.put("0-18", 0L);
            statistiques.put("19-30", 0L);
            statistiques.put("31-50", 0L);
            statistiques.put("51-70", 0L);
            statistiques.put("71+", 0L);

            // Compter par tranche d'âge
            for (Patient patient : patients) {
                if (patient.getDateNaissance() != null) {
                    int age = calculerAge(patient.getDateNaissance());
                    String tranche = determinerTrancheAge(age);
                    statistiques.put(tranche, statistiques.get(tranche) + 1);
                }
            }

            return statistiques;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors du comptage par tranche d'âge : " + e.getMessage(), e);
        }
    }

    @Override
    public double calculerAgeMoyen() throws ServiceException {
        try {
            List<Patient> patients = patientService.listerTousLesPatients();
            int sommeAges = 0;
            int nombrePatientsAvecAge = 0;

            for (Patient patient : patients) {
                if (patient.getDateNaissance() != null) {
                    sommeAges += calculerAge(patient.getDateNaissance());
                    nombrePatientsAvecAge++;
                }
            }

            if (nombrePatientsAvecAge == 0) {
                return 0.0;
            }

            return (double) sommeAges / nombrePatientsAvecAge;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors du calcul de l'âge moyen : " + e.getMessage(), e);
        }
    }

    @Override
    public int trouverAgeMinimum() throws ServiceException {
        try {
            List<Patient> patients = patientService.listerTousLesPatients();
            int ageMinimum = Integer.MAX_VALUE;
            boolean trouve = false;

            for (Patient patient : patients) {
                if (patient.getDateNaissance() != null) {
                    int age = calculerAge(patient.getDateNaissance());
                    if (age < ageMinimum) {
                        ageMinimum = age;
                        trouve = true;
                    }
                }
            }

            return trouve ? ageMinimum : -1;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la recherche de l'âge minimum : " + e.getMessage(), e);
        }
    }

    @Override
    public int trouverAgeMaximum() throws ServiceException {
        try {
            List<Patient> patients = patientService.listerTousLesPatients();
            int ageMaximum = -1;

            for (Patient patient : patients) {
                if (patient.getDateNaissance() != null) {
                    int age = calculerAge(patient.getDateNaissance());
                    if (age > ageMaximum) {
                        ageMaximum = age;
                    }
                }
            }

            return ageMaximum;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la recherche de l'âge maximum : " + e.getMessage(), e);
        }
    }

    // ============================================
    //  STATISTIQUES AVANCÉES
    // ============================================

    @Override
    public long compterPatientsAvecAntecedents() throws ServiceException {
        try {
            List<Patient> patients = patientService.listerTousLesPatients();
            long compteur = 0;

            for (Patient patient : patients) {
                List<Antecedent> antecedents = patientService.consulterAntecedentsPatient(patient.getId());
                if (antecedents != null && !antecedents.isEmpty()) {
                    compteur++;
                }
            }

            return compteur;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors du comptage des patients avec antécédents : " + e.getMessage(), e);
        }
    }

    @Override
    public long compterPatientsSansAntecedents() throws ServiceException {
        try {
            long total = compterTotalPatients();
            long avecAntecedents = compterPatientsAvecAntecedents();
            return total - avecAntecedents;
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors du comptage des patients sans antécédents : " + e.getMessage(), e);
        }
    }

    @Override
    public double calculerPourcentagePatientsAvecEmail() throws ServiceException {
        try {
            List<Patient> patients = patientService.listerTousLesPatients();
            long total = patients.size();
            
            if (total == 0) {
                return 0.0;
            }

            long avecEmail = 0;
            for (Patient patient : patients) {
                if (patient.getEmail() != null && !patient.getEmail().trim().isEmpty()) {
                    avecEmail++;
                }
            }

            return (double) avecEmail / total * 100.0;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors du calcul du pourcentage avec email : " + e.getMessage(), e);
        }
    }

    @Override
    public double calculerPourcentagePatientsAvecTelephone() throws ServiceException {
        try {
            List<Patient> patients = patientService.listerTousLesPatients();
            long total = patients.size();
            
            if (total == 0) {
                return 0.0;
            }

            long avecTelephone = 0;
            for (Patient patient : patients) {
                if (patient.getTelephone() != null && !patient.getTelephone().trim().isEmpty()) {
                    avecTelephone++;
                }
            }

            return (double) avecTelephone / total * 100.0;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors du calcul du pourcentage avec téléphone : " + e.getMessage(), e);
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

    /**
     * Détermine la tranche d'âge à partir de l'âge
     * 
     * @param age L'âge en années
     * @return La tranche d'âge (ex: "0-18", "19-30", etc.)
     */
    private String determinerTrancheAge(int age) {
        if (age <= 18) {
            return "0-18";
        } else if (age <= 30) {
            return "19-30";
        } else if (age <= 50) {
            return "31-50";
        } else if (age <= 70) {
            return "51-70";
        } else {
            return "71+";
        }
    }
}

