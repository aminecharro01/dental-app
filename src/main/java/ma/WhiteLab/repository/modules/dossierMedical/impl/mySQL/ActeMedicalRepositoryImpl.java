package ma.WhiteLab.repository.modules.dossierMedical.impl.mySQL;

import ma.WhiteLab.conf.SessionFactory;
import ma.WhiteLab.entities.dossierMedical.ActeMedical;
import ma.WhiteLab.repository.common.RowMappers;
import ma.WhiteLab.repository.modules.dossierMedical.api.ActeMedicalRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ActeMedicalRepositoryImpl implements ActeMedicalRepository {

    @Override
    public List<ActeMedical> findAll() {
        String sql = "SELECT * FROM acte_medical ORDER BY libelle ASC";
        List<ActeMedical> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(RowMappers.mapActeMedical(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return out;
    }

    @Override
    public ActeMedical findById(Long id) {
        String sql = "SELECT * FROM acte_medical WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapActeMedical(rs);
                return null;
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(ActeMedical acte) {
        String sql = """
                INSERT INTO acte_medical(libelle, categorie, prixDeBase, dateCreation, creePar)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, acte.getLibelle());
            ps.setString(2, acte.getCategorie());
            ps.setFloat(3, acte.getPrixDeBase());
            ps.setTimestamp(4, Timestamp.valueOf(acte.getDateCreation() != null ? acte.getDateCreation() : java.time.LocalDateTime.now()));
            ps.setString(5, acte.getCreePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) acte.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(ActeMedical acte) {
        String sql = """
                UPDATE acte_medical
                SET libelle=?, categorie=?, prixDeBase=?, dateMiseAJour=?, modifierPar=?
                WHERE id=?
                """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, acte.getLibelle());
            ps.setString(2, acte.getCategorie());
            ps.setFloat(3, acte.getPrixDeBase());
            ps.setTimestamp(4, Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setString(5, acte.getModifierPar());
            ps.setLong(6, acte.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(ActeMedical acte) {
        if (acte != null) deleteById(acte.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM acte_medical WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Optional<ActeMedical> findByLibelle(String libelle) {
        String sql = "SELECT * FROM acte_medical WHERE libelle = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, libelle);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapActeMedical(rs));
                return Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
