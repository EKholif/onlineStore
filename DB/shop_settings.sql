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
-- Dumping data for table `settings`
--

LOCK TABLES `settings` WRITE;
/*!40000 ALTER TABLE `settings` DISABLE KEYS */;
INSERT INTO `settings` VALUES ('COPY_RIGHT','Copyright © GLink','GENERAL'),('COPYRIGHT','Copyright (C) 2021 Shopme Ltd.','GENERAL'),('CURRENCY_ID','1','CURRENCY'),('CURRENCY_SYMBOLE','$','CURRENCY'),('CURRENCY_SYMBOLE_POSITION','Before Price','CURRENCY'),('CUSTOMER_VERIFY_CONTENT','Copyright (C) 2021 Shopme Ltd.','MAIL_TEMPLATES'),('CUSTOMER_VERIFY_SUBJECT','Copyright (C) 2021 Shopme Ltd.','MAIL_TEMPLATES'),('DECIMAL_DIGITS','1','CURRENCY'),('DECIMAL_POINT_TYPE','POINT','CURRENCY'),('MAIL_FROM','admin@fungigrotto.com','MAIL_SERVER'),('MAIL_HOST','localhost','MAIL_SERVER'),('MAIL_PASSWORD','bob','MAIL_SERVER'),('MAIL_PORT','587','MAIL_SERVER'),('MAIL_SENDER_NAME','Copyright (C) 2021 Shopme Ltd.','MAIL_SERVER'),('MAIL_USERNAME','admin@fungigrotto.com','MAIL_SERVER'),('ORDER_CONFIRMATION_CONTENT','<div style=\"background-color:#ffffff;color:#080808\"><div style=\"background-color:#ffffff;color:#080808\"><pre style=\"font-family:\'Cascadia Code\',monospace;font-size:11.3pt;\"><span style=\"color:#6a84db;font-weight:bold;\">[[name]]\"</span>, order.<br><span style=\"color:#6a84db;font-weight:bold;\">[[orderId]]\"</span>, <span style=\"color:#000000;\">Str</span><span style=\"color:#000000;\"><br></span><span style=\"color:#6a84db;font-weight:bold;\">[[orderTime]]\"</span>, <span style=\"color:#000000;\">o</span><span style=\"color:#000000;\"></span><span style=\"color:#6a84db;font-weight:bold;\"><br>[[shippingAddress]]<br></span><span style=\"color:#6a84db;font-weight:bold;\"><br></span><span style=\"color:#6a84db;font-weight:bold;\">[[total]]\"</span>, <span style=\"color:#000000;\">total</span><span style=\"color:#000000;\"><br></span><span style=\"color:#6a84db;font-weight:bold;\">[[paymentMethod]]<br></span><br></pre></div></div>','MAIL_TEMPLATES'),('ORDER_CONFIRMATION_SUBJECT','ORDER_CONFIRMATION','MAIL_TEMPLATES'),('PAYPAL_API_BASE_URL','https://api-m.sandbox.paypal.com','PAYMENT'),('PAYPAL_API_CLIENT_ID','AXJHz4OA-_lzBdIrxkbFhHZsW-X63y3qrChR2RBU7HnkONcuEj5tOX2cnlrURUWiEGFoV4U3n4IsqpBb','PAYMENT'),('PAYPAL_API_CLIENT_SECRET','EGsMcLjVJLDgl7e1_0nmZ5ozjxGYhxKW_UmIUoIrMeBnMXuU07onIG9qDvpIsL4bitQHv5PAxhd-6RN1','PAYMENT'),('SITE_LOGO','/site-logo/Matthew.png','GENERAL'),('SITE_NAME','Online Shop','GENERAL'),('SMTP_AUTH','true','MAIL_SERVER'),('STMP_SECURED','true','MAIL_SERVER'),('THOUSANDS_POINT_TYPE','COMMA','CURRENCY');
/*!40000 ALTER TABLE `settings` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-09-18 22:33:07
