CREATE DATABASE IF NOT EXISTS user_database;

-- Usar la base de datos creada
USE user_database;

-- Crear la tabla users con tamaños optimizados para los campos VARCHAR
CREATE TABLE users (
    document_id BIGINT NOT NULL,         -- Documento del usuario, clave primaria
    full_name VARCHAR(100) NOT NULL,     -- Nombre completo del usuario (optimizado a 100 caracteres)
    status VARCHAR(20) DEFAULT NULL,     -- Estado del usuario (optimizado a 20 caracteres, puede ser NULL)
    email VARCHAR(100) NOT NULL,         -- Correo electrónico del usuario (optimizado a 100 caracteres)
    description TEXT DEFAULT NULL,       -- Descripción del usuario, campo de texto opcional
    password VARCHAR(60) NOT NULL,       -- Contraseña del usuario (optimizado a 60 caracteres, compatible con hashes)
    address VARCHAR(150) NOT NULL,       -- Dirección física del usuario (optimizado a 150 caracteres)
    PRIMARY KEY (document_id)            -- Clave primaria basada en document_id
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;