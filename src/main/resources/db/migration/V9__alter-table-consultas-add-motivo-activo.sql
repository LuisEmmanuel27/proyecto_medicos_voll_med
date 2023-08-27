ALTER TABLE consultas ADD motivo_cancelacion VARCHAR(200);

ALTER TABLE consultas ADD activo TINYINT NOT NULL;

UPDATE consultas SET activo = 1;