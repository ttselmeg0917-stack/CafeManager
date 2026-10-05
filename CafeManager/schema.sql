-- Программ эхлэхдээ эдгээрийг автоматаар үүсгэдэг тул заавал ажиллуулах шаардлагагүй.
CREATE DATABASE IF NOT EXISTS cafe_db CHARACTER SET utf8mb4;
USE cafe_db;

CREATE TABLE IF NOT EXISTS products (
    product_id INT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100)  NOT NULL,
    category   VARCHAR(50)   NOT NULL,
    price      DECIMAL(10,2) NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
    user_id       INT AUTO_INCREMENT PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    password_hash VARCHAR(64)  NOT NULL,   -- SHA-256
    full_name     VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS orders (
    order_id       INT AUTO_INCREMENT PRIMARY KEY,
    order_date     DATETIME      NOT NULL,
    total_amount   DECIMAL(10,2) NOT NULL,
    payment_method VARCHAR(20)   NOT NULL DEFAULT 'Бэлэн',
    paid_amount    DECIMAL(10,2) NULL,
    user_id        INT NULL,
    FOREIGN KEY (user_id) REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS order_items (
    item_id    INT AUTO_INCREMENT PRIMARY KEY,
    order_id   INT NOT NULL,
    product_id INT NOT NULL,
    quantity   INT NOT NULL,
    unit_price DECIMAL(10,2) NOT NULL,
    FOREIGN KEY (order_id)   REFERENCES orders(order_id),
    FOREIGN KEY (product_id) REFERENCES products(product_id)
);

INSERT INTO products (name, category, price) VALUES
('Americano', 'Кофе', 6000), ('Latte', 'Кофе', 7500),
('Ногоон цай', 'Цай', 5000), ('Cheesecake', 'Амттан', 9000);

-- Анхны хэрэглэгч: admin / 1234
INSERT INTO users (username, password_hash, full_name)
VALUES ('admin', SHA2('1234', 256), 'Админ');
