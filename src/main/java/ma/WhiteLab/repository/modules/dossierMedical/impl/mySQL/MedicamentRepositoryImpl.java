package ma.WhiteLab.repository.modules.dossierMedical.impl.mySQL;

import ma.WhiteLab.conf.SessionFactory;
import ma.WhiteLab.entities.dossierMedical.Medicament;
import ma.WhiteLab.repository.common.RowMappers;
import ma.WhiteLab.repository.modules.dossierMedical.api.MedicamentRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class MedicamentRepositoryImpl implements MedicamentRepository {

    @Override
    public List<Medicament> findAll() {
        String sql = "SELECT * FROM medicament";
        List<Medicament> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(RowMappers.mapMedicament(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return out;
    }

    @Override
    public Medicament findById(Long id) {
        String sql = "SELECT * FROM medicament WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapMedicament(rs);
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(Medicament m) {
        String sql = """
            INSERT INTO medicament(nom, labo, type, forme, remboursable, prix_unitaire, description)
            VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, m.getNom());
            ps.setString(2, m.getLabo());
            ps.setString(3, m.getType());
            ps.setString(4, m.getForme() != null ? m.getForme().name() : null);
            ps.setBoolean(5, m.isRemboursable());
            ps.setDouble(6, m.getPrixUnitaire());
            ps.setString(7, m.getDescription());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) m.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Medicament m) {
        String sql = """
            UPDATE medicament
            SET nom=?, labo=?, type=?, forme=?, remboursable=?, prix_unitaire=?, description=?
            WHERE id=?
        """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, m.getNom());
            ps.setString(2, m.getLabo());
            ps.setString(3, m.getType());
            ps.setString(4, m.getForme() != null ? m.getForme().name() : null);
            ps.setBoolean(5, m.isRemboursable());
            ps.setDouble(6, m.getPrixUnitaire());
            ps.setString(7, m.getDescription());
            ps.setLong(8, m.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Medicament m) {
        if (m != null) deleteById(m.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM medicament WHERE id=?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
    // ------------------------------------------------------
    //            MÉTHODES SPÉCIFIQUES
    // ------------------------------------------------------

    @Override
    public List<Medicament> searchByName(String nom) {
        String sql = "SELECT * FROM medicament WHERE nom = ?";
        List<Medicament> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nom);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                out.add(RowMappers.mapMedicament(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public List<Medicament> searchByNameLike(String text) {
        String sql = "SELECT * FROM medicament WHERE nom LIKE ?";
        List<Medicament> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "%" + text + "%");

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                out.add(RowMappers.mapMedicament(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public List<Medicament> findByLabo(String labo) {
        String sql = "SELECT * FROM medicament WHERE labo = ?";
        List<Medicament> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, labo);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                out.add(RowMappers.mapMedicament(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public List<Medicament> findRemboursables() {
        String sql = "SELECT * FROM medicament WHERE remboursable = 1";
        List<Medicament> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(RowMappers.mapMedicament(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public List<Medicament> findByForme(String forme) {
        String sql = "SELECT * FROM medicament WHERE forme = ?";
        List<Medicament> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, forme);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                out.add(RowMappers.mapMedicament(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public List<Medicament> findByPrixBetween(double min, double max) {
        String sql = "SELECT * FROM medicament WHERE prix_unitaire BETWEEN ? AND ?";
        List<Medicament> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDouble(1, min);
            ps.setDouble(2, max);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) {
                out.add(RowMappers.mapMedicament(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

}
