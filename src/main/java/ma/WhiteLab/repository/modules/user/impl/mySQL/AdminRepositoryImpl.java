package ma.WhiteLab.repository.modules.user.impl.mySQL;

import ma.WhiteLab.conf.SessionFactory;
import ma.WhiteLab.entities.user.Admin;
import ma.WhiteLab.repository.common.RowMappers;
import ma.WhiteLab.repository.modules.user.api.AdminRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class AdminRepositoryImpl implements AdminRepository {

    // --- CORRIGÉ: INSERT_SQL ne contient que les champs de la table Utilisateur ---
    private static final String INSERT_UTILISATEUR_SQL = """
        INSERT INTO Utilisateur (
            nom, prenom, email, adresse, cin, telephone, dateNaissance, sexe,
            motDePasse, dateCreation, creePar
        ) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)
        """;

    // --- NOUVEAU: INSERT_SQL pour la table fille Admin ---
    private static final String INSERT_ADMIN_SQL = "INSERT INTO Admin (id) VALUES (?)";


    // --- CORRIGÉ: Suppression de "AND type = 'ADMIN'" qui n'existe pas ---
    private static final String UPDATE_UTILISATEUR_SQL = """
        UPDATE Utilisateur SET
            nom = ?, prenom = ?, email = ?, adresse = ?, cin = ?,
            telephone = ?, dateNaissance = ?, sexe = ?,
            motDePasse = ?, dateMiseAJour = ?, modifierPar = ?
        WHERE id = ?
        """;

    private static final String SELECT_ADMIN_BASE_SQL = """
        SELECT u.* FROM Utilisateur u
        INNER JOIN Admin a ON u.id = a.id
        """;

    @Override
    public List<Admin> findAll() {
        // CORRIGÉ: Utilisation du JOIN pour ne sélectionner que les Admins
        String sql = SELECT_ADMIN_BASE_SQL + " ORDER BY nom, prenom";
        List<Admin> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                list.add(RowMappers.mapAdmin(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération des administrateurs", e);
        }
        return list;
    }

    @Override
    public Admin findById(Long id) {
        // CORRIGÉ: Utilisation du JOIN
        String sql = SELECT_ADMIN_BASE_SQL + " WHERE u.id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                // mapAdmin utilise mapUtilisateur et le constructeur Admin est vide, c'est OK.
                return rs.next() ? RowMappers.mapAdmin(rs) : null;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Administrateur non trouvé avec id = " + id, e);
        }
    }

    @Override
    public void create(Admin a) {
        LocalDateTime now = LocalDateTime.now();
        // Utilisation des transactions pour garantir que les deux inserts réussissent
        try (Connection conn = SessionFactory.getInstance().getConnection()) {

            conn.setAutoCommit(false);

            // 1. INSERTION DANS LA TABLE UTILISATEUR
            try (PreparedStatement psUser = conn.prepareStatement(INSERT_UTILISATEUR_SQL, Statement.RETURN_GENERATED_KEYS)) {

                psUser.setString(1, a.getNom());
                psUser.setString(2, a.getPrenom());
                psUser.setString(3, a.getEmail());
                psUser.setString(4, a.getAdresse());
                psUser.setString(5, a.getCin());
                psUser.setString(6, a.getTelephone());

                // dateNaissance
                psUser.setDate(7, a.getDateNaissance() != null ? Date.valueOf(a.getDateNaissance()) : null);

                // sexe
                psUser.setString(8, a.getSexe() != null ? a.getSexe().name() : null);

                // mot de passe
                psUser.setString(9, a.getMotDePasse());

                // dateCreation + creePar
                psUser.setTimestamp(10, Timestamp.valueOf(a.getDateCreation() != null ? a.getDateCreation() : now));
                psUser.setString(11, a.getCreePar());

                psUser.executeUpdate();

                try (ResultSet keys = psUser.getGeneratedKeys()) {
                    if (keys.next()) {
                        a.setId(keys.getLong(1)); // Récupération de l'ID généré
                    }
                }
            }

            // 2. INSERTION DANS LA TABLE ADMIN
            try (PreparedStatement psAdmin = conn.prepareStatement(INSERT_ADMIN_SQL)) {
                if (a.getId() == null) {
                    throw new SQLException("ID d'utilisateur non généré. Échec de la création.");
                }
                psAdmin.setLong(1, a.getId());
                psAdmin.executeUpdate();
            }

            conn.commit(); // Validation de la transaction
            conn.setAutoCommit(true);

            if (a.getDateCreation() == null) {
                a.setDateCreation(now);
            }

        } catch (SQLException e) {
            // Rollback en cas d'erreur
            try (Connection conn = SessionFactory.getInstance().getConnection()) {
                conn.rollback();
            } catch (SQLException rollbackE) {
                // Ignorer l'erreur de rollback
            }
            throw new RuntimeException("Erreur création administrateur : " + a.getNom() + " " + a.getPrenom(), e);
        }
    }

    @Override
    public void update(Admin a) {
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(UPDATE_UTILISATEUR_SQL)) {

            // Les champs de la table Admin n'ont pas besoin d'être mis à jour (seulement l'ID)
            ps.setString(1, a.getNom());
            ps.setString(2, a.getPrenom());
            ps.setString(3, a.getEmail());
            ps.setString(4, a.getAdresse());
            ps.setString(5, a.getCin());
            ps.setString(6, a.getTelephone());

            ps.setDate(7, a.getDateNaissance() != null ? Date.valueOf(a.getDateNaissance()) : null);
            ps.setString(8, a.getSexe() != null ? a.getSexe().name() : null);
            ps.setString(9, a.getMotDePasse());

            ps.setTimestamp(10, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(11, a.getModifierPar());
            ps.setLong(12, a.getId());

            int rows = ps.executeUpdate();
            if (rows == 0) {
                // Si l'UPDATE échoue, cela pourrait signifier que l'utilisateur n'existe pas.
                throw new RuntimeException("Administrateur (Utilisateur) non trouvé pour mise à jour (id=" + a.getId() + ")");
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur mise à jour administrateur id=" + a.getId(), e);
        }
    }

    @Override
    public void delete(Admin a) {
        if (a != null && a.getId() != null) {
            deleteById(a.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        // CORRIGÉ: La suppression de l'ID dans la table UTILISATEUR entraîne automatiquement
        // la suppression de l'enregistrement dans la table ADMIN grâce à ON DELETE CASCADE.
        // Les liaisons Utilisateur_Role/Notification doivent être nettoyées AVANT
        // ou par cascade. Pour la sécurité, on supprime de Utilisateur.

        // Nettoyage des liaisons (si elles ne sont pas en CASCADE)
        // (En supposant que vous appelez une méthode UtilitaireRepository pour les liaisons)
        // new UtilisateurRepositoryImpl<>(Utilisateur.class).deleteAllRolesForUtilisateur(id);
        // new UtilisateurRepositoryImpl<>(Utilisateur.class).deleteAllNotificationsForUtilisateur(id);

        String sql = "DELETE FROM Utilisateur WHERE id = ?";
        try (Connection conn = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {

            ps.setLong(1, id);
            int rows = ps.executeUpdate();
            if (rows == 0) {
                System.out.println("Aucun Utilisateur/Admin supprimé avec l'id : " + id);
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur suppression administrateur id=" + id, e);
        }
    }

    @Override
    public Optional<Admin> findByEmail(String email) {
        // CORRIGÉ: Utilisation du JOIN
        String sql = SELECT_ADMIN_BASE_SQL + " WHERE u.email = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(RowMappers.mapAdmin(rs)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche admin par email : " + email, e);
        }
    }


    // Bonus utile : chercher par nom/prénom
    public List<Admin> findByNomOrPrenom(String search) {
        // CORRIGÉ: Utilisation du JOIN et de la syntaxe SQL standard (LIKE)
        String sql = SELECT_ADMIN_BASE_SQL + " WHERE u.nom LIKE ? OR u.prenom LIKE ? ORDER BY u.nom";
        List<Admin> list = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            String pattern = "%" + search + "%";
            ps.setString(1, pattern);
            ps.setString(2, pattern);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(RowMappers.mapAdmin(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Erreur recherche admin par nom/prénom", e);
        }
        return list;
    }
}