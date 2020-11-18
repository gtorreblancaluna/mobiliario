
-- MySQL dump 10.13  Distrib 5.7.24, for macos10.14 (x86_64)
--
-- Host: localhost    Database: mobiliario
-- ------------------------------------------------------
-- Server version	5.7.24

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `abonos`
--

DROP TABLE IF EXISTS `abonos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `abonos` (
  `id_abonos` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `id_renta` int(11) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  `fecha` varchar(45) NOT NULL,
  `abono` float NOT NULL,
  `comentario` varchar(45) DEFAULT NULL,
  `id_tipo_abono` int(11) NOT NULL DEFAULT '1',
  `fecha_pago` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id_abonos`),
  KEY `FK_id_renta` (`id_renta`),
  KEY `FK_id_usuario` (`id_usuario`),
  KEY `fk_id_tipo_abono` (`id_tipo_abono`),
  CONSTRAINT `FK_id_renta` FOREIGN KEY (`id_renta`) REFERENCES `renta` (`id_renta`),
  CONSTRAINT `FK_id_usuario` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuarios`),
  CONSTRAINT `abonos_ibfk_1` FOREIGN KEY (`id_tipo_abono`) REFERENCES `tipo_abono` (`id_tipo_abono`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

DROP TABLE IF EXISTS `email`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE IF NOT EXISTS `email` (
  `id` INT NOT NULL AUTO_INCREMENT,
  `cuenta_correo` VARCHAR(45) NOT NULL,
  `contrasenia` VARCHAR(45) NOT NULL,
  `servidor` VARCHAR(45) NOT NULL,
  `puerto` VARCHAR(45) NOT NULL,
  `utiliza_conexion_TLS` VARCHAR(45) NOT NULL,
  `utiliza_autenticacion` VARCHAR(45) NOT NULL,
  `gmail` VARCHAR(45) NOT NULL,
  `hotmail` VARCHAR(45) NOT NULL,
  `personalizada` VARCHAR(45) NOT NULL,
  `creado` TIMESTAMP NULL,
  `actualizado` TIMESTAMP NULL,
  PRIMARY KEY (`id`))
ENGINE=InnoDB AUTO_INCREMENT=104 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `abonos`
--

LOCK TABLES `abonos` WRITE;
/*!40000 ALTER TABLE `abonos` DISABLE KEYS */;
INSERT INTO `abonos` VALUES (1,2,1,'23/03/2015',100,'',1,NULL),(2,8,1,'25/03/2015',100,'',1,NULL),(3,9,1,'25/03/2015',500,'',1,NULL),(4,10,1,'25/03/2015',500,'',1,NULL),(5,13,1,'26/03/2015',1000,'',1,NULL),(6,14,1,'26/03/2015',100,'',1,NULL),(7,17,1,'26/03/2015',3000,'',1,NULL),(8,18,1,'04/04/2015',700,'',1,NULL),(9,19,1,'04/04/2015',500,'',1,NULL),(10,20,1,'04/04/2015',500,'',1,NULL),(11,25,1,'08/04/2015',1000,'',1,NULL),(12,29,1,'09/04/2015',1000,'',1,NULL),(13,31,1,'10/04/2015',170,'',1,NULL),(14,37,1,'11/04/2015',350,'',1,NULL),(15,39,1,'13/04/2015',544,'',1,NULL),(16,42,1,'13/04/2015',305,'',1,NULL),(17,50,1,'16/04/2015',270,'',1,NULL),(18,61,1,'20/04/2015',3200,'',1,NULL),(19,68,1,'21/04/2015',16,'',1,NULL),(20,70,1,'23/04/2015',200,'',1,NULL),(21,71,1,'23/04/2015',1000,'',1,NULL),(22,73,1,'25/04/2015',100,'',1,NULL),(23,76,1,'25/04/2015',3000,'',1,NULL),(24,85,1,'28/04/2015',200,'',1,NULL),(25,121,1,'16/05/2015',1000,'',1,NULL),(26,127,1,'19/05/2015',304,'',1,NULL),(27,128,1,'19/05/2015',304,'',1,NULL),(28,129,1,'19/05/2015',304,'',1,NULL),(29,134,1,'19/05/2015',588,'',1,NULL),(30,139,1,'27/04/2018',66.5,'',1,NULL),(31,140,1,'02/05/2018',100,'',1,NULL),(32,141,1,'12/11/2018',1000,'',1,NULL),(33,142,1,'12/11/2018',10,'',1,NULL),(34,143,1,'12/11/2018',10,'',1,NULL),(35,144,1,'12/11/2018',100,'',1,NULL),(36,145,1,'12/11/2018',100,'',1,NULL),(37,146,1,'13/11/2018',500,'',1,NULL),(38,147,1,'13/11/2018',100,'',1,NULL),(39,148,1,'14/11/2018',10,'',1,NULL),(40,149,1,'14/11/2018',100,'',1,NULL),(41,150,1,'14/11/2018',10,'',1,NULL),(42,151,1,'14/11/2018',10,'',1,NULL),(43,152,1,'14/11/2018',50,'',1,NULL),(44,153,1,'14/11/2018',10,'',1,NULL),(45,154,1,'15/11/2018',500,'',1,NULL),(46,155,1,'20/11/2018',100,'',1,NULL),(47,156,1,'21/11/2018',10,'',1,NULL),(48,157,1,'21/11/2018',50,'',1,NULL),(49,158,1,'21/11/2018',10,'',1,NULL),(50,159,1,'26/11/2018',100,'',1,NULL),(51,160,1,'29/11/2018',100,'',1,NULL),(52,161,1,'29/11/2018',1000,'',1,NULL),(53,162,1,'29/11/2018',200,'',1,NULL),(54,163,1,'30/11/2018',50,'',1,NULL),(55,164,1,'30/11/2018',10,'',1,NULL),(56,165,1,'4/12/2018',10,'',1,NULL),(57,169,1,'13/12/2018',10,'',1,NULL),(58,170,1,'08/01/2019',500,'',1,NULL),(59,171,1,'14/01/2019',10,'',1,NULL),(60,172,1,'14/01/2019',10,'',1,NULL),(61,173,1,'14/01/2019',10,'',1,NULL),(62,174,1,'14/01/2019',10,'',1,NULL),(63,178,1,'22/01/2019',10500,'COMENTARIO',1,NULL),(64,179,1,'22/01/2019',590,'',1,NULL),(65,179,1,'22/01/2019',100,'',1,NULL),(66,179,1,'22/01/2019',50,'Cometnario',1,NULL),(67,180,1,'22/01/2019',150000,'',1,NULL),(68,181,1,'22/01/2019',10,'',1,NULL),(69,182,1,'23/01/2019',10,'',1,NULL),(70,183,1,'24/01/2019',500,'',1,NULL),(71,184,1,'25/01/2019',100,'',1,NULL),(72,185,1,'25/01/2019',100,'',1,NULL),(73,186,1,'25/01/2019',100,'',1,NULL),(74,187,1,'28/01/2019',200,'',1,NULL),(75,187,1,'28/01/2019',500,NULL,1,NULL),(76,188,1,'28/01/2019',1000,'',1,NULL),(77,188,1,'28/01/2019',100,NULL,1,NULL),(78,189,1,'29/01/2019',100,'',1,NULL),(79,189,1,'29/01/2019',100,'',1,NULL),(80,190,1,'29/01/2019',1000,'',1,NULL),(81,191,1,'29/01/2019',50,'',1,NULL),(82,192,1,'29/01/2019',100,'',1,NULL),(83,197,1,'06/02/2019',500,'',1,NULL),(84,199,1,'06/02/2019',10,'',1,NULL),(85,199,1,'06/02/2019',1500,'',1,NULL),(86,200,1,'07/02/2019',10,NULL,1,NULL),(87,200,1,'07/02/2019',10,NULL,1,NULL),(88,208,1,'08/02/2019',1000,'',1,NULL),(89,209,1,'08/02/2019',200,'',1,NULL),(90,210,1,'14/02/2019',300,'',2,NULL),(91,210,1,'15/02/2019',200,NULL,1,NULL),(92,210,1,'15/02/2019',500,'desde consulta1',1,NULL),(93,210,1,'15/02/2019',10,'',1,NULL),(96,211,1,'25/02/2019',600,'fecha pago',4,'03/02/2019'),(97,212,1,'25/02/2019',900,'Nada',3,'28/02/2019'),(98,185,1,'06/03/2019',5000,'nada',4,'01/03/2019'),(99,213,1,'22/03/2019',60,'nada1',2,''),(100,213,1,'22/03/2019',20,'nada',1,''),(101,203,1,'22/03/2019',11015,'',1,''),(102,214,1,'19/05/2019',500,'nada',5,'19/05/2019'),(103,215,1,'15/08/2019',1000,'primero comentario',1,'15/08/2019');
/*!40000 ALTER TABLE `abonos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `articulo`
--

