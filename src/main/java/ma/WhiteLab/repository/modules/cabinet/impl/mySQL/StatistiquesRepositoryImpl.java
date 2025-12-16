package ma.WhiteLab.repository.modules.cabinet.impl.mySQL;

import ma.WhiteLab.conf.SessionFactory;
import ma.WhiteLab.entities.cabinet.Statistiques;
import ma.WhiteLab.repository.common.RowMappers;
import ma.WhiteLab.repository.modules.cabinet.api.StatistiquesRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class StatistiquesRepositoryImpl implements StatistiquesRepository {

    @Override
    public List<Statistiques> findAll() {
        String sql = "SELECT * FROM statistiques ORDER BY dateCalcul DESC";
        List<Statistiques> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(RowMappers.mapStatistiques(rs));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return out;
    }

    @Override
    public Statistiques findById(Long id) {
        String sql = "SELECT * FROM statistiques WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapStatistiques(rs);
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(Statistiques s) {
        String sql = """
                INSERT INTO statistiques(
                    nom, categorie, chiffre, dateCalcul, cabinet_id, dateCreation, creePar
                ) VALUES (?, ?, ?, ?, ?, ?, ?)
                """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, s.getNom());
            ps.setString(2, s.getCategorie() != null ? s.getCategorie().name() : null);
            ps.setDouble(3, s.getChiffre() != null ? s.getChiffre() : 0.0);
            ps.setDate(4, s.getDateCalcul() != null ? Date.valueOf(s.getDateCalcul()) : null);
            ps.setLong(5, s.getCabinetMedicale() != null ? s.getCabinetMedicale().getId() : 0L);
            ps.setTimestamp(6, Timestamp.valueOf(s.getDateCreation() != null ? s.getDateCreation() : java.time.LocalDateTime.now()));
            ps.setString(7, s.getCreePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) s.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Statistiques s) {
        String sql = """
                UPDATE statistiques
                SET nom=?, categorie=?, chiffre=?, dateCalcul=?, cabinet_id=?, dateMiseAJour=?, modifierPar=?
                WHERE id=?
                """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, s.getNom());
            ps.setString(2, s.getCategorie() != null ? s.getCategorie().name() : null);
            ps.setDouble(3, s.getChiffre() != null ? s.getChiffre() : 0.0);
            ps.setDate(4, s.getDateCalcul() != null ? Date.valueOf(s.getDateCalcul()) : null);
            ps.setLong(5, s.getCabinetMedicale() != null ? s.getCabinetMedicale().getId() : 0L);
            ps.setTimestamp(6, Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setString(7, s.getModifierPar());
            ps.setLong(8, s.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Statistiques s) {
        if (s != null) deleteById(s.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM statistiques WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<Statistiques> findByCabinetId(Long cabinetId) {
        String sql = "SELECT * FROM statistiques WHERE cabinet_id = ? ORDER BY dateCalcul DESC";
        List<Statistiques> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, cabinetId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapStatistiques(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return out;
    }
}
