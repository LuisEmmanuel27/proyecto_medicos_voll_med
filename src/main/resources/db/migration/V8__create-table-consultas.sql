CREATE TABLE
    consultas (
        id BIGINT NOT NULL AUTO_INCREMENT,
        medico_id BIGINT NOT NULL,
        paciente_id BIGINT NOT NULL,
        fecha DATETIME NOT NULL,

PRIMARY KEY (id),
FOREIGN KEY (medico_id) REFERENCES medicos (id),
FOREIGN KEY (paciente_id) REFERENCES pacientes (id)
);