-- =====================================================================
--  SIVER - Base de datos completa (estructura, triggers, vista y datos)
--  Generado con mysqldump desde la base de desarrollo el 24/11/2026.
--  Docker lo ejecuta solo la PRIMERA vez que crea el volumen de datos.
--  Tambien sirve para crear la base en la VM-BD:  mysql -u root -p < 01-siver_bd.sql
-- =====================================================================
CREATE DATABASE IF NOT EXISTS siver_bd CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE siver_bd;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;
DROP TABLE IF EXISTS `categoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `categoria` (
  `id_categoria` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(80) NOT NULL,
  `descripcion` varchar(200) DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `fecha_registro` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_categoria`),
  UNIQUE KEY `uq_categoria_nombre` (`nombre`),
  CONSTRAINT `ck_categoria_nombre` CHECK ((char_length(trim(`nombre`)) >= 3))
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `categoria` WRITE;
/*!40000 ALTER TABLE `categoria` DISABLE KEYS */;
INSERT INTO `categoria` VALUES (1,'Refrigerantes','Gases refrigerantes R134a, R410a, R32 y similares',1,'2026-08-27 22:41:02'),(2,'Compresores','Compresores para equipos de refrigeracion',1,'2026-08-27 22:41:02'),(3,'Repuestos','Repuestos generales y componentes',1,'2026-08-27 22:41:02'),(4,'Herramientas','Herramientas para instalacion y mantenimiento',1,'2026-08-27 22:41:02'),(5,'Equipos','Equipos completos de refrigeracion y climatizacion',1,'2026-08-27 22:41:02'),(6,'Accesorios','Mangueras, filtros, valvulas y conexiones',1,'2026-08-27 22:41:02'),(15,'Congeladoras 4307','Equipos de congelacion comercial',1,'2026-09-17 10:17:42'),(16,'Congeladoras 1476','Equipos de congelacion comercial',1,'2026-09-17 22:47:44'),(23,'Termostatos y controles','Termostatos, timers de deshielo y tarjetas electrónicas',1,'2026-10-20 01:26:50'),(24,'Motores y ventiladores','Motores ventiladores de condensador y evaporador',1,'2026-10-20 01:26:50'),(25,'Aire acondicionado','Repuestos y equipos split, ventana y centrales',1,'2026-10-20 01:26:50');
/*!40000 ALTER TABLE `categoria` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `cliente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cliente` (
  `id_cliente` int NOT NULL AUTO_INCREMENT,
  `tipo_documento` enum('DNI','RUC','CE') NOT NULL DEFAULT 'DNI',
  `numero_documento` varchar(12) NOT NULL,
  `nombres` varchar(150) NOT NULL,
  `telefono` char(9) DEFAULT NULL,
  `correo` varchar(120) DEFAULT NULL,
  `direccion` varchar(200) DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `fecha_registro` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_cliente`),
  UNIQUE KEY `uq_cliente_documento` (`tipo_documento`,`numero_documento`),
  KEY `idx_cliente_documento` (`numero_documento`),
  KEY `idx_cliente_nombres` (`nombres`),
  CONSTRAINT `ck_cliente_correo` CHECK (((`correo` is null) or regexp_like(`correo`,_utf8mb4'^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$'))),
  CONSTRAINT `ck_cliente_documento` CHECK ((((`tipo_documento` = _utf8mb4'DNI') and regexp_like(`numero_documento`,_utf8mb4'^[0-9]{8}$')) or ((`tipo_documento` = _utf8mb4'RUC') and regexp_like(`numero_documento`,_utf8mb4'^(10|15|17|20)[0-9]{9}$')) or ((`tipo_documento` = _utf8mb4'CE') and regexp_like(`numero_documento`,_utf8mb4'^[0-9]{9,12}$')))),
  CONSTRAINT `ck_cliente_nombres` CHECK ((char_length(trim(`nombres`)) >= 3)),
  CONSTRAINT `ck_cliente_telefono` CHECK (((`telefono` is null) or regexp_like(`telefono`,_utf8mb4'^9[0-9]{8}$')))
) ENGINE=InnoDB AUTO_INCREMENT=33 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `cliente` WRITE;
/*!40000 ALTER TABLE `cliente` DISABLE KEYS */;
INSERT INTO `cliente` VALUES (13,'DNI','78705075','Carmen Rosa Quispe','981617259','carmen6389@gmail.com','Jr. Puno 345, Lima',1,'2026-09-17 10:17:43'),(14,'RUC','20300670752','Hotel Costa Azul 2914 SAC','942407850','admin2914@costaazul.pe','Av. Larco 890, Miraflores',1,'2026-09-17 10:17:43'),(15,'DNI','77269326','Carmen Rosa Quispe','936949164','carmen6131@gmail.com','Jr. Puno 345, Lima',1,'2026-09-17 22:47:44'),(16,'RUC','20784175038','Hotel Costa Azul 8896 SAC','983468877','admin8896@costaazul.pe','Av. Larco 890, Miraflores',1,'2026-09-17 22:47:44'),(25,'DNI','75832227','María Lopez','943089344','ale.alv2006surco@gmail.com','Lima',1,'2026-10-20 00:47:53'),(26,'DNI','12344555','Juan Perez','908766888','juan@gmail.com','av. la paz',0,'2026-10-20 00:48:43'),(27,'DNI','45781236','José Luis Mendoza Ríos','949112356','jlmendoza@gmail.com','Calle Los Pinos 245, Trujillo',1,'2026-10-20 01:26:50'),(28,'DNI','70125489','Ana Patricia Vargas León','962335874','anavargas@hotmail.com','Av. Larco 1120, Trujillo',1,'2026-10-20 01:26:50'),(29,'DNI','41236578','Pedro Castillo Huamán','951478236',NULL,'Jr. Bolívar 530, Trujillo',1,'2026-10-20 01:26:50'),(30,'RUC','20605478123','Restaurante El Sabor Norteño SAC','944785123','compras@elsabornorteno.pe','Av. Huamán 980, Víctor Larco',1,'2026-10-20 01:26:50'),(31,'RUC','20498765412','Minimarket Don Lucho EIRL','948652147','donlucho@gmail.com','Av. Perú 1580, Trujillo',1,'2026-10-20 01:26:50'),(32,'CE','001478523','Carlos Eduardo Ramírez','955321478','cramirez@gmail.com','Urb. California, Mz B Lt 12',1,'2026-10-20 01:26:50');
/*!40000 ALTER TABLE `cliente` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `detalle_venta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `detalle_venta` (
  `id_detalle` int NOT NULL AUTO_INCREMENT,
  `id_venta` int NOT NULL,
  `id_producto` int NOT NULL,
  `cantidad` int NOT NULL,
  `precio_unitario` decimal(10,2) NOT NULL,
  `descuento` decimal(10,2) NOT NULL DEFAULT '0.00',
  `subtotal` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id_detalle`),
  UNIQUE KEY `uq_detalle_venta_producto` (`id_venta`,`id_producto`),
  KEY `idx_detalle_producto` (`id_producto`),
  CONSTRAINT `fk_detalle_producto` FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id_producto`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_detalle_venta` FOREIGN KEY (`id_venta`) REFERENCES `venta` (`id_venta`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `ck_detalle_cantidad` CHECK ((`cantidad` > 0)),
  CONSTRAINT `ck_detalle_descuento` CHECK (((`descuento` >= 0) and (`descuento` <= (`cantidad` * `precio_unitario`)))),
  CONSTRAINT `ck_detalle_precio` CHECK ((`precio_unitario` > 0)),
  CONSTRAINT `ck_detalle_subtotal` CHECK ((`subtotal` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `detalle_venta` WRITE;
/*!40000 ALTER TABLE `detalle_venta` DISABLE KEYS */;
INSERT INTO `detalle_venta` VALUES (6,12,9,2,300.00,0.00,600.00),(7,14,10,2,300.00,0.00,600.00),(11,19,22,2,9.00,0.00,18.00),(12,19,18,1,45.00,0.00,45.00),(13,20,16,2,285.00,20.00,550.00),(14,20,24,10,12.00,0.00,120.00),(15,21,25,1,48.00,0.00,48.00),(16,22,20,1,520.00,0.00,520.00),(17,22,23,2,35.00,5.00,65.00),(18,23,27,1,55.00,0.00,55.00),(19,23,22,1,9.00,0.00,9.00),(20,24,29,1,1899.00,100.00,1799.00);
/*!40000 ALTER TABLE `detalle_venta` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 */ /*!50003 TRIGGER `trg_detalle_venta_before_insert` BEFORE INSERT ON `detalle_venta` FOR EACH ROW BEGIN
    DECLARE v_stock  INT;
    DECLARE v_activo BOOLEAN;
    DECLARE v_nombre VARCHAR(150);

    SELECT stock, activo, nombre
      INTO v_stock, v_activo, v_nombre
      FROM producto
     WHERE id_producto = NEW.id_producto;

    IF v_activo = FALSE THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'No se puede vender un producto inactivo.';
    END IF;

    IF v_stock < NEW.cantidad THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Stock insuficiente para completar la venta.';
    END IF;

    SET NEW.subtotal = (NEW.cantidad * NEW.precio_unitario) - NEW.descuento;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 */ /*!50003 TRIGGER `trg_detalle_venta_after_insert` AFTER INSERT ON `detalle_venta` FOR EACH ROW BEGIN
    DECLARE v_stock_ant INT;
    DECLARE v_usuario   INT;

    SELECT stock INTO v_stock_ant
      FROM producto WHERE id_producto = NEW.id_producto;

    SELECT id_usuario INTO v_usuario
      FROM venta WHERE id_venta = NEW.id_venta;

    UPDATE producto
       SET stock = stock - NEW.cantidad
     WHERE id_producto = NEW.id_producto;

    INSERT INTO movimiento_inventario
        (id_producto, id_usuario, id_venta, tipo_movimiento,
         cantidad, stock_anterior, stock_nuevo, motivo)
    VALUES
        (NEW.id_producto, v_usuario, NEW.id_venta, 'SALIDA',
         NEW.cantidad, v_stock_ant, v_stock_ant - NEW.cantidad,
         CONCAT('Venta ID ', NEW.id_venta));

    -- Recalcula los totales de la cabecera (IGV 18%)
    UPDATE venta v
       SET v.subtotal = (SELECT ROUND(SUM(d.subtotal) / 1.18, 2)
                           FROM detalle_venta d WHERE d.id_venta = v.id_venta),
           v.igv      = (SELECT ROUND(SUM(d.subtotal) - (SUM(d.subtotal) / 1.18), 2)
                           FROM detalle_venta d WHERE d.id_venta = v.id_venta),
           v.total    = (SELECT ROUND(SUM(d.subtotal), 2)
                           FROM detalle_venta d WHERE d.id_venta = v.id_venta)
     WHERE v.id_venta = NEW.id_venta;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
DROP TABLE IF EXISTS `estado_orden`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `estado_orden` (
  `id_estado` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `descripcion` varchar(200) DEFAULT NULL,
  `orden_flujo` tinyint NOT NULL,
  `es_final` tinyint(1) NOT NULL DEFAULT '0',
  PRIMARY KEY (`id_estado`),
  UNIQUE KEY `uq_estado_nombre` (`nombre`),
  UNIQUE KEY `uq_estado_flujo` (`orden_flujo`),
  CONSTRAINT `ck_estado_flujo` CHECK ((`orden_flujo` > 0))
) ENGINE=InnoDB AUTO_INCREMENT=10 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `estado_orden` WRITE;
/*!40000 ALTER TABLE `estado_orden` DISABLE KEYS */;
INSERT INTO `estado_orden` VALUES (1,'Recibido','El equipo ingreso al taller',1,0),(2,'En revision','El tecnico esta diagnosticando la falla',2,0),(3,'En reparacion','Reparacion en curso',3,0),(4,'Reparado','Listo para ser entregado',4,0),(5,'Entregado','El cliente recogio el equipo',5,1),(6,'Cancelado','El servicio no se realizo',6,1);
/*!40000 ALTER TABLE `estado_orden` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `movimiento_inventario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimiento_inventario` (
  `id_movimiento` int NOT NULL AUTO_INCREMENT,
  `id_producto` int NOT NULL,
  `id_usuario` int DEFAULT NULL,
  `id_venta` int DEFAULT NULL,
  `tipo_movimiento` enum('ENTRADA','SALIDA','AJUSTE') NOT NULL,
  `cantidad` int NOT NULL,
  `stock_anterior` int NOT NULL,
  `stock_nuevo` int NOT NULL,
  `motivo` varchar(200) DEFAULT NULL,
  `fecha_movimiento` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_movimiento`),
  KEY `fk_movimiento_usuario` (`id_usuario`),
  KEY `fk_movimiento_venta` (`id_venta`),
  KEY `idx_movimiento_producto` (`id_producto`),
  KEY `idx_movimiento_fecha` (`fecha_movimiento`),
  CONSTRAINT `fk_movimiento_producto` FOREIGN KEY (`id_producto`) REFERENCES `producto` (`id_producto`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_movimiento_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `fk_movimiento_venta` FOREIGN KEY (`id_venta`) REFERENCES `venta` (`id_venta`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `ck_movimiento_cantidad` CHECK ((`cantidad` > 0)),
  CONSTRAINT `ck_movimiento_stock_ant` CHECK ((`stock_anterior` >= 0)),
  CONSTRAINT `ck_movimiento_stock_new` CHECK ((`stock_nuevo` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=32 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `movimiento_inventario` WRITE;
/*!40000 ALTER TABLE `movimiento_inventario` DISABLE KEYS */;
INSERT INTO `movimiento_inventario` VALUES (8,9,9,12,'SALIDA',2,30,28,'Venta ID 12','2026-09-17 10:17:43'),(9,10,10,14,'SALIDA',2,30,28,'Venta ID 14','2026-09-17 22:47:44'),(13,22,17,19,'SALIDA',2,120,118,'Venta ID 19','2026-10-20 01:26:51'),(14,18,17,19,'SALIDA',1,40,39,'Venta ID 19','2026-10-20 01:26:51'),(15,16,17,20,'SALIDA',2,18,16,'Venta ID 20','2026-10-20 01:26:51'),(16,24,17,20,'SALIDA',10,150,140,'Venta ID 20','2026-10-20 01:26:51'),(17,25,17,21,'SALIDA',1,5,4,'Venta ID 21','2026-10-20 01:26:51'),(18,20,17,22,'SALIDA',1,2,1,'Venta ID 22','2026-10-20 01:26:51'),(19,23,17,22,'SALIDA',2,25,23,'Venta ID 22','2026-10-20 01:26:51'),(20,27,17,23,'SALIDA',1,11,10,'Venta ID 23','2026-10-20 01:26:51'),(21,22,17,23,'SALIDA',1,118,117,'Venta ID 23','2026-10-20 01:26:51'),(22,29,17,24,'SALIDA',1,4,3,'Venta ID 24','2026-10-20 01:26:51');
/*!40000 ALTER TABLE `movimiento_inventario` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `orden_servicio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `orden_servicio` (
  `id_orden` int NOT NULL AUTO_INCREMENT,
  `numero_orden` varchar(20) NOT NULL,
  `id_cliente` int NOT NULL,
  `id_usuario` int NOT NULL,
  `id_estado` int NOT NULL,
  `equipo` varchar(100) NOT NULL,
  `marca` varchar(60) DEFAULT NULL,
  `modelo` varchar(60) DEFAULT NULL,
  `numero_serie` varchar(60) DEFAULT NULL,
  `descripcion_falla` text NOT NULL,
  `diagnostico` text,
  `trabajo_realizado` text,
  `fecha_ingreso` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_entrega_estimada` date DEFAULT NULL,
  `fecha_entrega_real` datetime DEFAULT NULL,
  `costo_estimado` decimal(10,2) NOT NULL DEFAULT '0.00',
  `costo_final` decimal(10,2) DEFAULT NULL,
  PRIMARY KEY (`id_orden`),
  UNIQUE KEY `uq_orden_numero` (`numero_orden`),
  KEY `idx_orden_cliente` (`id_cliente`),
  KEY `idx_orden_usuario` (`id_usuario`),
  KEY `idx_orden_estado` (`id_estado`),
  CONSTRAINT `fk_orden_cliente` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_orden_estado` FOREIGN KEY (`id_estado`) REFERENCES `estado_orden` (`id_estado`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_orden_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `ck_orden_costo_estimado` CHECK ((`costo_estimado` >= 0)),
  CONSTRAINT `ck_orden_costo_final` CHECK (((`costo_final` is null) or (`costo_final` >= 0))),
  CONSTRAINT `ck_orden_equipo` CHECK ((char_length(trim(`equipo`)) >= 3)),
  CONSTRAINT `ck_orden_falla` CHECK ((char_length(trim(`descripcion_falla`)) >= 10)),
  CONSTRAINT `ck_orden_fecha_entrega` CHECK (((`fecha_entrega_real` is null) or (`fecha_entrega_real` >= `fecha_ingreso`)))
) ENGINE=InnoDB AUTO_INCREMENT=19 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `orden_servicio` WRITE;
/*!40000 ALTER TABLE `orden_servicio` DISABLE KEYS */;
INSERT INTO `orden_servicio` VALUES (7,'OS-2026-9073',14,9,6,'Congeladora vertical','Coldex','CV-350','SN9116','No enfria y hace ruido en el compresor','Compresor quemado por sobrecarga electrica, requiere reemplazo','Se reemplazo el compresor y se recargo gas R134a','2026-09-17 10:17:43','2026-10-05','2026-09-17 10:17:43',250.00,320.00),(8,'OS-2026-7718',16,10,6,'Congeladora vertical','Coldex','CV-350','SN8054','No enfria y hace ruido en el compresor','Compresor quemado por sobrecarga electrica, requiere reemplazo','Se reemplazo el compresor y se recargo gas R134a','2026-09-17 22:47:44','2026-10-05','2026-09-17 22:47:45',250.00,320.00),(13,'OS-2026-0101',27,15,1,'Refrigeradora No Frost 14 pies','LG','GT40',NULL,'No enfría la parte de abajo y hace escarcha atras',NULL,NULL,'2026-10-20 01:26:51','2026-10-24',NULL,180.00,NULL),(14,'OS-2026-0102',28,16,2,'Refrigeradora side by side','Samsung','WD10',NULL,'El congelador no enciende y huele a quemado','Termostato dañado, se revisa compresor',NULL,'2026-10-20 01:26:51','2026-10-25',NULL,120.00,NULL),(15,'OS-2026-0103',30,15,3,'Cámara frigorífica 3x3','Bohn',NULL,NULL,'La cámara no baja de 8 grados y el compresor se apaga','Fuga de gas en el evaporador','Se ubicó la fuga y se soldó la tubería','2026-10-20 01:26:51','2026-10-28',NULL,650.00,NULL),(16,'OS-2026-0104',31,16,4,'Congeladora horizontal 300 L','Indurama','CI-300',NULL,'No congela, el motor arranca y se apaga','Capacitor de arranque quemado','Cambio de capacitor y recarga de gas R600a','2026-10-20 01:26:51','2026-10-22',NULL,150.00,165.00),(17,'OS-2026-0105',29,15,5,'Aire acondicionado split 18000 BTU','Midea','MSAG-18',NULL,'Gotea agua por dentro y no enfria bien','Drenaje obstruido y falta de gas','Limpieza de drenaje, mantenimiento y carga de R410A','2026-10-20 01:26:51','2026-10-21','2026-10-20 01:26:51',220.00,240.00),(18,'OS-2026-0106',32,16,1,'Frigobar 90 L','Electrolux',NULL,NULL,'Hace mucho ruido al encender el motor',NULL,NULL,'2026-10-20 01:26:51','2026-10-30',NULL,90.00,NULL);
/*!40000 ALTER TABLE `orden_servicio` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 */ /*!50003 TRIGGER `trg_orden_before_update` BEFORE UPDATE ON `orden_servicio` FOR EACH ROW BEGIN
    DECLARE v_es_final BOOLEAN;

    SELECT es_final INTO v_es_final
      FROM estado_orden WHERE id_estado = NEW.id_estado;

    IF v_es_final = TRUE THEN
        IF NEW.diagnostico IS NULL OR CHAR_LENGTH(TRIM(NEW.diagnostico)) < 10 THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'No se puede cerrar la orden sin un diagnostico.';
        END IF;
        IF NEW.costo_final IS NULL THEN
            SIGNAL SQLSTATE '45000'
            SET MESSAGE_TEXT = 'No se puede cerrar la orden sin el costo final.';
        END IF;
        IF NEW.fecha_entrega_real IS NULL THEN
            SET NEW.fecha_entrega_real = NOW();
        END IF;
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
DROP TABLE IF EXISTS `producto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `producto` (
  `id_producto` int NOT NULL AUTO_INCREMENT,
  `codigo` varchar(30) NOT NULL,
  `nombre` varchar(150) NOT NULL,
  `descripcion` varchar(300) DEFAULT NULL,
  `id_categoria` int NOT NULL,
  `id_proveedor` int DEFAULT NULL,
  `unidad_medida` enum('UNIDAD','KILOGRAMO','LITRO','METRO','CAJA','GALON') NOT NULL DEFAULT 'UNIDAD',
  `precio_compra` decimal(10,2) NOT NULL DEFAULT '0.00',
  `precio_venta` decimal(10,2) NOT NULL,
  `stock` int NOT NULL DEFAULT '0',
  `stock_minimo` int NOT NULL DEFAULT '5',
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `fecha_registro` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_producto`),
  UNIQUE KEY `uq_producto_codigo` (`codigo`),
  KEY `idx_producto_categoria` (`id_categoria`),
  KEY `idx_producto_proveedor` (`id_proveedor`),
  KEY `idx_producto_nombre` (`nombre`),
  CONSTRAINT `fk_producto_categoria` FOREIGN KEY (`id_categoria`) REFERENCES `categoria` (`id_categoria`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_producto_proveedor` FOREIGN KEY (`id_proveedor`) REFERENCES `proveedor` (`id_proveedor`) ON DELETE SET NULL ON UPDATE CASCADE,
  CONSTRAINT `ck_producto_margen` CHECK ((`precio_venta` >= `precio_compra`)),
  CONSTRAINT `ck_producto_nombre` CHECK ((char_length(trim(`nombre`)) >= 3)),
  CONSTRAINT `ck_producto_precio_compra` CHECK ((`precio_compra` >= 0)),
  CONSTRAINT `ck_producto_precio_venta` CHECK ((`precio_venta` > 0)),
  CONSTRAINT `ck_producto_stock` CHECK ((`stock` >= 0)),
  CONSTRAINT `ck_producto_stock_minimo` CHECK ((`stock_minimo` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=30 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `producto` WRITE;
/*!40000 ALTER TABLE `producto` DISABLE KEYS */;
INSERT INTO `producto` VALUES (9,'PRD-1282','Compresor 1HP Premium','Descripcion actualizada',15,6,'UNIDAD',180.00,320.00,28,5,0,'2026-09-17 10:17:43'),(10,'PRD-3053','Compresor 1HP Premium','Descripcion actualizada',16,7,'UNIDAD',180.00,320.00,28,5,0,'2026-09-17 22:47:44'),(16,'REF-R134A-13','Gas refrigerante R134a 13.6 kg','Balón para refrigeradoras y aire automotriz',1,11,'UNIDAD',210.00,285.00,16,4,1,'2026-10-20 01:26:50'),(17,'REF-R410A-11','Gas refrigerante R410A 11.3 kg','Balón para aire acondicionado split inverter',1,11,'UNIDAD',260.00,349.00,9,3,1,'2026-10-20 01:26:50'),(18,'REF-R600A-420','Gas refrigerante R600a 420 g','Lata para refrigeradoras ecológicas',1,12,'UNIDAD',28.00,45.00,39,10,1,'2026-10-20 01:26:50'),(19,'COM-EMB-1/4','Compresor Embraco 1/4 HP R134a','Compresor hermético para refrigeradora doméstica',2,12,'UNIDAD',320.00,450.00,6,2,1,'2026-10-20 01:26:50'),(20,'COM-EMB-1/3','Compresor Embraco 1/3 HP R600a','Compresor para congeladora horizontal',2,12,'UNIDAD',365.00,520.00,1,2,1,'2026-10-20 01:26:50'),(21,'COM-TEC-1HP','Compresor Tecumseh 1 HP','Compresor para cámaras frigoríficas',2,13,'UNIDAD',980.00,1350.00,3,1,1,'2026-10-20 01:26:50'),(22,'REP-FILT-15','Filtro deshidratador 15 g','Filtro secador soldable 1/4',3,12,'UNIDAD',4.50,9.00,117,30,1,'2026-10-20 01:26:50'),(23,'REP-CAP-35','Capacitor de marcha 35 uF','Capacitor para compresor de aire acondicionado',3,14,'UNIDAD',18.00,35.00,23,8,1,'2026-10-20 01:26:50'),(24,'REP-TUBO-1/4','Tubo de cobre 1/4 pulgada','Rollo de tubo de cobre flexible',3,11,'METRO',6.50,12.00,140,40,1,'2026-10-20 01:26:50'),(25,'CTL-TERM-RC','Termostato para refrigeradora RC','Termostato universal con perilla',23,12,'UNIDAD',22.00,48.00,4,6,1,'2026-10-20 01:26:50'),(26,'CTL-TIMER-DH','Timer de deshielo Paragon','Timer de deshielo para refrigeradoras no frost',23,12,'UNIDAD',30.00,65.00,14,4,1,'2026-10-20 01:26:50'),(27,'MOT-VENT-10W','Motor ventilador 10 W','Motor ventilador de condensador',24,14,'UNIDAD',25.00,55.00,10,4,1,'2026-10-20 01:26:50'),(28,'HER-MANI-R410','Manómetro digital R410A','Juego de manómetros para carga de gas',4,13,'UNIDAD',240.00,380.00,4,1,1,'2026-10-20 01:26:50'),(29,'AC-SPLIT-12K','Aire acondicionado split 12000 BTU','Equipo split inverter con instalación básica',25,13,'UNIDAD',1450.00,1899.00,3,1,1,'2026-10-20 01:26:50');
/*!40000 ALTER TABLE `producto` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `proveedor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `proveedor` (
  `id_proveedor` int NOT NULL AUTO_INCREMENT,
  `ruc` char(11) NOT NULL,
  `razon_social` varchar(150) NOT NULL,
  `contacto` varchar(100) DEFAULT NULL,
  `telefono` char(9) DEFAULT NULL,
  `correo` varchar(120) DEFAULT NULL,
  `direccion` varchar(200) DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `fecha_registro` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_proveedor`),
  UNIQUE KEY `uq_proveedor_ruc` (`ruc`),
  CONSTRAINT `ck_proveedor_correo` CHECK (((`correo` is null) or regexp_like(`correo`,_utf8mb4'^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$'))),
  CONSTRAINT `ck_proveedor_razon` CHECK ((char_length(trim(`razon_social`)) >= 3)),
  CONSTRAINT `ck_proveedor_ruc` CHECK (regexp_like(`ruc`,_utf8mb4'^(10|15|17|20)[0-9]{9}$')),
  CONSTRAINT `ck_proveedor_telefono` CHECK (((`telefono` is null) or regexp_like(`telefono`,_utf8mb4'^[0-9]{9}$')))
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `proveedor` WRITE;
/*!40000 ALTER TABLE `proveedor` DISABLE KEYS */;
INSERT INTO `proveedor` VALUES (6,'20410541123','Friotec Importaciones 2517 SAC','Luis Ramirez','014567890','ventas2517@friotec.pe','Av. Argentina 1520, Callao',1,'2026-09-17 10:17:43'),(7,'20261582186','Friotec Importaciones 6871 SAC','Luis Ramirez','014567890','ventas6871@friotec.pe','Av. Argentina 1520, Callao',1,'2026-09-17 22:47:44'),(11,'20601234561','Frío Andino Distribuciones SAC','Jorge Salazar','044281530','ventas@frioandino.pe','Av. América Sur 1450, Trujillo',1,'2026-10-20 01:26:50'),(12,'20487654329','Repuestos Refrigeración del Norte EIRL','Lucía Paredes','944512780','pedidos@rrnorte.pe','Jr. Pizarro 812, Trujillo',1,'2026-10-20 01:26:50'),(13,'20556677881','Climatec Perú SAC','Miguel Torres','017154400','contacto@climatec.pe','Av. Argentina 2380, Lima',1,'2026-10-20 01:26:50'),(14,'20481122334','Distribuidora Polar Trujillo SAC','Rosa Chávez','948330125','polar@distpolar.pe','Av. España 1765, Trujillo',1,'2026-10-20 01:26:50');
/*!40000 ALTER TABLE `proveedor` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `rol`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `rol` (
  `id_rol` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(50) NOT NULL,
  `descripcion` varchar(200) DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `fecha_registro` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_rol`),
  UNIQUE KEY `uq_rol_nombre` (`nombre`),
  CONSTRAINT `ck_rol_nombre` CHECK ((char_length(trim(`nombre`)) >= 3))
) ENGINE=InnoDB AUTO_INCREMENT=22 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `rol` WRITE;
/*!40000 ALTER TABLE `rol` DISABLE KEYS */;
INSERT INTO `rol` VALUES (1,'Administrador','Acceso total al sistema',1,'2026-08-27 22:41:02'),(2,'Vendedor','Gestiona ventas, clientes e inventario',1,'2026-08-27 22:41:02'),(3,'Tecnico','Gestiona ordenes de servicio y reparaciones',1,'2026-08-27 22:41:02'),(13,'Supervisor 8081','Descripcion actualizada desde Postman',0,'2026-09-17 10:17:42'),(15,'Supervisor 7248','Descripcion actualizada desde Postman',0,'2026-09-17 22:47:44'),(17,'Supervisor 1788','Supervisa las operaciones de tienda',1,'2026-09-17 22:49:22'),(18,'Supervisor 9364','Supervisa las operaciones de tienda',1,'2026-09-17 22:49:27');
/*!40000 ALTER TABLE `rol` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id_usuario` int NOT NULL AUTO_INCREMENT,
  `id_rol` int NOT NULL,
  `nombres` varchar(100) NOT NULL,
  `apellidos` varchar(100) NOT NULL,
  `dni` char(8) NOT NULL,
  `correo` varchar(120) NOT NULL,
  `clave` varchar(255) NOT NULL,
  `telefono` char(9) DEFAULT NULL,
  `activo` tinyint(1) NOT NULL DEFAULT '1',
  `ultimo_acceso` datetime DEFAULT NULL,
  `fecha_registro` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_usuario`),
  UNIQUE KEY `uq_usuario_correo` (`correo`),
  UNIQUE KEY `uq_usuario_dni` (`dni`),
  KEY `idx_usuario_rol` (`id_rol`),
  CONSTRAINT `fk_usuario_rol` FOREIGN KEY (`id_rol`) REFERENCES `rol` (`id_rol`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `ck_usuario_apellidos` CHECK ((char_length(trim(`apellidos`)) >= 2)),
  CONSTRAINT `ck_usuario_clave` CHECK ((char_length(`clave`) >= 20)),
  CONSTRAINT `ck_usuario_correo` CHECK (regexp_like(`correo`,_utf8mb4'^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$')),
  CONSTRAINT `ck_usuario_dni` CHECK (regexp_like(`dni`,_utf8mb4'^[0-9]{8}$')),
  CONSTRAINT `ck_usuario_nombres` CHECK ((char_length(trim(`nombres`)) >= 2)),
  CONSTRAINT `ck_usuario_telefono` CHECK (((`telefono` is null) or regexp_like(`telefono`,_utf8mb4'^9[0-9]{8}$')))
) ENGINE=InnoDB AUTO_INCREMENT=18 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (5,1,'Administrador','SIVER','00000001','admin@refricentro.pe','$2a$10$EtJqgwXog1xa.33.YArET.rEyEL3c40Dt6tszizcnl8boNgv/GGhu','900000001',1,'2026-11-24 09:10:23','2026-09-17 09:46:23'),(9,1,'Usuario Demo','Prueba Postman','74493240','demo7558@refricentro.pe','$2a$10$YAiDnFhZmP4g.72aRe0rC.Uy1gmcfPTMv1iQ70Tg3Lg5pccfzbtG.','941229939',0,'2026-09-17 10:17:43','2026-09-17 10:17:42'),(10,1,'Usuario Demo','Prueba Postman','76544444','demo1830@refricentro.pe','$2a$10$ni1gmbypao72rcni/onHDe/8hx5m7OMkmagOz7aD1ZT0/yj..tcB.','945962334',0,'2026-09-17 22:47:44','2026-09-17 22:47:44'),(15,3,'Luis Alberto','Gutiérrez Peña','46598712','lgutierrez@refricentro.pe','$2a$10$cjUU/r/5kE9TWivIYoWa4uNVCgiEo7JmQl7c1nGs/1xznVkhH2cym','949778541',1,NULL,'2026-10-20 01:26:51'),(16,3,'Marco Antonio','Rojas Díaz','47125896','mrojas@refricentro.pe','$2a$10$teU8U6GFaIBxX/X8jHo7nuUcDZov.7vaJrllFTLqBlCuTxwibxB16','952114789',1,NULL,'2026-10-20 01:26:51'),(17,2,'Karina','Sánchez Flores','72458913','ksanchez@refricentro.pe','$2a$10$kpctTbitur2qidFFgD8hP.F0te8pDTHBbqXC.twzjrBo62XccvP.a','961247853',1,NULL,'2026-10-20 01:26:51');
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;
DROP TABLE IF EXISTS `venta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `venta` (
  `id_venta` int NOT NULL AUTO_INCREMENT,
  `numero_comprobante` varchar(20) NOT NULL,
  `tipo_comprobante` enum('BOLETA','FACTURA','NOTA_VENTA') NOT NULL DEFAULT 'BOLETA',
  `id_cliente` int NOT NULL,
  `id_usuario` int NOT NULL,
  `fecha_venta` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `metodo_pago` enum('EFECTIVO','TARJETA','YAPE','PLIN','TRANSFERENCIA') NOT NULL DEFAULT 'EFECTIVO',
  `subtotal` decimal(10,2) NOT NULL DEFAULT '0.00',
  `igv` decimal(10,2) NOT NULL DEFAULT '0.00',
  `total` decimal(10,2) NOT NULL DEFAULT '0.00',
  `estado` enum('EMITIDA','ANULADA') NOT NULL DEFAULT 'EMITIDA',
  `observacion` varchar(300) DEFAULT NULL,
  PRIMARY KEY (`id_venta`),
  UNIQUE KEY `uq_venta_comprobante` (`tipo_comprobante`,`numero_comprobante`),
  KEY `idx_venta_cliente` (`id_cliente`),
  KEY `idx_venta_usuario` (`id_usuario`),
  KEY `idx_venta_fecha` (`fecha_venta`),
  CONSTRAINT `fk_venta_cliente` FOREIGN KEY (`id_cliente`) REFERENCES `cliente` (`id_cliente`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `fk_venta_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuario` (`id_usuario`) ON DELETE RESTRICT ON UPDATE CASCADE,
  CONSTRAINT `ck_venta_comprobante` CHECK ((char_length(trim(`numero_comprobante`)) >= 4)),
  CONSTRAINT `ck_venta_igv` CHECK ((`igv` >= 0)),
  CONSTRAINT `ck_venta_subtotal` CHECK ((`subtotal` >= 0)),
  CONSTRAINT `ck_venta_total` CHECK ((`total` >= 0))
) ENGINE=InnoDB AUTO_INCREMENT=34 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

LOCK TABLES `venta` WRITE;
/*!40000 ALTER TABLE `venta` DISABLE KEYS */;
INSERT INTO `venta` VALUES (12,'B001-9681','BOLETA',13,9,'2026-09-17 10:17:43','EFECTIVO',508.47,91.53,600.00,'ANULADA','Venta de mostrador'),(14,'B001-8048','BOLETA',15,10,'2026-09-17 22:47:44','EFECTIVO',508.47,91.53,600.00,'ANULADA','Venta de mostrador'),(19,'B001-000101','BOLETA',27,17,'2026-10-20 01:26:51','EFECTIVO',53.39,9.61,63.00,'EMITIDA',NULL),(20,'F001-000051','FACTURA',30,17,'2026-10-20 01:26:51','TRANSFERENCIA',567.80,102.20,670.00,'EMITIDA','Entrega en el local del cliente'),(21,'B001-000102','BOLETA',28,17,'2026-10-20 01:26:51','YAPE',40.68,7.32,48.00,'EMITIDA',NULL),(22,'F001-000052','FACTURA',31,17,'2026-10-20 01:26:51','TARJETA',495.76,89.24,585.00,'EMITIDA',NULL),(23,'B001-000103','BOLETA',29,17,'2026-10-20 01:26:51','PLIN',54.24,9.76,64.00,'EMITIDA',NULL),(24,'B001-000104','BOLETA',32,17,'2026-10-20 01:26:51','EFECTIVO',1524.58,274.42,1799.00,'EMITIDA','Incluye instalación básica');
/*!40000 ALTER TABLE `venta` ENABLE KEYS */;
UNLOCK TABLES;
/*!50003 SET @saved_cs_client      = @@character_set_client */ ;
/*!50003 SET @saved_cs_results     = @@character_set_results */ ;
/*!50003 SET @saved_col_connection = @@collation_connection */ ;
/*!50003 SET character_set_client  = utf8mb4 */ ;
/*!50003 SET character_set_results = utf8mb4 */ ;
/*!50003 SET collation_connection  = utf8mb4_0900_ai_ci */ ;
/*!50003 SET @saved_sql_mode       = @@sql_mode */ ;
/*!50003 SET sql_mode              = 'ONLY_FULL_GROUP_BY,STRICT_TRANS_TABLES,NO_ZERO_IN_DATE,NO_ZERO_DATE,ERROR_FOR_DIVISION_BY_ZERO,NO_ENGINE_SUBSTITUTION' */ ;
DELIMITER ;;
/*!50003 CREATE*/ /*!50017 */ /*!50003 TRIGGER `trg_venta_before_insert` BEFORE INSERT ON `venta` FOR EACH ROW BEGIN
    DECLARE v_tipo_doc VARCHAR(5);

    SELECT tipo_documento INTO v_tipo_doc
      FROM cliente WHERE id_cliente = NEW.id_cliente;

    IF NEW.tipo_comprobante = 'FACTURA' AND v_tipo_doc <> 'RUC' THEN
        SIGNAL SQLSTATE '45000'
        SET MESSAGE_TEXT = 'Solo se puede emitir factura a clientes con RUC.';
    END IF;
END */;;
DELIMITER ;
/*!50003 SET sql_mode              = @saved_sql_mode */ ;
/*!50003 SET character_set_client  = @saved_cs_client */ ;
/*!50003 SET character_set_results = @saved_cs_results */ ;
/*!50003 SET collation_connection  = @saved_col_connection */ ;
DROP TABLE IF EXISTS `vw_stock_bajo`;
/*!50001 DROP VIEW IF EXISTS `vw_stock_bajo`*/;
SET @saved_cs_client     = @@character_set_client;
/*!50503 SET character_set_client = utf8mb4 */;
/*!50001 CREATE VIEW `vw_stock_bajo` AS SELECT 
 1 AS `id_producto`,
 1 AS `codigo`,
 1 AS `nombre`,
 1 AS `categoria`,
 1 AS `stock`,
 1 AS `stock_minimo`*/;
