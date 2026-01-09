package ma.WhiteLab.repository.modules.agenda.impl.mySQL;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.WhiteLab.entities.agenda.RendezVous;
import ma.WhiteLab.repository.common.RowMappers;
import ma.WhiteLab.repository.modules.agenda.api.RendezVousRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RendezVousRepositoryImpl implements RendezVousRepository {

    private Connection connection; // Injectée par ApplicationContext

    // =========================
    // CRUD
    // =========================

    @Override
    public List<RendezVous> findAll() {
        String sql = "SELECT * FROM rendezvous";
        System.out.println("[JDBC RENDEZVOUS SQL] " + sql);

        List<RendezVous> out = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(RowMappers.mapRendezVous(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de tous les rendez-vous", e);
        }

        return out;
    }

    @Override
    public RendezVous findById(Long id) {
        String sql = "SELECT * FROM rendezvous WHERE id = ?";
        System.out.println("[JDBC RENDEZVOUS SQL] " + sql + " | id=" + id);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapRendezVous(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche du rendez-vous par ID : " + id, e);
        }

        return null;
    }

    @Override
    public void create(RendezVous r) {
        String sql = """
            INSERT INTO rendezvous(dateRDV, heure_rdv, motif, status, note_medecin,
                                    dossier_med_id, consultation_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
            """;
        System.out.println("[JDBC RENDEZVOUS SQL] INSERT RendezVous | date=" + r.getDate());

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setTimestamp(1, r.getDate() != null ? Timestamp.valueOf(r.getDate()) : null);
            ps.setTime(2, r.getTime() != null ? Time.valueOf(r.getTime()) : null);
            ps.setString(3, r.getMotif());
            ps.setString(4, r.getStatus() != null ? r.getStatus().name() : null);
            ps.setString(5, r.getNoteMedecin());

            ps.setObject(6, r.getDossierMed() != null ? r.getDossierMed().getId() : null, Types.BIGINT);
            ps.setObject(7, r.getConsultation() != null ? r.getConsultation().getId() : null, Types.BIGINT);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    r.setId(keys.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création du rendez-vous", e);
        }
    }

    @Override
    public void update(RendezVous r) {
        String sql = """
            UPDATE rendezvous
            SET dateRDV=?, heure_rdv=?, motif=?, status=?, note_medecin=?,
                dossier_med_id=?, consultation_id=?
            WHERE id=?
            """;
        System.out.println("[JDBC RENDEZVOUS SQL] UPDATE RendezVous | id=" + r.getId());

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setTimestamp(1, r.getDate() != null ? Timestamp.valueOf(r.getDate()) : null);
            ps.setTime(2, r.getTime() != null ? Time.valueOf(r.getTime()) : null);
            ps.setString(3, r.getMotif());
            ps.setString(4, r.getStatus() != null ? r.getStatus().name() : null);
            ps.setString(5, r.getNoteMedecin());

            ps.setObject(6, r.getDossierMed() != null ? r.getDossierMed().getId() : null, Types.BIGINT);
            ps.setObject(7, r.getConsultation() != null ? r.getConsultation().getId() : null, Types.BIGINT);
            ps.setLong(8, r.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour du rendez-vous ID=" + r.getId(), e);
        }
    }

    @Override
    public void delete(RendezVous r) {
        if (r != null && r.getId() != null) {
            deleteById(r.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM rendezvous WHERE id=?";
        System.out.println("[JDBC RENDEZVOUS SQL] DELETE RendezVous | id=" + id);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression du rendez-vous ID=" + id, e);
        }
    }

    @Override
    public List<RendezVous> findByDossierMedId(Long dossierMedId) {
        String sql = "SELECT * FROM rendezvous WHERE dossier_med_id = ?";
        System.out.println("[JDBC RENDEZVOUS SQL] " + sql + " | dossierMedId=" + dossierMedId);

        List<RendezVous> out = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, dossierMedId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapRendezVous(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des rendez-vous par dossier médical ID=" + dossierMedId, e);
        }

        return out;
    }

    @Override
    public List<RendezVous> findByConsultationId(Long consultationId) {
        String sql = "SELECT * FROM rendezvous WHERE consultation_id = ?";
        System.out.println("[JDBC RENDEZVOUS SQL] " + sql + " | consultationId=" + consultationId);

        List<RendezVous> out = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, consultationId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapRendezVous(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des rendez-vous par consultation ID=" + consultationId, e);
        }

        return out;
    }

    @Override
    public List<RendezVous> findByStatus(String status) {
        String sql = "SELECT * FROM rendezvous WHERE status = ?";
        System.out.println("[JDBC RENDEZVOUS SQL] " + sql + " | status=" + status);

        List<RendezVous> out = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapRendezVous(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche des rendez-vous par statut : " + status, e);
        }

        return out;
    }

    // =========================
    //   DASHBOARD STATISTIQUES
    // =========================

    @Override
    public long countByCabinet(Long cabinetId) {
        String sql = """
            SELECT COUNT(r.id)
            FROM rendezvous r
            JOIN dossiermedical d ON r.dossier_med_id = d.id
            JOIN staff s ON d.medecin_id = s.id
            WHERE s.cabinetMedicale_id = ?
            """;
        System.out.println("[JDBC RENDEZVOUS SQL] COUNT by cabinet | cabinetId=" + cabinetId);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage des rendez-vous par cabinet ID=" + cabinetId, e);
        }
    }

    @Override
    public long countByCabinetAndMonth(Long cabinetId, int year, int month) {
        String sql = """
            SELECT COUNT(r.id)
            FROM rendezvous r
            JOIN dossiermedical d ON r.dossier_med_id = d.id
            JOIN staff s ON d.medecin_id = s.id
            WHERE s.cabinetMedicale_id = ?
              AND YEAR(r.dateRDV) = ?
              AND MONTH(r.dateRDV) = ?
            """;
        System.out.println("[JDBC RENDEZVOUS SQL] COUNT by cabinet and month | cabinetId=" + cabinetId +
                ", année=" + year + ", mois=" + month);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            ps.setInt(2, year);
            ps.setInt(3, month);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage des rendez-vous par cabinet et mois", e);
        }
    }

    @Override
    public long countByDateAndCabinet(LocalDate date, Long cabinetId) {
        String sql = """
            SELECT COUNT(r.id)
            FROM rendezvous r
            JOIN dossiermedical d ON r.dossier_med_id = d.id
            JOIN staff s ON d.medecin_id = s.id
            WHERE s.cabinetMedicale_id = ?
              AND DATE(r.dateRDV) = ?
            """;
        System.out.println("[JDBC RENDEZVOUS SQL] COUNT by date and cabinet | date=" + date + ", cabinetId=" + cabinetId);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            ps.setDate(2, Date.valueOf(date));

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage des rendez-vous par date et cabinet", e);
        }
    }

    @Override
    public long countRdvBetweenDatesAndCabinet(LocalDate debut, LocalDate fin, Long cabinetId) {
        String sql = """
            SELECT COUNT(r.id)
            FROM rendezvous r
            JOIN dossiermedical d ON r.dossier_med_id = d.id
            JOIN staff s ON d.medecin_id = s.id
            WHERE s.cabinetMedicale_id = ?
              AND DATE(r.dateRDV) BETWEEN ? AND ?
              AND r.status = 'CONFIRME'
            """;
        System.out.println("[JDBC RENDEZVOUS SQL] COUNT confirmés entre dates | cabinetId=" + cabinetId +
                ", du " + debut + " au " + fin);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);
            ps.setDate(2, Date.valueOf(debut));
            ps.setDate(3, Date.valueOf(fin));

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage des RDV confirmés entre " + debut + " et " + fin, e);
        }
    }

    @Override
    public double calculateTauxAnnulationCeMois(Long cabinetId) {
        String sql = """
            SELECT 
                (SUM(CASE WHEN r.status = 'ANNULE' THEN 1 ELSE 0 END) * 100.0) / COUNT(r.id)
            FROM rendezvous r
            JOIN dossiermedical d ON r.dossier_med_id = d.id
            JOIN staff s ON d.medecin_id = s.id
            WHERE s.cabinetMedicale_id = ?
              AND MONTH(r.dateRDV) = MONTH(CURDATE())
              AND YEAR(r.dateRDV) = YEAR(CURDATE())
            """;
        System.out.println("[JDBC RENDEZVOUS SQL] Taux annulation ce mois | cabinetId=" + cabinetId);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getDouble(1) : 0.0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du calcul du taux d'annulation ce mois pour le cabinet ID=" + cabinetId, e);
        }
    }

    @Override
    public long countCreneauxDisponiblesSemaine(Long cabinetId) {
        String sql = """
            SELECT COUNT(s.id) * 40
            FROM staff s
            WHERE s.cabinetMedicale_id = ?
              AND s.role = 'MEDECIN'
            """;
        System.out.println("[JDBC RENDEZVOUS SQL] Créneaux disponibles semaine | cabinetId=" + cabinetId);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, cabinetId);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? rs.getLong(1) : 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du calcul des créneaux disponibles pour le cabinet ID=" + cabinetId, e);
        }
    }
}