package ma.WhiteLab.repository.modules.dossierMedical.impl.mySQL;

import ma.WhiteLab.conf.SessionFactory;
import ma.WhiteLab.entities.dossierMedical.DossierMedical;
import ma.WhiteLab.repository.common.RowMappers;
import ma.WhiteLab.repository.modules.dossierMedical.api.DossierMedicalRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
//auteur : Aymane Akarbich

public class DossierMedicalRepositoryImpl implements DossierMedicalRepository {

    @Override
    public List<DossierMedical> findAll() {
        String sql = "SELECT * FROM dossiermedical ORDER BY id DESC";
        List<DossierMedical> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapDossierMedical(rs));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public DossierMedical findById(Long id) {
        String sql = "SELECT * FROM dossiermedical WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapDossierMedical(rs);
                return null;
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void create(DossierMedical d) {
        String sql = """
            INSERT INTO dossiermedical(historique, pat_id, medcine_id, dateCreation, creePar)
            VALUES(?,?,?,?,?)
            """;
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, d.getHistorique());
            ps.setLong(2, d.getPat() != null ? d.getPat().getId() : Types.NULL);
            ps.setLong(3, d.getMedcine() != null ? d.getMedcine().getId() : Types.NULL);

            ps.setTimestamp(4, Timestamp.valueOf(d.getDateCreation() != null ? d.getDateCreation() : java.time.LocalDateTime.now()));
            ps.setString(5, d.getCreePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) d.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(DossierMedical d) {
        String sql = """
            UPDATE dossiermedical
            SET historique=?, pat_id=?, medcine_id=?, dateMiseAJour=?, modifierPar=?
            WHERE id=?
            """;
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, d.getHistorique());
            ps.setLong(2, d.getPat() != null ? d.getPat().getId() : Types.NULL);
            ps.setLong(3, d.getMedcine() != null ? d.getMedcine().getId() : Types.NULL);
            ps.setTimestamp(4, Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setString(5, d.getModifierPar());
            ps.setLong(6, d.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(DossierMedical d) {
        if (d != null) deleteById(d.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM dossiermedical WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // --- Extras ---
    @Override
    public Optional<DossierMedical> findByPatientId(Long patientId) {
        String sql = "SELECT * FROM dossiermedical WHERE pat_id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, patientId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapDossierMedical(rs));
                return Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public List<DossierMedical> findByMedecinId(Long medecinId) {
        String sql = "SELECT * FROM dossiermedical WHERE medcine_id = ?";
        List<DossierMedical> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, medecinId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapDossierMedical(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return out;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM dossiermedical";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
