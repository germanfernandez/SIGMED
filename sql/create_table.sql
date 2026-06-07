USE sigmed_db;
 
CREATE TABLE IF NOT EXISTS usuarios (
    id_usuario      INT          NOT NULL AUTO_INCREMENT,
    username        VARCHAR(50)  NOT NULL UNIQUE,
    password        VARCHAR(255) NOT NULL,
    rol             VARCHAR(50)  NOT NULL,
    activo          TINYINT(1)   NOT NULL DEFAULT 1,
    fecha_creacion  DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
 
    CONSTRAINT pk_usuarios PRIMARY KEY (id_usuario),
    CONSTRAINT chk_rol CHECK (rol IN ('Secretaria','Médico Pediatra','Médica Dermatóloga'))
);
 
CREATE TABLE IF NOT EXISTS medicos (
    id_medico    INT          NOT NULL AUTO_INCREMENT,
    id_usuario   INT          NOT NULL,
    nombre       VARCHAR(100) NOT NULL,
    apellido     VARCHAR(100) NOT NULL,
    dni          VARCHAR(20)  NULL,
    telefono     VARCHAR(20)  NULL,
    especialidad VARCHAR(100) NOT NULL,
    matricula    VARCHAR(20)  NOT NULL UNIQUE,
 
    CONSTRAINT pk_medicos PRIMARY KEY (id_medico),
    CONSTRAINT fk_med_usr FOREIGN KEY (id_usuario)
        REFERENCES usuarios(id_usuario)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);
 
CREATE TABLE IF NOT EXISTS disponibilidad_medico (
    id_disponibilidad INT         NOT NULL AUTO_INCREMENT,
    id_medico         INT         NOT NULL,
    dia_semana        VARCHAR(20) NOT NULL,
    hora_inicio       TIME        NOT NULL,
    hora_fin          TIME        NOT NULL,
 
    CONSTRAINT pk_disponibilidad PRIMARY KEY (id_disponibilidad),
    CONSTRAINT fk_disp_med FOREIGN KEY (id_medico)
        REFERENCES medicos(id_medico)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT chk_dia CHECK (dia_semana IN
        ('Lunes','Martes','Miércoles','Jueves','Viernes','Sábado')),
    CONSTRAINT chk_horario CHECK (hora_fin > hora_inicio),
    CONSTRAINT uq_medico_dia UNIQUE (id_medico, dia_semana)
);
 
CREATE TABLE IF NOT EXISTS pacientes (
    id_paciente      INT          NOT NULL AUTO_INCREMENT,
    nombre           VARCHAR(100) NOT NULL,
    apellido         VARCHAR(100) NOT NULL,
    dni              VARCHAR(20)  NOT NULL UNIQUE,
    fecha_nacimiento DATE         NULL,
    telefono         VARCHAR(20)  NULL,
    email            VARCHAR(100) NULL,
    obra_social      VARCHAR(100) NULL,
    fecha_registro   DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
 
    CONSTRAINT pk_pacientes PRIMARY KEY (id_paciente)
);
 
CREATE TABLE IF NOT EXISTS turnos (
    id_turno           INT          NOT NULL AUTO_INCREMENT,
    id_paciente        INT          NOT NULL,
    id_medico          INT          NOT NULL,
    fecha              DATE         NOT NULL,
    hora               TIME         NOT NULL,
    motivo_consulta    VARCHAR(255) NULL,
    estado             VARCHAR(20)  NOT NULL DEFAULT 'Pendiente',
    fecha_creacion     DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    fecha_modificacion DATETIME     NULL,
 
    CONSTRAINT pk_turnos       PRIMARY KEY (id_turno),
    CONSTRAINT fk_tur_pac      FOREIGN KEY (id_paciente)
        REFERENCES pacientes(id_paciente)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT fk_tur_med      FOREIGN KEY (id_medico)
        REFERENCES medicos(id_medico)
        ON DELETE RESTRICT
        ON UPDATE CASCADE,
    CONSTRAINT chk_estado      CHECK (estado IN
        ('Pendiente','Presente','Ausente','Atendido')),
    CONSTRAINT uq_turno_medico UNIQUE (id_medico, fecha, hora)
);
 
CREATE TABLE IF NOT EXISTS notas_clinicas (
    id_nota        INT      NOT NULL AUTO_INCREMENT,
    id_turno       INT      NOT NULL UNIQUE,
    fecha          DATE     NOT NULL DEFAULT (CURRENT_DATE),
    diagnostico    TEXT     NOT NULL,
    tratamiento    TEXT     NULL,
    observaciones  TEXT     NULL,
    fecha_creacion DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
 
    CONSTRAINT pk_notas    PRIMARY KEY (id_nota),
    CONSTRAINT fk_nota_tur FOREIGN KEY (id_turno)
        REFERENCES turnos(id_turno)
        ON DELETE RESTRICT
        ON UPDATE CASCADE
);