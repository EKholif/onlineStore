-- MySQL dump 10.13  Distrib 8.0.31, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: shop
-- ------------------------------------------------------
-- Server version	8.0.31

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Dumping data for table `shipping_rates`
--

LOCK TABLES `shipping_rates` WRITE;
/*!40000 ALTER TABLE `shipping_rates` DISABLE KEYS */;
INSERT INTO `shipping_rates` VALUES (2,234,'New York',10,7,_binary ''),(3,234,'Florida',12,6,_binary ''),(5,242,'Hanoi',3.98,2,_binary ''),(6,234,'California',11.56,6,_binary ''),(7,242,'Hai Phong',3.93,2,_binary ''),(8,242,'Bac Giang',4.12,2,_binary ''),(9,242,'Phu Tho',4.21,3,_binary ''),(10,242,'Thanh Hoa',3.52,1,_binary ''),(11,106,'Karnataka',8.22,5,_binary ''),(12,106,'Maharashtra',8.69,5,_binary ''),(13,106,'Meghalaya',8.1,4,_binary '\0'),(14,106,'Punjab',7.89,3,_binary '\0'),(15,106,'Tamil Nadu',8.25,4,_binary ''),(16,106,'Telangana',7.72,4,_binary ''),(17,242,'Da Nang',0.5,1,_binary ''),(18,234,'Ohio',11.5,8,_binary ''),(19,78,'London',9.88,6,_binary ''),(20,106,'Delhi',8.45,5,_binary ''),(21,106,'West Bengal',8.88,5,_binary ''),(22,78,'Barton',7.78,6,_binary ''),(23,106,'Andhra Pradesh',8.12,6,_binary ''),(24,234,'Tennessee',12,8,_binary ''),(25,234,'Massachusetts',11.85,7,_binary ''),(26,14,'Queensland',4.99,5,_binary ''),(27,199,'Singapore',3.33,3,_binary ''),(28,39,'British Columbia',9.88,7,_binary ''),(29,14,'New South Wales',4.57,6,_binary ''),(30,234,'Illinois',13,9,_binary ''),(32,234,'Mississippi',9,9,_binary ''),(33,16,'ssss',7,7,_binary ''),(34,39,'ON',1,5,_binary '');
/*!40000 ALTER TABLE `shipping_rates` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-09-18 22:33:06
