-- Database initialization script for Order Management System
-- This script will be executed automatically when PostgreSQL container starts

-- Create tables first (matching the JPA entity definitions)
CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    email VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS manufacturer (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS product (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(255),
    price DECIMAL(19,2),
    manufacturer_id BIGINT REFERENCES manufacturer(id)
);

CREATE TABLE IF NOT EXISTS orders (
    id BIGSERIAL PRIMARY KEY,
    order_date TIMESTAMP WITH TIME ZONE,
    total_amount DECIMAL(19,2),
    user_id BIGINT REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS order_items (
    id BIGSERIAL PRIMARY KEY,
    quantity INTEGER,
    unit_price DECIMAL(19,2),
    order_id BIGINT REFERENCES orders(id),
    product_id BIGINT REFERENCES product(id)
);

-- Insert sample users
INSERT INTO users (id, name, email) VALUES 
(1, 'John Doe', 'john.doe@example.com'),
(2, 'Jane Smith', 'jane.smith@example.com'),
(3, 'Bob Johnson', 'bob.johnson@example.com'),
(4, 'Alice Brown', 'alice.brown@example.com'),
(5, 'Charlie Wilson', 'charlie.wilson@example.com');

-- Insert sample manufacturers (vendors)
INSERT INTO manufacturer (id, name) VALUES 
(1, 'TechCorp Electronics'),
(2, 'Global Manufacturing Inc'),
(3, 'Premium Products Ltd'),
(4, 'Innovation Industries'),
(5, 'Quality Solutions Co');

-- Insert sample products (5 products for each manufacturer)
INSERT INTO product (id, name, price, manufacturer_id) VALUES 
-- TechCorp Electronics products
(1, 'Wireless Headphones Pro', 299.99, 1),
(2, 'Smart Watch Series X', 399.99, 1),
(3, 'Bluetooth Speaker Max', 199.99, 1),
(4, 'Gaming Mouse Elite', 89.99, 1),
(5, 'USB-C Hub Deluxe', 79.99, 1),

-- Global Manufacturing Inc products
(6, 'Steel Water Bottle', 24.99, 2),
(7, 'Ergonomic Office Chair', 449.99, 2),
(8, 'Adjustable Desk Lamp', 69.99, 2),
(9, 'Portable Phone Stand', 19.99, 2),
(10, 'Multi-Tool Kit', 39.99, 2),

-- Premium Products Ltd products
(11, 'Leather Wallet Premium', 89.99, 3),
(12, 'Silk Scarf Collection', 129.99, 3),
(13, 'Titanium Sunglasses', 349.99, 3),
(14, 'Cashmere Sweater', 299.99, 3),
(15, 'Designer Handbag', 599.99, 3),

-- Innovation Industries products
(16, 'Smart Home Hub', 199.99, 4),
(17, 'Wireless Charging Pad', 49.99, 4),
(18, 'Voice Assistant Pro', 149.99, 4),
(19, 'Security Camera System', 299.99, 4),
(20, 'Smart Doorbell', 179.99, 4),

-- Quality Solutions Co products
(21, 'Professional Toolkit', 159.99, 5),
(22, 'High-Quality Backpack', 89.99, 5),
(23, 'Stainless Steel Cookware', 249.99, 5),
(24, 'Precision Scale', 79.99, 5),
(25, 'Digital Thermometer', 29.99, 5);

-- Insert sample orders (without status field since it's not in the entity)
INSERT INTO orders (id, user_id, total_amount, order_date) VALUES 
(1, 1, 699.98, CURRENT_TIMESTAMP),
(2, 2, 519.98, CURRENT_TIMESTAMP),
(3, 3, 289.98, CURRENT_TIMESTAMP),
(4, 4, 849.97, CURRENT_TIMESTAMP);

-- Insert sample order items
INSERT INTO order_items (id, order_id, product_id, quantity, unit_price) VALUES 
-- Order 1 items
(1, 1, 1, 1, 299.99),
(2, 1, 3, 2, 199.99),

-- Order 2 items
(3, 2, 2, 1, 399.99),
(4, 2, 8, 1, 69.99),
(5, 2, 17, 1, 49.99),

-- Order 3 items
(6, 3, 11, 1, 89.99),
(7, 3, 6, 8, 24.99),

-- Order 4 items
(8, 4, 15, 1, 599.99),
(9, 4, 13, 1, 349.99),
(10, 4, 4, 1, 89.99),
(11, 4, 9, 1, 19.99);

-- Update sequences to avoid conflicts with manually inserted IDs
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('manufacturer_id_seq', (SELECT MAX(id) FROM manufacturer));
SELECT setval('product_id_seq', (SELECT MAX(id) FROM product));
SELECT setval('orders_id_seq', (SELECT MAX(id) FROM orders));
SELECT setval('order_items_id_seq', (SELECT MAX(id) FROM order_items));