package ma.WhiteLab.repository.modules.agenda.api;

import ma.WhiteLab.entities.agenda.RendezVous;
import ma.WhiteLab.repository.common.CrudRepository;

import java.time.LocalDate;
import java.util.List;

public interface RendezVousRepository extends CrudRepository<RendezVous, Long> {

    // Recherche par dossier médical
    List<RendezVous> findByDossierMedId(Long dossierMedId);

    // Recherche par consultation
    List<RendezVous> findByConsultationId(Long consultationId);

    // Recherche par status
    List<RendezVous> findByStatus(String status);
    long countByCabinet(Long cabinetId);
    long countByCabinetAndMonth(Long cabinetId, int year, int month);

    long countByDateAndCabinet(LocalDate localDate, Long cabinetId);

    double calculateTauxAnnulationCeMois(Long cabinetId);

    long countRdvBetweenDatesAndCabinet(LocalDate lundi, LocalDate dimanche, Long cabinetId);

    long countCreneauxDisponiblesSemaine(Long cabinetId);
}
