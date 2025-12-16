package ma.WhiteLab.repository.modules.dossierMedical.impl.mySQL;

import ma.WhiteLab.conf.SessionFactory;
import ma.WhiteLab.entities.dossierMedical.Prescription;
import ma.WhiteLab.repository.common.RowMappers;
import ma.WhiteLab.repository.modules.dossierMedical.api.PrescriptionRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class PrescriptionRepositoryImpl implements PrescriptionRepository {

    @Override
    public List<Prescription> findAll() {
        String sql = "SELECT * FROM prescription";
        List<Prescription> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(RowMappers.mapPrescription(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return out;
    }

    @Override
    public Prescription findById(Long id) {
        String sql = "SELECT * FROM prescription WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapPrescription(rs);
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(Prescription p) {
        String sql = """
            INSERT INTO prescription (qte, frequence, duree, ordonnance_id, medicament_id)
            VALUES (?, ?, ?, ?, ?)
        """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setInt(1, p.getQte());
            ps.setString(2, p.getFrequence());
            ps.setInt(3, p.getDuree());
            ps.setObject(4, p.getOrdonnance() != null ? p.getOrdonnance().getId() : null, Types.BIGINT);
            ps.setObject(5, p.getMedicament() != null ? p.getMedicament().getId() : null, Types.BIGINT);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Prescription p) {
        String sql = """
            UPDATE prescription
            SET qte = ?, frequence = ?, duree = ?, ordonnance_id = ?, medicament_id = ?
            WHERE id = ?
        """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setInt(1, p.getQte());
            ps.setString(2, p.getFrequence());
            ps.setInt(3, p.getDuree());
            ps.setObject(4, p.getOrdonnance() != null ? p.getOrdonnance().getId() : null, Types.BIGINT);
            ps.setObject(5, p.getMedicament() != null ? p.getMedicament().getId() : null, Types.BIGINT);
            ps.setLong(6, p.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Prescription p) {
        if (p != null) deleteById(p.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM prescription WHERE id = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // -----------------------------------------
    //            MÉTHODES SPÉCIFIQUES
    // -----------------------------------------

    @Override
    public List<Prescription> findByOrdonnanceId(Long ordonnanceId) {
        String sql = "SELECT * FROM prescription WHERE ordonnance_id = ?";
        List<Prescription> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, ordonnanceId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(RowMappers.mapPrescription(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    @Override
    public List<Prescription> findByMedicamentId(Long medicamentId) {
        String sql = "SELECT * FROM prescription WHERE medicament_id = ?";
        List<Prescription> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, medicamentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(RowMappers.mapPrescription(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    @Override
    public List<Prescription> findByFrequence(String frequence) {
        String sql = "SELECT * FROM prescription WHERE frequence LIKE ?";
        List<Prescription> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "%" + frequence + "%");

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(RowMappers.mapPrescription(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return list;
    }

    @Override
    public boolean exists(Long ordonnanceId, Long medicamentId) {
        String sql = """
            SELECT COUNT(*) FROM prescription
            WHERE ordonnance_id = ? AND medicament_id = ?
        """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, ordonnanceId);
            ps.setLong(2, medicamentId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1) > 0;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return false;
    }
}
