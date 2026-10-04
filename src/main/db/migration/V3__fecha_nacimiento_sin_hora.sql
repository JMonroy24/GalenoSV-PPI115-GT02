-- La columna anterior no tiene zona horaria: conservar su día almacenado.
-- Auditar fechas históricas antes de migrar; no se infiere ni corrige su zona original.
ALTER TABLE persona ALTER COLUMN fecha_nacimiento TYPE date USING fecha_nacimiento::date;
