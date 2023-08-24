ALTER TABLE pacientes ADD activo TINYINT NOT NULL;

UPDATE pacientes SET activo = 1;