ALTER TABLE medicos ADD activo TINYINT NOT NULL;

UPDATE medicos SET activo = 1;