DROP TABLE IF EXISTS `articulo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `articulo` (
  `id_articulo` int(11) NOT NULL AUTO_INCREMENT,
  `id_categoria` int(11) NOT NULL,
  `id_usuario` int(11) NOT NULL,
  `cantidad` float DEFAULT NULL,
  `descripcion` varchar(250) DEFAULT NULL,
  `id_color` int(11) DEFAULT NULL,
  `fecha_ingreso` varchar(45) DEFAULT NULL,
  `precio_compra` float DEFAULT NULL,
  `precio_renta` float DEFAULT NULL,
  `activo` varchar(5) DEFAULT NULL,
  `stock` float DEFAULT NULL,
  `codigo` varchar(45) DEFAULT NULL,
  `en_renta` float DEFAULT NULL,
  `fecha_ultima_modificacion` timestamp NULL DEFAULT NULL,
  PRIMARY KEY (`id_articulo`),
  KEY `fk_articulo_usuarios1_idx` (`id_usuario`),
  KEY `fk_articulo_cateogria1_idx` (`id_categoria`),
  KEY `fk_articulo_color1_idx` (`id_color`),
  CONSTRAINT `fk_articulo_cateogria1` FOREIGN KEY (`id_categoria`) REFERENCES `categoria` (`id_categoria`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_articulo_color1` FOREIGN KEY (`id_color`) REFERENCES `color` (`id_color`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_articulo_usuarios1` FOREIGN KEY (`id_usuario`) REFERENCES `usuarios` (`id_usuarios`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=86 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `articulo`
--

LOCK TABLES `articulo` WRITE;
/*!40000 ALTER TABLE `articulo` DISABLE KEYS */;
INSERT INTO `articulo` VALUES (1,1,1,62,'Mesa Redonda',2,'23/03/2015',100,18,'1',NULL,NULL,10,NULL),(2,1,1,870,'Silla plegable',1,'23/03/2015',100,4,'1',NULL,'987855',250,'2019-03-26 15:37:05'),(3,2,1,1270,'Fundas',2,'23/03/2015',100,7,'1',NULL,NULL,10,NULL),(4,3,1,200,'Platos trinche',2,'23/03/2015',100,3,'1',NULL,NULL,NULL,NULL),(5,3,1,200,'Plato sopero',2,'23/03/2015',100,3,'1',NULL,NULL,10,NULL),(6,3,1,200,'Cuchara',2,'23/03/2015',100,1.5,'1',NULL,NULL,NULL,NULL),(7,3,1,200,'Tenedor',2,'23/03/2015',100,1.5,'1',NULL,NULL,NULL,NULL),(8,3,1,200,'Cuchillos',2,'23/03/2015',100,1.5,'1',NULL,NULL,NULL,NULL),(9,1,1,86,'Mesa tablon',2,'23/03/2015',100,18,'1',NULL,NULL,0,NULL),(10,2,1,180,'Mantel rectangular',2,'23/03/2015',100,18,'1',NULL,NULL,NULL,NULL),(11,2,1,186,'Cubre mantel',4,'23/03/2015',100,8,'1',NULL,NULL,NULL,NULL),(12,1,1,200,'Moño',4,'23/03/2015',100,2,'1',NULL,NULL,NULL,NULL),(13,2,1,500,'mantel redondo',2,'24/03/2015',100,18,'1',NULL,NULL,NULL,NULL),(14,1,1,130,'silla infantil',2,'24/03/2015',100,3,'1',NULL,NULL,NULL,NULL),(15,1,1,18,'sombriyas',5,'24/03/2015',100,130,'1',NULL,NULL,NULL,NULL),(16,4,1,4,'lona de 5x10',1,'24/03/2015',100,800,'1',NULL,'9999',-1,NULL),(17,3,1,300,'cuchara cafetera',1,'24/03/2015',100,1.5,'1',NULL,NULL,10,NULL),(18,3,1,400,'vaso',1,'24/03/2015',100,2,'1',NULL,NULL,10,NULL),(19,3,1,350,'plato arrocero',1,'24/03/2015',100,2,'1',NULL,NULL,NULL,NULL),(20,3,1,90,'taza',1,'24/03/2015',100,4,'1',NULL,'',10,NULL),(21,3,1,80,'plato cafetero',1,'24/03/2015',100,3,'1',NULL,NULL,10,NULL),(22,2,1,5,'mantel infantils',2,'24/03/2015',100,15,'1',NULL,'0005',NULL,NULL),(23,1,1,6,'tablon infantil',2,'24/03/2015',100,15,'1',NULL,NULL,NULL,NULL),(24,2,1,401,'servilleta ',2,'24/03/2015',100,3,'1',NULL,NULL,NULL,NULL),(25,2,1,383,'servilleta',6,'24/03/2015',100,3,'1',NULL,NULL,NULL,NULL),(26,2,1,47,'CAMINOS',6,'24/03/2015',100,30,'1',NULL,NULL,NULL,NULL),(27,2,1,29,'CAMINOS',7,'24/03/2015',100,30,'1',NULL,NULL,NULL,NULL),(28,2,1,19,'CAMINOS',5,'24/03/2015',100,30,'0',NULL,'M008',0,NULL),(29,2,1,7,'CAMINOS',8,'24/03/2015',100,30,'1',NULL,NULL,NULL,NULL),(30,2,1,27,'CAMINOS',9,'24/03/2015',100,27,'1',NULL,NULL,NULL,NULL),(31,2,1,10,'CAMINOS',10,'24/03/2015',100,30,'1',NULL,NULL,NULL,NULL),(32,2,1,26,'CUBRE',11,'25/03/2015',100,8,'1',NULL,NULL,NULL,NULL),(33,2,1,50,'CUBREMANTEL',7,'26/03/2015',100,8,'1',NULL,NULL,NULL,NULL),(34,2,1,80,'CUBRE GRANDE',12,'27/03/2015',100,12,'1',NULL,NULL,NULL,NULL),(35,2,1,76,'CUBRE GRANDE',13,'27/03/2015',100,12,'1',NULL,NULL,NULL,NULL),(36,2,1,105,'CUBRE GRANDE',14,'27/03/2015',100,12,'1',NULL,NULL,NULL,NULL),(37,2,1,34,'CUBRE GRANDE',15,'27/03/2015',100,12,'1',NULL,NULL,NULL,NULL),(38,2,1,40,'CUBRE GRANDE',16,'27/03/2015',100,12,'1',NULL,NULL,NULL,NULL),(39,2,1,27,'CUBRE GRANDE',17,'27/03/2015',100,12,'1',NULL,NULL,NULL,NULL),(40,2,1,9,'CUBRE GRANDE',18,'27/03/2015',100,12,'1',NULL,NULL,NULL,NULL),(41,2,1,16,'CUBRE GRANDE',4,'27/03/2015',100,12,'1',NULL,NULL,NULL,NULL),(42,2,1,44,'CUBRE GRANDE',10,'27/03/2015',100,12,'1',NULL,NULL,NULL,NULL),(43,2,1,14,'CUBRE GRANDE',11,'27/03/2015',100,12,'1',NULL,NULL,NULL,NULL),(44,2,1,9,'MANTEL DE COLOR',9,'31/03/2015',0,25,'1',NULL,NULL,NULL,NULL),(45,2,1,26,'MANTEL DE COLOR',12,'31/03/2015',0,25,'1',NULL,NULL,NULL,NULL),(46,2,1,28,'MANTEL DE COLOR',6,'31/03/2015',0,25,'1',NULL,NULL,NULL,NULL),(47,2,1,60,'MANTEL DE COLOR',23,'31/03/2015',0,25,'1',NULL,NULL,NULL,NULL),(48,2,1,17,'MANTEL DE COLOR',20,'31/03/2015',0,25,'1',NULL,NULL,NULL,NULL),(49,2,1,16,'MANTEL DE COLOR',18,'31/03/2015',0,25,'1',NULL,NULL,NULL,NULL),(50,2,1,27,'MANTEL DE COLOR',7,'31/03/2015',0,25,'1',NULL,NULL,NULL,NULL),(51,2,1,14,'MANTEL DE COLOR',17,'31/03/2015',0,25,'1',NULL,NULL,NULL,NULL),(52,2,1,47,'MANTEL DE COLOR',14,'31/03/2015',0,25,'1',NULL,NULL,NULL,NULL),(53,2,1,8,'MANTEL DE COLOR',10,'31/03/2015',0,25,'1',NULL,NULL,NULL,NULL),(54,2,1,30,'MANTEL DE COLOR',24,'31/03/2015',0,25,'1',NULL,NULL,NULL,NULL),(55,2,1,40,'MANTEL DE COLOR',25,'31/03/2015',0,25,'1',NULL,NULL,NULL,NULL),(56,2,1,23,'MANTEL DE COLOR',11,'31/03/2015',0,25,'1',NULL,NULL,NULL,NULL),(57,2,1,11,'MANTEL DE COLOR',22,'31/03/2015',0,24,'1',NULL,NULL,NULL,NULL),(58,2,1,19,'MANTEL DE COLOR',21,'31/03/2015',0,25,'1',NULL,NULL,NULL,NULL),(59,2,1,2,'MANTEL DE COLOR DE TABLON',18,'31/03/2015',0,25,'1',NULL,NULL,NULL,NULL),(60,2,1,2,'MANTEL DE MESA IMPERIAL',2,'31/03/2015',0,40,'1',NULL,NULL,NULL,NULL),(61,2,1,18,'MANTEL DESHILADO',2,'31/03/2015',0,70,'1',NULL,NULL,NULL,NULL),(62,2,1,55,'MANTEL DE DOMINO',2,'31/03/2015',0,70,'1',NULL,NULL,NULL,NULL),(63,2,1,23,'MANTEL DE PALITOS',2,'31/03/2015',0,70,'1',NULL,NULL,NULL,NULL),(64,2,1,42,'MANTEL DE ORGANZA',2,'31/03/2015',0,42,'1',NULL,NULL,NULL,NULL),(65,2,1,16,'CUBRE GRANDE',21,'04/04/2015',0,10,'1',NULL,NULL,NULL,NULL),(66,1,1,60,'SALA LAUNCHS',2,'04/04/2015',0,90,'1',NULL,NULL,NULL,NULL),(67,2,1,20,'CUBRE CHICO ',21,'08/04/2015',0,10,'1',NULL,NULL,NULL,NULL),(68,5,1,3,'CARPAS DE 5X5',1,'08/04/2015',0,1000,'1',NULL,NULL,NULL,NULL),(69,3,1,150,'COPA  DE FLAUTA',1,'13/04/2015',0,4,'0',NULL,NULL,NULL,NULL),(70,3,1,300,'TENEDOR',2,'13/04/2015',0,1.5,'1',NULL,NULL,NULL,NULL),(71,3,1,100,'PLATO VASE',1,'13/04/2015',0,15,'1',NULL,NULL,0,NULL),(72,2,1,100,'MOÑO ',12,'13/04/2015',0,2,'1',NULL,NULL,NULL,NULL),(73,1,1,10,'catsup',1,'13/11/2018',500,10,'0',NULL,'MM007',NULL,NULL),(82,3,1,10,'plato grande',1,'21/03/2019',500,10,'1',NULL,'87865',NULL,NULL),(83,3,1,0,'plato grande',1,'21/03/2019',0,0,'0',NULL,'87865',NULL,NULL),(84,3,1,0,'plato grandissisisimo',1,'21/03/2019',0,0,'0',NULL,'2345',NULL,NULL),(85,7,1,10,'Vaso cubero',27,'15/08/2019',100,5,'1',NULL,'00000',NULL,NULL);
/*!40000 ALTER TABLE `articulo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `asigna_categoria`
--

DROP TABLE IF EXISTS `asigna_categoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `asigna_categoria` (
  `id_asigna_categoria` int(11) NOT NULL AUTO_INCREMENT,
  `id_usuarios` int(11) NOT NULL,
  `id_categoria` int(11) NOT NULL,
  `fecha_alta` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id_asigna_categoria`),
  KEY `FK_id_usuarios` (`id_usuarios`),
  KEY `FK_id_categoria` (`id_categoria`),
  CONSTRAINT `FK_id_categoria` FOREIGN KEY (`id_categoria`) REFERENCES `categoria` (`id_categoria`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `FK_id_usuarios` FOREIGN KEY (`id_usuarios`) REFERENCES `usuarios` (`id_usuarios`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `asigna_categoria`
--

LOCK TABLES `asigna_categoria` WRITE;
/*!40000 ALTER TABLE `asigna_categoria` DISABLE KEYS */;
INSERT INTO `asigna_categoria` VALUES (2,7,2,'2019-01-22 16:43:23'),(3,10,3,'2019-01-22 16:44:16'),(4,10,4,'2019-01-22 16:44:23'),(8,8,1,'2019-01-28 17:35:38'),(9,8,2,'2019-01-28 17:35:45'),(10,1,3,'2019-01-28 17:38:12');
/*!40000 ALTER TABLE `asigna_categoria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categoria`
--

DROP TABLE IF EXISTS `categoria`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `categoria` (
  `id_categoria` int(11) NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id_categoria`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categoria`
--

LOCK TABLES `categoria` WRITE;
/*!40000 ALTER TABLE `categoria` DISABLE KEYS */;
INSERT INTO `categoria` VALUES (1,'Mobiliario'),(2,'Manteleria'),(3,'Loza'),(4,'lonas'),(5,'CARPAS'),(6,'TEMPLETES'),(7,'Cristaleria');
/*!40000 ALTER TABLE `categoria` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `categoria_contabilidad`
--

DROP TABLE IF EXISTS `categoria_contabilidad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `categoria_contabilidad` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(150) DEFAULT NULL,
  `fg_activo` enum('1','0') NOT NULL DEFAULT '1',
  `fecha_registro` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `categoria_contabilidad`
--

LOCK TABLES `categoria_contabilidad` WRITE;
/*!40000 ALTER TABLE `categoria_contabilidad` DISABLE KEYS */;
INSERT INTO `categoria_contabilidad` VALUES (1,'operativos','1','2019-05-21 01:11:00'),(2,'administrativos','1','2019-05-21 01:11:00');
/*!40000 ALTER TABLE `categoria_contabilidad` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `clientes`
--

DROP TABLE IF EXISTS `clientes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `clientes` (
  `id_clientes` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(45) DEFAULT NULL,
  `apellidos` varchar(45) DEFAULT NULL,
  `apodo` varchar(45) DEFAULT NULL,
  `tel_movil` varchar(45) DEFAULT NULL,
  `tel_fijo` varchar(45) DEFAULT NULL,
  `email` varchar(45) DEFAULT NULL,
  `direccion` varchar(500) DEFAULT NULL,
  `localidad` varchar(45) DEFAULT NULL,
  `rfc` varchar(45) DEFAULT NULL,
  `activo` varchar(5) DEFAULT NULL,
  PRIMARY KEY (`id_clientes`)
) ENGINE=InnoDB AUTO_INCREMENT=82 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `clientes`
--

LOCK TABLES `clientes` WRITE;
/*!40000 ALTER TABLE `clientes` DISABLE KEYS */;
INSERT INTO `clientes` VALUES (1,'Salon','Cielo Azul','','7471444504','','','','Chilpancingo','','1'),(2,'paula','valtasar','','','','','col.acolsualca homiltemic','chilpancingo','','1'),(3,'salon ','amaran','','7471202537','','','','chilpancingo','','1'),(4,'salon','driboli','','7473991356','','','','','','1'),(5,'antonio','gonzalez','','','4715577','','josefa ortiz de dominguez #9col.progreso','chilpancingo','','1'),(6,'martha veatris','lopez','','7471293558','','','calle:diamante cuñica#10col.diamante','chilpancingo','','1'),(7,'MARINA','LEON HERNANDEZ','','7471409322','','','AYUTLA #14','CHILPANCINGO','','1'),(8,'SALON','CUICALI','','','','','BACHICHERES','CHILPANCINGO','','1'),(9,'MICHEL','MALDA','','4716070','','','MADERO Y MORELOS CALLE:TEOFILO OLEA Y LEIVA','CHILPANCINGO','','1'),(10,'TOMAS ','ANTONIO','','7471014527','','','FRAC.REAL DEL VALLE RUMBO A PETAQUIÑAS','','','1'),(11,'ORLANDO','CORDENARES','','7471228604','','','AV.GRO ENTRE K LAS Y FARMACIAS CIMILARES','','','1'),(12,'OSCAR','PACHECO','','7471443367','','','','EN LA CANCHA DEL CENTRO POR LA COMISARIA','','1'),(13,'GABRIELA','DIAZ','','7471296135','','','COL.PRD PARTE ALTA','','','1'),(14,'SALON','MARINA','','','','','COL.GALEANA','','','1'),(15,'LIANA','TREJO','','7471368384','','','','','','1'),(16,'ARTURO','MANUEL','','7471101357','','','AYUTLA','CHILPANCINGO','','1'),(17,'SARAI','MONCERRAT FLORES','','7475932801','','','HUERTA LOS VEGA','CHILPANCINGO','','1'),(18,'PAULA','ADRIANA LEIVA','','7471198484','','','BARRIO DE LA FUENTE POR LA AMERICANA','','','1'),(19,'ELIA','AMERICA','','7471295271','','','CALLE ALTAMIRANO#75','','','1'),(20,'JUVENTINO','ARCOS','','','','','','CHILPANCINGO','','1'),(21,'ESTHER','HERNANDEZ','','4945235','','','COL,UNIDAD DE PORTIVA CALLE;PRINCIPAL','','','1'),(22,'BEATRIZ','SANDOVAL','','4724927','','','COL,SAN JUAN CALLE:CANCUN#2','','','1'),(23,'ISAC','MENDOZA','','74750938383','','','SAN RAFAEL NORTE','CHILPANCINGO','','1'),(24,'ezequiel','vazquez','','7471242538','','','calle:francisco gonzalez col.obrera','chilpancingo','','1'),(25,'marisol','bello','','7471028711','','','guerrero200','chilpancingo','','1'),(26,'SERGIO','MOCTEZUMA','','7471213998','','','COL.EL POLVORIN MAZ.2LT.6','CHILPANCINGO','','1'),(27,'salon','villas las flores','','7471076392','','','','chilpancingo','','1'),(28,'gabriela','vazquez','','7471483048','','','salon rivoli','chilpancingo','','1'),(29,'SALON','SEÑORIAL','','12041','','','','CHILPANCINGO','','1'),(30,'BETO','MORLET','','7471028918','','','','CHILPANCINGO','','1'),(31,'salon','real jardin','','7471186335','','','zumpango','chilpancingo','','1'),(32,'FRUMENCIO','MARTINEZ','','7471245273','','','CALLE:18 DE MARZO N#30 SALON EMA','CHILPANCINGO','','1'),(33,'MIRIAM','ASAVAEL','','7474592219','','','EN EL EDIFICIO DE LA CONSTRUCCION','CHILPANCINGO','','1'),(34,'EDGAR','CASTIÑO','','7471300845','','','SALON LOS PINOS','CHILPANCINGO','','1'),(35,'FERNANDO','LAZO','','7471258494','','','SUPER ISSTE.POR LA RUFO FIGUEROA','CHILPANCINGO','','1'),(36,'escuela','damian carmona','','7471331765','','','ADENTRO DEL MILITAR','','','1'),(37,'JOSE LUIS','RAMOS','','74786284','','','','CHILPANCINGO','','1'),(38,'EFRAIN','CARBAJAL','','7471312930','','','SALON REAL CAMPESTRE','CHILPANCINGO','','1'),(39,'SERAFIN','CLINICA SAN JOSE','','','4725977','','AVASOLO #115 COL.RUFO FIGUEROA','','','1'),(40,'SILVIA','ROMAN','','7474981271','','','28 DE FEBRERO COL.LUCIAR ARCORCER #12','CHILPANCINGO','','1'),(41,'guadalupe','martinez','','','4725636','','rio de janeiro#1col.gro','chilpancingo','','1'),(42,'kala','vazquez','','7471191687','','','avasolo 123col.ruffo figueroa','chilpancingo','','1'),(43,'miguel','angel','','7471271509','','','cuahtemoc n#38','zumpango','','1'),(44,'capiña','de jesus el buen pastor','','7471255699','','','col.isasaga rio.amacusac','','','1'),(45,'PRI','PRI','','','','','EN EL PRI','CHILPANCINGO','','1'),(46,'SALON','HUERTA LOS VEGA','','','','','','CHILPANCINGO','','1'),(47,'SALON','HACIENDA TEPANGO','','7471487647','','','','','','1'),(48,'LIVERPOOL','.','','','4949833','SF@LIBERPOOL.COM.MX','GALERIAS','CHILPANCINGO','','1'),(49,'ELIZABHET','MARTINEZ','','7471123097','','','UNIDAD ABITACIONAL HUMZ COL.PROGRESO','CHILPANCINGO','','1'),(50,'JOSE','LUIS','','7471454158','','','CALLE:PRIVADA LA FUENTE#2COLJURISTAS','CHILPANCINGO','','1'),(51,'GUSTAVO','RUIS','','7471426295','','','CAMPO MILITAR','CHILPANCINGO','','1'),(52,'MILITAR','BATAÑON','','7471016038','','','EN EL BATAÑON','chilpancingo','','1'),(53,'maria','de los angeles','','4781031','','','sumpango alberca kiribiqui','chilpancingo','','1'),(54,'tecnologico','instituto','','7471002717','','','canchas jerardo','chilpancingo','','1'),(55,'VANESA','ASFAY','','7471301625','','','SECUANDARIA ASFAY','CHILPANCINGO','','1'),(56,'GUADALUPE ','MARTIN','','7474991441','','','CALLE:RIO DE JANEIRO','CHILPANCINGO','','1'),(57,'DAVID ','SANDOVAL','','7471409106','','','AVENIDA OLIMPIA MZ.8LT.2','CHILPANCINGO','','1'),(58,'MIRIAM','VARAGAN','','7471063186','','','SALON REGIS.POR LA COMERCIAL HACIA ARRIBA','CHILPANCINGO','','1'),(59,'JOSE LUIS','GOMEZ','','8341693966','','','EXPLANADA DEL PRI','CHILPANCINGO','','1'),(60,'pozoleria','.','','','','','','chilpancingo','','1'),(61,'angela','ZANTOS','','4726380','','','SALON DE LOS ELECTRISISTAS','CHILPANCINGO','','1'),(62,'NORMA','LA DEL POLLO','','7471506396','4717390','','POR LA NORMAL','CHILPANCINGO','','1'),(63,'JUAN','GIMENEZ','','7471014121','','','AVENIDA DEL SOL SIN NUMERO','CHILPANCINGO','','1'),(64,'SALON','DIAMANTE','','7471103531','','','','CHILPANCINGO','','1'),(65,'claudia','corona hernandez','','7471226909','','','alameda','chilpancingo','','1'),(66,'SNTE','.','.','..','.','.','.','CHILPANCINGO','','1'),(67,'HERICH','VAZQUEZ','','7475297475','','','EN SORIANA','CHILPANCINGO','','1'),(68,'NICOLAS','ESPIRITUD','','7471052025','','','ESTACIONAMIENTO DE SORIANA','CHILPANCINHO','','1'),(69,'GREGORIO','ARCOS','','7471244691','','','PROLONGACION PLAZA LA CAIDE','CHILPANCINGO','','1'),(70,'MIRIAM','ROMERO','','7471221964','','','CALLE;ANDADOR FRUTALES,JARDIN DEL SUR','CHILPANCINGO','','1'),(71,'SECRETARIA','DEL MEDIO AMBIENTE','','5545336114','','','','CHILPANCINGO','','1'),(72,'RUBI','GARCIA','','7471288898','','','COL.AMATE CALLE:ACASIA#12','CHILPANCINGO','','1'),(73,'SALON','ADQUETZALI','','7471048906','','','COL.20 DE NOVIEMBRE','CHILPANCINGO','','1'),(74,'ARTURO','ARCOS','','7471213174','','','PARQUE DE LA RUFO FIGUEROA.EN EL MODULO','CHILPANCINGO','','1'),(75,'POLET','.','','747124009','','','RADIO UNIVERCIDAD','','','1'),(76,'HOTEL','PARADOR DEL MARQUEZ','','2224268856','','','','','','1'),(77,'EDUARDO','CHAVEZ','','7471250878','','','COL,MORELOS CALLE:NICOLAS BRAVO#1','CHILPANCINGO','','1'),(78,'MOZO',',','',',','','','EN LA CABAÑA','CHILPANCINGO','','1'),(79,'Elvíra','Castro nuñezó','','','','nada@email.com','','','','1'),(80,'Alejandra','Alejandra García Gutierrez','','','','email@email.com','','','','1'),(81,'Juan','Perez','Juan','555555555','5555555555','juan@email.com','','','','1');
/*!40000 ALTER TABLE `clientes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `color`
--

DROP TABLE IF EXISTS `color`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `color` (
  `id_color` int(11) NOT NULL AUTO_INCREMENT,
  `color` varchar(45) DEFAULT NULL,
  `tono` varchar(45) DEFAULT NULL,
  `comentario` varchar(75) DEFAULT NULL,
  PRIMARY KEY (`id_color`)
) ENGINE=InnoDB AUTO_INCREMENT=28 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `color`
--

LOCK TABLES `color` WRITE;
/*!40000 ALTER TABLE `color` DISABLE KEYS */;
INSERT INTO `color` VALUES (1,'Negro',NULL,NULL),(2,'Blanco',NULL,NULL),(3,'Cafe',NULL,NULL),(4,'Rosa pastel',NULL,NULL),(5,'beys',NULL,NULL),(6,'cafe',NULL,NULL),(7,'ROJO',NULL,NULL),(8,'VERDE AGUA',NULL,NULL),(9,'PLATA',NULL,NULL),(10,'LILA',NULL,NULL),(11,'MORADO',NULL,NULL),(12,'AMARILLO',NULL,NULL),(13,'NARANJA',NULL,NULL),(14,'VERDE LIMON',NULL,NULL),(15,'JADE',NULL,NULL),(16,'AZUL REY',NULL,NULL),(17,'AZUL TURQUEZA',NULL,NULL),(18,'VINO',NULL,NULL),(19,'AZUL MRINO',NULL,NULL),(20,'FIUSHA',NULL,NULL),(21,'DORADO',NULL,NULL),(22,'AZUL CIELO',NULL,NULL),(23,'PALO DE ROSA',NULL,NULL),(24,'DURAZNO',NULL,NULL),(25,'CORAL',NULL,NULL),(26,'CIAN',NULL,NULL),(27,'cristal',NULL,NULL);
/*!40000 ALTER TABLE `color` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contabilidad`
--

DROP TABLE IF EXISTS `contabilidad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contabilidad` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_sub_categoria_contabilidad` int(11) NOT NULL,
  `id_usuarios` int(11) NOT NULL,
  `cuenta_id` int(11) NOT NULL,
  `fecha_registro` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `fecha_movimiento` timestamp NULL DEFAULT NULL,
  `comentario` varchar(350) DEFAULT NULL,
  `fg_activo` enum('1','0') NOT NULL DEFAULT '1',
  `cantidad` float DEFAULT NULL,
  PRIMARY KEY (`id`),
  KEY `fk_contabilidad_sub_categoria_contablidad` (`id_sub_categoria_contabilidad`),
  KEY `fk_contabilidad_usuario` (`id_usuarios`),
  KEY `fk_contabilidad_cuenta` (`cuenta_id`),
  CONSTRAINT `fk_contabilidad_cuenta` FOREIGN KEY (`cuenta_id`) REFERENCES `cuenta` (`id`),
  CONSTRAINT `fk_contabilidad_sub_categoria_contablidad` FOREIGN KEY (`id_sub_categoria_contabilidad`) REFERENCES `sub_categoria_contabilidad` (`id`),
  CONSTRAINT `fk_contabilidad_usuario` FOREIGN KEY (`id_usuarios`) REFERENCES `usuarios` (`id_usuarios`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contabilidad`
--

LOCK TABLES `contabilidad` WRITE;
/*!40000 ALTER TABLE `contabilidad` DISABLE KEYS */;
INSERT INTO `contabilidad` VALUES (1,2,1,7,'2019-05-21 01:34:08','2019-05-21 01:34:08','','1',900),(2,4,9,7,'2019-05-21 02:05:32','2019-05-21 02:05:28','','1',900),(3,4,9,6,'2019-05-21 02:05:59','2019-05-21 02:05:28','','0',1200),(4,3,1,7,'2019-05-21 02:11:57','2019-05-02 02:11:55','otra fecha','0',900);
/*!40000 ALTER TABLE `contabilidad` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `cuenta`
--

DROP TABLE IF EXISTS `cuenta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `cuenta` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(150) DEFAULT NULL,
  `created_at` date DEFAULT NULL,
  `updated_at` date DEFAULT NULL,
  `fg_activo` enum('1','0') NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=8 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cuenta`
--

LOCK TABLES `cuenta` WRITE;
/*!40000 ALTER TABLE `cuenta` DISABLE KEYS */;
INSERT INTO `cuenta` VALUES (1,'BANORTE',NULL,NULL,'1'),(2,'BBVA',NULL,NULL,'1'),(3,'BACOMER','2019-05-18',NULL,'1'),(4,'CITIBANAMEX','2019-05-19',NULL,'1'),(5,'PERRA','2019-05-19',NULL,'1'),(6,'NO MAS OTRO','2019-05-20',NULL,'1'),(7,'MASSS','2019-05-20',NULL,'1');
/*!40000 ALTER TABLE `cuenta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `datos_generales`
--

DROP TABLE IF EXISTS `datos_generales`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `datos_generales` (
  `id_datos_generales` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `nombre_empresa` varchar(45) DEFAULT NULL,
  `direccion1` varchar(45) DEFAULT NULL,
  `direccion2` varchar(45) DEFAULT NULL,
  `direccion3` varchar(45) DEFAULT NULL,
  `folio` int(10) unsigned NOT NULL,
  `folio_cambio` varchar(2) DEFAULT NULL,
  PRIMARY KEY (`id_datos_generales`)
) ENGINE=InnoDB AUTO_INCREMENT=2 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `datos_generales`
--

LOCK TABLES `datos_generales` WRITE;
/*!40000 ALTER TABLE `datos_generales` DISABLE KEYS */;
INSERT INTO `datos_generales` VALUES (1,'ventrua',NULL,NULL,NULL,215,'0');
/*!40000 ALTER TABLE `datos_generales` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `detalle_renta`
--

DROP TABLE IF EXISTS `detalle_renta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `detalle_renta` (
  `id_detalle_renta` int(11) NOT NULL AUTO_INCREMENT,
  `id_renta` int(11) DEFAULT NULL,
  `cantidad` float DEFAULT NULL,
  `id_articulo` int(11) DEFAULT NULL,
  `p_unitario` float DEFAULT NULL,
  `comentario` varchar(75) DEFAULT NULL,
  `se_desconto` varchar(2) DEFAULT NULL,
  `porcentaje_descuento` float DEFAULT NULL,
  PRIMARY KEY (`id_detalle_renta`),
  KEY `fk_detalle_renta_renta1_idx` (`id_renta`),
  KEY `FK_articulo` (`id_articulo`),
  CONSTRAINT `FK_articulo` FOREIGN KEY (`id_articulo`) REFERENCES `articulo` (`id_articulo`),
  CONSTRAINT `fk_detalle_renta_renta1` FOREIGN KEY (`id_renta`) REFERENCES `renta` (`id_renta`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=656 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `detalle_renta`
--

LOCK TABLES `detalle_renta` WRITE;
/*!40000 ALTER TABLE `detalle_renta` DISABLE KEYS */;
INSERT INTO `detalle_renta` VALUES (1,1,15,1,18,NULL,'0',NULL),(2,1,150,2,4,NULL,'0',NULL),(3,1,150,3,7,NULL,'0',NULL),(4,1,134,4,3,NULL,'0',NULL),(5,1,77,5,3,NULL,'0',NULL),(6,1,109,6,1.5,NULL,'0',NULL),(7,1,104,7,1.5,NULL,'0',NULL),(8,1,116,8,1.5,NULL,'0',NULL),(9,1,3,9,18,NULL,'0',NULL),(10,1,3,10,18,NULL,'0',NULL),(11,1,3,11,8,NULL,'0',NULL),(12,1,75,12,2,NULL,NULL,NULL),(13,2,7,9,18,NULL,'0',NULL),(14,2,70,2,4,NULL,'0',NULL),(15,2,7,10,18,NULL,'0',NULL),(16,3,14,9,18,NULL,'0',NULL),(17,3,16,10,18,NULL,'0',NULL),(18,3,150,2,4,NULL,'0',NULL),(19,4,4,1,18,NULL,'0',NULL),(20,4,40,2,4,NULL,'0',NULL),(21,4,4,13,18,NULL,'0',NULL),(22,5,2,9,18,NULL,'1',NULL),(23,5,50,2,4,NULL,'1',NULL),(24,5,2,10,18,NULL,'1',NULL),(25,5,1,16,800,NULL,'1',NULL),(26,6,16,1,18,NULL,'0',NULL),(27,6,160,4,3,NULL,'0',NULL),(28,6,160,2,4,NULL,'0',NULL),(29,6,280,19,2,NULL,'0',NULL),(30,6,120,17,1.5,NULL,'0',NULL),(31,6,200,18,2,NULL,'0',NULL),(32,6,60,20,4,NULL,'0',NULL),(33,6,60,21,3,NULL,'0',NULL),(34,6,16,10,18,NULL,'0',NULL),(35,6,160,3,7,NULL,'0',NULL),(36,7,3,9,18,NULL,'0',NULL),(37,7,30,2,4,NULL,'0',NULL),(38,7,20,14,3,NULL,'0',NULL),(39,7,2,23,15,NULL,'0',NULL),(40,7,3,11,8,NULL,'0',NULL),(41,7,2,22,15,NULL,'0',NULL),(42,8,4,9,18,NULL,'0',NULL),(43,8,40,2,4,NULL,'0',NULL),(44,8,4,10,18,NULL,'0',NULL),(45,8,4,32,8,NULL,'0',NULL),(46,9,100,2,4,NULL,'1',NULL),(47,9,14,9,18,NULL,'1',NULL),(48,9,14,10,18,NULL,'1',NULL),(49,10,3,9,18,NULL,'0',NULL),(50,10,35,2,4,NULL,'0',NULL),(51,10,3,15,130,NULL,'0',NULL),(52,10,3,13,18,NULL,'0',NULL),(53,11,3,9,18,NULL,'1',NULL),(54,11,50,2,4,NULL,'1',NULL),(55,11,18,10,18,NULL,'1',NULL),(56,12,50,2,4,NULL,'0',NULL),(57,13,30,1,18,NULL,'0',NULL),(58,13,300,2,4,NULL,'0',NULL),(59,13,30,13,18,NULL,'0',NULL),(60,13,300,3,7,NULL,'0',NULL),(61,14,3,9,18,NULL,'0',NULL),(62,14,30,2,4,NULL,'0',NULL),(63,14,3,10,18,NULL,'0',NULL),(64,15,15,1,18,NULL,'0',NULL),(65,15,15,13,18,NULL,'0',NULL),(66,15,150,2,4,NULL,'0',NULL),(67,15,150,3,7,NULL,'0',NULL),(68,16,6,10,18,NULL,'1',NULL),(69,16,30,3,7,NULL,'1',NULL),(70,16,6,11,8,NULL,'1',NULL),(71,17,25,1,18,NULL,'0',NULL),(72,17,250,2,4,NULL,'0',NULL),(73,17,25,13,18,NULL,'0',NULL),(74,17,25,33,8,NULL,'0',NULL),(75,17,250,3,7,NULL,'0',NULL),(76,18,80,4,3,NULL,'1',NULL),(77,18,80,7,1.5,NULL,'1',NULL),(78,18,80,8,1.5,NULL,'1',NULL),(79,18,80,18,2,NULL,'1',NULL),(80,18,10,65,10,NULL,'1',NULL),(81,19,9,1,18,NULL,'0',NULL),(82,19,90,2,4,NULL,'0',NULL),(83,19,9,13,18,NULL,'0',NULL),(84,19,9,39,12,NULL,'0',NULL),(85,19,2,9,18,NULL,'0',NULL),(86,19,2,10,18,NULL,'0',NULL),(87,20,8,1,18,NULL,'0',NULL),(88,20,8,42,12,NULL,'0',NULL),(89,20,80,3,7,NULL,'0',NULL),(90,20,80,4,3,NULL,'0',NULL),(91,20,80,18,2,NULL,'0',NULL),(92,20,80,7,1.5,NULL,'0',NULL),(93,20,80,8,1.5,NULL,'0',NULL),(94,20,4,66,90,NULL,'0',NULL),(95,21,15,1,18,NULL,'1',NULL),(96,21,150,2,4,NULL,'1',NULL),(97,21,15,13,18,NULL,'1',NULL),(98,21,15,11,8,NULL,'1',NULL),(99,21,150,3,7,NULL,'1',NULL),(100,21,3,9,18,NULL,'1',NULL),(101,21,3,10,18,NULL,'1',NULL),(102,21,30,5,3,NULL,'1',NULL),(103,21,80,4,3,NULL,'1',NULL),(104,21,80,6,1.5,NULL,'1',NULL),(105,21,80,8,1.5,NULL,'1',NULL),(106,21,80,7,1.5,NULL,'1',NULL),(107,22,5,1,18,NULL,'0',NULL),(108,22,50,2,4,NULL,'0',NULL),(109,22,5,13,18,NULL,'0',NULL),(110,22,50,3,7,NULL,'0',NULL),(111,22,5,33,8,NULL,'0',NULL),(112,23,25,1,18,NULL,'0',NULL),(113,23,250,2,4,NULL,'0',NULL),(114,23,1,10,18,NULL,'0',NULL),(115,23,25,13,18,NULL,'0',NULL),(116,23,20,67,10,NULL,'0',NULL),(117,24,30,1,18,NULL,'0',NULL),(118,24,300,2,4,NULL,'0',NULL),(119,24,30,13,18,NULL,'0',NULL),(120,24,1,9,18,NULL,'0',NULL),(121,24,1,10,18,NULL,'0',NULL),(122,25,25,9,18,NULL,'0',NULL),(123,25,250,2,4,NULL,'0',NULL),(124,25,25,10,18,NULL,'0',NULL),(125,26,5,9,18,NULL,'0',NULL),(126,26,50,2,4,NULL,'0',NULL),(127,26,5,10,18,NULL,'0',NULL),(128,27,4,9,18,NULL,'0',NULL),(129,27,40,2,4,NULL,'0',NULL),(130,27,4,10,18,NULL,'0',NULL),(131,27,1,68,1000,NULL,'0',NULL),(132,28,17,9,18,NULL,'0',NULL),(133,28,170,2,4,NULL,'0',NULL),(134,28,170,3,7,NULL,'0',NULL),(135,29,17,9,18,NULL,'0',NULL),(136,29,170,2,4,NULL,'0',NULL),(137,29,17,10,18,NULL,'0',NULL),(138,29,170,3,7,NULL,'0',NULL),(139,30,30,2,4,NULL,'0',NULL),(140,30,1,9,18,NULL,'0',NULL),(141,31,20,14,3,NULL,'0',NULL),(142,31,20,2,4,NULL,'0',NULL),(143,31,2,23,15,NULL,'0',NULL),(144,32,7,9,18,NULL,'0',NULL),(145,32,70,2,4,NULL,'0',NULL),(146,33,20,14,3,NULL,'0',NULL),(147,33,2,23,15,NULL,'0',NULL),(148,33,2,38,12,NULL,'0',NULL),(149,34,20,1,18,NULL,'0',NULL),(150,34,200,2,4,NULL,'0',NULL),(151,34,20,13,18,NULL,'0',NULL),(152,34,200,3,7,NULL,'0',NULL),(153,34,20,33,8,NULL,'0',NULL),(154,35,2,68,1000,NULL,'0',NULL),(155,36,25,1,18,NULL,'0',NULL),(156,36,250,2,4,NULL,'0',NULL),(157,36,25,13,18,NULL,'0',NULL),(158,37,10,1,18,NULL,'0',NULL),(159,37,100,2,4,NULL,'0',NULL),(160,37,10,13,18,NULL,'0',NULL),(161,38,80,4,3,NULL,'0',NULL),(162,38,80,5,3,NULL,'0',NULL),(163,38,80,69,4,NULL,'0',NULL),(164,38,80,6,1.5,NULL,'0',NULL),(165,38,80,8,1.5,NULL,'0',NULL),(166,38,80,70,1.5,NULL,'0',NULL),(167,38,80,71,15,NULL,'0',NULL),(168,38,80,24,3,NULL,'0',NULL),(169,38,40,12,2,NULL,'0',NULL),(170,38,10,1,18,NULL,'0',NULL),(171,38,100,2,4,NULL,'0',NULL),(172,38,10,13,18,NULL,'0',NULL),(173,39,100,2,4,NULL,'0',NULL),(174,39,4,9,18,NULL,'0',NULL),(175,39,4,10,18,NULL,'0',NULL),(176,40,10,1,18,NULL,'0',NULL),(177,40,100,2,4,NULL,'0',NULL),(178,40,1,10,18,NULL,'0',NULL),(179,40,11,36,12,NULL,'0',NULL),(180,41,20,1,18,NULL,'0',NULL),(181,41,200,2,4,NULL,'0',NULL),(182,41,1,10,18,NULL,'0',NULL),(183,41,21,33,8,NULL,'0',NULL),(184,42,15,34,12,NULL,'0',NULL),(185,42,75,72,2,NULL,'0',NULL),(186,43,15,2,4,NULL,'1',NULL),(187,43,1,9,18,NULL,'1',NULL),(188,43,1,10,18,NULL,'1',NULL),(189,44,10,1,18,NULL,'0',NULL),(190,44,100,2,4,NULL,'0',NULL),(191,44,10,10,18,NULL,'0',NULL),(192,45,11,9,18,NULL,'0',NULL),(193,45,110,2,4,NULL,'0',NULL),(194,45,11,10,18,NULL,'0',NULL),(195,45,11,33,8,NULL,'0',NULL),(196,46,10,1,18,NULL,'0',NULL),(197,46,100,2,4,NULL,'0',NULL),(198,46,10,13,18,NULL,'0',NULL),(199,47,16,1,18,NULL,'0',NULL),(200,47,160,2,4,NULL,'0',NULL),(201,47,16,13,18,NULL,'0',NULL),(202,48,10,1,18,NULL,'0',NULL),(203,48,100,2,4,NULL,'0',NULL),(204,48,10,13,18,NULL,'0',NULL),(205,49,300,2,4,NULL,'0',NULL),(206,49,84,3,7,NULL,'0',NULL),(207,50,21,10,18,NULL,'0',NULL),(208,50,24,11,8,NULL,'0',NULL),(209,51,45,1,18,NULL,'0',NULL),(210,51,25,13,18,NULL,'0',NULL),(211,51,456,3,7,NULL,'0',NULL),(212,52,50,2,4,NULL,'0',NULL),(213,52,150,3,7,NULL,'0',NULL),(214,53,20,2,4,NULL,'0',NULL),(215,53,1,9,18,NULL,'0',NULL),(216,54,2,1,18,NULL,'0',NULL),(217,54,20,2,4,NULL,'0',NULL),(218,54,2,11,8,NULL,'0',NULL),(219,55,5,1,18,NULL,'0',NULL),(220,55,50,2,4,NULL,'0',NULL),(221,55,5,13,18,NULL,'0',NULL),(222,56,30,1,18,NULL,'0',NULL),(223,56,300,2,4,NULL,'0',NULL),(224,57,100,2,4,NULL,'0',NULL),(225,58,100,19,2,NULL,'0',NULL),(226,58,25,1,18,NULL,'0',NULL),(227,58,250,2,4,NULL,'0',NULL),(228,58,250,3,7,NULL,'0',NULL),(229,59,150,2,4,NULL,'0',NULL),(230,60,5,1,18,NULL,'0',NULL),(231,60,50,2,4,NULL,'0',NULL),(232,60,56,3,7,NULL,'0',NULL),(233,61,25,9,18,NULL,'0',NULL),(234,61,250,2,4,NULL,'0',NULL),(235,61,250,3,7,NULL,'0',NULL),(236,62,20,1,18,NULL,'0',NULL),(237,62,200,2,4,NULL,'0',NULL),(238,63,20,1,18,NULL,'0',NULL),(239,63,200,2,4,NULL,'0',NULL),(240,64,20,1,18,NULL,'0',NULL),(241,64,200,2,4,NULL,'0',NULL),(242,65,35,1,18,NULL,'0',NULL),(243,65,350,2,4,NULL,'0',NULL),(244,65,35,13,18,NULL,'0',NULL),(245,65,350,3,7,NULL,'0',NULL),(246,66,1,68,1000,NULL,'0',NULL),(247,67,100,2,4,NULL,'0',NULL),(248,68,2,9,18,NULL,'0',NULL),(249,68,20,2,4,NULL,'0',NULL),(250,69,20,14,3,NULL,'0',NULL),(251,69,2,23,15,NULL,'0',NULL),(252,70,10,33,8,NULL,'0',NULL),(253,70,70,72,2,NULL,'0',NULL),(254,71,30,1,18,NULL,'0',NULL),(255,71,300,2,4,NULL,'0',NULL),(256,71,10,34,12,NULL,'0',NULL),(257,71,10,35,12,NULL,'0',NULL),(258,72,50,3,7,NULL,'0',NULL),(259,72,40,24,3,NULL,'0',NULL),(260,73,200,3,7,NULL,'0',NULL),(261,74,150,2,4,NULL,'0',NULL),(262,75,10,1,18,NULL,'0',NULL),(263,75,100,2,4,NULL,'0',NULL),(264,75,10,13,18,NULL,'0',NULL),(265,75,100,3,7,NULL,'0',NULL),(266,76,800,2,4,NULL,'0',NULL),(267,76,80,9,18,NULL,'0',NULL),(268,77,2,9,18,NULL,'0',NULL),(269,77,50,2,4,NULL,'0',NULL),(270,77,2,10,18,NULL,'0',NULL),(271,78,10,1,18,NULL,'0',NULL),(272,78,100,2,4,NULL,'0',NULL),(273,78,3,10,18,NULL,'0',NULL),(274,78,10,13,18,NULL,'0',NULL),(275,78,13,11,8,NULL,'0',NULL),(276,78,50,3,7,NULL,'0',NULL),(277,79,4,1,18,NULL,'0',NULL),(278,79,40,2,4,NULL,'0',NULL),(279,79,2,10,18,NULL,'0',NULL),(280,79,6,39,12,NULL,'0',NULL),(281,80,10,1,18,NULL,'0',NULL),(282,80,100,2,4,NULL,'0',NULL),(283,80,3,10,18,NULL,'0',NULL),(284,80,13,39,12,NULL,'0',NULL),(285,81,25,1,18,NULL,'0',NULL),(286,81,250,2,4,NULL,'0',NULL),(287,81,2,10,18,NULL,'0',NULL),(288,81,14,33,8,NULL,'0',NULL),(289,82,3,23,15,NULL,'0',NULL),(290,82,30,14,3,NULL,'0',NULL),(291,82,2,9,18,NULL,'0',NULL),(292,82,20,2,4,NULL,'0',NULL),(293,82,2,10,18,NULL,'0',NULL),(294,83,50,14,3,NULL,'0',NULL),(295,83,3,23,15,NULL,'0',NULL),(296,83,60,2,4,NULL,'0',NULL),(297,83,3,34,12,NULL,'0',NULL),(298,84,3,23,15,NULL,'0',NULL),(299,84,30,14,3,NULL,'0',NULL),(300,84,2,9,18,NULL,'0',NULL),(301,84,20,2,4,NULL,'0',NULL),(302,84,2,40,12,NULL,'0',NULL),(303,85,8,9,18,NULL,'0',NULL),(304,85,80,2,4,NULL,'0',NULL),(305,85,8,37,12,NULL,'0',NULL),(306,86,100,2,4,NULL,'0',NULL),(307,87,10,1,18,NULL,'0',NULL),(308,87,100,2,4,NULL,'0',NULL),(309,87,100,3,7,NULL,'0',NULL),(310,88,20,9,18,NULL,'0',NULL),(311,88,200,2,4,NULL,'0',NULL),(312,88,200,3,7,NULL,'0',NULL),(313,89,23,9,18,NULL,'0',NULL),(314,89,230,2,4,NULL,'0',NULL),(315,89,230,3,7,NULL,'0',NULL),(316,89,23,39,12,NULL,'0',NULL),(317,90,10,1,18,NULL,'0',NULL),(318,90,100,2,4,NULL,'0',NULL),(319,90,100,3,7,NULL,'0',NULL),(320,90,10,39,12,NULL,'0',NULL),(321,91,15,9,18,NULL,'0',NULL),(322,91,150,2,4,NULL,'0',NULL),(323,91,10,10,18,NULL,'0',NULL),(324,91,15,39,12,NULL,'0',NULL),(325,92,20,9,18,NULL,'0',NULL),(326,92,200,2,4,NULL,'0',NULL),(327,92,200,3,7,NULL,'0',NULL),(328,92,20,10,18,NULL,'0',NULL),(329,93,20,9,18,NULL,'0',NULL),(330,93,200,2,4,NULL,'0',NULL),(331,93,200,3,7,NULL,'0',NULL),(332,94,8,1,18,NULL,'1',NULL),(333,94,80,2,4,NULL,'1',NULL),(334,94,8,11,8,NULL,'1',NULL),(335,95,250,2,4,NULL,'0',NULL),(336,95,25,9,18,NULL,'0',NULL),(337,96,8,1,18,NULL,'0',NULL),(338,96,80,2,4,NULL,'0',NULL),(339,96,8,11,8,NULL,'0',NULL),(340,97,8,1,18,NULL,'1',NULL),(341,97,80,2,4,NULL,'1',NULL),(342,97,8,41,12,NULL,'1',NULL),(343,98,50,2,4,NULL,'0',NULL),(344,98,4,1,18,NULL,'0',NULL),(345,98,4,51,25,NULL,'0',NULL),(346,98,1,68,1000,NULL,'0',NULL),(347,99,300,2,4,NULL,'0',NULL),(348,100,30,1,18,NULL,'0',NULL),(349,100,300,2,4,NULL,'0',NULL),(350,101,500,2,4,NULL,'0',NULL),(351,102,18,1,18,NULL,'0',NULL),(352,102,180,2,4,NULL,'0',NULL),(353,102,1,9,18,NULL,'0',NULL),(354,102,18,13,18,NULL,'0',NULL),(355,102,18,42,12,NULL,'0',NULL),(356,103,12,9,18,NULL,'0',NULL),(357,103,36,2,4,NULL,'0',NULL),(358,104,1,68,1000,NULL,'0',NULL),(359,105,10,9,18,NULL,'0',NULL),(360,105,30,2,4,NULL,'0',NULL),(361,106,10,9,18,NULL,'0',NULL),(362,106,30,2,4,NULL,'0',NULL),(363,107,6,1,18,NULL,'0',NULL),(364,107,60,2,4,NULL,'0',NULL),(365,107,6,13,18,NULL,'0',NULL),(366,107,60,3,7,NULL,'0',NULL),(367,107,3,39,12,NULL,'0',NULL),(368,108,6,1,18,NULL,'0',NULL),(369,108,60,2,4,NULL,'0',NULL),(370,108,60,3,7,NULL,'0',NULL),(371,108,3,39,12,NULL,'0',NULL),(372,109,6,1,18,NULL,'0',NULL),(373,109,60,2,4,NULL,'0',NULL),(374,109,60,3,7,NULL,'0',NULL),(375,109,3,39,12,NULL,'0',NULL),(376,110,6,1,18,NULL,'0',NULL),(377,110,60,2,4,NULL,'0',NULL),(378,110,60,3,7,NULL,'0',NULL),(379,111,6,1,18,NULL,'0',NULL),(380,111,60,2,4,NULL,'0',NULL),(381,111,60,3,7,NULL,'0',NULL),(382,112,80,2,4,NULL,'0',NULL),(383,113,20,1,18,NULL,'0',NULL),(384,113,200,2,4,NULL,'0',NULL),(385,113,1,9,18,NULL,'0',NULL),(386,114,15,1,18,NULL,'0',NULL),(387,114,150,2,4,NULL,'0',NULL),(388,114,1,9,18,NULL,'0',NULL),(389,115,7,1,18,NULL,'0',NULL),(390,115,70,2,4,NULL,'0',NULL),(391,115,2,9,18,NULL,'0',NULL),(392,115,9,39,12,NULL,'0',NULL),(393,116,11,1,18,NULL,'0',NULL),(394,116,110,2,4,NULL,'0',NULL),(395,116,1,9,18,NULL,'0',NULL),(396,116,12,38,12,NULL,'0',NULL),(397,117,11,1,18,NULL,'0',NULL),(398,117,110,2,4,NULL,'0',NULL),(399,117,1,9,18,NULL,'0',NULL),(400,117,12,11,8,NULL,'0',NULL),(401,118,5,1,18,NULL,'0',NULL),(402,118,50,2,4,NULL,'0',NULL),(403,119,6,9,18,NULL,'0',NULL),(404,119,60,2,4,NULL,'0',NULL),(405,119,6,36,12,NULL,'0',NULL),(406,120,100,2,4,NULL,'0',NULL),(407,120,2,9,18,NULL,'0',NULL),(408,120,2,10,18,NULL,'0',NULL),(409,120,2,11,8,NULL,'0',NULL),(410,120,50,14,3,NULL,'0',NULL),(411,121,130,2,4,NULL,'0',NULL),(412,121,13,1,18,NULL,'0',NULL),(413,121,4,13,18,NULL,'0',NULL),(414,121,130,3,7,NULL,'0',NULL),(415,121,8,39,12,NULL,'0',NULL),(416,122,220,2,4,NULL,'0',NULL),(417,123,220,2,4,NULL,'0',NULL),(418,123,15,9,18,NULL,'0',NULL),(419,123,15,10,18,NULL,'0',NULL),(420,124,200,2,4,NULL,'0',NULL),(421,125,200,2,4,NULL,'0',NULL),(422,126,8,1,18,NULL,'0',NULL),(423,126,80,2,4,NULL,'0',NULL),(424,126,1,9,18,NULL,'0',NULL),(425,126,9,42,12,NULL,'0',NULL),(426,127,4,9,18,NULL,'0',NULL),(427,127,40,2,4,NULL,'0',NULL),(428,127,4,10,18,NULL,'0',NULL),(429,128,4,9,18,NULL,'0',NULL),(430,128,40,2,4,NULL,'0',NULL),(431,128,4,10,18,NULL,'0',NULL),(432,129,4,9,18,NULL,'0',NULL),(433,129,40,2,4,NULL,'0',NULL),(434,129,4,10,18,NULL,'0',NULL),(435,130,4,9,18,NULL,'0',NULL),(436,130,40,2,4,NULL,'0',NULL),(437,130,4,10,18,NULL,'0',NULL),(438,131,32,2,4,NULL,'0',NULL),(439,132,15,9,18,NULL,'0',NULL),(440,132,200,2,4,NULL,'0',NULL),(441,133,4,9,18,NULL,'0',NULL),(442,133,40,2,4,NULL,'0',NULL),(443,134,7,9,18,NULL,'0',NULL),(444,134,70,2,4,NULL,'0',NULL),(445,134,7,10,18,NULL,'0',NULL),(446,134,7,11,8,NULL,'0',NULL),(447,135,4,9,18,NULL,'0',NULL),(448,135,40,2,4,NULL,'0',NULL),(449,135,4,10,18,NULL,'0',NULL),(450,136,2,68,1000,NULL,'0',NULL),(451,137,7,9,18,NULL,'0',NULL),(452,137,7,10,18,NULL,'0',NULL),(453,137,40,2,4,NULL,'0',NULL),(454,138,14,1,18,NULL,'0',NULL),(455,138,140,2,4,NULL,'0',NULL),(456,138,140,3,7,NULL,'0',NULL),(457,138,14,39,12,NULL,'0',NULL),(458,139,10,17,1.5,NULL,'0',NULL),(459,139,1,68,1000,NULL,'0',NULL),(460,140,300,19,2,NULL,'0',NULL),(461,140,3,21,3,NULL,'0',NULL),(462,140,3,16,800,NULL,NULL,NULL),(463,140,400,18,2,NULL,NULL,NULL),(464,140,5,69,4,NULL,NULL,NULL),(465,141,100,2,4,NULL,'0',NULL),(466,141,2,68,1000,NULL,'0',NULL),(467,142,300,17,1.5,NULL,'0',NULL),(468,143,10,2,4,NULL,'0',NULL),(469,143,2,68,1000,NULL,'0',NULL),(470,144,100,18,2,NULL,'0',NULL),(471,144,100,19,2,NULL,'0',NULL),(472,145,10,2,4,NULL,'0',NULL),(473,145,1,16,800,NULL,'0',NULL),(474,145,100,17,1.5,NULL,'0',NULL),(475,146,50,2,4,NULL,'0',NULL),(479,148,70,2,4,NULL,'0',NULL),(480,149,100,2,4,NULL,'1',NULL),(481,150,900,2,4,NULL,'0',NULL),(482,151,200,2,4,NULL,'0',NULL),(483,152,100,2,4,NULL,'0',NULL),(484,152,10,1,18,NULL,'0',NULL),(485,152,1,16,800,NULL,'0',NULL),(486,153,10,2,4,NULL,'1',NULL),(487,153,1,16,800,NULL,'1',NULL),(488,153,10,17,1.5,NULL,'1',NULL),(489,154,100,2,4,NULL,'1',NULL),(490,154,1,16,800,NULL,'1',NULL),(491,154,10,18,2,NULL,'1',NULL),(492,154,10,20,4,NULL,'1',NULL),(493,154,10,3,7,NULL,'1',NULL),(494,154,10,5,3,NULL,'1',NULL),(503,147,10,21,3,NULL,NULL,NULL),(504,147,10,2,4,NULL,NULL,NULL),(506,155,100,2,4,NULL,'1',NULL),(507,156,10,2,4,NULL,'0',NULL),(508,157,10,2,4,NULL,'0',NULL),(509,158,10,2,4,NULL,'0',NULL),(510,158,20,14,3,NULL,NULL,NULL),(511,159,500,2,4,NULL,'0',NULL),(512,159,100,14,3,NULL,'0',NULL),(513,160,50,2,4,NULL,'1',NULL),(514,160,10,17,1.5,NULL,'1',NULL),(515,161,100,2,4,NULL,'0',NULL),(516,161,10,16,800,NULL,'0',NULL),(517,161,10,17,1.5,NULL,'0',NULL),(518,161,10,20,4,NULL,'0',NULL),(519,162,100,2,4,NULL,'0',NULL),(520,163,100,2,4,NULL,'0',NULL),(521,163,100,17,1.5,NULL,'0',NULL),(522,164,10,67,10,NULL,'0',NULL),(523,165,50,2,4,NULL,'0',NULL),(524,166,50,2,4,NULL,'0',NULL),(525,167,10,17,1.5,NULL,'0',NULL),(526,168,10,17,1.5,NULL,'0',NULL),(527,169,10,2,4,NULL,'0',NULL),(528,170,10,2,4,NULL,'0',NULL),(529,170,10,16,800,NULL,'0',NULL),(530,170,10,18,2,NULL,'0',NULL),(531,171,10,2,4,NULL,'0',NULL),(532,171,10,19,2,NULL,'0',NULL),(533,172,10,2,4,NULL,'0',NULL),(534,172,10,16,800,NULL,'0',NULL),(535,173,150,2,4,NULL,'0',NULL),(536,173,10,16,800,NULL,'0',NULL),(537,174,10,2,4,NULL,'0',NULL),(538,175,100,2,4,NULL,'1',NULL),(539,176,50,2,4,NULL,'0',NULL),(540,176,10,16,800,NULL,'0',NULL),(541,177,100,2,4,NULL,'0',NULL),(542,177,10,16,800,NULL,'0',NULL),(543,178,10,17,1.5,NULL,'0',NULL),(544,179,10,2,4,NULL,'0',NULL),(545,180,100,2,4,NULL,'0',NULL),(546,181,100,2,4,NULL,'0',NULL),(547,181,1,16,800,NULL,NULL,NULL),(548,182,10,4,3,NULL,'0',NULL),(549,182,100,2,4,NULL,NULL,NULL),(550,182,1003,16,800,NULL,NULL,NULL),(551,183,100,2,4,NULL,'0',NULL),(552,182,70,1,18,NULL,NULL,NULL),(553,184,100,2,4,NULL,'0',NULL),(554,184,3,16,800,NULL,'0',NULL),(555,184,3,17,1.5,NULL,'0',NULL),(556,185,100,2,4,NULL,'0',NULL),(557,186,1,68,1000,NULL,'0',NULL),(558,187,100,2,4,NULL,'0',NULL),(559,187,10,9,18,NULL,'0',NULL),(560,187,100,8,1.5,NULL,NULL,NULL),(561,188,10,2,4,NULL,'0',10),(562,188,10,16,800,NULL,'0',0),(566,189,500,2,4,NULL,'0',0),(567,189,500,18,2,NULL,'0',20),(568,190,500,2,4,NULL,'0',0),(569,190,500,18,2,NULL,'0',10),(570,190,1,68,1000,NULL,'0',50),(571,191,10,16,800,NULL,'0',0),(572,191,50,19,2,NULL,'0',10),(573,192,10,71,15,NULL,'0',0),(574,192,10,4,3,NULL,'0',50),(575,193,50,16,800,NULL,'0',0),(576,193,10,68,1000,NULL,'0',0),(577,194,650,2,4,NULL,'0',10),(578,195,1,16,800,NULL,'0',6),(581,195,1,68,1000,NULL,NULL,10),(582,195,1,14,10,NULL,NULL,10),(583,196,10,16,800,NULL,'0',0),(584,196,10,18,2,NULL,'0',10),(585,197,100,17,1.5,NULL,'0',0),(586,197,100,71,15,NULL,'0',0),(587,198,50,18,2,NULL,'0',0),(588,198,10,1,18,NULL,'0',0),(589,198,10,4,3,NULL,'0',10),(590,199,10,17,1.5,NULL,'0',0),(591,199,10,18,2,NULL,'0',0),(592,199,10,20,4,NULL,'0',0),(593,199,10,71,15,NULL,'0',0),(594,199,10,1,18,NULL,'0',0),(595,199,10,3,7,NULL,'0',0),(596,199,10,4,3,NULL,'0',0),(597,199,10,5,3,NULL,'0',0),(598,199,10,6,1.5,NULL,'0',0),(599,199,10,7,1.5,NULL,'0',0),(600,199,10,13,18,NULL,'0',0),(601,199,10,14,3,NULL,'0',0),(602,199,10,22,15,NULL,'0',0),(603,199,10,23,15,NULL,'0',0),(604,199,10,24,3,NULL,'0',0),(605,199,10,60,40,NULL,'0',0),(606,199,10,61,70,NULL,'0',0),(607,199,10,62,70,NULL,'0',0),(608,199,10,63,70,NULL,'0',0),(609,199,10,64,42,NULL,'0',0),(610,199,10,66,90,NULL,'0',0),(611,199,10,70,1.5,NULL,'0',0),(612,199,10,11,8,NULL,'0',0),(613,199,10,12,2,NULL,'0',0),(614,199,10,15,130,NULL,'0',0),(615,199,10,27,30,NULL,'0',0),(616,199,10,33,8,NULL,'0',0),(617,199,10,50,25,NULL,'0',0),(618,199,10,30,27,NULL,'0',0),(619,199,10,44,25,NULL,'0',0),(620,199,0,31,30,NULL,'0',0),(621,199,10,42,12,NULL,'0',0),(622,199,10,53,25,NULL,'0',0),(623,199,10,32,8,NULL,'0',0),(624,199,10,40,12,NULL,'0',0),(625,199,10,49,25,NULL,'0',0),(626,199,10,59,25,NULL,'0',0),(627,199,10,48,25,NULL,'0',0),(628,199,10,57,24,NULL,'0',0),(629,199,10,47,25,NULL,'0',0),(630,199,10,54,25,NULL,'0',0),(631,199,10,55,25,NULL,'0',0),(632,200,10,16,800,NULL,'0',0),(633,200,10,17,1.5,NULL,'0',0),(634,200,10,18,2,NULL,'0',0),(635,201,20,17,1.5,NULL,'0',0),(636,202,10,16,800,NULL,'0',0),(637,203,10,17,1.5,NULL,'0',0),(638,204,10,16,800,NULL,'0',0),(639,205,10,2,4,NULL,'0',0),(640,206,10,2,4,NULL,'0',0),(641,207,10,17,1.5,NULL,'0',0),(642,208,10,16,800,NULL,'0',0),(643,209,50,17,1.5,NULL,'0',0),(644,209,10,18,2,NULL,'0',50),(645,210,20,2,4,NULL,'0',0),(646,210,10,4,3,NULL,'0',0),(647,210,10,1,18,NULL,NULL,0),(648,211,60,2,4,NULL,'0',0),(649,212,60,2,4,NULL,'0',0),(650,185,100,60,40,NULL,NULL,0),(651,203,10,16,800,NULL,NULL,0),(652,213,100,2,4,NULL,'0',0),(653,214,600,2,4,NULL,'0',0),(654,215,10,85,8,NULL,'0',5),(655,215,1,1,18,NULL,'0',0);
/*!40000 ALTER TABLE `detalle_renta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `estado`
--

DROP TABLE IF EXISTS `estado`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `estado` (
  `id_estado` int(11) NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id_estado`)
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `estado`
--

LOCK TABLES `estado` WRITE;
/*!40000 ALTER TABLE `estado` DISABLE KEYS */;
INSERT INTO `estado` VALUES (1,'Apartado'),(2,'En renta'),(3,'Pendiente'),(4,'Cancelado'),(5,'Finalizado'),(6,'finish');
/*!40000 ALTER TABLE `estado` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `faltantes`
--

DROP TABLE IF EXISTS `faltantes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `faltantes` (
  `id_faltante` int(11) NOT NULL AUTO_INCREMENT,
  `id_articulo` int(11) NOT NULL,
  `id_renta` int(11) NOT NULL,
  `id_usuarios` int(11) NOT NULL,
  `fecha_registro` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `cantidad` float DEFAULT NULL,
  `comentario` varchar(350) DEFAULT NULL,
  `fg_faltante` enum('1','0') NOT NULL DEFAULT '1',
  `fg_devolucion` enum('1','0') NOT NULL DEFAULT '0',
  `fg_accidente_trabajo` enum('1','0') NOT NULL DEFAULT '0',
  `fg_activo` enum('1','0') NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_faltante`),
  KEY `fk_faltantes_articulo` (`id_articulo`),
  KEY `fk_faltantes_renta` (`id_renta`),
  KEY `fk_faltantes_usuario` (`id_usuarios`),
  CONSTRAINT `fk_faltantes_articulo` FOREIGN KEY (`id_articulo`) REFERENCES `articulo` (`id_articulo`),
  CONSTRAINT `fk_faltantes_renta` FOREIGN KEY (`id_renta`) REFERENCES `renta` (`id_renta`),
  CONSTRAINT `fk_faltantes_usuario` FOREIGN KEY (`id_usuarios`) REFERENCES `usuarios` (`id_usuarios`)
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `faltantes`
--

LOCK TABLES `faltantes` WRITE;
/*!40000 ALTER TABLE `faltantes` DISABLE KEYS */;
INSERT INTO `faltantes` VALUES (1,16,202,1,'2019-03-11 19:18:34',6,'','1','0','0','1'),(2,16,202,1,'2019-03-11 19:18:38',6,'','1','0','0','1'),(3,16,202,1,'2019-03-11 19:18:38',6,'','1','0','0','1'),(4,16,202,1,'2019-03-11 19:18:39',6,'','1','0','0','1'),(5,16,202,1,'2019-03-11 19:20:03',9,'','0','1','0','1'),(6,16,202,1,'2019-03-11 19:20:06',9,'','0','0','0','1'),(7,16,202,1,'2019-03-11 20:32:41',1,'','0','1','0','0'),(8,16,202,1,'2019-03-11 20:34:34',9,'Esta es una prueba jeje','1','0','0','0'),(9,16,203,1,'2019-03-11 22:04:47',5,'','1','0','0','1'),(10,17,203,1,'2019-03-11 22:11:53',10,'','1','0','0','1'),(11,17,203,1,'2019-03-11 22:12:11',5,'','0','1','0','1'),(12,17,0,1,'2019-03-13 18:35:04',1,'folio 0','1','0','0','1'),(13,16,0,1,'2019-03-13 18:35:33',1,'se quemo alv','1','0','0','1'),(14,16,203,1,'2019-03-13 18:36:00',1,'se quemo alv','1','0','0','1'),(15,16,202,1,'2019-03-14 14:12:21',2,'work accident','0','0','1','1'),(16,85,215,1,'2019-08-16 01:29:45',1,'no entrego un vaso','1','0','0','1');
/*!40000 ALTER TABLE `faltantes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `puesto`
--

DROP TABLE IF EXISTS `puesto`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `puesto` (
  `id_puesto` int(11) NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id_puesto`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `puesto`
--

LOCK TABLES `puesto` WRITE;
/*!40000 ALTER TABLE `puesto` DISABLE KEYS */;
INSERT INTO `puesto` VALUES (1,'Chofer'),(2,'Repartidor'),(3,'Administrador'),(4,'Mostrador');
/*!40000 ALTER TABLE `puesto` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `renta`
--

DROP TABLE IF EXISTS `renta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `renta` (
  `id_renta` int(11) NOT NULL AUTO_INCREMENT,
  `id_estado` int(11) DEFAULT NULL,
  `id_clientes` int(11) DEFAULT NULL,
  `id_usuarios` int(11) DEFAULT NULL,
  `fecha_pedido` varchar(45) DEFAULT NULL,
  `fecha_entrega` varchar(45) DEFAULT NULL,
  `hora_entrega` varchar(145) DEFAULT NULL,
  `fecha_devolucion` varchar(45) DEFAULT NULL,
  `descripcion` varchar(400) DEFAULT NULL,
  `descuento` varchar(80) DEFAULT NULL,
  `cantidad_descuento` float DEFAULT NULL,
  `iva` float DEFAULT NULL,
  `comentario` varchar(500) DEFAULT NULL,
  `id_usuario_chofer` int(11) DEFAULT NULL,
  `folio` int(10) unsigned DEFAULT NULL,
  `stock` varchar(4) DEFAULT NULL,
  `id_tipo` int(10) unsigned NOT NULL,
  `hora_devolucion` varchar(145) DEFAULT NULL,
  `fecha_evento` varchar(45) DEFAULT NULL,
  `deposito_garantia` float DEFAULT NULL,
  `envio_recoleccion` float DEFAULT NULL,
  `mostrar_precios_pdf` enum('0','1') NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_renta`),
  KEY `fk_renta_estado_idx` (`id_estado`),
  KEY `fk_renta_clientes1_idx` (`id_clientes`),
  KEY `fk_renta_usuarios1_idx` (`id_usuarios`),
  KEY `FK_chofer` (`id_usuario_chofer`),
  KEY `FK_tipo` (`id_tipo`),
  CONSTRAINT `FK_chofer` FOREIGN KEY (`id_usuario_chofer`) REFERENCES `usuarios` (`id_usuarios`),
  CONSTRAINT `FK_tipo` FOREIGN KEY (`id_tipo`) REFERENCES `tipo` (`id_tipo`),
  CONSTRAINT `fk_renta_clientes1` FOREIGN KEY (`id_clientes`) REFERENCES `clientes` (`id_clientes`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_renta_estado` FOREIGN KEY (`id_estado`) REFERENCES `estado` (`id_estado`) ON DELETE NO ACTION ON UPDATE NO ACTION,
  CONSTRAINT `fk_renta_usuarios1` FOREIGN KEY (`id_usuarios`) REFERENCES `usuarios` (`id_usuarios`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=216 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `renta`
--

LOCK TABLES `renta` WRITE;
/*!40000 ALTER TABLE `renta` DISABLE KEYS */;
INSERT INTO `renta` VALUES (0,1,1,1,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,NULL,0,NULL,1,NULL,NULL,NULL,NULL,'1'),(1,1,1,1,'23/03/2015','28/03/2015','09:30 a.m.','30/03/2015','Salon Cielo Azul',NULL,128.18,0,'',7,55,'0',1,'',NULL,NULL,NULL,'1'),(2,1,2,1,'23/03/2015','27/03/2015','10:00 a.m.','28/03/2015','7 tablones con silla y cubre rosa pastel',NULL,48.72,0,'casa rustica antes de las antenas hay una tienda naranja ala buelta en la esquina.',7,1,'0',1,'',NULL,NULL,NULL,'1'),(3,1,3,1,'24/03/2015','27/03/2015','03:00 p.m.','28/03/2015','.',NULL,0,0,'entregar el jueves en la tarde 3 a 4',7,2,'0',1,'',NULL,NULL,NULL,'1'),(4,1,4,1,'24/03/2015','28/03/2015','01:00 p.m.','30/03/2015','4 mesas redondas con sillas y cubre verde limon.',NULL,20,0,'por el real campestre,villa ocotepec casa grande colonial.yevar el viernes en la tarde',7,3,'0',1,'',NULL,NULL,NULL,'1'),(5,5,5,1,'24/03/2015','24/03/2015','03:00 p.m.','25/03/2015','2 tablones con sillas y 30 sillas mas,una lona de 5x10',NULL,0,0,'martes  alas 3',7,4,'0',1,'',NULL,NULL,NULL,'1'),(6,5,6,1,'24/03/2015','28/03/2015','10:00 a.m.','30/03/2015','cubres verde jade,verde bandera,verde agua,moños de los mismos colores.',NULL,0,0,'por el infonavit saliendo al cebetis',7,5,'0',1,'',NULL,NULL,NULL,'1'),(7,1,6,1,'24/03/2015','28/03/2015','11:00 a.m.','30/03/2015','.',NULL,0,0,'7471213199. bajando por bombas afret.PAGADO',7,6,'0',1,'',NULL,NULL,NULL,'1'),(8,5,7,1,'25/03/2015','02/04/2015','10:00 a.m.','03/04/2015','.',NULL,55,0,'SAJUAN NEGRO CASA BLANCA',7,7,'0',1,'',NULL,NULL,NULL,'1'),(9,2,8,1,'25/03/2015','25/03/2015','02:00 p.m.','27/03/2015','.',NULL,0,0,'ENTREGAR EL MIERCOLES O CUPAN EL JUEVES\r\n',7,8,'1',1,'',NULL,NULL,NULL,'1'),(10,5,9,1,'25/03/2015','26/03/2015','11:00 a.m.','27/03/2015','.',NULL,0,0,'COL.CENTRO,ESTUDIO FOTOGRAFICO,PUERTA BLANCA.4716070',7,9,'0',1,'',NULL,NULL,NULL,'1'),(11,5,10,1,'26/03/2015','26/03/2015','10:00 a.m.','27/03/2015','.',NULL,250,0,'CUBRE,VERDE,',7,10,'0',1,'',NULL,NULL,NULL,'1'),(12,1,11,1,'26/03/2015','26/03/2015','05:00 p.m.','27/03/2015','.',NULL,0,0,'PUERTA CAFE OSCURO #38.SILLA DE PLASTICO',7,11,'0',1,'',NULL,NULL,NULL,'1'),(13,5,12,1,'26/03/2015','25/04/2016','10:00 a.m.','26/04/2015','.',NULL,170,0,'ENTREGAR EL SABADO 25 DE ABRIL ADELANTE DE MAZATLAN.MOÑOS Y CUBRES VINO\r\n',7,12,'0',1,'',NULL,NULL,NULL,'1'),(14,1,13,1,'26/03/2015','26/03/2015','06:00 p.m.','28/03/2015','.',NULL,20,0,'POR LA SECUNDARIA EN LA 3 CALLE AMANO DERECHA CASA RUSTICA CON PORTON NEGRO.',7,13,'0',1,'',NULL,NULL,NULL,'1'),(15,1,14,1,'26/03/2015','28/03/2015','05:00 p.m.','30/03/2015','.',NULL,0,0,'PAGADO.ENTREGAR EL VIERNES EN LA TARDE..MOÑOS Y MANTEL CORAL.',7,14,'0',1,'',NULL,NULL,NULL,'1'),(16,2,15,1,'26/03/2015','26/03/2015','11:00 a.m.','27/03/2015','.',NULL,0,0,'RECOGEN EN LA OFICINA',7,15,'1',1,'',NULL,NULL,NULL,'1'),(17,1,14,1,'26/03/2015','04/04/2015','09:00 a.m.','06/04/2015','.',NULL,295,0,'ENTREGAR EL VIERNES 3 DE ABRIL EN LA TARDE',7,16,'0',1,'',NULL,NULL,NULL,'1'),(18,5,16,1,'04/04/2015','04/04/2015','12:00 a.m.','06/04/2015','.',NULL,0,0,'OCUPAN EL DOMINGO.\r\n',8,17,'0',1,'',NULL,NULL,NULL,'1'),(19,5,17,1,'04/04/2015','18/04/2015','10:00 a.m.','20/04/2015','.',NULL,0,0,'',8,18,'0',1,'',NULL,NULL,NULL,'1'),(20,1,18,1,'04/04/2015','11/04/2015','11:00 a.m.','13/04/2015','.',NULL,0,0,'',8,19,'0',1,'',NULL,NULL,NULL,'1'),(21,5,1,1,'04/04/2015','04/04/2015','10:00 a.m.','06/04/2015','.',NULL,150,0,'',8,20,'0',1,'',NULL,NULL,NULL,'1'),(22,5,19,1,'07/04/2015','24/04/2015','10:00 a.m.','25/04/2015','.',NULL,65,0,'',7,21,'0',1,'',NULL,NULL,NULL,'1'),(23,5,1,1,'08/04/2015','18/04/2015','10:00 a.m.','20/04/2015','.',NULL,330,0,'',7,22,'0',1,'',NULL,NULL,NULL,'1'),(24,5,1,1,'08/04/2015','10/04/2015','11:00 a.m.','11/04/2015','.',NULL,170,0,'',7,23,'0',1,'',NULL,NULL,NULL,'1'),(25,5,20,1,'08/04/2015','01/05/2015','10:00 a.m.','04/05/2015','.',NULL,0,0,'',7,24,'0',1,'',NULL,NULL,NULL,'1'),(26,1,21,1,'08/04/2015','25/04/2015','10:00 a.m.','27/04/2015','.',NULL,0,0,'HAY UN LETRERO SASTRERIA TEL.4945235',7,25,'0',1,'',NULL,NULL,NULL,'1'),(27,5,23,1,'08/04/2015','11/04/2015','10:00 a.m.','13/04/2015','.',NULL,20,0,'AUN LADO DEL SALON EL TORONJIL.TEL7475093838\r\n',7,26,'0',1,'',NULL,NULL,NULL,'1'),(28,1,23,1,'09/04/2015','11/04/2015','03:00 p.m.','13/04/2015','.',NULL,0,0,'por el mirador calle:serrada',8,27,'0',1,'',NULL,NULL,NULL,'1'),(29,1,24,1,'09/04/2015','11/04/2015','11:00 p.m.','13/04/2015','-',NULL,95,0,'por el mirador calle serrada casa de 3 pisos.cubre mantel y moños fiusha.\r\n',8,28,'0',1,'',NULL,NULL,NULL,'1'),(30,5,25,1,'09/04/2015','11/04/2015','02:00 p.m.','13/04/2015','.',NULL,0,0,'parada de las combis amano dereha atopar con paret casa amariya porton cafe.PAGADO',8,29,'0',1,'',NULL,NULL,NULL,'1'),(31,5,26,1,'10/04/2015','11/04/2015','10:00 a.m.','13/04/2015','.',NULL,0,0,'FRENTE ALA ESCUELA PRIMARIA PARADOR.PAGADO',7,30,'0',1,'',NULL,NULL,NULL,'1'),(32,5,27,1,'10/04/2015','10/04/2015','12:00 a.m.','11/04/2015','.',NULL,25,0,'sin mantel y sin cubre.',7,31,'0',1,'',NULL,NULL,NULL,'1'),(33,1,28,1,'10/04/2015','12/04/2015','11:00 a.m.','13/04/2015','.',NULL,0,0,'pagado.ocupan el domingo',7,32,'0',1,'',NULL,NULL,NULL,'1'),(34,5,29,1,'10/04/2015','11/04/2015','05:00 p.m.','13/04/2015','.',NULL,225,0,'OCUPAN EL DOMINGO.EN LA CAMA DE CONSTRUCCION',7,33,'0',1,'',NULL,NULL,NULL,'1'),(35,5,30,1,'10/04/2015','11/04/2015','08:00 a.m.','13/04/2015','.',NULL,0,0,'MADERO#31 EN LA PASTELERIA CON EL SEÑOR JUAQUIN O LA SEÑORA ALVA BELEZ.',7,34,'0',1,'',NULL,NULL,NULL,'1'),(36,5,31,1,'10/04/2015','11/04/2015','09:00 a.m.','12/04/2015','.',NULL,0,0,'zumpango recoguer el domingo 8:am',7,35,'0',1,'',NULL,NULL,NULL,'1'),(37,5,32,1,'11/04/2015','25/04/2015','10:00 a.m.','27/04/2015','.',NULL,0,0,'SIN CUBRE',7,36,'0',1,'',NULL,NULL,NULL,'1'),(38,5,31,1,'13/04/2015','15/04/2015','11:00 a.m.','16/04/2015','.',NULL,0,0,'ZUMPANGO',7,37,'0',1,'',NULL,NULL,NULL,'1'),(39,5,33,1,'13/04/2015','14/04/2015','08:00 a.m.','15/04/2015','.',NULL,0,0,'ENTRAR POR LAS MOTOS.PAGADO',7,38,'0',1,'',NULL,NULL,NULL,'1'),(40,5,1,1,'13/04/2015','19/04/2015','10:00 a.m.','20/04/2015','.',NULL,5,0,'OCUPAN EL DOMINGO.',7,39,'0',1,'',NULL,NULL,NULL,'1'),(41,5,1,1,'13/04/2015','25/04/2015','10:00 a.m.','27/04/2015','.',NULL,0,0,'',7,40,'0',1,'',NULL,NULL,NULL,'1'),(42,1,34,1,'13/04/2015','09/05/2015','10:00 a.m.','10/04/2015','.',NULL,25,0,'PAGADO.ENTREGAR EL 8/MAYO/ ALAS 5P:M EN LA OFICINA',7,41,'0',1,'',NULL,NULL,NULL,'1'),(43,5,35,1,'14/04/2015','14/04/2015','05:00 a.m.','16/04/2015','.',NULL,0,0,'OCUPAN EL MIERCOLES ALAS 8:OO AM',7,42,'0',1,'',NULL,NULL,NULL,'1'),(44,5,14,1,'14/04/2015','18/04/2015','03:00 a.m.','20/04/2015','.',NULL,0,0,'YA TIENE MATERIAL SOLO FALTA MANTELERIA\r\n',7,43,'0',1,'',NULL,NULL,NULL,'1'),(45,1,27,1,'14/04/2015','22/05/2015','09:00 a.m.','23/05/2015','.',NULL,150,0,'ENTREGAR EL 21 DE MAYO EN LA TARDE',7,44,'0',1,'',NULL,NULL,NULL,'1'),(46,1,27,1,'14/04/2015','23/05/2015','09:00 a.m.','25/05/2015','.',NULL,55,0,'ENTREGAR EN HUERTA LOS VEGA.',7,45,'0',1,'',NULL,NULL,NULL,'1'),(47,1,27,1,'14/04/2015','06/06/2015','09:00 a.m.','08/06/2015','.',NULL,90,0,'ENTREGAR EL 5 DE JUNIO EN LA TARDE',7,46,'0',1,'',NULL,NULL,NULL,'1'),(48,1,27,1,'14/04/2015','23/05/2015','09:00 a.m.','25/05/2015','.',NULL,55,0,'ENTREGAR EN HUERTA LOS VEGA',7,47,'0',1,'',NULL,NULL,NULL,'1'),(49,1,36,1,'15/04/2015','10/07/2015','09:00 a.m.','11/07/2015','.',NULL,0,0,'ENTREGAR EL 9 JULIO ALAS 6:PM .PAGADO',7,48,'0',1,'',NULL,NULL,NULL,'1'),(50,5,37,1,'16/04/2015','15/05/2015','09:00 a.m.','18/05/2015','.',NULL,0,0,'RECOGEN EN LA OFICINA',7,49,'0',1,'',NULL,NULL,NULL,'1'),(51,1,4,1,'16/04/2015','21/04/2015','11:00 a.m.','23/04/2015','.',NULL,0,0,'',7,50,'0',1,'',NULL,NULL,NULL,'1'),(52,1,38,1,'16/04/2015','18/04/2015','10:00 a.m.','20/04/2015','.',NULL,0,0,'MOÑO,ROJO',7,51,'0',1,'',NULL,NULL,NULL,'1'),(53,5,39,1,'16/04/2015','16/04/2015','02:00 p.m.','17/04/2015','.',NULL,0,0,'',7,52,'0',1,'',NULL,NULL,NULL,'1'),(54,5,40,1,'16/04/2015','17/04/2015','10:00 a.m.','18/04/2015','.',NULL,0,0,'DEL OXO DEL AEROPUERTO EN LA SEGUNDA CALLE HACIA LA DERECHA CASA MELON.',7,53,'0',1,'',NULL,NULL,NULL,'1'),(55,5,41,1,'17/04/2015','18/04/2015','12:00 a.m.','20/04/2015','.',NULL,0,0,'pagado. cubre rojo',7,54,'0',1,'',NULL,NULL,NULL,'1'),(56,5,43,1,'17/04/2015','18/04/2015','11:00 a.m.','20/04/2015','.',NULL,0,0,'zumpango',7,55,'0',1,'',NULL,NULL,NULL,'1'),(57,5,44,1,'17/04/2015','18/04/2015','09:00 a.m.','20/04/2015','.',NULL,0,0,'ocupan el sabado 9:am.tel.7471255699',7,56,'0',1,'',NULL,NULL,NULL,'1'),(58,5,29,1,'17/04/2015','20/04/2015','10:00 a.m.','21/04/2015','.',NULL,0,0,'entregar el domingo en la tarde. verde y rojo',7,57,'0',1,'',NULL,NULL,NULL,'1'),(59,5,45,1,'17/04/2015','18/04/2015','10:00 a.m.','20/04/2015','-',NULL,0,0,'',7,58,'0',1,'',NULL,NULL,NULL,'1'),(60,5,4,1,'17/04/2015','18/04/2015','09:00 a.m.','20/04/2015','.',NULL,0,0,'OCUPAN EL SABADO',7,59,'0',1,'',NULL,NULL,NULL,'1'),(61,5,14,1,'20/04/2015','22/04/2015','10:00 a.m.','23/04/2015','.',NULL,0,0,'PAGADO',7,60,'0',1,'',NULL,NULL,NULL,'1'),(62,1,45,1,'20/04/2015','21/04/2015','10:00 a.m.','22/04/2015','.',NULL,0,0,'HOTEL REAL DEL SOL\r\n',7,61,'0',1,'',NULL,NULL,NULL,'1'),(63,1,45,1,'20/04/2015','21/04/2015','10:00 a.m.','22/04/2015','.',NULL,0,0,'HOTEL REAL DEL SOL',7,62,'0',1,'',NULL,NULL,NULL,'1'),(64,1,45,1,'20/04/2015','21/04/2015','10:00 a.m.','22/04/2015','.',NULL,0,0,'',7,63,'0',1,'',NULL,NULL,NULL,'1'),(65,5,46,1,'20/04/2015','02/05/2015','09:00 a.m.','04/05/2015','.',NULL,180,0,'ENTREGAR UN DIA ANTES',7,64,'0',1,'',NULL,NULL,NULL,'1'),(66,5,47,1,'20/04/2015','25/04/2015','09:00 a.m.','27/04/2015','.',NULL,0,0,'MONTAR EL VIERNES EN LA TARDE',7,65,'0',1,'',NULL,NULL,NULL,'1'),(67,5,48,1,'21/04/2015','30/04/2015','05:00 p.m.','01/05/2015','.',NULL,0,0,'',7,66,'0',1,'',NULL,NULL,NULL,'1'),(68,5,49,1,'21/04/2015','21/04/2015','01:00 a.m.','22/04/2015','.',NULL,0,0,'SIN MANTEL NI CUBRE',7,67,'0',1,'',NULL,NULL,NULL,'1'),(69,5,50,1,'22/04/2015','02/05/2015','12:00 a.m.','04/05/2015','.',NULL,0,0,'',7,68,'0',1,'',NULL,NULL,NULL,'1'),(70,1,51,1,'23/04/2015','25/04/2015','09:00 a.m.','27/04/2015','.',NULL,0,0,'',7,69,'0',1,'',NULL,NULL,NULL,'1'),(71,1,51,1,'23/04/2015','11/05/2015','08:00 a.m.','13/05/2015','.',NULL,0,0,'',7,70,'0',1,'',NULL,NULL,NULL,'1'),(72,1,52,1,'25/04/2015','25/04/2015','11:00 a.m.','27/04/2015','.',NULL,0,0,'ocuapan el lunes.entregar en el peloton de intendecia del 50 batañon.',7,71,'0',1,'',NULL,NULL,NULL,'1'),(73,5,53,1,'25/04/2015','01/05/2015','10:00 a.m.','02/05/2015','.',NULL,0,0,'por iguala retorna sobre la carretera.moños de colores',7,72,'0',1,'',NULL,NULL,NULL,'1'),(74,5,54,1,'25/04/2015','27/04/2015','08:00 a.m.','28/04/2015','.',NULL,0,0,'',7,73,'0',1,'',NULL,NULL,NULL,'1'),(75,5,29,1,'25/04/2015','26/04/2015','10:00 a.m.','27/04/2015','.\r\n',NULL,0,0,'CUBRE ROJO . EN SEMIC DE LA CONSTRUCCION\r\n',7,74,'0',1,'',NULL,NULL,NULL,'1'),(76,1,55,1,'25/04/2015','08/05/2015','06:00 a.m.','07/05/2015','.',NULL,0,0,'SIN MANTEL ENTREGAR EL JUEVES',7,75,'0',1,'',NULL,NULL,NULL,'1'),(77,1,55,1,'27/04/2015','27/04/2015','11:00 a.m.','28/04/2015','.',NULL,0,0,'',7,76,'0',1,'',NULL,NULL,NULL,'1'),(78,1,1,1,'27/04/2015','01/05/2015','09:00 a.m.','02/05/2015','.',NULL,150,0,'',7,77,'0',1,'',NULL,NULL,NULL,'1'),(79,5,1,1,'27/04/2015','02/05/2015','09:00 a.m.','04/05/2015','.',NULL,0,0,'',7,78,'0',1,'',NULL,NULL,NULL,'1'),(80,1,1,1,'27/04/2015','08/05/2015','09:00 a.m.','09/05/2015','.',NULL,0,0,'',7,79,'0',1,'',NULL,NULL,NULL,'1'),(81,5,1,1,'27/04/2015','09/05/2015','10:00 a.m.','11/05/2015','.',NULL,0,0,'',7,80,'0',1,'',NULL,NULL,NULL,'1'),(82,1,55,1,'27/04/2015','02/05/2015','11:00 a.m.','04/05/2015','.',NULL,0,0,'',7,81,'0',1,'',NULL,NULL,NULL,'1'),(83,5,56,1,'27/04/2015','02/05/2015','09:00 a.m.','04/05/2015','.',NULL,0,0,'',7,82,'0',1,'',NULL,NULL,NULL,'1'),(84,5,57,1,'27/04/2015','02/05/2015','11:00 a.m.','04/05/2015','.',NULL,0,0,'',7,83,'0',1,'',NULL,NULL,NULL,'1'),(85,1,58,1,'28/04/2015','09/05/2015','11:00 a.m.','11/05/2015','.',NULL,0,0,'',7,84,'0',1,'',NULL,NULL,NULL,'1'),(86,5,59,1,'28/04/2015','30/04/2015','12:00 a.m.','01/05/2015','.',NULL,0,0,'',7,85,'0',1,'',NULL,NULL,NULL,'1'),(87,1,14,1,'28/04/2015','02/05/2015','09:00 a.m.','04/05/2015','.',NULL,0,0,'ENTREGAR EL VIERNES EN LA TARDE.MOÑOS AZUL TURQUEZA',7,86,'0',1,'',NULL,NULL,NULL,'1'),(88,1,14,1,'28/04/2015','09/05/2015','10:00 a.m.','11/05/2015','.',NULL,0,0,'',7,87,'0',1,'',NULL,NULL,NULL,'1'),(89,5,14,1,'28/04/2015','15/05/2015','09:00 a.m.','16/05/2015','.',NULL,0,0,'',7,88,'0',1,'',NULL,NULL,NULL,'1'),(90,1,14,1,'28/04/2015','23/05/2015','09:00 a.m.','25/05/2015','.',NULL,0,0,'',7,89,'0',1,'',NULL,NULL,NULL,'1'),(91,1,14,1,'28/04/2015','13/06/2015','09:00 a.m.','15/06/2015','.',NULL,0,0,'',7,90,'0',1,'',NULL,NULL,NULL,'1'),(92,1,14,1,'28/04/2015','20/06/2015','09:00 a.m.','22/06/2015','.',NULL,0,0,'',7,91,'0',1,'',NULL,NULL,NULL,'1'),(93,1,14,1,'28/04/2015','27/06/2015','09:00 a.m.','29/06/2015','.',NULL,0,0,'',7,92,'0',1,'',NULL,NULL,NULL,'1'),(94,2,59,1,'30/04/2015','09/05/2015','10:00 a.m.','11/05/2015','.',NULL,0,0,'',7,93,'1',1,'',NULL,NULL,NULL,'1'),(95,1,60,1,'30/04/2015','06/05/2015','09:00 a.m.','10/05/2015','.',NULL,0,0,'montar el 4 de mayo',7,94,'0',1,'',NULL,NULL,NULL,'1'),(96,1,60,1,'30/04/2015','09/05/2015','10:00 a.m.','11/05/2015','.',NULL,0,0,'',7,95,'0',1,'',NULL,NULL,NULL,'1'),(97,5,61,1,'30/04/2015','09/05/2015','10:00 a.m.','11/05/2015','.',NULL,0,0,'',7,96,'0',1,'',NULL,NULL,NULL,'1'),(98,5,62,1,'30/04/2015','02/05/2015','09:00 -sel-','04/05/2015','.',NULL,0,0,'',7,97,'0',1,'',NULL,NULL,NULL,'1'),(99,5,63,1,'05/05/2015','09/05/2015','09:00 a.m.','11/05/2015','.',NULL,0,0,'AUN COSTADO DEL DEPORTIVO,AV GRANDE COMUNIDA CRISTIANA DE MEXICO.\r\n\r\n',7,98,'0',1,'',NULL,NULL,NULL,'1'),(100,1,64,1,'05/05/2015','07/05/2015','09:00 a.m.','08/05/2015','.',NULL,0,0,'SIN MANTEL,ENTREGAR EL MIERCOLES',7,99,'0',1,'',NULL,NULL,NULL,'1'),(101,1,64,1,'05/05/2015','06/05/2015','09:00 a.m.','07/05/2015','.',NULL,0,0,'',7,100,'0',1,'',NULL,NULL,NULL,'1'),(102,1,1,1,'05/05/2015','07/05/2015','10:00 a.m.','08/05/2015','.',NULL,0,0,'',7,101,'0',1,'',NULL,NULL,NULL,'1'),(103,1,65,1,'07/05/2015','07/06/2015','07:00 a.m.','08/06/2015','.',NULL,0,0,'',7,102,'0',1,'',NULL,NULL,NULL,'1'),(104,1,66,1,'07/05/2015','30/04/2015','04:00 -sel-','30/04/2015','.',NULL,0,0,'',7,103,'0',1,'',NULL,NULL,NULL,'1'),(105,1,67,1,'07/05/2015','07/06/2015','07:00 a.m.','08/06/2015','.',NULL,0,0,'',7,104,'0',1,'',NULL,NULL,NULL,'1'),(106,1,68,1,'07/05/2015','07/06/2015','07:00 a.m.','08/06/2015','.',NULL,0,0,'',7,105,'0',1,'',NULL,NULL,NULL,'1'),(107,1,1,1,'07/05/2015','23/05/2015','11:00 a.m.','25/05/2015','.',NULL,0,0,'',7,106,'0',1,'',NULL,NULL,NULL,'1'),(108,1,68,1,'07/05/2015','23/05/2015','11:00 a.m.','25/05/2015','.',NULL,0,0,'',7,107,'0',1,'',NULL,NULL,NULL,'1'),(109,1,68,1,'07/05/2015','23/05/2015','11:00 a.m.','25/05/2015','.',NULL,0,0,'CASA DE BLOQ CON UNA ENREDADERA PORTON BEYS',7,108,'0',1,'',NULL,NULL,NULL,'1'),(110,1,68,1,'07/05/2015','23/05/2015','11:00 a.m.','25/05/2015','.',NULL,0,0,'',7,109,'0',1,'',NULL,NULL,NULL,'1'),(111,1,69,1,'07/05/2015','23/05/2015','11:00 a.m.','25/05/2015','.',NULL,0,0,'',7,110,'0',1,'',NULL,NULL,NULL,'1'),(112,1,69,1,'13/05/2015','02/07/2015','05:00 p.m.','03/07/2015','.',NULL,0,0,'',7,111,'0',1,'',NULL,NULL,NULL,'1'),(113,5,1,1,'13/05/2015','15/05/2015','12:00 a.m.','16/05/2015','.',NULL,0,0,'',7,112,'0',1,'',NULL,NULL,NULL,'1'),(114,5,1,1,'13/05/2015','16/05/2015','12:00 a.m.','18/05/2015','.',NULL,0,0,'',7,113,'0',1,'',NULL,NULL,NULL,'1'),(115,5,1,1,'13/05/2015','17/05/2015','11:00 a.m.','18/05/2015','.',NULL,0,0,'',7,114,'0',1,'',NULL,NULL,NULL,'1'),(116,1,1,1,'13/05/2015','21/05/2015','12:00 a.m.','22/05/2015','.',NULL,0,0,'',7,115,'0',1,'',NULL,NULL,NULL,'1'),(117,1,1,1,'13/05/2015','23/05/2015','01:00 p.m.','25/05/2015','.',NULL,0,0,'',7,116,'0',1,'',NULL,NULL,NULL,'1'),(118,5,70,1,'13/05/2015','16/05/2015','09:00 -sel-','18/05/2015','.',NULL,0,0,'',7,117,'0',1,'',NULL,NULL,NULL,'1'),(119,5,71,1,'13/05/2015','14/05/2015','08:00 a.m.','15/05/2015','.',NULL,0,0,'',7,118,'0',1,'',NULL,NULL,NULL,'1'),(120,1,72,1,'16/05/2015','07/06/2015','11:00 a.m.','08/06/2015','.',NULL,0,0,'CASA DE 3 PISOS COLOR MOSTAZA PORTON BLANCO',7,119,'0',1,'',NULL,NULL,NULL,'1'),(121,1,73,1,'16/05/2015','18/05/2015','09:00 a.m.','19/05/2015','.',NULL,0,0,'',7,120,'0',1,'',NULL,NULL,NULL,'1'),(122,1,3,1,'18/05/2015','22/05/2015','10:00 a.m.','23/05/2015','.',NULL,0,0,'',7,121,'0',1,'',NULL,NULL,NULL,'1'),(123,1,3,1,'18/05/2015','23/05/2015','10:00 a.m.','25/05/2015','.',NULL,0,0,'',7,122,'0',1,'',NULL,NULL,NULL,'1'),(124,1,3,1,'18/05/2015','26/06/2015','10:00 -sel-','27/06/2015','.',NULL,0,0,'',7,123,'0',1,'',NULL,NULL,NULL,'1'),(125,1,3,1,'18/05/2015','08/08/2015','10:00 -sel-','10/08/2015','.',NULL,0,0,'',7,124,'0',1,'',NULL,NULL,NULL,'1'),(126,1,1,1,'19/05/2015','21/05/2015','12:00 a.m.','22/05/2015','.',NULL,0,0,'',7,125,'0',1,'',NULL,NULL,NULL,'1'),(127,1,73,1,'19/05/2015','23/05/2015','10:00 a.m.','25/05/2015','.',NULL,0,0,'POR EL PRI Y EL MERCADO TODO DERECHO CASA COLOR VERDE PISTACHE S/N TEMPLO JESEMANY',7,126,'0',1,'',NULL,NULL,NULL,'1'),(128,1,73,1,'19/05/2015','23/05/2015','11:00 a.m.','25/05/2015','.',NULL,0,0,'',7,127,'0',1,'',NULL,NULL,NULL,'1'),(129,1,73,1,'19/05/2015','23/05/2015','10:00 a.m.','25/05/2015','.',NULL,0,0,'CASA VERDE PISTAÑE,POR EL PRI TODO DERECHO',7,128,'0',1,'',NULL,NULL,NULL,'1'),(130,1,73,1,'19/05/2015','23/05/2015','10:00 a.m.','25/05/2015','.',NULL,0,0,'',7,129,'0',1,'',NULL,NULL,NULL,'1'),(131,1,74,1,'19/05/2015','19/05/2015','05:00 a.m.','20/05/2015','.',NULL,0,0,'',7,130,'0',2,'',NULL,NULL,NULL,'1'),(132,1,75,1,'19/05/2015','21/05/2015','05:00 p.m.','23/05/2015','.',NULL,0,0,'OCUPAN EL VIERNES TEMPRANO',7,131,'0',1,'',NULL,NULL,NULL,'1'),(133,1,75,1,'19/05/2015','23/05/2015','10:00 a.m.','25/05/2015','.',NULL,0,0,'',7,132,'0',1,'',NULL,NULL,NULL,'1'),(134,1,75,1,'19/05/2015','23/05/2015','11:00 a.m.','25/05/2015','.',NULL,0,0,'',7,133,'0',1,'',NULL,NULL,NULL,'1'),(135,1,76,1,'20/05/2015','20/05/2015','03:00 p.m.','21/05/2015','.',NULL,0,0,'',7,134,'0',1,'',NULL,NULL,NULL,'1'),(136,1,77,1,'20/05/2015','20/05/2015','04:00 p.m.','21/05/2015','.',NULL,0,0,'',7,135,'0',1,'',NULL,NULL,NULL,'1'),(137,1,71,1,'20/05/2015','20/05/2015','05:00 p.m.','21/05/2015','.',NULL,0,0,'OCUPAN EL JUEVES ALAS 8:AM EN EL ZOLOGICO',7,136,'0',1,'',NULL,NULL,NULL,'1'),(138,1,78,1,'20/05/2015','23/05/2015','10:00 a.m.','25/05/2015','.',NULL,0,0,'',7,137,'0',1,'',NULL,NULL,NULL,'1'),(139,1,34,1,'27/04/2018','27/04/2018','01:00 p.m.','28/04/2018','asd',NULL,150,10,'',8,138,'0',1,'',NULL,NULL,NULL,'1'),(140,1,57,1,'02/05/2018','02/05/2018','01:00 p.m.','03/05/2018','prueba',NULL,100,10,'',8,139,'0',1,'',NULL,NULL,NULL,'1'),(141,1,61,1,'12/11/2018','17/11/2018','01:00 p.m.','19/11/2018','PRUEBA',NULL,0,0,'',8,140,'0',2,'',NULL,NULL,NULL,'1'),(142,1,22,1,'12/11/2018','17/11/2018','01:00 p.m.','19/11/2018','PRUEBA',NULL,0,0,'',8,141,'0',2,'',NULL,NULL,NULL,'1'),(143,1,61,1,'12/11/2018','23/11/2018','01:00 p.m.','25/11/2018','Prueba',NULL,0,0,'',8,142,'0',2,'',NULL,NULL,NULL,'1'),(144,1,30,1,'12/11/2018','23/11/2018','01:00 p.m.','25/11/2018','.',NULL,0,0,'',7,143,'0',1,'',NULL,NULL,NULL,'1'),(145,1,44,1,'12/11/2018','23/11/2018','01:50 a.m.','25/11/2018','PRUEBA',NULL,0,0,'',8,144,'0',1,'',NULL,NULL,NULL,'1'),(146,1,61,1,'13/11/2018','22/11/2018','01:00 p.m.','24/11/2018','Prueba',NULL,0,0,'',8,145,'0',1,'',NULL,NULL,NULL,'1'),(147,2,61,1,'13/11/2018','22/11/2018','01:00 p.m.','25/11/2018','SALON DE EVENTOS GABY',NULL,0,0,'ENTRE CALLE GALO SOBERON Y PARRA ESQUINA ABASOLO, COLONIA CENTRO, TELEFONO 5555555555',7,146,'0',1,'',NULL,NULL,NULL,'1'),(148,1,61,1,'14/11/2018','14/11/2018','01:00 p.m.','17/11/2018','prueba 870',NULL,0,0,'',7,147,'0',1,'',NULL,NULL,NULL,'1'),(149,1,61,1,'14/11/2018','14/11/2018','01:00 p.m.','16/11/2018','prueba',NULL,0,0,'',7,148,'1',1,'',NULL,NULL,NULL,'1'),(150,5,61,1,'14/11/2018','14/11/2018','01:00 p.m.','16/11/2018','prueba 3',NULL,0,0,'',7,149,'0',1,'',NULL,NULL,NULL,'1'),(151,1,61,1,'14/11/2018','14/11/2018','01:00 p.m.','17/11/2018','prueba 4',NULL,0,0,'',8,150,'0',2,'',NULL,NULL,NULL,'1'),(152,1,61,1,'14/11/2018','14/11/2018','01:50 p.m.','16/11/2018','prueba 5',NULL,0,0,'',7,151,'0',1,'',NULL,NULL,NULL,'1'),(153,5,30,1,'14/11/2018','14/11/2018','01:00 p.m.','16/11/2018','prueba 6',NULL,0,0,'',7,152,'1',1,'',NULL,NULL,NULL,'1'),(154,2,49,1,'15/11/2018','15/11/2018','01:00 p.m.','17/11/2018','SALON DE EVENTOS MORLETT SA DE CV',NULL,10,10,'CALLE SAN JUAN 117, COLONIA CENTRO, CIUDAD DE MEXICO CP. 06400',7,153,'1',1,'',NULL,NULL,NULL,'1'),(155,2,61,1,'20/11/2018','14/12/2018','01:00 p.m.','16/12/2018','prueba',NULL,10,0,'',7,154,'1',1,'',NULL,NULL,NULL,'1'),(156,1,22,1,'21/11/2018','16/12/2018','01:00 p.m.','18/12/2018','prueba 4',NULL,0,0,'',8,155,'0',1,'',NULL,NULL,NULL,'1'),(157,1,77,1,'21/11/2018','15/12/2018','01:00 p.m.','15/12/2018','prueba 5',NULL,0,0,'',8,156,'0',1,'',NULL,NULL,NULL,'1'),(158,1,77,1,'21/11/2018','15/12/2018','01:00 p.m.','16/12/2018','prueba 6',NULL,0,0,'',8,157,'0',1,'',NULL,NULL,NULL,'1'),(159,1,16,1,'26/11/2018','14/12/2018','01:00 p.m.','15/12/2018','PRUEBA 7',NULL,0,0,'',7,158,'0',1,'',NULL,NULL,NULL,'1'),(160,2,49,1,'29/11/2018','15/12/2018','02:00 p.m.','16/12/2018','SALON FLAMINGOS',NULL,0,0,'EN LA ENTRADA DE LA PUERTA NEGRA, JUNTO CON UN ARBOL',7,159,'1',1,'',NULL,NULL,NULL,'1'),(161,1,36,1,'29/11/2018','15/12/2018','06:00 p.m.','16/12/2018','SALON DE FIESTAS MARGARITA',NULL,10,0,'',7,160,'0',1,'',NULL,NULL,NULL,'1'),(162,1,38,1,'29/11/2018','15/12/2018','01:00 p.m.','16/12/2018','SALON MARINA',NULL,500,0,'AVENIDA CUAUHTEMOC 62, SAN MATEO, CP 3900, AUN LADO DEL OXXO',8,161,'0',1,'',NULL,NULL,NULL,'1'),(163,1,49,1,'30/11/2018','15/12/2018','03:00 p.m.','16/12/2018','COTIZACION',NULL,0,0,'',7,162,'0',1,'',NULL,NULL,NULL,'1'),(164,1,77,1,'30/11/2018','15/12/2018','01:00 p.m.','17/12/2018','15 al 17',NULL,0,0,'',7,163,'0',1,'',NULL,NULL,NULL,'1'),(165,1,22,1,'4/12/2018','30/12/2018','01:00 p.m.','31/12/2018','<asdsd',NULL,0,0,'',7,164,'0',1,'',NULL,NULL,NULL,'1'),(166,1,19,1,'4/12/2018','30/12/2018','01:00 p.m.','31/12/2018','asd',NULL,0,0,'',8,165,'0',1,'',NULL,NULL,NULL,'1'),(167,1,19,1,'4/12/2018','30/12/2018','01:00 p.m.','31/12/2018','hgfh',NULL,0,0,'',8,166,'0',1,'',NULL,NULL,NULL,'1'),(168,1,21,1,'4/12/2018','30/12/2018','01:00 p.m.','31/12/2018','dszfxgcfhg',NULL,0,0,'',8,167,'0',1,'',NULL,NULL,NULL,'1'),(169,1,74,1,'13/12/2018','28/12/2018','01:00 p.m.','29/12/2018','prueba 1',NULL,0,0,'',7,168,'0',1,'',NULL,NULL,NULL,'1'),(170,1,5,1,'08/01/2019','26/01/2019','01:00 p.m.','27/01/2019','prueba',NULL,0,0,'',7,169,'0',1,'',NULL,NULL,NULL,'1'),(171,1,79,1,'14/01/2019','25/01/2019','01:00 p.m.','28/01/2019','Normal con caracteres eps´cialesíales',NULL,0,0,'',7,170,'0',1,'',NULL,NULL,NULL,'1'),(172,1,80,1,'14/01/2019','25/01/2019','01:00 p.m.','31/01/2019','adwsfbg sdf  ñsñ f+ñ´ñ}{as{}d a}{{ñ áéíóú á\'ä',NULL,0,0,'',7,171,'0',1,'',NULL,NULL,NULL,'1'),(173,1,16,1,'14/01/2019','25/01/2019','01:00 p.m.','27/01/2019','+asd´pis´r´w´¨o',NULL,0,0,'',7,172,'0',1,'',NULL,NULL,NULL,'1'),(174,1,80,1,'14/01/2019','25/01/2019','01:00 p.m.','27/01/2019','DESCRIPCION',NULL,0,0,'',7,173,'0',1,'',NULL,NULL,NULL,'1'),(175,2,80,1,'14/01/2019','25/01/2019','9:00 a 14:00 Hrs','26/01/2019','Empresa: Maver San Martín\r\nDirección: Carretera San Martín de las Flores km. 2.5 #520 int. 6\r\nParque industrial Prologis\r\nTlaquepaque, Jalisco.',NULL,0,0,'',7,174,'1',1,'15:00 a 18:00 Hrs',NULL,NULL,NULL,'1'),(176,1,61,1,'21/01/2019','25/01/2019','9:00 a 14:00 Hrs','26/01/2019','COTIZACION 1',NULL,0,0,'',7,175,'0',1,'15:00 a 18:00 Hrs',NULL,NULL,NULL,'1'),(177,1,65,1,'21/01/2019','25/01/2019','9:00 a 14:00 Hrs','26/01/2019','PEDIDO 1',NULL,0,0,'',7,176,'0',2,'15:00 a 18:00 Hrs',NULL,NULL,NULL,'1'),(178,1,61,1,'22/01/2019','30/01/2019','1:00 a 2:00','31/01/2019','SPLIT',NULL,0,0,'',7,177,'0',1,'13:00',NULL,NULL,NULL,'1'),(179,1,57,1,'22/01/2019','30/01/2019','13:00 a 14:00','31/01/2019','SPLIT 2',NULL,0,0,'',7,178,'0',1,'22:00',NULL,NULL,NULL,'1'),(180,1,38,1,'22/01/2019','03/01/2019','21:00 a 22:00','04/01/2019','SPLIT 3',NULL,0,0,'',7,179,'0',1,'22:00 a 23:00',NULL,NULL,NULL,'1'),(181,1,22,1,'22/01/2019','30/01/2019','13:00 a 14:00','31/01/2019','split 4',NULL,0,0,'',7,180,'0',1,'20:00 a 20:00','30/01/2019',NULL,NULL,'1'),(182,1,74,1,'23/01/2019','30/01/2019','1:00 a 2:00','31/01/2019','reporte categorias',NULL,0,0,'',8,181,'0',1,'4:00 a 10:00','31/01/2019',NULL,NULL,'1'),(183,1,16,1,'24/01/2019','23/01/2019','1:00 a 2:00','24/01/2019','IVA 16',NULL,10,16,'',7,182,'0',1,'3:00 a 4:00','31/01/2019',NULL,NULL,'1'),(184,1,38,1,'25/01/2019','25/01/2019','1:00 a 2:00','26/01/2019','DATOS LLENOS','50.00',1402.25,16,'',7,183,'0',1,'3:00 a 4:00','25/01/2019',1000000,50000000,'1'),(185,1,38,1,'25/01/2019','26/01/2019','20:00 a 21:00','27/01/2019','COMPLETOS','10.00',440,16,'',7,184,'0',1,'22:00 a 23:00','27/01/2019',300,5000,'1'),(186,1,30,1,'25/01/2019','25/01/2019','22:00 a 23:00','26/01/2019','calculo en totales',NULL,100,16,'',8,185,'0',1,'21:00 a 22:00','26/01/2019',200,200,'1'),(187,5,81,1,'28/01/2019','01/02/2019','10:00 a 11:00','04/02/2019','SALON EVENTOS FLAMINGOS, calle olivalres 13, centro',NULL,100,16,'cometarios',7,186,'0',1,'19:00 a 21:00','02/02/2019',1000,500,'1'),(188,1,19,1,'28/01/2019','27/02/2019','21:00 a 22:00','28/02/2019','DESCRIPCION DESCUENTOS',NULL,100,16,'',7,187,'0',1,'22:00 a 23:00','28/02/2019',100,200,'1'),(189,1,16,1,'29/01/2019','25/01/2019','21:00 a 22:00','26/01/2019','TOTALES VERSION 11','10.00',363.8,16,'',7,188,'0',1,'23:00 a 23:00','26/01/2019',200,500,'1'),(190,1,19,1,'29/01/2019','30/01/2019','1:00 a 2:00','31/01/2019','PRUBEA 10000','10.00',340,16,'',7,189,'0',1,'3:00 a 4:00','31/01/2019',1000,500,'1'),(191,1,10,1,'29/01/2019','01/02/2019','5:00 a 6:00','02/02/2019','FEBRERO','16.00',1294.4,16,'',7,190,'0',1,'5:00 a 3:00','02/02/2019',100,100,'1'),(192,1,79,1,'29/01/2019','31/01/2019','3:00 a 2:00','31/01/2019','PRUEBA','10.00',16.5,16,'',7,191,'0',1,'4:00 a 2:00','31/01/2019',100,500,'1'),(193,1,21,1,'29/01/2019','31/01/2019','1:00 a 1:00','31/01/2019','asd','',0,0,'',8,192,'0',1,'2:00 a 2:00','31/01/2019',0,0,'1'),(194,1,44,1,'29/01/2019','31/01/2019','3:00 a 3:00','31/01/2019','oooooooooooo','0.00',0,0,'',10,193,'0',2,'2:00 a 2:00','31/01/2019',0,0,'1'),(195,1,38,1,'29/01/2019','31/01/2019','2:00 a 4:00','31/01/2019','aaaaaaaaaaaaaaaa','10.00',166.1,16,'',7,194,'0',2,'1:00 a 1:00','31/01/2019',0,1000,'1'),(196,1,74,1,'31/01/2019','31/01/2019','20:00 a 21:00','31/01/2019','','0',0,0,'',7,195,'0',1,'20:00 a 20:00','31/01/2019',0,0,'1'),(197,1,30,1,'06/02/2019','28/02/2019','20:00 a 21:00','28/02/2019','prueba 28 feb','0',0,0,'',8,196,'0',1,'21:00 a 23:00','28/02/2019',0,0,'1'),(198,1,57,1,'06/02/2019','28/02/2019','2:00 a 2:00','28/02/2019','prueba 28 feb 222222','0',0,0,'',8,197,'0',1,'3:00 a 3:00','28/02/2019',0,0,'1'),(199,1,22,1,'06/02/2019','28/02/2019','2:00 a 4:00','28/02/2019','324234234 mmmmmmm ','0',0,0,'',8,198,'0',1,'5:00 a 5:00','28/02/2019',0,0,'1'),(200,1,44,1,'07/02/2019','28/02/2019','4:00 a 4:00','28/02/2019','conexion 333','0.00',0,0,'',7,199,'0',1,'4:00 a 4:00','28/02/2019',0,0,'1'),(201,1,16,1,'08/02/2019','28/02/2019','21:00 a 21:00','28/02/2019','descuentos','0',0,0,'',8,200,'0',1,'21:00 a 23:00','28/02/2019',0,0,'1'),(202,1,36,1,'08/02/2019','28/02/2019','1:00 a 2:00','28/02/2019','asdd','0',0,0,'',8,201,'0',1,'5:00 a 4:00','28/02/2019',0,0,'1'),(203,1,65,1,'08/02/2019','28/02/2019','22:00 a 22:00','28/02/2019','asdsdsad','0.00',0,0,'',7,202,'0',1,'22:00 a 22:00','28/02/2019',3000,0,'1'),(204,1,34,1,'08/02/2019','28/02/2019','2:00 a 2:00','28/02/2019','asdasd','0',0,10,'',10,203,'0',1,'2:00 a 2:00','28/02/2019',0,1000,'1'),(205,1,44,1,'08/02/2019','28/02/2019','3:00 a 10:00','28/02/2019','asd','0',0,0,'',8,204,'0',1,'3:00 a 2:00','28/02/2019',500,0,'1'),(206,1,77,1,'08/02/2019','28/02/2019','2:00 a 3:00','28/02/2019','asds','10',4,0,'',7,205,'0',1,'2:00 a 2:00','28/02/2019',0,2000,'1'),(207,1,49,1,'08/02/2019','28/02/2019','3:00 a 3:00','28/02/2019','asds','5.00',0.75,0,'',8,206,'0',1,'2:00 a 2:00','28/02/2019',0,100,'1'),(208,1,34,1,'08/02/2019','28/02/2019','2:00 a 3:00','28/02/2019','mostrar precios','10.00',800,16,'',8,207,'0',1,'2:00 a 3:00','28/02/2019',1000,600,'0'),(209,1,38,1,'08/02/2019','28/02/2019','4:00 a 4:00','28/02/2019','asd','20.00',17,0,'',8,208,'0',2,'2:00 a 3:00','28/02/2019',0,0,'1'),(210,1,19,1,'14/02/2019','28/02/2019','5:00 a 5:00','28/02/2019','ABONOS','0.00',0,0,'',8,209,'0',2,'3:00 a 3:00','28/02/2019',0,0,'1'),(211,1,74,1,'25/02/2019','28/02/2019','1:00 a 1:00','28/02/2019','fecha pago','0',0,0,'',7,210,'0',1,'1:00 a 3:00','28/02/2019',0,0,'1'),(212,1,74,1,'25/02/2019','28/02/2019','1:00 a 1:00','28/02/2019','asdasds','0',0,0,'',7,211,'0',1,'2:00 a 1:00','28/02/2019',0,0,'1'),(213,1,74,1,'22/03/2019','29/03/2019','1:00 a 1:00','30/03/2019','wqerty','0',0,0,'',7,212,'0',1,'1:00 a 1:00','31/03/2019',0,0,'1'),(214,1,65,1,'19/05/2019','31/05/2019','1:00 a 1:00','31/05/2019','.','0',0,0,'',7,213,'0',1,'1:00 a 1:00','31/05/2019',0,0,'0'),(215,1,16,1,'15/08/2019','07/09/2019','20:00 a 21:00','09/09/2019','Salon flamingos','0.00',0,16,'',7,214,'0',1,'22:00 a 23:00','08/09/2019',1000,500,'1');
/*!40000 ALTER TABLE `renta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sub_categoria_contabilidad`
--

DROP TABLE IF EXISTS `sub_categoria_contabilidad`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `sub_categoria_contabilidad` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `id_categoria_contabilidad` int(11) NOT NULL,
  `descripcion` varchar(150) DEFAULT NULL,
  `ingreso` enum('1','0') NOT NULL DEFAULT '1',
  `fg_activo` enum('1','0') NOT NULL DEFAULT '1',
  PRIMARY KEY (`id`),
  KEY `fk_categoria_contabilidad` (`id_categoria_contabilidad`),
  CONSTRAINT `fk_categoria_contabilidad` FOREIGN KEY (`id_categoria_contabilidad`) REFERENCES `categoria_contabilidad` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sub_categoria_contabilidad`
--

LOCK TABLES `sub_categoria_contabilidad` WRITE;
/*!40000 ALTER TABLE `sub_categoria_contabilidad` DISABLE KEYS */;
INSERT INTO `sub_categoria_contabilidad` VALUES (1,1,'sueldos','0','1'),(2,1,'gasolina','0','1'),(3,2,'facturacion','0','1'),(4,2,'nomina','0','1');
/*!40000 ALTER TABLE `sub_categoria_contabilidad` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tipo`
--

DROP TABLE IF EXISTS `tipo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tipo` (
  `id_tipo` int(10) unsigned NOT NULL AUTO_INCREMENT,
  `tipo` varchar(45) NOT NULL,
  PRIMARY KEY (`id_tipo`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tipo`
--

LOCK TABLES `tipo` WRITE;
/*!40000 ALTER TABLE `tipo` DISABLE KEYS */;
INSERT INTO `tipo` VALUES (1,'Pedido'),(2,'Cotizacion');
/*!40000 ALTER TABLE `tipo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tipo_abono`
--

DROP TABLE IF EXISTS `tipo_abono`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `tipo_abono` (
  `id_tipo_abono` int(11) NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(150) DEFAULT NULL,
  `fg_activo` enum('1','0') NOT NULL DEFAULT '1',
  `fecha_registro` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `cuenta_id` int(11) NOT NULL DEFAULT '1',
  PRIMARY KEY (`id_tipo_abono`),
  KEY `fk_cuenta_tipo_abono` (`cuenta_id`),
  CONSTRAINT `tipo_abono_ibfk_1` FOREIGN KEY (`cuenta_id`) REFERENCES `cuenta` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=7 DEFAULT CHARSET=latin1;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tipo_abono`
--

LOCK TABLES `tipo_abono` WRITE;
/*!40000 ALTER TABLE `tipo_abono` DISABLE KEYS */;
INSERT INTO `tipo_abono` VALUES (1,'Efectivo','1','2019-02-14 21:27:52',1),(2,'Transferencia bancaria','1','2019-02-14 21:27:52',1),(3,'Cheque','1','2019-02-14 21:27:53',1),(4,'Deposito a OXXO','1','2019-02-14 22:29:34',1),(5,'Efectivo BBVAs','1','2019-05-18 17:00:07',2),(6,'Deposito a banamex','1','2019-05-19 14:51:51',4);
/*!40000 ALTER TABLE `tipo_abono` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuarios`
--

DROP TABLE IF EXISTS `usuarios`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `usuarios` (
  `id_usuarios` int(11) NOT NULL AUTO_INCREMENT,
  `nombre` varchar(45) DEFAULT NULL,
  `apellidos` varchar(45) DEFAULT NULL,
  `tel_movil` varchar(45) DEFAULT NULL,
  `tel_fijo` varchar(45) DEFAULT NULL,
  `direccion` varchar(45) DEFAULT NULL,
  `administrador` varchar(5) DEFAULT NULL,
  `nivel1` varchar(5) DEFAULT NULL,
  `nivel2` varchar(5) DEFAULT NULL,
  `contrasenia` varchar(45) DEFAULT NULL,
  `activo` varchar(5) DEFAULT NULL,
  `id_puesto` int(11) DEFAULT NULL,
  PRIMARY KEY (`id_usuarios`),
  KEY `fk_usuarios_puesto1_idx` (`id_puesto`),
  CONSTRAINT `fk_usuarios_puesto1` FOREIGN KEY (`id_puesto`) REFERENCES `puesto` (`id_puesto`) ON DELETE NO ACTION ON UPDATE NO ACTION
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuarios`
--

LOCK TABLES `usuarios` WRITE;
/*!40000 ALTER TABLE `usuarios` DISABLE KEYS */;
INSERT INTO `usuarios` VALUES (1,'Liz','Tapia Luviano','132123','123123','null','1','0',NULL,'0000','1',3),(7,'Antonio','Orozco Mancilla','7471285672','','','0','1',NULL,'12345','1',1),(8,'leonardo gabriel','bustillos villalobos','7471366538','.','col.progreso calle ayutla #9','0','1',NULL,'.','1',1),(9,'Prueba','Luna Garcia','8856975','6152309861','','1','0',NULL,'00000','1',2),(10,'Nivel1','perrita','32456','23456','','0','1',NULL,'nivel1','1',1);
/*!40000 ALTER TABLE `usuarios` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2019-09-10 16:42:25
