package com.cafe;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderDAO {

    public int save(List<OrderItem> items, double total, String method, double paid) throws SQLException {
        try (Connection con = DBConnection.getConnection()) {
            con.setAutoCommit(false);
            try {
                int orderId;
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO orders (order_date, total_amount, payment_method, paid_amount, user_id) "
                                + "VALUES (?, ?, ?, ?, ?)",
                        Statement.RETURN_GENERATED_KEYS)) {
                    ps.setTimestamp(1, Timestamp.valueOf(LocalDateTime.now()));
                    ps.setDouble(2, total);
                    ps.setString(3, method);
                    ps.setDouble(4, paid);
                    if (Session.userId > 0) ps.setInt(5, Session.userId);
                    else ps.setNull(5, java.sql.Types.INTEGER);
                    ps.executeUpdate();
                    try (ResultSet keys = ps.getGeneratedKeys()) {
                        keys.next();
                        orderId = keys.getInt(1);
                    }
                }
                try (PreparedStatement ps = con.prepareStatement(
                        "INSERT INTO order_items (order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?)")) {
                    for (OrderItem item : items) {
                        ps.setInt(1, orderId);
                        ps.setInt(2, item.getProductId());
                        ps.setInt(3, item.getQuantity());
                        ps.setDouble(4, item.getUnitPrice());
                        ps.addBatch();
                    }
                    ps.executeBatch();
                }
                con.commit();
                return orderId;
            } catch (SQLException e) {
                con.rollback();
                throw e;
            }
        }
    }

    public List<Order> getAll() throws SQLException {
        List<Order> list = new ArrayList<>();
        String sql = "SELECT o.order_id, o.order_date, o.total_amount, o.payment_method, "
                + "COALESCE(o.paid_amount, o.total_amount), COALESCE(u.full_name, '-') "
                + "FROM orders o LEFT JOIN users u ON u.user_id = o.user_id ORDER BY o.order_id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Order(rs.getInt(1), rs.getTimestamp(2).toLocalDateTime(), rs.getDouble(3),
                        rs.getString(4), rs.getDouble(5), rs.getString(6)));
            }
        }
        return list;
    }

    public List<OrderItem> getItems(int orderId) throws SQLException {
        List<OrderItem> list = new ArrayList<>();
        String sql = "SELECT oi.product_id, p.name, oi.unit_price, oi.quantity "
                + "FROM order_items oi JOIN products p ON p.product_id = oi.product_id "
                + "WHERE oi.order_id = ?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new OrderItem(rs.getInt(1), rs.getString(2), rs.getDouble(3), rs.getInt(4)));
                }
            }
        }
        return list;
    }

    /** Өнөөдрийн борлуулалт: [0] = захиалгын тоо, [1] = нийт орлого. */
    public double[] getTodaySales() throws SQLException {
        String sql = "SELECT COUNT(*), COALESCE(SUM(total_amount), 0) FROM orders "
                + "WHERE order_date >= ? AND order_date < ?";
        LocalDateTime start = LocalDateTime.now().toLocalDate().atStartOfDay();
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(start));
            ps.setTimestamp(2, Timestamp.valueOf(start.plusDays(1)));
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return new double[] { rs.getInt(1), rs.getDouble(2) };
            }
        }
    }
}
