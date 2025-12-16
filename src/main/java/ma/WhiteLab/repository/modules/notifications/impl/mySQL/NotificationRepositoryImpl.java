package ma.WhiteLab.repository.modules.notifications.impl.mySQL;

import ma.WhiteLab.conf.SessionFactory;
import ma.WhiteLab.entities.user.Notification;
import ma.WhiteLab.repository.common.RowMappers;
import ma.WhiteLab.repository.modules.notifications.api.NotificationRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
//auteur : Aymane Akarbich

public class NotificationRepositoryImpl implements NotificationRepository {

    @Override
    public List<Notification> findAll() {
        String sql = "SELECT * FROM notification ORDER BY date DESC, time DESC";
        List<Notification> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) out.add(RowMappers.mapNotification(rs));
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public Notification findById(Long id) {
        String sql = "SELECT * FROM notification WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return RowMappers.mapNotification(rs);
                return null;
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void create(Notification n) {
        String sql = """
            INSERT INTO notification(titre, message, date, time, type, priorite, dateCreation, creePar)
            VALUES(?,?,?,?,?,?,?,?)
            """;
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, n.getTitre().name());
            ps.setString(2, n.getMessage());
            ps.setDate(3, n.getDate() != null ? Date.valueOf(n.getDate()) : null);
            ps.setTime(4, n.getTime() != null ? Time.valueOf(n.getTime()) : null);
            ps.setString(5, n.getType().name());
            ps.setString(6, n.getPriorite().name());
            ps.setTimestamp(7, Timestamp.valueOf(n.getDateCreation() != null ? n.getDateCreation() : java.time.LocalDateTime.now()));
            ps.setString(8, n.getCreePar());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) n.setId(keys.getLong(1));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void update(Notification n) {
        String sql = """
            UPDATE notification
            SET titre=?, message=?, date=?, time=?, type=?, priorite=?, dateMiseAJour=?, modifierPar=?
            WHERE id=?
            """;
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, n.getTitre().name());
            ps.setString(2, n.getMessage());
            ps.setDate(3, n.getDate() != null ? Date.valueOf(n.getDate()) : null);
            ps.setTime(4, n.getTime() != null ? Time.valueOf(n.getTime()) : null);
            ps.setString(5, n.getType().name());
            ps.setString(6, n.getPriorite().name());
            ps.setTimestamp(7, Timestamp.valueOf(java.time.LocalDateTime.now()));
            ps.setString(8, n.getModifierPar());
            ps.setLong(9, n.getId());

            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public void delete(Notification n) { if (n != null) deleteById(n.getId()); }

    @Override
    public void deleteById(Long id) {
        String sql = "DELETE FROM notification WHERE id = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public Optional<Notification> findByTitre(String titre) {
        String sql = "SELECT * FROM notification WHERE titre = ?";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, titre);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(RowMappers.mapNotification(rs));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

    @Override
    public List<Notification> findByType(String type) {
        String sql = "SELECT * FROM notification WHERE type = ? ORDER BY date DESC, time DESC";
        List<Notification> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, type);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapNotification(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public List<Notification> findByPriorite(String priorite) {
        String sql = "SELECT * FROM notification WHERE priorite = ? ORDER BY date DESC, time DESC";
        List<Notification> out = new ArrayList<>();
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, priorite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) out.add(RowMappers.mapNotification(rs));
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
        return out;
    }

    @Override
    public long count() {
        String sql = "SELECT COUNT(*) FROM notification";
        try (Connection c = SessionFactory.getInstance().getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            return rs.getLong(1);
        } catch (SQLException e) { throw new RuntimeException(e); }
    }

}
