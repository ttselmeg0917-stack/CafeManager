package com.cafe;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/** MySQL холболт. Нууц үг, хэрэглэгчийн нэрээ энд тохируулна. */
public class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/cafe_db"
            + "?createDatabaseIfNotExist=true&useUnicode=true&characterEncoding=UTF-8"
            + "&serverTimezone=Asia/Ulaanbaatar&allowPublicKeyRetrieval=true&useSSL=false";
    private static final String USER = "root";
    private static final String PASSWORD = "0917";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }

    /** Хүснэгтүүд байхгүй бол үүсгэж, жишээ бүтээгдэхүүн нэмнэ. */
    public static void init() throws SQLException {
        try (Connection con = getConnection(); Statement st = con.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS products ("
                    + "product_id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "name VARCHAR(100) NOT NULL,"
                    + "category VARCHAR(50) NOT NULL,"
                    + "price DECIMAL(10,2) NOT NULL) CHARACTER SET utf8mb4");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS orders ("
                    + "order_id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "order_date DATETIME NOT NULL,"
                    + "total_amount DECIMAL(10,2) NOT NULL)");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS order_items ("
                    + "item_id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "order_id INT NOT NULL,"
                    + "product_id INT NOT NULL,"
                    + "quantity INT NOT NULL,"
                    + "unit_price DECIMAL(10,2) NOT NULL,"
                    + "FOREIGN KEY (order_id) REFERENCES orders(order_id),"
                    + "FOREIGN KEY (product_id) REFERENCES products(product_id))");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS users ("
                    + "user_id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "username VARCHAR(50) NOT NULL UNIQUE,"
                    + "password_hash VARCHAR(64) NOT NULL,"
                    + "full_name VARCHAR(100) NOT NULL) CHARACTER SET utf8mb4");

            // Өмнөх хувилбарын orders хүснэгтэд шинэ багануудыг нэмнэ
            if (!hasColumn(con, "orders", "payment_method")) {
                st.executeUpdate("ALTER TABLE orders ADD COLUMN payment_method VARCHAR(20) "
                        + "CHARACTER SET utf8mb4 NOT NULL DEFAULT 'Бэлэн'");
            }
            if (!hasColumn(con, "orders", "paid_amount")) {
                st.executeUpdate("ALTER TABLE orders ADD COLUMN paid_amount DECIMAL(10,2) NULL");
            }
            if (!hasColumn(con, "orders", "user_id")) {
                st.executeUpdate("ALTER TABLE orders ADD COLUMN user_id INT NULL, "
                        + "ADD FOREIGN KEY (user_id) REFERENCES users(user_id)");
            }

            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM users")) {
                rs.next();
                if (rs.getInt(1) == 0) {
                    // Анхны хэрэглэгч: admin / 1234
                    st.executeUpdate("INSERT INTO users (username, password_hash, full_name) VALUES "
                            + "('admin', '" + UserDAO.hash("1234") + "', 'Админ')");
                }
            }
            try (ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM products")) {
                rs.next();
                if (rs.getInt(1) == 0) {
                    st.executeUpdate("INSERT INTO products (name, category, price) VALUES "
                            + "('Americano','Кофе',6000),('Latte','Кофе',7500),"
                            + "('Ногоон цай','Цай',5000),('Cheesecake','Амттан',9000)");
                }
            }
        }
    }

    private static boolean hasColumn(Connection con, String table, String column) throws SQLException {
        try (ResultSet rs = con.getMetaData().getColumns(con.getCatalog(), null, table, column)) {
            return rs.next();
        }
    }
}
