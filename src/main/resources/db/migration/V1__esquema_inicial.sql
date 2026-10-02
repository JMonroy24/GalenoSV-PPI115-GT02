-- Esquema inicial inferido de los mapeos JPA. Para una base EXISTENTE: auditar y hacer baseline 1.
-- No contiene datos clínicos ni usuarios. PostgreSQL 15+.

CREATE TABLE clinica (
    id_clinica uuid PRIMARY KEY,
    nombre varchar(255),
    activo boolean,
    tipo varchar(20),
    comentarios varchar(2000)
);

CREATE TABLE consulta (
    id_consulta uuid PRIMARY KEY,
    fecha_inicio timestamp without time zone,
    fecha_fin timestamp without time zone,
    referencia_externa varchar(255),
    observaciones varchar(2000),
    id_persona_rol uuid
);

CREATE TABLE consulta_procedimiento (
    id_consulta_procedimiento uuid PRIMARY KEY,
    fecha_inicio timestamp without time zone,
    fecha_fin timestamp without time zone,
    observaciones varchar(2000),
    id_consulta uuid,
    id_procedimiento uuid
);

CREATE TABLE consulta_procedimiento_paso (
    id_consulta_procedimiento_paso uuid PRIMARY KEY,
    fecha_inicio timestamp without time zone,
    fecha_fin timestamp without time zone,
    estado varchar(20),
    id_consulta_procedimiento uuid,
    id_persona_rol uuid
);

CREATE TABLE documento (
    id_documento uuid PRIMARY KEY,
    valor varchar(50),
    ruta_fisica varchar(500),
    id_persona uuid,
    id_tipo_documento uuid
);

CREATE TABLE examen (
    id_examen uuid PRIMARY KEY,
    nombre varchar(255),
    activo boolean,
    observaciones varchar(2000)
);

CREATE TABLE examen_resultado (
    id_examen_resultado uuid PRIMARY KEY,
    fecha_creacion timestamp without time zone,
    resultado varchar(4000),
    interpretacion varchar(4000),
    ruta_atestado varchar(500),
    id_orden_examen uuid
);

CREATE TABLE examen_tipo_examen (
    id_examen_tipo_examen uuid PRIMARY KEY,
    fecha_creacion timestamp without time zone,
    observaciones varchar(2000),
    id_examen uuid,
    id_tipo_examen uuid
);

CREATE TABLE medio_contacto (
    id_medio_contacto uuid PRIMARY KEY,
    valor varchar(255),
    fecha_creacion timestamp without time zone,
    id_persona uuid,
    id_tipo_medio_contacto uuid
);

CREATE TABLE orden_examen (
    id_orden_examen uuid PRIMARY KEY,
    fecha_creacion timestamp without time zone,
    indicaciones varchar(2000),
    id_consulta_procedimiento_paso uuid
);

CREATE TABLE persona (
    id_persona uuid PRIMARY KEY,
    nombres varchar(255),
    apellidos varchar(255),
    fecha_nacimiento timestamp without time zone,
    fecha_creacion timestamp without time zone
);

CREATE TABLE persona_rol (
    id_persona_rol uuid PRIMARY KEY,
    fecha_creacion timestamp without time zone,
    id_clinica uuid,
    id_persona uuid,
    id_rol uuid
);

CREATE TABLE procedimiento (
    id_procedimiento uuid PRIMARY KEY,
    nombre varchar(155),
    activo boolean,
    observaciones varchar(2000)
);

CREATE TABLE procedimiento_paso (
    id_procedimiento_paso uuid PRIMARY KEY,
    nombre varchar(155),
    indica_fin boolean,
    id_procedimiento uuid,
    id_rol uuid
);

CREATE TABLE procedimiento_paso_examen (
    id_procedimiento_paso_examen uuid PRIMARY KEY,
    fecha_creacion timestamp without time zone,
    activo boolean,
    observaciones varchar(2000),
    id_examen uuid,
    id_procedimiento_paso uuid
);

CREATE TABLE procedimiento_paso_secuencia (
    id_procedimiento_paso_secuencia uuid PRIMARY KEY,
    id_procedimiento_paso_referencia uuid,
    tipo_secuencia varchar(20),
    id_procedimiento_paso uuid
);

CREATE TABLE rol (
    id_rol uuid PRIMARY KEY,
    nombre varchar(155),
    activo boolean,
    observaciones varchar(2000)
);

CREATE TABLE tipo_documento (
    id_tipo_documento uuid PRIMARY KEY,
    nombre varchar(155),
    indicaciones varchar(2000),
    expresion_regular varchar(500),
    activo boolean
);

