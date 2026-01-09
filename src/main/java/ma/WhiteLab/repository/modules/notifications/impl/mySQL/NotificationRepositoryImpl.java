package ma.WhiteLab.repository.modules.notifications.impl.mySQL;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ma.WhiteLab.entities.user.Notification;
import ma.WhiteLab.repository.common.RowMappers;
import ma.WhiteLab.repository.modules.notifications.api.NotificationRepository;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationRepositoryImpl implements NotificationRepository {

    private Connection connection; // Injectée par ApplicationContext

    @Override
    public List<Notification> findAll() {
        String sql = "SELECT * FROM notification ORDER BY date DESC, time DESC";
        System.out.println("[JDBC NOTIFICATION SQL] " + sql);

        List<Notification> out = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                out.add(RowMappers.mapNotification(rs));
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la récupération de toutes les notifications", e);
        }

        return out;
    }

    @Override
    public Notification findById(Long id) {
        String sql = "SELECT * FROM notification WHERE id = ?";
        System.out.println("[JDBC NOTIFICATION SQL] " + sql + " | id=" + id);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return RowMappers.mapNotification(rs);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche de la notification par ID : " + id, e);
        }

        return null;
    }

    @Override
    public void create(Notification n) {
        String sql = """
            INSERT INTO notification(titre, message, date, time, type, priorite, dateCreation, creePar)
            VALUES(?,?,?,?,?,?,?,?)
            """;
        System.out.println("[JDBC NOTIFICATION SQL] INSERT Notification | titre=" + n.getTitre());

        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, n.getTitre().name());
            ps.setString(2, n.getMessage());
            ps.setDate(3, n.getDate() != null ? Date.valueOf(n.getDate()) : null);
            ps.setTime(4, n.getTime() != null ? Time.valueOf(n.getTime()) : null);
            ps.setString(5, n.getType().name());
            ps.setString(6, n.getPriorite().name());
            ps.setTimestamp(7, Timestamp.valueOf(n.getDateCreation() != null ? n.getDateCreation() : LocalDateTime.now()));
            ps.setString(8, n.getCreePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    n.setId(keys.getLong(1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la création de la notification", e);
        }
    }

    @Override
    public void update(Notification n) {
        String sql = """
            UPDATE notification
            SET titre=?, message=?, date=?, time=?, type=?, priorite=?, dateMiseAJour=?, modifierPar=?
            WHERE id=?
            """;
        System.out.println("[JDBC NOTIFICATION SQL] UPDATE Notification | id=" + n.getId());

        try (PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, n.getTitre().name());
            ps.setString(2, n.getMessage());
            ps.setDate(3, n.getDate() != null ? Date.valueOf(n.getDate()) : null);
            ps.setTime(4, n.getTime() != null ? Time.valueOf(n.getTime()) : null);
            ps.setString(5, n.getType().name());
            ps.setString(6, n.getPriorite().name());
            ps.setTimestamp(7, Timestamp.valueOf(LocalDateTime.now()));
            ps.setString(8, n.getModifierPar());
            ps.setLong(9, n.getId());

            ps.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la mise à jour de la notification ID=" + n.getId(), e);
        }
    }

    @Override
    public void delete(Notification n) {
        if (n != null && n.getId() != null) {
            deleteById(n.getId());
        }
    }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM notification WHERE id = ?";
        System.out.println("[JDBC NOTIFICATION SQL] DELETE Notification | id=" + id);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la suppression de la notification ID=" + id, e);
        }
    }

    @Override
    public Optional<Notification> findByTitre(String titre) {
        String sql = "SELECT * FROM notification WHERE titre = ?";
        System.out.println("[JDBC NOTIFICATION SQL] " + sql + " | titre=" + titre);

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, titre);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(RowMappers.mapNotification(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par titre : " + titre, e);
        }

        return Optional.empty();
    }

    @Override
    public List<Notification> findByType(String type) {
        String sql = "SELECT * FROM notification WHERE type = ? ORDER BY date DESC, time DESC";
        System.out.println("[JDBC NOTIFICATION SQL] " + sql + " | type=" + type);

        List<Notification> out = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, type);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapNotification(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par type : " + type, e);
        }

        return out;
    }

    @Override
    public List<Notification> findByPriorite(String priorite) {
        String sql = "SELECT * FROM notification WHERE priorite = ? ORDER BY date DESC, time DESC";
        System.out.println("[JDBC NOTIFICATION SQL] " + sql + " | priorite=" + priorite);

        List<Notification> out = new ArrayList<>();

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, priorite);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    out.add(RowMappers.mapNotification(rs));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors de la recherche par priorité : " + priorite, e);
        }

        return out;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM notification";
        System.out.println("[JDBC NOTIFICATION SQL] " + sql);

        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            rs.next();
            return rs.getLong(1);

        } catch (SQLException e) {
            throw new RuntimeException("Erreur lors du comptage des notifications", e);
        }
    }
}