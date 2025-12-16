package ma.WhiteLab.repository.modules.dossierMedical.impl.mySQL;

import ma.WhiteLab.conf.SessionFactory;
import ma.WhiteLab.entities.dossierMedical.InterventionMedecin;
import ma.WhiteLab.repository.common.RowMappers;
import ma.WhiteLab.repository.modules.dossierMedical.api.InterventionMedecinRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class InterventionMedecinRepositoryImpl implements InterventionMedecinRepository {

    @Override
    public List<InterventionMedecin> findAll() {
        String sql = "SELECT * FROM InterventionMedecin ORDER BY id ASC";
        List<InterventionMedecin> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(RowMappers.mapInterventionMedecin(rs));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public InterventionMedecin findById(Long id) {
        String sql = "SELECT * FROM InterventionMedecin WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapInterventionMedecin(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(InterventionMedecin im) {
        String sql = """
                INSERT INTO InterventionMedecin(prixDePatient, numDent, consultation_id, acteMedical_id, dateCreation, creePar)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDouble(1, im.getPrixDePatient());
            ps.setInt(2, im.getNumDent());
            ps.setLong(3, im.getConsultation() != null ? im.getConsultation().getId() : 0L);
            ps.setLong(4, im.getActeMedical() != null ? im.getActeMedical().getId() : 0L);
            ps.setTimestamp(5, Timestamp.valueOf(im.getDateCreation() != null ? im.getDateCreation() : java.time.LocalDateTime.now()));
            ps.setString(6, im.getCreePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) im.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(InterventionMedecin im) {
        String sql = """
                UPDATE InterventionMedecin
                SET prixDePatient=?, numDent=?, consultation_id=?, acteMedical_id=?, dateMiseAJour=?, modifierPar=?
                WHERE id=?
                """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDouble(1, im.getPrixDePatient());
            ps.setInt(2, im.getNumDent());
            ps.setLong(3, im.getConsultation() != null ? im.getConsultation().getId() : 0L);
            ps.setLong(4, im.getActeMedical() != null ? im.getActeMedical().getId() : 0L);
            ps.setTimestamp(5, Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setString(6, im.getModifierPar());
            ps.setLong(7, im.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(InterventionMedecin im) {
        if (im != null) deleteById(im.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM InterventionMedecin WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<InterventionMedecin> findByNumDent(int numDent) {
        String sql = "SELECT * FROM InterventionMedecin WHERE numDent = ? ORDER BY id ASC";
        List<InterventionMedecin> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, numDent);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapInterventionMedecin(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public List<InterventionMedecin> findByConsultationId(Long consultationId) {
        String sql = "SELECT * FROM InterventionMedecin WHERE consultation_id = ? ORDER BY id ASC";
        List<InterventionMedecin> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, consultationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapInterventionMedecin(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public List<InterventionMedecin> findByActeMedicalId(Long acteMedicalId) {
        String sql = "SELECT * FROM InterventionMedecin WHERE acteMedical_id = ? ORDER BY id ASC";
        List<InterventionMedecin> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, acteMedicalId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapInterventionMedecin(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }
}