CREATE TABLE tipo_examen (
    id_tipo_examen uuid PRIMARY KEY,
    nombre varchar(255),
    activo boolean,
    observaciones varchar(2000)
);

CREATE TABLE tipo_medio_contacto (
    id_tipo_medio_contacto uuid PRIMARY KEY,
    nombre varchar(155),
    indicaciones varchar(2000),
    expresion_regular varchar(500),
    activo boolean
);

ALTER TABLE consulta ADD CONSTRAINT fk_consulta_persona_rol FOREIGN KEY (id_persona_rol) REFERENCES persona_rol (id_persona_rol);
ALTER TABLE consulta_procedimiento ADD CONSTRAINT fk_consulta_procedimiento_consulta FOREIGN KEY (id_consulta) REFERENCES consulta (id_consulta);
ALTER TABLE consulta_procedimiento ADD CONSTRAINT fk_consulta_procedimiento_procedimiento FOREIGN KEY (id_procedimiento) REFERENCES procedimiento (id_procedimiento);
ALTER TABLE consulta_procedimiento_paso ADD CONSTRAINT fk_consulta_procedimiento_paso_consulta_procedimiento FOREIGN KEY (id_consulta_procedimiento) REFERENCES consulta_procedimiento (id_consulta_procedimiento);
ALTER TABLE consulta_procedimiento_paso ADD CONSTRAINT fk_consulta_procedimiento_paso_persona_rol FOREIGN KEY (id_persona_rol) REFERENCES persona_rol (id_persona_rol);
ALTER TABLE documento ADD CONSTRAINT fk_documento_persona FOREIGN KEY (id_persona) REFERENCES persona (id_persona);
ALTER TABLE documento ADD CONSTRAINT fk_documento_tipo_documento FOREIGN KEY (id_tipo_documento) REFERENCES tipo_documento (id_tipo_documento);
ALTER TABLE examen_resultado ADD CONSTRAINT fk_examen_resultado_orden_examen FOREIGN KEY (id_orden_examen) REFERENCES orden_examen (id_orden_examen);
ALTER TABLE examen_tipo_examen ADD CONSTRAINT fk_examen_tipo_examen_examen FOREIGN KEY (id_examen) REFERENCES examen (id_examen);
ALTER TABLE examen_tipo_examen ADD CONSTRAINT fk_examen_tipo_examen_tipo_examen FOREIGN KEY (id_tipo_examen) REFERENCES tipo_examen (id_tipo_examen);
ALTER TABLE medio_contacto ADD CONSTRAINT fk_medio_contacto_persona FOREIGN KEY (id_persona) REFERENCES persona (id_persona);
ALTER TABLE medio_contacto ADD CONSTRAINT fk_medio_contacto_tipo_medio_contacto FOREIGN KEY (id_tipo_medio_contacto) REFERENCES tipo_medio_contacto (id_tipo_medio_contacto);
ALTER TABLE orden_examen ADD CONSTRAINT fk_orden_examen_consulta_procedimiento_paso FOREIGN KEY (id_consulta_procedimiento_paso) REFERENCES consulta_procedimiento_paso (id_consulta_procedimiento_paso);
ALTER TABLE persona_rol ADD CONSTRAINT fk_persona_rol_clinica FOREIGN KEY (id_clinica) REFERENCES clinica (id_clinica);
ALTER TABLE persona_rol ADD CONSTRAINT fk_persona_rol_persona FOREIGN KEY (id_persona) REFERENCES persona (id_persona);
ALTER TABLE persona_rol ADD CONSTRAINT fk_persona_rol_rol FOREIGN KEY (id_rol) REFERENCES rol (id_rol);
ALTER TABLE procedimiento_paso ADD CONSTRAINT fk_procedimiento_paso_procedimiento FOREIGN KEY (id_procedimiento) REFERENCES procedimiento (id_procedimiento);
ALTER TABLE procedimiento_paso ADD CONSTRAINT fk_procedimiento_paso_rol FOREIGN KEY (id_rol) REFERENCES rol (id_rol);
ALTER TABLE procedimiento_paso_examen ADD CONSTRAINT fk_procedimiento_paso_examen_examen FOREIGN KEY (id_examen) REFERENCES examen (id_examen);
ALTER TABLE procedimiento_paso_examen ADD CONSTRAINT fk_procedimiento_paso_examen_procedimiento_paso FOREIGN KEY (id_procedimiento_paso) REFERENCES procedimiento_paso (id_procedimiento_paso);
ALTER TABLE procedimiento_paso_secuencia ADD CONSTRAINT fk_procedimiento_paso_secuencia_procedimiento_paso FOREIGN KEY (id_procedimiento_paso) REFERENCES procedimiento_paso (id_procedimiento_paso);
