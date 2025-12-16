package ma.WhiteLab.repository.modules.user.impl.mySQL;

import ma.WhiteLab.conf.SessionFactory;
import ma.WhiteLab.entities.user.Medecin;
import ma.WhiteLab.repository.common.RowMappers;
import ma.WhiteLab.repository.modules.user.api.MedecinRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MedecinRepositoryImpl implements MedecinRepository {

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

    private static final String INSERT_MEDECIN_SQL = """
        INSERT INTO Medecin (id, specialite) VALUES (?, ?)
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

    private static final String UPDATE_MEDECIN_SQL = """
        UPDATE Medecin SET specialite = ? WHERE id = ?
        """;

    // --- Requête SELECT avec JOIN pour récupérer toutes les données ---
    private static final String SELECT_MEDECIN_BASE_SQL = """
        SELECT u.*, s.salaire, s.prime, s.dateRecrutement, s.soldeConge, 
               s.cabinetMedicale_id, m.specialite 
        FROM Utilisateur u
        INNER JOIN Staff s ON u.id = s.id
        INNER JOIN Medecin m ON u.id = m.id
        """;


    // ---------------------- READ Operations ----------------------

    @Override
    public List<Medecin> findAll() {
        // CORRIGÉ: Utilisation du JOIN
        String sql = SELECT_MEDECIN_BASE_SQL + " ORDER BY u.nom, u.prenom";
        List<Medecin> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(RowMappers.mapMedecin(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des médecins", e);
        }
        return list;
    }

    @Override
    public Medecin findById(Long id) {
        // CORRIGÉ: Utilisation du JOIN
        String sql = SELECT_MEDECIN_BASE_SQL + " WHERE u.id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? RowMappers.mapMedecin(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Médecin non trouvé avec id = " + id, e);
        }
    }


    // ---------------------- CREATE Operation (Transaction) ----------------------

    @Override
    public void create(Medecin m) {
        LocalDateTime now = LocalDateTime.now();

        // Utilisation de la transaction pour garantir que les 3 INSERTs réussissent
        try (Connection conn = SessionFactory.getInstance().getConnection()) {

            conn.setAutoCommit(false);

            // 1. INSERTION DANS LA TABLE UTILISATEUR
            try (PreparedStatement psUser = conn.prepareStatement(INSERT_UTILISATEUR_SQL, Statement.RETURN_GENERATED_KEYS)) {
                psUser.setString(1, m.getNom());
                psUser.setString(2, m.getPrenom());
                psUser.setString(3, m.getEmail());
                psUser.setString(4, m.getAdresse());
                psUser.setString(5, m.getCin());
                psUser.setString(6, m.getTelephone());
                psUser.setDate(7, m.getDateNaissance() != null ? Date.valueOf(m.getDateNaissance()) : null);
                psUser.setString(8, m.getSexe() != null ? m.getSexe().name() : null);
                psUser.setString(9, m.getMotDePasse());
                psUser.setTimestamp(10, Timestamp.valueOf(m.getDateCreation() != null ? m.getDateCreation() : now));
                psUser.setString(11, m.getCreePar());

                psUser.executeUpdate();

                try (ResultSet keys = psUser.getGeneratedKeys()) {
                    if (keys.next()) m.setId(keys.getLong(1));
                }
            }

            if (m.getId() == null) throw new SQLException("ID d'utilisateur non généré. Échec de la création.");

            // 2. INSERTION DANS LA TABLE STAFF
            try (PreparedStatement psStaff = conn.prepareStatement(INSERT_STAFF_SQL)) {
                psStaff.setLong(1, m.getId()); // Utilise l'ID généré
                psStaff.setObject(2, m.getSalaire(), Types.DOUBLE);
                psStaff.setObject(3, m.getPrime(), Types.DOUBLE);
                psStaff.setDate(4, m.getDateRecrutement() != null ? Date.valueOf(m.getDateRecrutement()) : null);
                psStaff.setInt(5, m.getSoldeConge());

                Long cabinetId = m.getCabinetMedicale() != null ? m.getCabinetMedicale().getId() : null;
                if (cabinetId != null) {
                    psStaff.setLong(6, cabinetId);
                } else {
                    psStaff.setNull(6, Types.BIGINT);
                }

                psStaff.executeUpdate();
            }

            // 3. INSERTION DANS LA TABLE MEDECIN
            try (PreparedStatement psMedecin = conn.prepareStatement(INSERT_MEDECIN_SQL)) {
                psMedecin.setLong(1, m.getId()); // Utilise le même ID
                psMedecin.setString(2, m.getSpecialite());
                psMedecin.executeUpdate();
            }

            conn.commit(); // Validation de la transaction
            conn.setAutoCommit(true);

            if (m.getDateCreation() == null) {
                m.setDateCreation(now);
            }

        } catch (SQLException e) {
            // Rollback en cas d'erreur
            try (Connection conn = SessionFactory.getInstance().getConnection()) {
                conn.rollback();
            } catch (SQLException rollbackE) {
                // Ignorer l'erreur de rollback
            }
            throw new RuntimeException("Erreur lors de la création du médecin : " + m.getNom() + " " + m.getPrenom(), e);
        }
    }


    // ---------------------- UPDATE Operation (Transaction) ----------------------

    @Override
    public void update(Medecin m) {
        // Utilisation de la transaction pour garantir que les 3 UPDATEs réussissent
        try (Connection conn = SessionFactory.getInstance().getConnection()) {

            conn.setAutoCommit(false);

            // 1. UPDATE UTILISATEUR
            try (PreparedStatement psUser = conn.prepareStatement(UPDATE_UTILISATEUR_SQL)) {
                psUser.setString(1, m.getNom());
                psUser.setString(2, m.getPrenom());
                psUser.setString(3, m.getEmail());
                psUser.setString(4, m.getAdresse());
                psUser.setString(5, m.getCin());
                psUser.setString(6, m.getTelephone());
                psUser.setDate(7, m.getDateNaissance() != null ? Date.valueOf(m.getDateNaissance()) : null);
                psUser.setString(8, m.getSexe() != null ? m.getSexe().name() : null);
                psUser.setString(9, m.getMotDePasse());
                psUser.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
                psUser.setString(11, m.getModifierPar());
                psUser.setLong(12, m.getId());

                if (psUser.executeUpdate() == 0) throw new SQLException("Utilisateur non trouvé pour mise à jour.");
            }

            // 2. UPDATE STAFF
            try (PreparedStatement psStaff = conn.prepareStatement(UPDATE_STAFF_SQL)) {
                psStaff.setObject(1, m.getSalaire(), Types.DOUBLE);
                psStaff.setObject(2, m.getPrime(), Types.DOUBLE);
                psStaff.setDate(3, m.getDateRecrutement() != null ? Date.valueOf(m.getDateRecrutement()) : null);
                psStaff.setInt(4, m.getSoldeConge());

                Long cabinetId = m.getCabinetMedicale() != null ? m.getCabinetMedicale().getId() : null;
                if (cabinetId != null) {
                    psStaff.setLong(5, cabinetId);
                } else {
                    psStaff.setNull(5, Types.BIGINT);
                }

                psStaff.setLong(6, m.getId());

                if (psStaff.executeUpdate() == 0) throw new SQLException("Staff non trouvé pour mise à jour.");
            }

            // 3. UPDATE MEDECIN
            try (PreparedStatement psMedecin = conn.prepareStatement(UPDATE_MEDECIN_SQL)) {
                psMedecin.setString(1, m.getSpecialite());
                psMedecin.setLong(2, m.getId());

                if (psMedecin.executeUpdate() == 0) throw new SQLException("Medecin non trouvé pour mise à jour.");
            }

            conn.commit();
            conn.setAutoCommit(true);

        } catch (SQLException e) {
            try (Connection conn = SessionFactory.getInstance().getConnection()) {
                conn.rollback();
            } catch (SQLException rollbackE) { }
            throw new RuntimeException("Erreur mise à jour médecin id=" + m.getId(), e);
        }
    }


    // ---------------------- DELETE Operations ----------------------

    @Override
    public void delete(Medecin m) {
        if (m != null && m.getId() != null) {
            deleteById(m.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        // La suppression de l'enregistrement dans Utilisateur (table mère) devrait
        // cascader la suppression dans Staff et Medecin (tables filles) grâce à
        // FOREIGN KEY(id) REFERENCES Utilisateur(id) ON DELETE CASCADE.

        String sql = "DELETE FROM Utilisateur WHERE id = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur suppression médecin id=" + id, e);
        }
    }


    // ---------------------- RECHERCHE Operations ----------------------

    @Override
    public Optional<Medecin> findByEmail(String email) {
        String sql = SELECT_MEDECIN_BASE_SQL + " WHERE u.email = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapMedecin(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche médecin par email : " + email, e);
        }
    }

    @Override
    public List<Medecin> findBySpecialite(String specialite) {
        // CORRIGÉ: Utilisation du champ 'specialite' qui est maintenant dans le SELECT
        String sql = SELECT_MEDECIN_BASE_SQL + " WHERE m.specialite LIKE ? ORDER BY u.nom";
        List<Medecin> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            // Utilisation de LIKE au lieu de ILIKE si la base de données ne supporte pas ILIKE
            ps.setString(1, "%" + specialite + "%");
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(RowMappers.mapMedecin(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche médecins par spécialité : " + specialite, e);
        }
        return list;
    }

    public List<Medecin> findByCabinetId(Long cabinetId) {
        // CORRIGÉ: Utilisation du champ 'cabinetMedicale_id' qui est maintenant dans le SELECT
        String sql = SELECT_MEDECIN_BASE_SQL + " WHERE s.cabinetMedicale_id = ?";
        List<Medecin> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, cabinetId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(RowMappers.mapMedecin(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur find médecins du cabinet " + cabinetId, e);
        }
        return list;
    }
}