package ma.WhiteLab.repository.modules.cabinet.impl.mySQL;

import ma.WhiteLab.conf.SessionFactory;
import ma.WhiteLab.entities.cabinet.Revenus;
import ma.WhiteLab.repository.common.RowMappers;
import ma.WhiteLab.repository.modules.cabinet.api.RevenusRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class RevenusRepositoryImpl implements RevenusRepository {

    private static final String INSERT_SQL = """
        INSERT INTO revenus (titre, description, montant, date, cabinet_id, dateCreation, creePar)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

    private static final String UPDATE_SQL = """
        UPDATE revenus
        SET titre = ?, description = ?, montant = ?, date = ?, cabinet_id = ?,
            dateMiseAJour = ?, modifierPar = ?
        WHERE id = ?
        """;

    @Override
    public List<Revenus> findAll() {
        String sql = "SELECT * FROM revenus ORDER BY date DESC";
        List<Revenus> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(RowMappers.mapRevenus(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des revenus", e);
        }
        return list;
    }

    @Override
    public Revenus findById(Long id) {
        String sql = "SELECT * FROM revenus WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapRevenus(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById revenus id=" + id, e);
        }
    }

    @Override
    public void create(Revenus r) {
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, r.getTitre());
            ps.setString(2, r.getDescription());
            ps.setDouble(3, r.getMontant());

            // Date du revenu
            if (r.getDate() != null) {
                ps.setTimestamp(4, Timestamp.valueOf(r.getDate()));
            } else {
                ps.setNull(4, Types.TIMESTAMP);
            }

            // cabinet_id → crucial !
            Long cabinetId = r.getCabinetMedicale() != null ? r.getCabinetMedicale().getId() : null;
            if (cabinetId != null) {
                ps.setLong(5, cabinetId);
            } else {
                ps.setNull(5, Types.BIGINT);
            }

            // dateCreation
            LocalDateTime now = LocalDateTime.now();
            ps.setTimestamp(6, Timestamp.valueOf(r.getDateCreation() != null ? r.getDateCreation() : now));
            ps.setString(7, r.getCreePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    r.setId(keys.getLong(1));
                }
            }

            // On remplit dateCreation si elle était null
            if (r.getDateCreation() == null) {
                r.setDateCreation(now);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur création revenu : " + r.getTitre(), e);
        }
    }

    @Override
    public void update(Revenus r) {
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {

            ps.setString(1, r.getTitre());
            ps.setString(2, r.getDescription());
            ps.setDouble(3, r.getMontant());

            if (r.getDate() != null) {
                ps.setTimestamp(4, Timestamp.valueOf(r.getDate()));
            } else {
                ps.setNull(4, Types.TIMESTAMP);
            }

            // cabinet_id
            Long cabinetId = r.getCabinetMedicale() != null ? r.getCabinetMedicale().getId() : null;
            if (cabinetId != null) {
                ps.setLong(5, cabinetId);
            } else {
                ps.setNull(5, Types.BIGINT);
            }

            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(7, r.getModifierPar());
            ps.setLong(8, r.getId());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Aucun revenu trouvé avec l'id " + r.getId());
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur mise à jour revenu id=" + r.getId(), e);
        }
    }

    @Override
    public void delete(Revenus r) {
        if (r != null && r.getId() != null) {
            deleteById(r.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM revenus WHERE id = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur suppression revenu id=" + id, e);
        }
    }

    @Override
    public List<Revenus> findByCabinetId(Long cabinetId) {
        String sql = "SELECT * FROM revenus WHERE cabinet_id = ? ORDER BY date DESC";
        List<Revenus> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(RowMappers.mapRevenus(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByCabinetId revenus", e);
        }
        return list;
    }

    @Override
    public List<Revenus> findByDateBetween(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT * FROM revenus WHERE date BETWEEN ? AND ? ORDER BY date DESC";
        List<Revenus> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(RowMappers.mapRevenus(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche revenus par période", e);
        }
        return list;
    }

    @Override
    public List<Revenus> findByTitre(String titre) {
        String sql = "SELECT * FROM revenus WHERE titre LIKE ? ORDER BY date DESC";
        List<Revenus> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, "%" + titre + "%"); // LIKE pour recherche partielle
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(RowMappers.mapRevenus(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche revenus par titre", e);
        }
        return list;
    }
}