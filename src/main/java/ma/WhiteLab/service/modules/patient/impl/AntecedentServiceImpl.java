package ma.WhiteLab.service.modules.patient.impl;

import ma.WhiteLab.entities.enums.CategorieAntecedent;
import ma.WhiteLab.entities.patient.Antecedent;
import ma.WhiteLab.repository.modules.patient.api.AntecedentRepository;
import ma.WhiteLab.common.exceptions.ServiceException;
import ma.WhiteLab.common.exceptions.ValidationException;
import ma.WhiteLab.common.validation.Validators;
import ma.WhiteLab.service.modules.patient.api.AntecedentService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public class AntecedentServiceImpl implements AntecedentService {

    private final AntecedentRepository antecedentRepository;

    public AntecedentServiceImpl(AntecedentRepository antecedentRepository) {
        this.antecedentRepository = antecedentRepository;
    }

    @Override
    public Antecedent enregistrerAntecedent(Antecedent antecedent) throws ValidationException, ServiceException {
        if (antecedent == null) {
            throw new ValidationException("L'antécédent ne peut pas être null.");
        }
        validerDonneesAntecedent(antecedent);

        if (antecedentRepository.findByNom(antecedent.getNom()).isPresent()) {
            throw new ValidationException("Un antécédent avec le nom '" + antecedent.getNom() + "' existe déjà.");
        }

        antecedent.setDateCreation(LocalDateTime.now());
        antecedent.setDateMiseAJour(LocalDateTime.now());

        try {
            antecedentRepository.create(antecedent);
            return antecedent;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de l'enregistrement de l'antécédent: " + e.getMessage(), e);
        }
    }

    @Override
    public Antecedent modifierAntecedent(Antecedent antecedent) throws ValidationException, ServiceException {
        if (antecedent == null || antecedent.getId() == null) {
            throw new ValidationException("L'antécédent à modifier doit avoir un ID.");
        }
        validerDonneesAntecedent(antecedent);

        if (!antecedentRepository.existsById(antecedent.getId())) {
            throw new ServiceException("L'antécédent avec l'ID " + antecedent.getId() + " n'existe pas.");
        }

        Optional<Antecedent> existing = antecedentRepository.findByNom(antecedent.getNom());
        if (existing.isPresent() && !existing.get().getId().equals(antecedent.getId())) {
            throw new ValidationException("Un autre antécédent avec le nom '" + antecedent.getNom() + "' existe déjà.");
        }

        antecedent.setDateMiseAJour(LocalDateTime.now());

        try {
            antecedentRepository.update(antecedent);
            return antecedent;
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la modification de l'antécédent: " + e.getMessage(), e);
        }
    }

    @Override
    public void supprimerAntecedent(Long antecedentId) throws ServiceException {
        if (antecedentId == null) {
            throw new ServiceException("L'ID de l'antécédent ne peut pas être null.");
        }

        if (!antecedentRepository.getPatientsHavingAntecedent(antecedentId).isEmpty()) {
            throw new ServiceException("Impossible de supprimer l'antécédent car il est associé à un ou plusieurs patients.");
        }

        try {
            antecedentRepository.deleteById(antecedentId);
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la suppression de l'antécédent: " + e.getMessage(), e);
        }
    }

    @Override
    public Antecedent consulterAntecedent(Long antecedentId) throws ServiceException {
        if (antecedentId == null) {
            return null;
        }
        try {
            return antecedentRepository.findById(antecedentId);
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la consultation de l'antécédent: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Antecedent> listerTousLesAntecedents() throws ServiceException {
        try {
            return antecedentRepository.findAll();
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la récupération de tous les antécédents: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Antecedent> rechercherAntecedentParNom(String nom) throws ServiceException {
        try {
            return antecedentRepository.findByNom(nom).map(List::of).orElse(List.of());
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la recherche d'antécédent par nom: " + e.getMessage(), e);
        }
    }

    @Override
    public List<Antecedent> listerAntecedentsParCategorie(CategorieAntecedent categorie) throws ServiceException {
        if (categorie == null) {
            return List.of();
        }
        try {
            return antecedentRepository.findByCategorie(categorie);
        } catch (Exception e) {
            throw new ServiceException("Erreur lors de la récupération des antécédents par catégorie: " + e.getMessage(), e);
        }
    }

    private void validerDonneesAntecedent(Antecedent antecedent) throws ValidationException {
        Validators.notBlank(antecedent.getNom(), "Le nom de l'antécédent");
        Validators.notBlank(antecedent.getDescription(), "La description de l'antécédent");

        if (antecedent.getCategorie() == null) {
            throw new ValidationException("La catégorie de l'antécédent est obligatoire.");
        }
        if (antecedent.getNiveauDeRisk() == null) {
            throw new ValidationException("Le niveau de risque de l'antécédent est obligatoire.");
        }
    }
}
