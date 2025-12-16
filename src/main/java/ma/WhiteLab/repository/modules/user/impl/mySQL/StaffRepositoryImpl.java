package ma.WhiteLab.repository.modules.user.impl.mySQL;

import ma.WhiteLab.conf.SessionFactory;
import ma.WhiteLab.entities.user.Staff;
import ma.WhiteLab.repository.common.RowMappers;
import ma.WhiteLab.repository.modules.user.api.StaffRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class StaffRepositoryImpl implements StaffRepository {

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

    // --- Requête SELECT avec JOIN pour récupérer toutes les données Staff ---
    private static final String SELECT_STAFF_BASE_SQL = """
        SELECT u.*, s.salaire, s.prime, s.dateRecrutement, s.soldeConge, 
               s.cabinetMedicale_id 
        FROM Utilisateur u
        INNER JOIN Staff s ON u.id = s.id
        """;


    // ---------------------- READ Operations ----------------------

    @Override
    public List<Staff> findAll() {
        // CORRIGÉ: Utilisation du JOIN pour récupérer tous les Staff (inclut Medecin/Secretaire si non filtré)
        // NOTE: Si vous voulez exclure les sous-classes, il faudrait une colonne 'type' ou une table 'StaffPur'.
        // Ici, on récupère tous les IDs qui existent dans Staff.
        String sql = SELECT_STAFF_BASE_SQL + " ORDER BY u.nom, u.prenom";
        List<Staff> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(RowMappers.mapStaff(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération du personnel", e);
        }
        return list;
    }

    @Override
    public Staff findById(Long id) {
        // CORRIGÉ: Utilisation du JOIN
        String sql = SELECT_STAFF_BASE_SQL + " WHERE u.id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapStaff(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Personnel non trouvé avec id = " + id, e);
        }
    }


    // ---------------------- CREATE Operation (Transaction) ----------------------

    @Override
    public void create(Staff s) {
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
                psStaff.setLong(1, s.getId()); // Utilise l'ID généré
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

            conn.commit();
            conn.setAutoCommit(true);

            if (s.getDateCreation() == null) {
                s.setDateCreation(now);
            }

        } catch (SQLException e) {
            try (Connection conn = SessionFactory.getInstance().getConnection()) {
                conn.rollback();
            } catch (SQLException rollbackE) { /* Ignorer l'erreur de rollback */ }
            throw new RuntimeException("Erreur création membre du personnel : " + s.getNom() + " " + s.getPrenom(), e);
        }
    }


    // ---------------------- UPDATE Operation (Transaction) ----------------------

    @Override
    public void update(Staff s) {
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

            conn.commit();
            conn.setAutoCommit(true);

        } catch (SQLException e) {
            try (Connection conn = SessionFactory.getInstance().getConnection()) {
                conn.rollback();
            } catch (SQLException rollbackE) { /* Ignorer l'erreur de rollback */ }
            throw new RuntimeException("Erreur mise à jour personnel id=" + s.getId(), e);
        }
    }


    // ---------------------- DELETE Operations ----------------------

    @Override
    public void delete(Staff s) {
        if (s != null && s.getId() != null) {
            deleteById(s.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        // La suppression dans Utilisateur (table mère) devrait cascader
        // vers Staff (table fille) grâce à ON DELETE CASCADE.

        String sql = "DELETE FROM Utilisateur WHERE id = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur suppression personnel id=" + id, e);
        }
    }


    // ---------------------- RECHERCHE Operations ----------------------

    @Override
    public List<Staff> findByCabinetId(Long cabinetId) {
        // CORRIGÉ: Utilisation du JOIN pour récupérer tous les champs
        String sql = SELECT_STAFF_BASE_SQL + " WHERE s.cabinetMedicale_id = ? ORDER BY u.nom";
        List<Staff> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(RowMappers.mapStaff(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche personnel par cabinet id=" + cabinetId, e);
        }
        return list;
    }

    // Le findByType original est maintenu pour la recherche par 'type',
    // mais il est peu utile si l'on suppose qu'il n'y a pas de colonne 'type'
    // dans Utilisateur et que cette table doit gérer toutes les sous-classes.
    // L'implémentation ci-dessus garantit l'intégrité des données dans Utilisateur et Staff.
    // Pour une recherche par type, un DAO spécifique (MedecinRepository, SecretaireRepository) est préférable.
    // Si la colonne 'type' existe dans Utilisateur, la logique ci-dessous fonctionnerait pour filtrer.

    // Maintenu sans correction majeure dans la requête, mais le mapping est basé sur le JOIN implicite.
    public List<Staff> findByType(String type) {
        // NOTE: Si la colonne 'type' est dans Utilisateur, la requête est simple.
        // Sinon, il faudrait des JOINs complexes.
        String sql = "SELECT u.*, s.* FROM Utilisateur u INNER JOIN Staff s ON u.id = s.id WHERE u.type = ? ORDER BY u.nom";
        List<Staff> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, type.toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(RowMappers.mapStaff(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche par type : " + type, e);
        }
        return list;
    }
}