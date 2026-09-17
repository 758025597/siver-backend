-- =====================================================================
--  USUARIO INICIAL DE SIVER
--
--  Desde que la API esta protegida con JWT, todos los endpoints exigen
--  token, incluido POST /api/usuarios. Eso deja un problema de arranque:
--  no se puede crear el primer usuario por la API porque para usarla hay
--  que iniciar sesion, y para iniciar sesion hace falta un usuario.
--
--  Este script rompe ese circulo insertando el primer administrador
--  directo en la base. Se ejecuta UNA sola vez.
--
--  Correo: admin@refricentro.pe
--  Clave:  Admin2026
--
--  La clave va cifrada con BCrypt, igual que lo hace la aplicacion.
--  Nunca se guarda en texto plano: por eso la tabla exige 20 caracteres
--  como minimo (ck_usuario_clave), que solo cumple un hash.
-- =====================================================================

INSERT INTO usuario (id_rol, nombres, apellidos, dni, correo, clave, telefono, activo)
SELECT 1, 'Administrador', 'SIVER', '00000001', 'admin@refricentro.pe',
       '$2a$10$EtJqgwXog1xa.33.YArET.rEyEL3c40Dt6tszizcnl8boNgv/GGhu',
       '900000001', TRUE
WHERE NOT EXISTS (SELECT 1 FROM usuario WHERE correo = 'admin@refricentro.pe');

-- Comprobacion
SELECT id_usuario, correo, LEFT(clave, 7) AS hash, activo
  FROM usuario WHERE correo = 'admin@refricentro.pe';
