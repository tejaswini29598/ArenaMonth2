-- Sample Data for Testing
INSERT INTO categories (name, description) VALUES
('Electronics', 'Electronic devices and gadgets'),
('Clothing', 'Apparel and fashion items'),
('Books', 'Physical and digital books'),
('Home & Garden', 'Home and garden products'),
('Sports', 'Sports and fitness equipment');

INSERT INTO products (name, description, price, stock_quantity, category_id, is_active) VALUES
('Laptop Pro', 'High-performance laptop for professionals', 999.99, 50, 1, true),
('Smartphone X', 'Latest smartphone with advanced features', 799.99, 100, 1, true),
('Wireless Earbuds', 'Premium sound quality earbuds', 149.99, 200, 1, true),
('T-Shirt', 'Comfortable cotton t-shirt', 29.99, 300, 2, true),
('Jeans', 'Classic denim jeans', 79.99, 150, 2, true),
('Programming Book', 'Learn Spring Boot from scratch', 49.99, 75, 3, true),
('Coffee Maker', 'Automatic coffee maker with timer', 89.99, 40, 4, true),
('Running Shoes', 'Professional running shoes', 119.99, 60, 5, true);

INSERT INTO users (username, email, password, full_name, phone_number, role, is_active) VALUES
('admin', 'admin@ecommerce.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/gee', 'Admin User', '+1234567890', 'ADMIN', true),
('john_doe', 'john@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/gee', 'John Doe', '+1234567891', 'USER', true),
('jane_smith', 'jane@example.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcg7b3XeKeUxWdeS86E36P4/gee', 'Jane Smith', '+1234567892', 'USER', true);
