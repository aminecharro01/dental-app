package ma.WhiteLab.repository.modules.dossierMedical.impl.mySQL;

import ma.WhiteLab.conf.SessionFactory;
import ma.WhiteLab.entities.dossierMedical.Ordonnance;
import ma.WhiteLab.repository.common.RowMappers;
import ma.WhiteLab.repository.modules.dossierMedical.api.OrdonnanceRepository;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class OrdonnanceRepositoryImpl implements OrdonnanceRepository {

    // ------------------------------------------------------
    //                      CRUD
    // ------------------------------------------------------

    @Override
    public List<Ordonnance> findAll() {
        String sql = "SELECT * FROM ordonnance";
        List<Ordonnance> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(RowMappers.mapOrdonnance(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return out;
    }

    @Override
    public Ordonnance findById(Long id) {
        String sql = "SELECT * FROM ordonnance WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapOrdonnance(rs);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return null;
    }

    @Override
    public void create(Ordonnance o) {
        String sql = """
            INSERT INTO ordonnance(date_ordonnance, consultation_id, dossier_med_id)
            VALUES (?, ?, ?)
        """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setDate(1, o.getDateOrdonnance() != null ? Date.valueOf(o.getDateOrdonnance()) : null);
            ps.setObject(2, o.getConsultation() != null ? o.getConsultation().getId() : null, Types.BIGINT);
            ps.setObject(3, o.getDossierMedical() != null ? o.getDossierMedical().getId() : null, Types.BIGINT);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) o.setId(keys.getLong(1));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void update(Ordonnance o) {
        String sql = """
            UPDATE ordonnance
            SET date_ordonnance=?, consultation_id=?, dossier_med_id=?
            WHERE id=?
        """;

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setDate(1, o.getDateOrdonnance() != null ? Date.valueOf(o.getDateOrdonnance()) : null);
            ps.setObject(2, o.getConsultation() != null ? o.getConsultation().getId() : null, Types.BIGINT);
            ps.setObject(3, o.getDossierMedical() != null ? o.getDossierMedical().getId() : null, Types.BIGINT);
            ps.setLong(4, o.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public void delete(Ordonnance o) {
        if (o != null) deleteById(o.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM ordonnance WHERE id = ?";

        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    // ------------------------------------------------------
    //              MÉTHODES SPÉCIFIQUES
    // ------------------------------------------------------

    @Override
    public List<Ordonnance> findByDossierId(Long dossierId) {
        String sql = "SELECT * FROM ordonnance WHERE dossier_med_id = ?";
        List<Ordonnance> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, dossierId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) out.add(RowMappers.mapOrdonnance(rs));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return out;
    }

    @Override
    public List<Ordonnance> findByConsultationId(Long consultationId) {
        String sql = "SELECT * FROM ordonnance WHERE consultation_id = ?";
        List<Ordonnance> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, consultationId);

            ResultSet rs = ps.executeQuery();
            while (rs.next()) out.add(RowMappers.mapOrdonnance(rs));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return out;
    }

    @Override
    public List<Ordonnance> findByDate(LocalDate date) {
        String sql = "SELECT * FROM ordonnance WHERE date_ordonnance = ?";
        List<Ordonnance> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(date));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) out.add(RowMappers.mapOrdonnance(rs));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return out;
    }

    @Override
    public List<Ordonnance> findBetweenDates(LocalDate start, LocalDate end) {
        String sql = "SELECT * FROM ordonnance WHERE date_ordonnance BETWEEN ? AND ?";
        List<Ordonnance> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));

            ResultSet rs = ps.executeQuery();
            while (rs.next()) out.add(RowMappers.mapOrdonnance(rs));

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return out;
    }
}
