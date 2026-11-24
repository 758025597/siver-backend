-- =====================================================================
--  SIVER - Tablas agregadas para la Evaluacion Final
--
--  Se ejecuta UNA vez sobre una base siver_bd que ya tiene las tablas
--  originales. Es idempotente: si las tablas ya existen, no hace nada.
--
--    auditoria             quien hizo que y cuando (consulta del admin)
--    notificacion          avisos del sistema (stock bajo, ordenes vencidas...)
--    notificacion_lectura  que usuario leyo que aviso
--
--  notificacion_lectura es la tabla intermedia de una relacion N:M
--  (una notificacion la leen muchos usuarios y un usuario lee muchas
--  notificaciones) con CLAVE PRIMARIA COMPUESTA (id_notificacion, id_usuario).
--  En Java se mapea con @EmbeddedId (NotificacionLecturaId).
-- =====================================================================

USE siver_bd;

CREATE TABLE IF NOT EXISTS auditoria (
    id_auditoria  BIGINT       NOT NULL AUTO_INCREMENT,
    fecha         DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    id_usuario    INT          NULL,
    usuario       VARCHAR(120) NOT NULL,
    rol           VARCHAR(50)  NULL,
    accion        VARCHAR(20)  NOT NULL,
    modulo        VARCHAR(50)  NOT NULL,
    id_registro   VARCHAR(20)  NULL,
    metodo        VARCHAR(10)  NOT NULL,
    ruta          VARCHAR(200) NOT NULL,
    estado_http   INT          NOT NULL,
    ip            VARCHAR(45)  NULL,
    PRIMARY KEY (id_auditoria),
    KEY idx_auditoria_fecha (fecha),
    KEY idx_auditoria_usuario (usuario),
    KEY idx_auditoria_modulo (modulo),
    CONSTRAINT fk_auditoria_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario) ON DELETE SET NULL ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS notificacion (
    id_notificacion INT          NOT NULL AUTO_INCREMENT,
    tipo            VARCHAR(30)  NOT NULL,
    titulo          VARCHAR(120) NOT NULL,
    mensaje         VARCHAR(300) NOT NULL,
    enlace          VARCHAR(200) NULL,
    roles_destino   VARCHAR(120) NULL,
    referencia      VARCHAR(50)  NULL,
    fecha           DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_notificacion),
    KEY idx_notificacion_fecha (fecha),
    KEY idx_notificacion_referencia (tipo, referencia)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE IF NOT EXISTS notificacion_lectura (
    id_notificacion INT      NOT NULL,
    id_usuario      INT      NOT NULL,
    fecha_lectura   DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (id_notificacion, id_usuario),
    KEY idx_lectura_usuario (id_usuario),
    CONSTRAINT fk_lectura_notificacion FOREIGN KEY (id_notificacion)
        REFERENCES notificacion (id_notificacion) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_lectura_usuario FOREIGN KEY (id_usuario)
        REFERENCES usuario (id_usuario) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
