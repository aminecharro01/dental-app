package ma.WhiteLab.repository.modules.cabinet.impl.mySQL;

import ma.WhiteLab.conf.SessionFactory;
import ma.WhiteLab.entities.cabinet.CabinetMedicale;
import ma.WhiteLab.repository.modules.cabinet.api.CabinetMedicaleRepository;
import ma.WhiteLab.repository.common.RowMappers;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class CabinetMedicaleRepositoryImpl implements CabinetMedicaleRepository {

    @Override
    public List<CabinetMedicale> findAll() {
        String sql = "SELECT * FROM CabinetMedicale";
        List<CabinetMedicale> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(RowMappers.mapCabinetMedical(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return out;
    }

    @Override
    public CabinetMedicale findById(Long id) {
        String sql = "SELECT * FROM CabinetMedicale WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapCabinetMedical(rs);
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(CabinetMedicale c) {
        String sql = """
                INSERT INTO CabinetMedicale(
                    nom, email, logo, categorie, tel1, tel2, siteWeb, instagram, facebook, description, dateCreation, creePar
                ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.getNom());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getLogo());
            ps.setString(4, c.getCategorie());
            ps.setString(5, c.getTel1());
            ps.setString(6, c.getTel2());
            ps.setString(7, c.getSiteWeb());
            ps.setString(8, c.getInstagram());
            ps.setString(9, c.getFacebook());
            ps.setString(10, c.getDescription());
            ps.setTimestamp(11, Timestamp.valueOf(c.getDateCreation() != null ? c.getDateCreation() : java.time.LocalDateTime.now()));
            ps.setString(12, c.getCreePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) c.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(CabinetMedicale c) {
        String sql = """
                UPDATE CabinetMedicale
                SET nom=?, email=?, logo=?, categorie=?, tel1=?, tel2=?, siteWeb=?, instagram=?, facebook=?, description=?, dateMiseAJour=?, modifierPar=?
                WHERE id=?
                """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setString(1, c.getNom());
            ps.setString(2, c.getEmail());
            ps.setString(3, c.getLogo());
            ps.setString(4, c.getCategorie());
            ps.setString(5, c.getTel1());
            ps.setString(6, c.getTel2());
            ps.setString(7, c.getSiteWeb());
            ps.setString(8, c.getInstagram());
            ps.setString(9, c.getFacebook());
            ps.setString(10, c.getDescription());
            ps.setTimestamp(11, Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setString(12, c.getModifierPar());
            ps.setLong(13, c.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(CabinetMedicale c) {
        if (c != null) deleteById(c.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM CabinetMedicale WHERE id = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<CabinetMedicale> findByNom(String nom) {
        String sql = "SELECT * FROM CabinetMedicale WHERE nom = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, nom);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapCabinetMedical(rs));
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CabinetMedicale> findByCategorie(String categorie) {
        String sql = "SELECT * FROM CabinetMedicale WHERE categorie = ?";
        List<CabinetMedicale> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, categorie);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapCabinetMedical(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public Optional<CabinetMedicale> findByEmail(String email) {
        String sql = "SELECT * FROM CabinetMedicale WHERE email = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapCabinetMedical(rs));
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<CabinetMedicale> findAllOrderByNom() {
        String sql = "SELECT * FROM CabinetMedicale ORDER BY nom";
        List<CabinetMedicale> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(RowMappers.mapCabinetMedical(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }
}
