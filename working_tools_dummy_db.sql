-- =========================================
-- AI CUSTOMER AGENT DATABASE
-- MySQL SAFE SCRIPT + DUMMY DATA
-- =========================================

DROP DATABASE IF EXISTS ai_agent;
CREATE DATABASE ai_agent;
USE ai_agent;

SET FOREIGN_KEY_CHECKS = 0;

-- =========================
-- 1. CLIENT
-- =========================
CREATE TABLE client (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_code VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(255) NOT NULL,
    subscription_plan VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB;

-- =========================
-- 2. PRODUCT
-- =========================
CREATE TABLE product (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    category VARCHAR(100),
    description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES client(id)
) ENGINE=InnoDB;

-- =========================
-- 3. PRODUCTION PROCESS
-- =========================
CREATE TABLE production_process (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    step_order INT NOT NULL,
    step_name VARCHAR(150) NOT NULL,
    description TEXT,
    FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB;

-- =========================
-- 4. WORKING TOOL
-- =========================
CREATE TABLE working_tool (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    tool_type VARCHAR(100),
    capacity_per_hour INT NOT NULL,
    operator_required INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES client(id)
) ENGINE=InnoDB;

-- =========================
-- 5. TOOL PROCESS
-- =========================
CREATE TABLE tool_process (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    process_id BIGINT NOT NULL,
    tool_id BIGINT NOT NULL,
    FOREIGN KEY (process_id) REFERENCES production_process(id),
    FOREIGN KEY (tool_id) REFERENCES working_tool(id)
) ENGINE=InnoDB;

-- =========================
-- 6. MATERIAL
-- =========================
CREATE TABLE material (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id BIGINT NOT NULL,
    name VARCHAR(255) NOT NULL,
    unit VARCHAR(50),
    price_per_unit DECIMAL(18,2),
    waste_ratio DECIMAL(5,4),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES client(id)
) ENGINE=InnoDB;

-- =========================
-- 7. PRODUCT MATERIAL
-- =========================
CREATE TABLE product_material (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    product_id BIGINT NOT NULL,
    material_id BIGINT NOT NULL,
    usage_per_unit DECIMAL(10,4),
    FOREIGN KEY (product_id) REFERENCES product(id),
    FOREIGN KEY (material_id) REFERENCES material(id)
) ENGINE=InnoDB;

-- =========================
-- 8. PRODUCTION CAPACITY
-- =========================
CREATE TABLE production_capacity (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    tool_id BIGINT NOT NULL,
    output_per_hour INT NOT NULL,
    FOREIGN KEY (tool_id) REFERENCES working_tool(id)
) ENGINE=InnoDB;

-- =========================
-- 9. SIMULATION
-- =========================
CREATE TABLE simulation (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id BIGINT NOT NULL,
    product_id BIGINT NOT NULL,
    quantity INT NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES client(id),
    FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB;

-- =========================
-- 10. SIMULATION RESULT
-- =========================
CREATE TABLE simulation_result (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    simulation_id BIGINT NOT NULL,
    total_material_cost DECIMAL(18,2),
    total_labor_cost DECIMAL(18,2),
    total_overhead_cost DECIMAL(18,2),
    estimated_days INT,
    FOREIGN KEY (simulation_id) REFERENCES simulation(id)
) ENGINE=InnoDB;

-- =========================
-- 11. QA LOG
-- =========================
CREATE TABLE qa_log (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    client_id BIGINT NOT NULL,
    user_id VARCHAR(100),
    question TEXT,
    answer TEXT,
    source_table VARCHAR(100),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (client_id) REFERENCES client(id)
) ENGINE=InnoDB;

-- =========================================
-- DUMMY DATA (>= 20 ROW TOTAL)
-- =========================================

-- CLIENT (3)
INSERT INTO client (client_code, name, subscription_plan) VALUES
('konveksi-a', 'Konveksi A', 'PRO'),
('konveksi-b', 'Konveksi B', 'BASIC'),
('konveksi-c', 'Konveksi C', 'FREE');

-- PRODUCT (6)
INSERT INTO product (client_id, name, category, description) VALUES
(1, 'Kaos Oblong', 'Kaos', 'Kaos cotton combed'),
(1, 'Hoodie', 'Jaket', 'Hoodie fleece'),
(2, 'Kaos Polo', 'Kaos', 'Polo shirt'),
(2, 'Jaket Parasut', 'Jaket', 'Jaket outdoor'),
(3, 'Kaos Event', 'Kaos', 'Kaos event murah'),
(3, 'Rompi', 'Aksesoris', 'Rompi proyek');

-- PRODUCTION PROCESS (6)
INSERT INTO production_process (product_id, step_order, step_name) VALUES
(1, 1, 'Cutting'),
(1, 2, 'Sewing'),
(1, 3, 'Finishing'),
(2, 1, 'Cutting'),
(2, 2, 'Sewing'),
(2, 3, 'Finishing');

-- WORKING TOOL (4)
INSERT INTO working_tool (client_id, name, tool_type, capacity_per_hour, operator_required) VALUES
(1, 'Mesin Jahit', 'Sewing', 40, 1),
(1, 'Mesin Obras', 'Finishing', 30, 1),
(2, 'Mesin Jahit', 'Sewing', 35, 1),
(3, 'Mesin Obras', 'Finishing', 25, 1);

-- MATERIAL (4)
INSERT INTO material (client_id, name, unit, price_per_unit, waste_ratio) VALUES
(1, 'Kain Cotton 24s', 'meter', 25000, 0.05),
(1, 'Benang Jahit', 'roll', 5000, 0.02),
(2, 'Kain Fleece', 'meter', 40000, 0.06),
(3, 'Kain PE', 'meter', 18000, 0.04);

-- PRODUCT MATERIAL (4)
INSERT INTO product_material (product_id, material_id, usage_per_unit) VALUES
(1, 1, 1.2),
(1, 2, 0.1),
(2, 3, 1.5),
(3, 4, 1.0);

-- SIMULATION (3)
INSERT INTO simulation (client_id, product_id, quantity) VALUES
(1, 1, 100),
(2, 3, 200),
(3, 5, 300);

-- SIMULATION RESULT (3)
INSERT INTO simulation_result (simulation_id, total_material_cost, total_labor_cost, total_overhead_cost, estimated_days) VALUES
(1, 3000000, 1500000, 500000, 5),
(2, 5000000, 2000000, 800000, 7),
(3, 4000000, 1200000, 600000, 6);

-- QA LOG (3)
INSERT INTO qa_log (client_id, user_id, question, answer, source_table) VALUES
(1, 'user1', 'Berapa kain untuk 100 kaos?', 'Butuh sekitar 120 meter kain', 'material'),
(2, 'user2', 'Mesin apa untuk kaos?', 'Menggunakan mesin jahit dan obras', 'working_tool'),
(3, 'user3', 'Estimasi waktu produksi?', 'Sekitar 6 hari kerja', 'simulation');

SET FOREIGN_KEY_CHECKS = 1;
