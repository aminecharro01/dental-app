package ma.WhiteLab.repository.modules.cabinet.impl.mySQL;

import ma.WhiteLab.conf.SessionFactory;
import ma.WhiteLab.entities.cabinet.Charges;
import ma.WhiteLab.repository.common.RowMappers;
import ma.WhiteLab.repository.modules.cabinet.api.ChargesRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class ChargesRepositoryImpl implements ChargesRepository {

    private static final String INSERT_SQL = """
        INSERT INTO charges (titre, description, montant, date, cabinet_id, dateCreation, creePar)
        VALUES (?, ?, ?, ?, ?, ?, ?)
        """;

    private static final String UPDATE_SQL = """
        UPDATE charges
        SET titre = ?, description = ?, montant = ?, date = ?, cabinet_id = ?,
            dateMiseAJour = ?, modifierPar = ?
        WHERE id = ?
        """;

    @Override
    public List<Charges> findAll() {
        String sql = "SELECT * FROM charges ORDER BY date DESC";
        List<Charges> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(RowMappers.mapCharges(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des charges", e);
        }
        return list;
    }

    @Override
    public Charges findById(Long id) {
        String sql = "SELECT * FROM charges WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapCharges(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById charges id=" + id, e);
        }
    }

    @Override
    public void create(Charges c) {
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, c.getTitre());
            ps.setString(2, c.getDescription());
            ps.setDouble(3, c.getMontant());

            // Gestion de la date de la charge
            if (c.getDate() != null) {
                ps.setTimestamp(4, Timestamp.valueOf(c.getDate()));
            } else {
                ps.setTimestamp(4, null);
            }

            // cabinet_id (Long) – on récupère l'ID de l'objet CabinetMedicale
            Long cabinetId = c.getCabinetMedicale() != null ? c.getCabinetMedicale().getId() : null;
            if (cabinetId != null) {
                ps.setLong(5, cabinetId);
            } else {
                ps.setNull(5, Types.BIGINT);
            }

            // dateCreation
            LocalDateTime now = LocalDateTime.now();
            ps.setTimestamp(6, Timestamp.valueOf(c.getDateCreation() != null ? c.getDateCreation() : now));

            // creePar
            ps.setString(7, c.getCreePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    c.setId(keys.getLong(1));
                }
            }

            // Mise à jour de dateCreation dans l'objet (au cas où on a utilisé now())
            if (c.getDateCreation() == null) {
                c.setDateCreation(now);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de la charge : " + c, e);
        }
    }

    @Override
    public void update(Charges c) {
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {

            ps.setString(1, c.getTitre());
            ps.setString(2, c.getDescription());
            ps.setDouble(3, c.getMontant());

            if (c.getDate() != null) {
                ps.setTimestamp(4, Timestamp.valueOf(c.getDate()));
            } else {
                ps.setNull(4, Types.TIMESTAMP);
            }

            Long cabinetId = c.getCabinetMedicale() != null ? c.getCabinetMedicale().getId() : null;
            if (cabinetId != null) {
                ps.setLong(5, cabinetId);
            } else {
                ps.setNull(5, Types.BIGINT);
            }

            ps.setTimestamp(6, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(7, c.getModifierPar());
            ps.setLong(8, c.getId());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Aucune charge trouvée avec l'id " + c.getId());
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la charge id=" + c.getId(), e);
        }
    }

    @Override
    public void delete(Charges c) {
        if (c != null && c.getId() != null) {
            deleteById(c.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM charges WHERE id = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur suppression charge id=" + id, e);
        }
    }

    @Override
    public List<Charges> findByCabinetId(Long cabinetId) {
        String sql = "SELECT * FROM charges WHERE cabinet_id = ? ORDER BY date DESC";
        List<Charges> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(RowMappers.mapCharges(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByCabinetId " + cabinetId, e);
        }
        return list;
    }

    @Override
    public List<Charges> findByDateBetween(LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT * FROM charges WHERE date BETWEEN ? AND ? ORDER BY date DESC";
        List<Charges> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(RowMappers.mapCharges(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche charges entre dates", e);
        }
        return list;
    }
}