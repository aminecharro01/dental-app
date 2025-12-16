package ma.WhiteLab.repository.modules.user.impl.mySQL;

import ma.WhiteLab.conf.SessionFactory;
import ma.WhiteLab.entities.enums.*;
import ma.WhiteLab.entities.user.*;
import ma.WhiteLab.repository.common.RowMappers;
import ma.WhiteLab.repository.modules.user.api.UtilisateurRepository;

import java.sql.*;
import java.sql.Date;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Repository JDBC générique pour la table Utilisateur uniquement.
 * Les sous-classes (Medecin, Staff, Admin) doivent avoir leur propre DAO avec JOIN.
 *
 * Auteur : Aymane Akarbich
 */
public abstract class UtilisateurRepositoryImpl<T extends Utilisateur>
        implements UtilisateurRepository<T> {

    private final Class<T> clazz;

    protected UtilisateurRepositoryImpl(Class<T> clazz) {
        this.clazz = clazz;
    }

    // =================== Mapping ===================
    protected T mapRow(ResultSet rs) throws SQLException {
        try {
            // On instancie la classe concrète passée dans le repository
            T utilisateur = clazz.getDeclaredConstructor().newInstance();
            return RowMappers.mapUtilisateur(rs, utilisateur);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Erreur lors du mapping de " + clazz.getSimpleName(), e
            );
        }
    }

    // ========================== CRUD UTILISATEUR ==========================

    @Override
    public List<T> findAll() {
        String sql = "SELECT * FROM Utilisateur ORDER BY nom, prenom";
        List<T> result = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                result.add(mapRow(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findAll Utilisateur", e);
        }
        return result;
    }

    @Override
    public T findById(Long id) {
        String sql = "SELECT * FROM Utilisateur WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapRow(rs) : null;
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findById Utilisateur", e);
        }
    }

    @Override
    public void create(T u) {
        String sql = """
            INSERT INTO Utilisateur
            (nom, prenom, email, adresse, cin, telephone, dateNaissance, sexe,
             lastLoginDate, motDePasse, dateCreation, creePar, dateMiseAJour, modifierPar)
            VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?)
        """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getAdresse());
            ps.setString(5, u.getCin());
            ps.setString(6, u.getTelephone());
            ps.setDate(7, u.getDateNaissance() != null ? Date.valueOf(u.getDateNaissance()) : null);
            ps.setString(8, u.getSexe() != null ? u.getSexe().name() : null);
            ps.setTimestamp(9, u.getLastLoginDate() != null ? Timestamp.valueOf(u.getLastLoginDate()) : null);
            ps.setString(10, u.getMotDePasse());

            ps.setTimestamp(11, Timestamp.valueOf(
                    u.getDateCreation() != null ? u.getDateCreation() : LocalDateTime.now()
            ));
            ps.setString(12, u.getCreePar());

            ps.setTimestamp(13, null);
            ps.setString(14, null);

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    u.setId(keys.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur create Utilisateur", e);
        }
    }

    @Override
    public void update(T u) {
        String sql = """
            UPDATE Utilisateur SET
                nom=?, prenom=?, email=?, adresse=?, cin=?, telephone=?,
                dateNaissance=?, sexe=?, lastLoginDate=?, motDePasse=?,
                dateMiseAJour=?, modifierPar=?
            WHERE id=?
        """;

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, u.getNom());
            ps.setString(2, u.getPrenom());
            ps.setString(3, u.getEmail());
            ps.setString(4, u.getAdresse());
            ps.setString(5, u.getCin());
            ps.setString(6, u.getTelephone());
            ps.setDate(7, u.getDateNaissance() != null ? Date.valueOf(u.getDateNaissance()) : null);
            ps.setString(8, u.getSexe() != null ? u.getSexe().name() : null);
            ps.setTimestamp(9, u.getLastLoginDate() != null ? Timestamp.valueOf(u.getLastLoginDate()) : null);
            ps.setString(10, u.getMotDePasse());

            ps.setTimestamp(11, Timestamp.valueOf(
                    u.getDateMiseAJour() != null ? u.getDateMiseAJour() : LocalDateTime.now()
            ));
            ps.setString(12, u.getModifierPar());
            ps.setLong(13, u.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur update Utilisateur", e);
        }
    }

    @Override
    public void deleteById(Long id) {
        deleteAllRolesForUtilisateur(id);
        deleteAllNotificationsForUtilisateur(id);

        String sql = "DELETE FROM Utilisateur WHERE id = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur delete Utilisateur", e);
        }
    }

    @Override
    public void delete(T u) {
        if (u != null) deleteById(u.getId());
    }

    // ========================== RECHERCHE ==========================

    @Override
    public Optional<T> findByEmail(String email) {
        String sql = "SELECT * FROM Utilisateur WHERE email = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, email);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByEmail", e);
        }
    }

    @Override
    public Optional<T> findByTelephone(String telephone) {
        String sql = "SELECT * FROM Utilisateur WHERE telephone = ?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, telephone);

            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(mapRow(rs)) : Optional.empty();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur findByTelephone", e);
        }
    }

    @Override
    public boolean existsByEmail(String email) {
        return exists("SELECT 1 FROM Utilisateur WHERE email = ?", email);
    }

    @Override
    public boolean existsByTelephone(String telephone) {
        return exists("SELECT 1 FROM Utilisateur WHERE telephone = ?", telephone);
    }

    private boolean exists(String sql, String value) {
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, value);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur exists()", e);
        }
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM Utilisateur";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getLong(1);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur count Utilisateur", e);
        }
    }

    // ========================== ROLES ==========================

    @Override
    public void addRoleToUtilisateur(Long utilisateurId, Long roleId) {
        String sql = "INSERT INTO utilisateur_role(utilisateur_id, role_id) VALUES (?,?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, utilisateurId);
            ps.setLong(2, roleId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur addRoleToUtilisateur", e);
        }
    }

    @Override
    public void removeRoleFromUtilisateur(Long utilisateurId, Long roleId) {
        String sql = "DELETE FROM utilisateur_role WHERE utilisateur_id=? AND role_id=?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, utilisateurId);
            ps.setLong(2, roleId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur removeRoleFromUtilisateur", e);
        }
    }

    @Override
    public List<Role> getRolesOfUtilisateur(Long utilisateurId) {
        String sql = """
            SELECT r.id, r.libelle, r.privileges
            FROM Role r
            JOIN utilisateur_role ur ON r.id = ur.role_id
            WHERE ur.utilisateur_id = ?
        """;

        List<Role> roles = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, utilisateurId);
            ResultSet rs = ps.executeQuery();

            while (rs.next()) {
                roles.add(Role.builder()
                        .id(rs.getLong("id"))
                        .libelle(RoleR.valueOf(rs.getString("libelle")))
                        .privileges(Arrays.asList(rs.getString("privileges").split(",")))
                        .build());
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur getRolesOfUtilisateur", e);
        }

        return roles;
    }

    // ========================== NOTIFICATIONS ==========================

    @Override
    public void addNotificationToUtilisateur(Long utilisateurId, Long notificationId) {
        String sql = "INSERT INTO utilisateur_notification(utilisateur_id, notification_id) VALUES (?,?)";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, utilisateurId);
            ps.setLong(2, notificationId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur addNotificationToUtilisateur", e);
        }
    }

    @Override
    public void removeNotificationFromUtilisateur(Long utilisateurId, Long notificationId) {
        String sql = "DELETE FROM utilisateur_notification WHERE utilisateur_id=? AND notification_id=?";

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, utilisateurId);
            ps.setLong(2, notificationId);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur removeNotificationFromUtilisateur", e);
        }
    }

    @Override
    public List<Notification> getNotificationsOfUtilisateur(Long utilisateurId) {
        String sql = """
        SELECT n.id, n.titre, n.message, n.date, n.time, n.type, n.priorite
        FROM Notification n
        JOIN Utilisateur_Notification un ON n.id = un.notification_id
        WHERE un.utilisateur_id = ?
    """;

        List<Notification> notifications = new ArrayList<>();

        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, utilisateurId);
            try (ResultSet rs = ps.executeQuery()) {

                while (rs.next()) {
                    Date sqlDate = rs.getDate("date");
                    Time sqlTime = rs.getTime("time");

                    Notification notification = Notification.builder()
                            .id(rs.getLong("id"))
                            .titre(TitreNotification.valueOf(rs.getString("titre")))
                            .message(rs.getString("message"))
                            .date(sqlDate != null ? sqlDate.toLocalDate() : null)
                            .time(sqlTime != null ? sqlTime.toLocalTime() : null)
                            .type(TypeNotification.valueOf(rs.getString("type")))
                            .priorite(PrioriteNotification.valueOf(rs.getString("priorite")))
                            .build();

                    notifications.add(notification);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur getNotificationsOfUtilisateur", e);
        }

        return notifications;
    }


    @Override
    public void deleteAllRolesForUtilisateur(Long utilisateurId) {
        executeDelete("DELETE FROM utilisateur_role WHERE utilisateur_id=?", utilisateurId);
    }

    @Override
    public void deleteAllNotificationsForUtilisateur(Long utilisateurId) {
        executeDelete("DELETE FROM utilisateur_notification WHERE utilisateur_id=?", utilisateurId);
    }

    private void executeDelete(String sql, Long id) {
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setLong(1, id);
            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur delete cascade", e);
        }
    }
}
