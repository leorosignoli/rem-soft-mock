-- Database initialization script for Order Management System
-- This script will be executed automatically when PostgreSQL container starts

-- Insert sample users
INSERT INTO users (id, name, email, created_at, updated_at) VALUES 
(1, 'John Doe', 'john.doe@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Jane Smith', 'jane.smith@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Bob Johnson', 'bob.johnson@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Alice Brown', 'alice.brown@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Charlie Wilson', 'charlie.wilson@example.com', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample manufacturers (vendors)
INSERT INTO manufacturers (id, name, created_at, updated_at) VALUES 
(1, 'TechCorp Electronics', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Global Manufacturing Inc', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Premium Products Ltd', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Innovation Industries', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'Quality Solutions Co', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample products (5 products for each manufacturer)
INSERT INTO products (id, name, price, manufacturer_id, created_at, updated_at) VALUES 
-- TechCorp Electronics products
(1, 'Wireless Headphones Pro', 299.99, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 'Smart Watch Series X', 399.99, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 'Bluetooth Speaker Max', 199.99, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 'Gaming Mouse Elite', 89.99, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 'USB-C Hub Deluxe', 79.99, 1, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Global Manufacturing Inc products
(6, 'Steel Water Bottle', 24.99, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 'Ergonomic Office Chair', 449.99, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(8, 'Adjustable Desk Lamp', 69.99, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 'Portable Phone Stand', 19.99, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 'Multi-Tool Kit', 39.99, 2, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Premium Products Ltd products
(11, 'Leather Wallet Premium', 89.99, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(12, 'Silk Scarf Collection', 129.99, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(13, 'Titanium Sunglasses', 349.99, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(14, 'Cashmere Sweater', 299.99, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(15, 'Designer Handbag', 599.99, 3, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Innovation Industries products
(16, 'Smart Home Hub', 199.99, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(17, 'Wireless Charging Pad', 49.99, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(18, 'Voice Assistant Pro', 149.99, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(19, 'Security Camera System', 299.99, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(20, 'Smart Doorbell', 179.99, 4, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Quality Solutions Co products
(21, 'Professional Toolkit', 159.99, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(22, 'High-Quality Backpack', 89.99, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(23, 'Stainless Steel Cookware', 249.99, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(24, 'Precision Scale', 79.99, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(25, 'Digital Thermometer', 29.99, 5, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample orders
INSERT INTO orders (id, user_id, total_amount, status, created_at, updated_at) VALUES 
(1, 1, 699.98, 'PENDING', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 2, 519.98, 'CONFIRMED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(3, 3, 289.98, 'SHIPPED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 4, 849.97, 'DELIVERED', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample order items
INSERT INTO order_items (id, order_id, product_id, quantity, unit_price, created_at, updated_at) VALUES 
-- Order 1 items
(1, 1, 1, 1, 299.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 1, 3, 2, 199.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Order 2 items
(3, 2, 2, 1, 399.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(4, 2, 8, 1, 69.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(5, 2, 17, 1, 49.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Order 3 items
(6, 3, 11, 1, 89.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(7, 3, 6, 8, 24.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),

-- Order 4 items
(8, 4, 15, 1, 599.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(9, 4, 13, 1, 349.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(10, 4, 4, 1, 89.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(11, 4, 9, 1, 19.99, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Update sequences to avoid conflicts with manually inserted IDs
SELECT setval('users_id_seq', (SELECT MAX(id) FROM users));
SELECT setval('manufacturers_id_seq', (SELECT MAX(id) FROM manufacturers));
SELECT setval('products_id_seq', (SELECT MAX(id) FROM products));
SELECT setval('orders_id_seq', (SELECT MAX(id) FROM orders));
SELECT setval('order_items_id_seq', (SELECT MAX(id) FROM order_items));