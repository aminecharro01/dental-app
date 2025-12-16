package ma.WhiteLab.repository.modules.patient.impl.mySQL;

import ma.WhiteLab.entities.patient.Antecedent;
import ma.WhiteLab.entities.patient.Patient;
import ma.WhiteLab.conf.SessionFactory;
import ma.WhiteLab.repository.common.RowMappers;
import ma.WhiteLab.repository.modules.patient.api.PatientRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

//auteur : Aymane Akarbich

public class PatientRepositoryImpl implements PatientRepository {

    // =========================
    //        CRUD
    // =========================

    @Override
    public List<Patient> findAll() {
        String sql = "SELECT * FROM Patient ORDER BY id";
        List<Patient> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) out.add(RowMappers.mapPatient(rs));

        } catch (SQLException e) { throw new RuntimeException(e); }

        return out;
    }

    @Override
    public Patient findById(Long id) {
        String sql = "SELECT * FROM Patient WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapPatient(rs);
            }

        } catch (SQLException e) { throw new RuntimeException(e); }

        return null;
    }

    @Override
    public void create(Patient p) {
        String sql = """
            INSERT INTO Patient(nom, prenom, adresse, telephone, email, 
                dateNaissance, dateCreation, sexe, assurance)
            VALUES(?,?,?,?,?,?,?,?,?)
        """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            ps.setString(3, p.getAdresse());
            ps.setString(4, p.getTelephone());
            ps.setString(5, p.getEmail());

            if (p.getDateNaissance() != null)
                ps.setDate(6, Date.valueOf(p.getDateNaissance()));
            else
                ps.setNull(6, Types.DATE);

            // dateCreation auto si null
            LocalDateTime created = p.getDateCreation() != null ?
                    p.getDateCreation() : LocalDateTime.now();

            ps.setTimestamp(7, Timestamp.valueOf(created));

            ps.setString(8, p.getSexe().name());
            ps.setString(9, p.getAssurance().name());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) p.setId(keys.getLong(1));
            }

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(Patient p) {
        String sql = """
            UPDATE Patient SET nom=?, prenom=?, adresse=?, telephone=?, email=?, 
                dateNaissance=?, dateMiseAJour=?, sexe=?, assurance=? 
            WHERE id=?
        """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, p.getNom());
            ps.setString(2, p.getPrenom());
            ps.setString(3, p.getAdresse());
            ps.setString(4, p.getTelephone());
            ps.setString(5, p.getEmail());

            if (p.getDateNaissance() != null)
                ps.setDate(6, Date.valueOf(p.getDateNaissance()));
            else
                ps.setNull(6, Types.DATE);

            // IMPORTANT : dateMiseAJour auto
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));

            ps.setString(8, p.getSexe().name());
            ps.setString(9, p.getAssurance().name());

            ps.setLong(10, p.getId());

            ps.executeUpdate();

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Patient p) {
        if (p != null) deleteById(p.getId());
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM Patient WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    // =========================
    //        EXTRA QUERIES
    // =========================

    @Override
    public Optional<Patient> findByEmail(String email) {
        String sql = "SELECT * FROM Patient WHERE email = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapPatient(rs));
            }

        } catch (SQLException e) { throw new RuntimeException(e); }

        return Optional.empty();
    }

    @Override
    public Optional<Patient> findByTelephone(String telephone) {
        String sql = "SELECT * FROM Patient WHERE telephone = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, telephone);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapPatient(rs));
            }

        } catch (SQLException e) { throw new RuntimeException(e); }

        return Optional.empty();
    }

    @Override
    public List<Patient> searchByNomPrenom(String keyword) {
        String sql = "SELECT * FROM Patient WHERE nom LIKE ? OR prenom LIKE ? ORDER BY nom, prenom";
        List<Patient> out = new ArrayList<>();
        String like = "%" + keyword + "%";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, like);
            ps.setString(2, like);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPatient(rs));
            }

        } catch (SQLException e) { throw new RuntimeException(e); }

        return out;
    }

    @Override
    public boolean existsById(Long id) {
        String sql = "SELECT 1 FROM Patient WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM Patient";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getLong(1);

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Patient> findPage(int limit, int offset) {
        String sql = "SELECT * FROM Patient ORDER BY id LIMIT ? OFFSET ?";
        List<Patient> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, limit);
            ps.setInt(2, offset);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPatient(rs));
            }

        } catch (SQLException e) { throw new RuntimeException(e); }

        return out;
    }

    // =========================
    //      MANY TO MANY
    // =========================

    @Override
    public void addAntecedentToPatient(Long patientId, Long antecedentId) {
        String sql = "INSERT INTO patient_antecedent(patient_id, antecedent_id) VALUES (?,?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, patientId);
            ps.setLong(2, antecedentId);
            ps.executeUpdate();

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void removeAntecedentFromPatient(Long patientId, Long antecedentId) {
        String sql = "DELETE FROM Patient_Antecedent WHERE patient_id=? AND antecedent_id=?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, patientId);
            ps.setLong(2, antecedentId);
            ps.executeUpdate();

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void removeAllAntecedentsFromPatient(Long patientId) {
        String sql = "DELETE FROM Patient_Antecedent WHERE patient_id=?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, patientId);
            ps.executeUpdate();

        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Antecedent> getAntecedentsOfPatient(Long patientId) {
        String sql = """
            SELECT a.*
            FROM Antecedent a 
            JOIN Patient_Antecedent pa ON pa.antecedent_id = a.id
            WHERE pa.patient_id = ?
            ORDER BY a.categorie, a.niveauRisque, a.nom
        """;

        List<Antecedent> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, patientId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapAntecedent(rs));
            }

        } catch (SQLException e) { throw new RuntimeException(e); }

        return out;
    }

    @Override
    public List<Patient> getPatientsByAntecedent(Long antecedentId) {
        String sql = """
            SELECT p.*
            FROM Patient p 
            JOIN Patient_Antecedent pa ON pa.patient_id = p.id
            WHERE pa.antecedent_id = ?
            ORDER BY p.nom, p.prenom
        """;

        List<Patient> out = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, antecedentId);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapPatient(rs));
            }

        } catch (SQLException e) { throw new RuntimeException(e); }

        return out;
    }
}
