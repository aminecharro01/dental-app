package ma.WhiteLab.repository.modules.agenda.impl.mySQL;

import ma.WhiteLab.conf.SessionFactory;
import ma.WhiteLab.entities.agenda.RendezVous;
import ma.WhiteLab.repository.common.RowMappers;
import ma.WhiteLab.repository.modules.agenda.api.RendezVousRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RendezVousRepositoryImpl implements RendezVousRepository {

    @Override
    public List<RendezVous> findAll() {
        String sql = "SELECT * FROM rendezvous";
        List<RendezVous> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(RowMappers.mapRendezVous(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return out;
    }

    @Override
    public RendezVous findById(Long id) {
        String sql = "SELECT * FROM rendezvous WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapRendezVous(rs);
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(RendezVous r) {
        String sql = """
            INSERT INTO rendezvous(dateRDV, heure_rdv, motif, status, note_medecin,
                                    dossier_med_id, consultation_id)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setTimestamp(1, r.getDate() != null ? Timestamp.valueOf(r.getDate()) : null);
            ps.setTime(2, r.getTime() != null ? Time.valueOf(r.getTime()) : null);
            ps.setString(3, r.getMotif());
            ps.setString(4, r.getStatus() != null ? r.getStatus().name() : null);
            ps.setString(5, r.getNoteMedecin());
            ps.setObject(6, r.getDossierMed() != null ? r.getDossierMed().getId() : null);
            ps.setObject(7, r.getConsultation() != null ? r.getConsultation().getId() : null);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) r.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
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

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setTimestamp(1, r.getDate() != null ? Timestamp.valueOf(r.getDate()) : null);
            ps.setTime(2, r.getTime() != null ? Time.valueOf(r.getTime()) : null);
            ps.setString(3, r.getMotif());
            ps.setString(4, r.getStatus() != null ? r.getStatus().name() : null);
            ps.setString(5, r.getNoteMedecin());
            ps.setObject(6, r.getDossierMed() != null ? r.getDossierMed().getId() : null);
            ps.setObject(7, r.getConsultation() != null ? r.getConsultation().getId() : null);
            ps.setLong(8, r.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(RendezVous r) {
        if (r != null) deleteById(r.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM rendezvous WHERE id=?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<RendezVous> findByDossierMedId(Long dossierMedId) {
        String sql = "SELECT * FROM rendezvous WHERE dossier_med_id = ?";
        List<RendezVous> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, dossierMedId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapRendezVous(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return out;
    }

    @Override
    public List<RendezVous> findByConsultationId(Long consultationId) {
        String sql = "SELECT * FROM rendezvous WHERE consultation_id = ?";
        List<RendezVous> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, consultationId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapRendezVous(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return out;
    }

    @Override
    public List<RendezVous> findByStatus(String status) {
        String sql = "SELECT * FROM rendezvous WHERE status = ?";
        List<RendezVous> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, status);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapRendezVous(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return out;
    }
}
