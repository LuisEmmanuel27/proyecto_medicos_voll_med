CREATE TABLE
    pacientes(
        id BIGINT NOT NULL AUTO_INCREMENT,
        nombre VARCHAR(100) NOT NULL,
        email VARCHAR(100) not NULL UNIQUE,
        documentoIdentidad VARCHAR(14) NOT NULL UNIQUE,
        telefono VARCHAR(20) NOT NULL,
        calle VARCHAR(100) NOT NULL,
        distrito VARCHAR(100) NOT NULL,
        complemento VARCHAR(100),
        numero VARCHAR(20),
        ciudad VARCHAR(100) NOT NULL,
        PRIMARY KEY(id)
    );