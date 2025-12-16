package ma.WhiteLab.repository.modules.dossierMedical.impl.mySQL;

import ma.WhiteLab.conf.SessionFactory;
import ma.WhiteLab.entities.dossierMedical.Consultation;
import ma.WhiteLab.repository.common.RowMappers;
import ma.WhiteLab.repository.modules.dossierMedical.api.ConsultationRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ConsultationRepositoryImpl implements ConsultationRepository {

    private static final String TABLE = "consultation";

    @Override
    public List<Consultation> findAll() {
        String sql = "SELECT * FROM " + TABLE + " ORDER BY date DESC";
        List<Consultation> result = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(RowMappers.mapConsultation(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de toutes les consultations", e);
        }
        return result;
    }

    @Override
    public Consultation findById(Long id) {
        String sql = "SELECT * FROM " + TABLE + " WHERE id = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapConsultation(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la consultation ID=" + id, e);
        }
    }

    @Override
    public void create(Consultation c) {
        String sql = """
                INSERT INTO consultation 
                (date, status, notes, observations_medecin,
                 dossier_medical_id,
                 dateCreation, dateMiseAJour, creePar, modifierPar)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            LocalDateTime now = LocalDateTime.now();

            ps.setTimestamp(1, c.getDate() != null ? Timestamp.valueOf(c.getDate()) : null);
            ps.setString(2, c.getStatus() != null ? c.getStatus().name() : null);
            ps.setString(3, c.getNotes());
            ps.setString(4, c.getObservationsMedecin());

            if (c.getDossierMedical() != null && c.getDossierMedical().getId() != null) {
                ps.setLong(5, c.getDossierMedical().getId());
            } else {
                ps.setNull(5, Types.BIGINT);
            }

            ps.setTimestamp(6, Timestamp.valueOf(c.getDateCreation() != null ? c.getDateCreation() : now));
            ps.setTimestamp(7, Timestamp.valueOf(now));
            ps.setString(8, c.getCreePar());
            ps.setString(9, c.getModifierPar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    c.setId(keys.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de la consultation", e);
        }
    }

    @Override
    public void update(Consultation c) {
        String sql = """
                UPDATE consultation SET 
                date = ?, 
                status = ?, 
                notes = ?, 
                observations_medecin = ?, 
                dossier_medical_id = ?,
                dateMiseAJour = ?, 
                modifierPar = ?
                WHERE id = ?
                """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            LocalDateTime now = LocalDateTime.now();

            ps.setTimestamp(1, c.getDate() != null ? Timestamp.valueOf(c.getDate()) : null);
            ps.setString(2, c.getStatus() != null ? c.getStatus().name() : null);
            ps.setString(3, c.getNotes());
            ps.setString(4, c.getObservationsMedecin());

            if (c.getDossierMedical() != null && c.getDossierMedical().getId() != null) {
                ps.setLong(5, c.getDossierMedical().getId());
            } else {
                ps.setNull(5, Types.BIGINT);
            }

            ps.setTimestamp(6, Timestamp.valueOf(now));
            ps.setString(7, c.getModifierPar() != null ? c.getModifierPar() : "SYSTEM");
            ps.setLong(8, c.getId());

            int updated = ps.executeUpdate();
            if (updated == 0) {
                throw new RuntimeException("Consultation non trouvée pour mise à jour, ID=" + c.getId());
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la consultation ID=" + c.getId(), e);
        }
    }

    @Override
    public void delete(Consultation c) {
        if (c != null && c.getId() != null) {
            deleteById(c.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM " + TABLE + " WHERE id = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la consultation ID=" + id, e);
        }
    }

    @Override
    public List<Consultation> findByStatus(String status) {
        String sql = "SELECT * FROM " + TABLE + " WHERE status = ? ORDER BY date DESC";
        List<Consultation> result = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, status);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(RowMappers.mapConsultation(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par statut: " + status, e);
        }
        return result;
    }

    @Override
    public List<Consultation> findByDossierMedicalId(Long dossierId) {

        String sql = """
                SELECT * FROM consultation 
                WHERE dossier_medical_id = ? 
                ORDER BY date DESC
                """;

        List<Consultation> result = new ArrayList<>();

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, dossierId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(RowMappers.mapConsultation(rs));
                }
            }

        } catch (SQLException e) {
            if (e.getMessage().contains("Unknown column 'dossier_medical_id'")) {
                System.err.println("AVERTISSEMENT : Colonne 'dossier_medical_id' manquante dans la table consultation");
                return new ArrayList<>();
            }
            throw new RuntimeException("Erreur lors de la recherche par dossier médical ID=" + dossierId, e);
        }
        return result;
    }
}
