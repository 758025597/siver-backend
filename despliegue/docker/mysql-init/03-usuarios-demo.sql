-- =====================================================================
--  SIVER - Claves conocidas para la DEMOSTRACION (un usuario por rol)
--
--  Solo para el ambiente de demostracion / sustentacion. Las claves van
--  cifradas con BCrypt, igual que las guarda la aplicacion.
--
--    Rol            Correo                       Clave
--    Administrador  admin@refricentro.pe         Admin2026
--    Vendedor       ksanchez@refricentro.pe      Vendedor2026
--    Tecnico        lgutierrez@refricentro.pe    Tecnico2026
--
--  En produccion cada usuario cambia su clave desde el modulo Usuarios.
-- =====================================================================
USE siver_bd;

UPDATE usuario SET clave = '$2a$10$qhztDB4R.cNfdnXS0QSNpeQAH.q4qBS.CvcrY1TtkIvYk/C5zNrX.', activo = 1 WHERE correo = 'ksanchez@refricentro.pe';
UPDATE usuario SET clave = '$2a$10$nZ0L.dQUZWfDILn4MeOece3iQ/iYuVfnT05lbV4SsMgB/dxgdlwvS', activo = 1 WHERE correo = 'lgutierrez@refricentro.pe';
