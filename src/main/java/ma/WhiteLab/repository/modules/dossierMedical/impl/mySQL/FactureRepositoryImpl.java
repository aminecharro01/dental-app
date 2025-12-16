package ma.WhiteLab.repository.modules.dossierMedical.impl.mySQL;

import ma.WhiteLab.conf.SessionFactory;
import ma.WhiteLab.entities.dossierMedical.Facture;
import ma.WhiteLab.repository.common.RowMappers;
import ma.WhiteLab.repository.modules.dossierMedical.api.FactureRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FactureRepositoryImpl implements FactureRepository {

    private static final String INSERT_SQL = """
        INSERT INTO facture (totalFact, totalPaye, reste, date, statut,
                             consultation_id, sf_id,
                             dateCreation, creePar)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

    private static final String UPDATE_SQL = """
        UPDATE facture
        SET totalFact = ?, totalPaye = ?, reste = ?, date = ?, statut = ?,
            consultation_id = ?, sf_id = ?,
            dateMiseAJour = ?, modifierPar = ?
        WHERE id = ?
        """;

    @Override
    public List<Facture> findAll() {
        String sql = "SELECT * FROM facture ORDER BY date DESC";
        List<Facture> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(RowMappers.mapFacture(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur récupération de toutes les factures", e);
        }
        return list;
    }

    @Override
    public Facture findById(Long id) {
        String sql = "SELECT * FROM facture WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapFacture(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById facture id=" + id, e);
        }
    }

    @Override
    public void create(Facture f) {
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            ps.setFloat(1, f.getTotalFact());
            ps.setFloat(2, f.getTotalPaye());
            ps.setFloat(3, f.getReste());

            // date de la facture (LocalDate → java.sql.Date)
            if (f.getDate() != null) {
                ps.setDate(4, Date.valueOf(f.getDate()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            // statut (enum → String)
            ps.setString(5, f.getStatut() != null ? f.getStatut().name() : null);

            // consultation_id
            Long consultationId = f.getConsultation() != null ? f.getConsultation().getId() : null;
            if (consultationId != null) {
                ps.setLong(6, consultationId);
            } else {
                ps.setNull(6, Types.BIGINT);
            }

            // sf_id (SuiviFinancier)
            Long sfId = f.getSf() != null ? f.getSf().getId() : null;
            if (sfId != null) {
                ps.setLong(7, sfId);
            } else {
                ps.setNull(7, Types.BIGINT);
            }

            // dateCreation + creePar
            LocalDateTime now = LocalDateTime.now();
            ps.setTimestamp(8, Timestamp.valueOf(f.getDateCreation() != null ? f.getDateCreation() : now));
            ps.setString(9, f.getCreePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    f.setId(keys.getLong(1));
                }
            }

            // On remplit dateCreation si elle était null
            if (f.getDateCreation() == null) {
                f.setDateCreation(now);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur création facture : " + f, e);
        }
    }

    @Override
    public void update(Facture f) {
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {

            ps.setFloat(1, f.getTotalFact());
            ps.setFloat(2, f.getTotalPaye());
            ps.setFloat(3, f.getReste());

            if (f.getDate() != null) {
                ps.setDate(4, Date.valueOf(f.getDate()));
            } else {
                ps.setNull(4, Types.DATE);
            }

            ps.setString(5, f.getStatut() != null ? f.getStatut().name() : null);

            Long consultationId = f.getConsultation() != null ? f.getConsultation().getId() : null;
            if (consultationId != null) {
                ps.setLong(6, consultationId);
            } else {
                ps.setNull(6, Types.BIGINT);
            }

            Long sfId = f.getSf() != null ? f.getSf().getId() : null;
            if (sfId != null) {
                ps.setLong(7, sfId);
            } else {
                ps.setNull(7, Types.BIGINT);
            }

            ps.setTimestamp(8, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(9, f.getModifierPar());
            ps.setLong(10, f.getId());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Facture non trouvée pour mise à jour, id=" + f.getId());
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur mise à jour facture id=" + f.getId(), e);
        }
    }

    @Override
    public void delete(Facture f) {
        if (f != null && f.getId() != null) {
            deleteById(f.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM facture WHERE id = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur suppression facture id=" + id, e);
        }
    }

    @Override
    public List<Facture> findByConsultationId(Long consultationId) {
        String sql = "SELECT * FROM facture WHERE consultation_id = ? ORDER BY date DESC";
        List<Facture> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, consultationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(RowMappers.mapFacture(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByConsultationId " + consultationId, e);
        }
        return list;
    }

    @Override
    public List<Facture> findByStatut(String statut) {
        String sql = "SELECT * FROM facture WHERE statut = ? ORDER BY date DESC";
        List<Facture> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, statut);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(RowMappers.mapFacture(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByStatut " + statut, e);
        }
        return list;
    }

    @Override
    public List<Facture> findByDateBetween(LocalDate start, LocalDate end) {
        String sql = "SELECT * FROM facture WHERE date BETWEEN ? AND ? ORDER BY date DESC";
        List<Facture> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(RowMappers.mapFacture(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche factures entre " + start + " et " + end, e);
        }
        return list;
    }
}