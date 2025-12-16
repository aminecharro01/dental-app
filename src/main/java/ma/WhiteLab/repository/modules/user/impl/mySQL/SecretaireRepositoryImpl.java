package ma.WhiteLab.repository.modules.user.impl.mySQL;

import ma.WhiteLab.conf.SessionFactory;
import ma.WhiteLab.entities.user.Secretaire;
import ma.WhiteLab.repository.common.RowMappers;
import ma.WhiteLab.repository.modules.user.api.SecretaireRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class SecretaireRepositoryImpl implements SecretaireRepository {

    // --- Requêtes INSERT par table ---
    private static final String INSERT_UTILISATEUR_SQL = """
        INSERT INTO Utilisateur (
            nom, prenom, email, adresse, cin, telephone, dateNaissance, sexe,
            motDePasse, dateCreation, creePar
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

    private static final String INSERT_STAFF_SQL = """
        INSERT INTO Staff (
            id, salaire, prime, dateRecrutement, soldeConge, cabinetMedicale_id
        ) VALUES (?, ?, ?, ?, ?, ?)
        """;

    private static final String INSERT_SECRETAIRE_SQL = """
        INSERT INTO Secretaire (id, numCNS, commission) VALUES (?, ?, ?)
        """;

    // --- Requêtes UPDATE par table ---
    private static final String UPDATE_UTILISATEUR_SQL = """
        UPDATE Utilisateur SET
            nom = ?, prenom = ?, email = ?, adresse = ?, cin = ?,
            telephone = ?, dateNaissance = ?, sexe = ?,
            motDePasse = ?, dateMiseAJour = ?, modifierPar = ?
        WHERE id = ?
        """;

    private static final String UPDATE_STAFF_SQL = """
        UPDATE Staff SET
            salaire = ?, prime = ?, dateRecrutement = ?, soldeConge = ?,
            cabinetMedicale_id = ?
        WHERE id = ?
        """;

    private static final String UPDATE_SECRETAIRE_SQL = """
        UPDATE Secretaire SET numCNS = ?, commission = ? WHERE id = ?
        """;

    // --- Requête SELECT avec JOIN pour récupérer toutes les données ---
    private static final String SELECT_SECRETAIRE_BASE_SQL = """
        SELECT u.*, s.salaire, s.prime, s.dateRecrutement, s.soldeConge, 
               s.cabinetMedicale_id, sec.numCNS, sec.commission 
        FROM Utilisateur u
        INNER JOIN Staff s ON u.id = s.id
        INNER JOIN Secretaire sec ON u.id = sec.id
        """;


    // ---------------------- READ Operations ----------------------

    @Override
    public List<Secretaire> findAll() {
        String sql = SELECT_SECRETAIRE_BASE_SQL + " ORDER BY u.nom, u.prenom";
        List<Secretaire> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(RowMappers.mapSecretaire(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des secrétaires", e);
        }
        return list;
    }

    @Override
    public Secretaire findById(Long id) {
        String sql = SELECT_SECRETAIRE_BASE_SQL + " WHERE u.id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapSecretaire(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Secrétaire non trouvée avec id = " + id, e);
        }
    }


    // ---------------------- CREATE Operation (Transaction) ----------------------

    @Override
    public void create(Secretaire s) {
        LocalDateTime now = LocalDateTime.now();

        try (Connection conn = SessionFactory.getInstance().getConnection()) {

            conn.setAutoCommit(false);

            // 1. INSERTION DANS LA TABLE UTILISATEUR
            try (PreparedStatement psUser = conn.prepareStatement(INSERT_UTILISATEUR_SQL, Statement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, s.getNom());
                psUser.setString(2, s.getPrenom());
                psUser.setString(3, s.getEmail());
                psUser.setString(4, s.getAdresse());
                psUser.setString(5, s.getCin());
                psUser.setString(6, s.getTelephone());
                psUser.setDate(7, s.getDateNaissance() != null ? Date.valueOf(s.getDateNaissance()) : null);
                psUser.setString(8, s.getSexe() != null ? s.getSexe().name() : null);
                psUser.setString(9, s.getMotDePasse());
                psUser.setTimestamp(10, Timestamp.valueOf(s.getDateCreation() != null ? s.getDateCreation() : now));
                psUser.setString(11, s.getCreePar());

                psUser.executeUpdate();

                try (ResultSet keys = psUser.getGeneratedKeys()) {
                    if (keys.next()) s.setId(keys.getLong(1));
                }
            }

            if (s.getId() == null) throw new SQLException("ID d'utilisateur non généré. Échec de la création.");

            // 2. INSERTION DANS LA TABLE STAFF
            try (PreparedStatement psStaff = conn.prepareStatement(INSERT_STAFF_SQL)) {
                psStaff.setLong(1, s.getId());
                psStaff.setObject(2, s.getSalaire(), Types.DOUBLE);
                psStaff.setObject(3, s.getPrime(), Types.DOUBLE);
                psStaff.setDate(4, s.getDateRecrutement() != null ? Date.valueOf(s.getDateRecrutement()) : null);
                psStaff.setInt(5, s.getSoldeConge());

                Long cabinetId = s.getCabinetMedicale() != null ? s.getCabinetMedicale().getId() : null;
                if (cabinetId != null) {
                    psStaff.setLong(6, cabinetId);
                } else {
                    psStaff.setNull(6, Types.BIGINT);
                }

                psStaff.executeUpdate();
            }

            // 3. INSERTION DANS LA TABLE SECRETAIRE
            try (PreparedStatement psSecretaire = conn.prepareStatement(INSERT_SECRETAIRE_SQL)) {
                psSecretaire.setLong(1, s.getId());
                psSecretaire.setString(2, s.getNumCNS());
                psSecretaire.setObject(3, s.getCommission(), Types.DOUBLE);
                psSecretaire.executeUpdate();
            }

            conn.commit();
            conn.setAutoCommit(true);

            if (s.getDateCreation() == null) {
                s.setDateCreation(now);
            }

        } catch (SQLException e) {
            try (Connection conn = SessionFactory.getInstance().getConnection()) {
                conn.rollback();
            } catch (SQLException rollbackE) { /* Ignorer l'erreur de rollback */ }
            throw new RuntimeException("Erreur création secrétaire : " + s.getNom() + " " + s.getPrenom(), e);
        }
    }


    // ---------------------- UPDATE Operation (Transaction) ----------------------

    @Override
    public void update(Secretaire s) {
        try (Connection conn = SessionFactory.getInstance().getConnection()) {

            conn.setAutoCommit(false);

            // 1. UPDATE UTILISATEUR
            try (PreparedStatement psUser = conn.prepareStatement(UPDATE_UTILISATEUR_SQL)) {
                psUser.setString(1, s.getNom());
                psUser.setString(2, s.getPrenom());
                psUser.setString(3, s.getEmail());
                psUser.setString(4, s.getAdresse());
                psUser.setString(5, s.getCin());
                psUser.setString(6, s.getTelephone());
                psUser.setDate(7, s.getDateNaissance() != null ? Date.valueOf(s.getDateNaissance()) : null);
                psUser.setString(8, s.getSexe() != null ? s.getSexe().name() : null);
                psUser.setString(9, s.getMotDePasse());
                psUser.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
                psUser.setString(11, s.getModifierPar());
                psUser.setLong(12, s.getId());

                if (psUser.executeUpdate() == 0) throw new SQLException("Utilisateur non trouvé pour mise à jour.");
            }

            // 2. UPDATE STAFF
            try (PreparedStatement psStaff = conn.prepareStatement(UPDATE_STAFF_SQL)) {
                psStaff.setObject(1, s.getSalaire(), Types.DOUBLE);
                psStaff.setObject(2, s.getPrime(), Types.DOUBLE);
                psStaff.setDate(3, s.getDateRecrutement() != null ? Date.valueOf(s.getDateRecrutement()) : null);
                psStaff.setInt(4, s.getSoldeConge());

                Long cabinetId = s.getCabinetMedicale() != null ? s.getCabinetMedicale().getId() : null;
                if (cabinetId != null) {
                    psStaff.setLong(5, cabinetId);
                } else {
                    psStaff.setNull(5, Types.BIGINT);
                }

                psStaff.setLong(6, s.getId());

                if (psStaff.executeUpdate() == 0) throw new SQLException("Staff non trouvé pour mise à jour.");
            }

            // 3. UPDATE SECRETAIRE
            try (PreparedStatement psSecretaire = conn.prepareStatement(UPDATE_SECRETAIRE_SQL)) {
                psSecretaire.setString(1, s.getNumCNS());
                psSecretaire.setObject(2, s.getCommission(), Types.DOUBLE);
                psSecretaire.setLong(3, s.getId());

                if (psSecretaire.executeUpdate() == 0) throw new SQLException("Secrétaire non trouvée pour mise à jour.");
            }

            conn.commit();
            conn.setAutoCommit(true);

        } catch (SQLException e) {
            try (Connection conn = SessionFactory.getInstance().getConnection()) {
                conn.rollback();
            } catch (SQLException rollbackE) { /* Ignorer l'erreur de rollback */ }
            throw new RuntimeException("Erreur mise à jour secrétaire id=" + s.getId(), e);
        }
    }


    // ---------------------- DELETE Operations ----------------------

    @Override
    public void delete(Secretaire s) {
        if (s != null && s.getId() != null) {
            deleteById(s.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        // La suppression dans Utilisateur cascade (ON DELETE CASCADE) vers Staff et Secretaire.
        String sql = "DELETE FROM Utilisateur WHERE id = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur suppression secrétaire id=" + id, e);
        }
    }


    // ---------------------- RECHERCHE Operations ----------------------

    @Override
    public Optional<Secretaire> findByNumCNS(String numCNS) {
        // Utilisation du JOIN pour être sûr de récupérer l'entité Secretaire complète
        String sql = SELECT_SECRETAIRE_BASE_SQL + " WHERE sec.numCNS = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, numCNS);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapSecretaire(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche secrétaire par numCNS : " + numCNS, e);
        }
    }

    // NOUVEAU: findByEmail (très commun)
    public Optional<Secretaire> findByEmail(String email) {
        String sql = SELECT_SECRETAIRE_BASE_SQL + " WHERE u.email = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapSecretaire(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche secrétaire par email : " + email, e);
        }
    }

    public List<Secretaire> findByCabinetId(Long cabinetId) {
        // Utilisation du JOIN
        String sql = SELECT_SECRETAIRE_BASE_SQL + " WHERE s.cabinetMedicale_id = ?";
        List<Secretaire> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(RowMappers.mapSecretaire(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur find secrétaires du cabinet " + cabinetId, e);
        }
        return list;
    }
}