SET character_set_client = @saved_cs_client;
/*!50001 DROP VIEW IF EXISTS `vw_stock_bajo`*/;
/*!50001 SET @saved_cs_client          = @@character_set_client */;
/*!50001 SET @saved_cs_results         = @@character_set_results */;
/*!50001 SET @saved_col_connection     = @@collation_connection */;
/*!50001 SET character_set_client      = utf8mb4 */;
/*!50001 SET character_set_results     = utf8mb4 */;
/*!50001 SET collation_connection      = utf8mb4_0900_ai_ci */;
/*!50001 CREATE ALGORITHM=UNDEFINED */
/*!50013  SQL SECURITY DEFINER */
/*!50001 VIEW `vw_stock_bajo` AS select `p`.`id_producto` AS `id_producto`,`p`.`codigo` AS `codigo`,`p`.`nombre` AS `nombre`,`c`.`nombre` AS `categoria`,`p`.`stock` AS `stock`,`p`.`stock_minimo` AS `stock_minimo` from (`producto` `p` join `categoria` `c` on((`c`.`id_categoria` = `p`.`id_categoria`))) where ((`p`.`activo` = true) and (`p`.`stock` <= `p`.`stock_minimo`)) */;
/*!50001 SET character_set_client      = @saved_cs_client */;
/*!50001 SET character_set_results     = @saved_cs_results */;
/*!50001 SET collation_connection      = @saved_col_connection */;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

