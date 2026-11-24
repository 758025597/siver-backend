-- =====================================================================
--  VM-BD: usuario de la aplicacion, restringido a la IP del backend
--
--  La aplicacion NO usa root. Usa siver_app, que:
--    - solo puede conectarse DESDE la IP de la VM-Backend (req. 4.5)
--    - solo tiene permisos sobre siver_bd, y solo para leer y escribir datos
--      (no puede borrar tablas ni crear usuarios)
--
--  Cambia 192.168.56.20 por la IP real de tu VM-Backend y la clave por una
--  propia. La misma clave va en DB_PASSWORD del setenv del Tomcat.
-- =====================================================================
CREATE USER IF NOT EXISTS 'siver_app'@'192.168.56.20' IDENTIFIED BY 'CambiaEstaClave-2026';
GRANT SELECT, INSERT, UPDATE, DELETE ON siver_bd.* TO 'siver_app'@'192.168.56.20';
FLUSH PRIVILEGES;

-- Comprobar:
SHOW GRANTS FOR 'siver_app'@'192.168.56.20';
