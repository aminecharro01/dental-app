package ma.WhiteLab.repository.modules.dossierMedical.impl.mySQL;

import ma.WhiteLab.conf.SessionFactory;
import ma.WhiteLab.entities.dossierMedical.Certificat;
import ma.WhiteLab.repository.common.RowMappers;
import ma.WhiteLab.repository.modules.dossierMedical.api.CertificatRepository;

import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class CertificatRepositoryImpl implements CertificatRepository {

    private static final String INSERT_SQL = """
        INSERT INTO Certificat 
        (dateDebut, dateFin, dureeRepos, contenu, 
         consultation_id, dossier_med_id, 
         dateCreation, creePar)
        VALUES (?, ?, ?, ?, ?, ?, ?, ?)
        """;

    private static final String UPDATE_SQL = """
        UPDATE Certificat
        SET dateDebut = ?, dateFin = ?, dureeRepos = ?, contenu = ?,
            consultation_id = ?, dossier_med_id = ?,
            dateMiseAJour = ?, modifierPar = ?
        WHERE id = ?
        """;

    @Override
    public List<Certificat> findAll() {
        String sql = "SELECT * FROM Certificat ORDER BY dateDebut DESC";
        List<Certificat> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(RowMappers.mapCertificat(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur récupération de tous les certificats", e);
        }
        return list;
    }

    @Override
    public Certificat findById(Long id) {
        String sql = "SELECT * FROM Certificat WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapCertificat(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById certificat id=" + id, e);
        }
    }

    @Override
    public void create(Certificat c) {
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(INSERT_SQL, Statement.RETURN_GENERATED_KEYS)) {

            // dateDebut
            if (c.getDateDebut() != null) {
                ps.setDate(1, Date.valueOf(c.getDateDebut()));
            } else {
                ps.setNull(1, Types.DATE);
            }

            // dateFin
            if (c.getDateFin() != null) {
                ps.setDate(2, Date.valueOf(c.getDateFin()));
            } else {
                ps.setNull(2, Types.DATE);
            }

            ps.setInt(3, c.getDureeRepos());
            ps.setString(4, c.getContenu());

            // consultation_id
            Long consultId = c.getConsultation() != null ? c.getConsultation().getId() : null;
            if (consultId != null) {
                ps.setLong(5, consultId);
            } else {
                ps.setNull(5, Types.BIGINT);
            }

            // dossier_med_id (ou dossierMedical_id selon ton vrai nom de colonne)
            Long dossierId = c.getDossierMedical() != null ? c.getDossierMedical().getId() : null;
            if (dossierId != null) {
                ps.setLong(6, dossierId);
            } else {
                ps.setNull(6, Types.BIGINT);
            }

            LocalDateTime now = LocalDateTime.now();
            ps.setTimestamp(7, Timestamp.valueOf(c.getDateCreation() != null ? c.getDateCreation() : now));
            ps.setString(8, c.getCreePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    c.setId(keys.getLong(1));
                }
            }

            if (c.getDateCreation() == null) {
                c.setDateCreation(now);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur création certificat", e);
        }
    }

    @Override
    public void update(Certificat c) {
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_SQL)) {

            if (c.getDateDebut() != null) {
                ps.setDate(1, Date.valueOf(c.getDateDebut()));
            } else {
                ps.setNull(1, Types.DATE);
            }

            if (c.getDateFin() != null) {
                ps.setDate(2, Date.valueOf(c.getDateFin()));
            } else {
                ps.setNull(2, Types.DATE);
            }

            ps.setInt(3, c.getDureeRepos());
            ps.setString(4, c.getContenu());

            Long consultId = c.getConsultation() != null ? c.getConsultation().getId() : null;
            if (consultId != null) {
                ps.setLong(5, consultId);
            } else {
                ps.setNull(5, Types.BIGINT);
            }

            Long dossierId = c.getDossierMedical() != null ? c.getDossierMedical().getId() : null;
            if (dossierId != null) {
                ps.setLong(6, dossierId);
            } else {
                ps.setNull(6, Types.BIGINT);
            }

            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(8, c.getModifierPar());
            ps.setLong(9, c.getId());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                throw new RuntimeException("Certificat non trouvé pour mise à jour (id=" + c.getId() + ")");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur mise à jour certificat id=" + c.getId(), e);
        }
    }

    @Override
    public void delete(Certificat c) {
        if (c != null && c.getId() != null) {
            deleteById(c.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Certificat WHERE id = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur suppression certificat id=" + id, e);
        }
    }

    @Override
    public List<Certificat> findByDateDebutRange(LocalDate start, LocalDate end) {
        String sql = "SELECT * FROM Certificat WHERE dateDebut BETWEEN ? AND ? ORDER BY dateDebut DESC";
        List<Certificat> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setDate(1, Date.valueOf(start));
            ps.setDate(2, Date.valueOf(end));

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(RowMappers.mapCertificat(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche certificats par période", e);
        }
        return list;
    }

    @Override
    public List<Certificat> findByDureeRepos(int duree) {
        String sql = "SELECT * FROM Certificat WHERE dureeRepos = ? ORDER BY dateDebut DESC";
        List<Certificat> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, duree);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(RowMappers.mapCertificat(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche certificats par durée", e);
        }
        return list;
    }

    @Override
    public List<Certificat> findByDossierMedicalId(Long dossierMedicalId) {
        String sql = "SELECT * FROM Certificat WHERE dossier_med_id = ? ORDER BY dateDebut DESC";
        List<Certificat> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, dossierMedicalId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(RowMappers.mapCertificat(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByDossierMedicalId " + dossierMedicalId, e);
        }
        return list;
    }

    // Bonus utile
    @Override
    public List<Certificat> findByConsultationId(Long consultationId) {
        String sql = "SELECT * FROM Certificat WHERE consultation_id = ? ORDER BY dateDebut DESC";
        List<Certificat> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, consultationId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(RowMappers.mapCertificat(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByConsultationId", e);
        }
        return list;
    }
}