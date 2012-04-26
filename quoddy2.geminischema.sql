-- MySQL dump 10.13  Distrib 5.1.61, for debian-linux-gnu (x86_64)
--
-- Host: localhost    Database: quoddy2
-- ------------------------------------------------------
-- Server version	5.1.61-0ubuntu0.11.10.1

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
-- Table structure for table `activity`
--


DROP TABLE IF EXISTS `activity`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `activity` (
  `id` bigint(20) NOT NULL,
  `actor_content` varchar(255) DEFAULT NULL,
  `actor_display_name` varchar(255) DEFAULT NULL,
  `actor_image_height` varchar(255) DEFAULT NULL,
  `actor_image_url` varchar(255) DEFAULT NULL,
  `actor_image_width` varchar(255) DEFAULT NULL,
  `actor_object_type` varchar(255) DEFAULT NULL,
  `actor_url` varchar(255) DEFAULT NULL,
  `actor_uuid` varchar(255) DEFAULT NULL,
  `content` varchar(255) NOT NULL,
  `generator_url` varchar(255) DEFAULT NULL,
  `icon` tinyblob,
  `object_content` varchar(255) DEFAULT NULL,
  `object_display_name` varchar(255) DEFAULT NULL,
  `object_image_height` varchar(255) DEFAULT NULL,
  `object_image_url` varchar(255) DEFAULT NULL,
  `object_image_width` varchar(255) DEFAULT NULL,
  `object_object_type` varchar(255) DEFAULT NULL,
  `object_url` varchar(255) DEFAULT NULL,
  `object_uuid` varchar(255) DEFAULT NULL,
  `provider_url` varchar(255) DEFAULT NULL,
  `published` datetime NOT NULL,
  `target_content` varchar(255) DEFAULT NULL,
  `target_display_name` varchar(255) DEFAULT NULL,
  `target_image_height` varchar(255) DEFAULT NULL,
  `target_image_url` varchar(255) DEFAULT NULL,
  `target_image_width` varchar(255) DEFAULT NULL,
  `target_object_type` varchar(255) DEFAULT NULL,
  `target_url` varchar(255) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `updated` datetime DEFAULT NULL,
  `url` tinyblob NOT NULL,
  `uuid` varchar(255) NOT NULL,
  `verb` varchar(255) NOT NULL,
  
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",

  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `activity`
--

LOCK TABLES `activity` WRITE;
/*!40000 ALTER TABLE `activity` DISABLE KEYS */;
/*!40000 ALTER TABLE `activity` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `calendar_event`
--

DROP TABLE IF EXISTS `calendar_event`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `calendar_event` (
  `id` bigint(20) NOT NULL,
  `date_event_created` datetime NOT NULL,
  `description` longtext,
  `end_date` datetime DEFAULT NULL,
  `geo_lat` float NOT NULL,
  `geo_long` float NOT NULL,
  `last_modified` datetime DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `start_date` datetime NOT NULL,
  `status` varchar(255) DEFAULT NULL,
  `summary` varchar(255) DEFAULT NULL,
  `uid` varchar(255) NOT NULL,
  `url` varchar(255) DEFAULT NULL,
  `uuid` varchar(255) NOT NULL,
  
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",
 
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `calendar_event`
--

LOCK TABLES `calendar_event` WRITE;
/*!40000 ALTER TABLE `calendar_event` DISABLE KEYS */;
/*!40000 ALTER TABLE `calendar_event` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `calendar_feed`
--

DROP TABLE IF EXISTS `calendar_feed`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `calendar_feed` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `name` varchar(255) NOT NULL,
  `owner_id` bigint(20) NOT NULL,
  `url` varchar(255) NOT NULL,
  `uuid` varchar(255) NOT NULL,

  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",

  PRIMARY KEY (`id`),
  KEY `FK74FCF9FFD1B1E9A7` (`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `calendar_feed`
--

LOCK TABLES `calendar_feed` WRITE;
/*!40000 ALTER TABLE `calendar_feed` DISABLE KEYS */;
/*!40000 ALTER TABLE `calendar_feed` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `contact_address`
--

DROP TABLE IF EXISTS `contact_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `contact_address` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `address` varchar(255) NOT NULL,
  `date_created` datetime NOT NULL,
  `service_type` int(11) NOT NULL,
  
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",

  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `contact_address`
--

LOCK TABLES `contact_address` WRITE;
/*!40000 ALTER TABLE `contact_address` DISABLE KEYS */;
/*!40000 ALTER TABLE `contact_address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `educational_experience`
--

DROP TABLE IF EXISTS `educational_experience`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `educational_experience` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `course_of_study` varchar(255) DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `institution_name` varchar(255) DEFAULT NULL,
  `month_from` varchar(255) DEFAULT NULL,
  `month_to` varchar(255) DEFAULT NULL,
  `year_from` varchar(255) DEFAULT NULL,
  `year_to` varchar(255) DEFAULT NULL,

  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",

  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `educational_experience`
--

LOCK TABLES `educational_experience` WRITE;
/*!40000 ALTER TABLE `educational_experience` DISABLE KEYS */;
/*!40000 ALTER TABLE `educational_experience` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_base`
--

DROP TABLE IF EXISTS `event_base`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event_base` (
  `id` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `effective_date` datetime NOT NULL,
  `name` varchar(255) NOT NULL,
  `owner_id` bigint(20) DEFAULT NULL,
  `target_uuid` varchar(255) NOT NULL,
  

  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",

  PRIMARY KEY (`id`),
  KEY `FK3AA3DD56D1B1E9A7` (`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_base`
--

LOCK TABLES `event_base` WRITE;
/*!40000 ALTER TABLE `event_base` DISABLE KEYS */;
/*!40000 ALTER TABLE `event_base` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `event_subscription`
--

DROP TABLE IF EXISTS `event_subscription`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `event_subscription` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `description` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `owner_id` bigint(20) NOT NULL,
  `uuid` varchar(255) NOT NULL,
  `x_query_expression` varchar(255) NOT NULL,

  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",

  PRIMARY KEY (`id`),
  KEY `FK4623DAA2D1B1E9A7` (`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `event_subscription`
--

LOCK TABLES `event_subscription` WRITE;
/*!40000 ALTER TABLE `event_subscription` DISABLE KEYS */;
/*!40000 ALTER TABLE `event_subscription` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `favorite`
--

DROP TABLE IF EXISTS `favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `favorite` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,

  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",

  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `favorite`
--

LOCK TABLES `favorite` WRITE;
/*!40000 ALTER TABLE `favorite` DISABLE KEYS */;
/*!40000 ALTER TABLE `favorite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `friend_collection`
--

DROP TABLE IF EXISTS `friend_collection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `friend_collection` (
  `id` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `owner_uuid` varchar(255) NOT NULL,

  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",

  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `friend_collection`
--

LOCK TABLES `friend_collection` WRITE;
/*!40000 ALTER TABLE `friend_collection` DISABLE KEYS */;
/*!40000 ALTER TABLE `friend_collection` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `friend_collection_friends`
--

DROP TABLE IF EXISTS `friend_collection_friends`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `friend_collection_friends` (
  `friend_collection_id` bigint(20) DEFAULT NULL,
  `friends_string` varchar(255) DEFAULT NULL,
  
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",
  KEY `FK9DE56C351508A68C` (`friend_collection_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `friend_collection_friends`
--

LOCK TABLES `friend_collection_friends` WRITE;
/*!40000 ALTER TABLE `friend_collection_friends` DISABLE KEYS */;
/*!40000 ALTER TABLE `friend_collection_friends` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `friend_request_collection`
--

DROP TABLE IF EXISTS `friend_request_collection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `friend_request_collection` (
  `id` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `owner_uuid` varchar(255) NOT NULL,

  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",

  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `friend_request_collection`
--

LOCK TABLES `friend_request_collection` WRITE;
/*!40000 ALTER TABLE `friend_request_collection` DISABLE KEYS */;
/*!40000 ALTER TABLE `friend_request_collection` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `friend_request_collection_friend_requests`
--

DROP TABLE IF EXISTS `friend_request_collection_friend_requests`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `friend_request_collection_friend_requests` (
  `friend_request_collection_id` bigint(20) DEFAULT NULL,
  `friend_requests_string` varchar(255) DEFAULT NULL,
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",
  KEY `FK85236D95D7D78385` (`friend_request_collection_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `friend_request_collection_friend_requests`
--

LOCK TABLES `friend_request_collection_friend_requests` WRITE;
/*!40000 ALTER TABLE `friend_request_collection_friend_requests` DISABLE KEYS */;
/*!40000 ALTER TABLE `friend_request_collection_friend_requests` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `historical_employer`
--

DROP TABLE IF EXISTS `historical_employer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `historical_employer` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `company_name` varchar(255) NOT NULL,
  `description` varchar(255) NOT NULL,
  `month_from` varchar(255) DEFAULT NULL,
  `month_to` varchar(255) DEFAULT NULL,
  `title` varchar(255) NOT NULL,
  `year_from` varchar(255) DEFAULT NULL,
  `year_to` varchar(255) DEFAULT NULL,

  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",

  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `historical_employer`
--

LOCK TABLES `historical_employer` WRITE;
/*!40000 ALTER TABLE `historical_employer` DISABLE KEYS */;
/*!40000 ALTER TABLE `historical_employer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ifollow_collection`
--

DROP TABLE IF EXISTS `ifollow_collection`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ifollow_collection` (
  `id` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `owner_uuid` varchar(255) NOT NULL,

  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",

  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ifollow_collection`
--

LOCK TABLES `ifollow_collection` WRITE;
/*!40000 ALTER TABLE `ifollow_collection` DISABLE KEYS */;
/*!40000 ALTER TABLE `ifollow_collection` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ifollow_collection_i_follow`
--

DROP TABLE IF EXISTS `ifollow_collection_i_follow`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `ifollow_collection_i_follow` (
  `ifollow_collection_id` bigint(20) DEFAULT NULL,
  `i_follow_string` varchar(255) DEFAULT NULL,
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",
  KEY `FKCBB19A6372B54A5A` (`ifollow_collection_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ifollow_collection_i_follow`
--

LOCK TABLES `ifollow_collection_i_follow` WRITE;
/*!40000 ALTER TABLE `ifollow_collection_i_follow` DISABLE KEYS */;
/*!40000 ALTER TABLE `ifollow_collection_i_follow` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `interest`
--

DROP TABLE IF EXISTS `interest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `interest` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `name` varchar(255) NOT NULL,
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",

  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `interest`
--

LOCK TABLES `interest` WRITE;
/*!40000 ALTER TABLE `interest` DISABLE KEYS */;
/*!40000 ALTER TABLE `interest` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `language`
--

DROP TABLE IF EXISTS `language`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `language` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",

  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `language`
--

LOCK TABLES `language` WRITE;
/*!40000 ALTER TABLE `language` DISABLE KEYS */;
/*!40000 ALTER TABLE `language` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `local_account`
--

DROP TABLE IF EXISTS `local_account`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `local_account` (
  `id` bigint(20) NOT NULL,
  `version` bigint(20) NOT NULL,
  `password` varchar(255) NOT NULL,
  `username` varchar(255) NOT NULL,
  `uuid` varchar(255) NOT NULL,
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",

  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `local_account`
--

LOCK TABLES `local_account` WRITE;
/*!40000 ALTER TABLE `local_account` DISABLE KEYS */;
/*!40000 ALTER TABLE `local_account` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `organization_association`
--

DROP TABLE IF EXISTS `organization_association`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `organization_association` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `name` varchar(255) NOT NULL,
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",

  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `organization_association`
--

LOCK TABLES `organization_association` WRITE;
/*!40000 ALTER TABLE `organization_association` DISABLE KEYS */;
/*!40000 ALTER TABLE `organization_association` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `photograph`
--

DROP TABLE IF EXISTS `photograph`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `photograph` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `path` varchar(255) NOT NULL,
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",

  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `photograph`
--

LOCK TABLES `photograph` WRITE;
/*!40000 ALTER TABLE `photograph` DISABLE KEYS */;
/*!40000 ALTER TABLE `photograph` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `profile`
--

DROP TABLE IF EXISTS `profile`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `profile` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `birth_day_of_month` int(11) NOT NULL,
  `birth_month` int(11) NOT NULL,
  `birth_year` int(11) NOT NULL,
  `date_created` datetime NOT NULL,
  `hometown` varchar(255) DEFAULT NULL,
  `location` varchar(255) DEFAULT NULL,
  `photo_id` bigint(20) DEFAULT NULL,
  `sex` int(11) NOT NULL,
  `summary` varchar(255) DEFAULT NULL,
  `user_uuid` varchar(255) NOT NULL,
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",

  PRIMARY KEY (`id`),
  KEY `FKED8E89A9E8EECC85` (`photo_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `profile`
--

LOCK TABLES `profile` WRITE;
/*!40000 ALTER TABLE `profile` DISABLE KEYS */;
/*!40000 ALTER TABLE `profile` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `profile_contact_address`
--

DROP TABLE IF EXISTS `profile_contact_address`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `profile_contact_address` (
  `profile_contact_addresses_id` bigint(20) DEFAULT NULL,
  `contact_address_id` bigint(20) DEFAULT NULL,
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",
  KEY `FKF0A9B43F8E5990FC` (`profile_contact_addresses_id`),
  KEY `FKF0A9B43FE7D62DD3` (`contact_address_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `profile_contact_address`
--

LOCK TABLES `profile_contact_address` WRITE;
/*!40000 ALTER TABLE `profile_contact_address` DISABLE KEYS */;
/*!40000 ALTER TABLE `profile_contact_address` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `profile_educational_experience`
--

DROP TABLE IF EXISTS `profile_educational_experience`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `profile_educational_experience` (
  `profile_education_history_id` bigint(20) DEFAULT NULL,
  `educational_experience_id` bigint(20) DEFAULT NULL,
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",
  KEY `FK3CD2BAACAAA44602` (`profile_education_history_id`),
  KEY `FK3CD2BAAC82E08DA7` (`educational_experience_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `profile_educational_experience`
--

LOCK TABLES `profile_educational_experience` WRITE;
/*!40000 ALTER TABLE `profile_educational_experience` DISABLE KEYS */;
/*!40000 ALTER TABLE `profile_educational_experience` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `profile_favorite`
--

DROP TABLE IF EXISTS `profile_favorite`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `profile_favorite` (
  `profile_favorites_id` bigint(20) DEFAULT NULL,
  `favorite_id` bigint(20) DEFAULT NULL,
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",
  KEY `FK483E3F725B4F6668` (`profile_favorites_id`),
  KEY `FK483E3F729D878B14` (`favorite_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `profile_favorite`
--

LOCK TABLES `profile_favorite` WRITE;
/*!40000 ALTER TABLE `profile_favorite` DISABLE KEYS */;
/*!40000 ALTER TABLE `profile_favorite` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `profile_historical_employer`
--

DROP TABLE IF EXISTS `profile_historical_employer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `profile_historical_employer` (
  `profile_employment_history_id` bigint(20) DEFAULT NULL,
  `historical_employer_id` bigint(20) DEFAULT NULL,
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",
  KEY `FK79F066FAA90E4852` (`profile_employment_history_id`),
  KEY `FK79F066FA1904ED29` (`historical_employer_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `profile_historical_employer`
--

LOCK TABLES `profile_historical_employer` WRITE;
/*!40000 ALTER TABLE `profile_historical_employer` DISABLE KEYS */;
/*!40000 ALTER TABLE `profile_historical_employer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `profile_interest`
--

DROP TABLE IF EXISTS `profile_interest`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `profile_interest` (
  `profile_interests_id` bigint(20) DEFAULT NULL,
  `interest_id` int(11) DEFAULT NULL,
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",
  KEY `FK2B9C1D00691EF854` (`interest_id`),
  KEY `FK2B9C1D007A47CCF6` (`profile_interests_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `profile_interest`
--

LOCK TABLES `profile_interest` WRITE;
/*!40000 ALTER TABLE `profile_interest` DISABLE KEYS */;
/*!40000 ALTER TABLE `profile_interest` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `profile_language`
--

DROP TABLE IF EXISTS `profile_language`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `profile_language` (
  `profile_languages_id` bigint(20) DEFAULT NULL,
  `language_id` bigint(20) DEFAULT NULL,
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",
  KEY `FKA96F092EBF4CF224` (`profile_languages_id`),
  KEY `FKA96F092E2B3C6194` (`language_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `profile_language`
--

LOCK TABLES `profile_language` WRITE;
/*!40000 ALTER TABLE `profile_language` DISABLE KEYS */;
/*!40000 ALTER TABLE `profile_language` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `profile_links`
--

DROP TABLE IF EXISTS `profile_links`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `profile_links` (
  `profile_id` bigint(20) DEFAULT NULL,
  `links_string` varchar(255) DEFAULT NULL,
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",
  KEY `FKD50099A3A2139300` (`profile_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `profile_links`
--

LOCK TABLES `profile_links` WRITE;
/*!40000 ALTER TABLE `profile_links` DISABLE KEYS */;
/*!40000 ALTER TABLE `profile_links` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `profile_organization_association`
--

DROP TABLE IF EXISTS `profile_organization_association`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `profile_organization_association` (
  `profile_organizations_id` bigint(20) DEFAULT NULL,
  `organization_association_id` int(11) DEFAULT NULL,
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",
  KEY `FK92AFD4B5C638B7F` (`profile_organizations_id`),
  KEY `FK92AFD4B2BE19959` (`organization_association_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `profile_organization_association`
--

LOCK TABLES `profile_organization_association` WRITE;
/*!40000 ALTER TABLE `profile_organization_association` DISABLE KEYS */;
/*!40000 ALTER TABLE `profile_organization_association` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `profile_skill`
--

DROP TABLE IF EXISTS `profile_skill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `profile_skill` (
  `profile_skills_id` bigint(20) DEFAULT NULL,
  `skill_id` int(11) DEFAULT NULL,
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",
  KEY `FKD564143B6EC63380` (`skill_id`),
  KEY `FKD564143B847BAEB1` (`profile_skills_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `profile_skill`
--

LOCK TABLES `profile_skill` WRITE;
/*!40000 ALTER TABLE `profile_skill` DISABLE KEYS */;
/*!40000 ALTER TABLE `profile_skill` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `project`
--

DROP TABLE IF EXISTS `project`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `project` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,

  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",

  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `project`
--

LOCK TABLES `project` WRITE;
/*!40000 ALTER TABLE `project` DISABLE KEYS */;
/*!40000 ALTER TABLE `project` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `recommendation`
--

DROP TABLE IF EXISTS `recommendation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `recommendation` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",

  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `recommendation`
--

LOCK TABLES `recommendation` WRITE;
/*!40000 ALTER TABLE `recommendation` DISABLE KEYS */;
/*!40000 ALTER TABLE `recommendation` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `share_target`
--

DROP TABLE IF EXISTS `share_target`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `share_target` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `name` varchar(255) NOT NULL,
  `uuid` varchar(255) NOT NULL,
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",

  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `share_target`
--

LOCK TABLES `share_target` WRITE;
/*!40000 ALTER TABLE `share_target` DISABLE KEYS */;
/*!40000 ALTER TABLE `share_target` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `skill`
--

DROP TABLE IF EXISTS `skill`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `skill` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `name` varchar(255) NOT NULL,
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",

  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `skill`
--

LOCK TABLES `skill` WRITE;
/*!40000 ALTER TABLE `skill` DISABLE KEYS */;
/*!40000 ALTER TABLE `skill` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `status_update`
--

DROP TABLE IF EXISTS `status_update`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `status_update` (
  `id` bigint(20) NOT NULL,
  `creator_id` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `text` varchar(255) NOT NULL,
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",

  PRIMARY KEY (`id`),
  KEY `FK9A27F1B6C0C73D8E` (`creator_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `status_update`
--

LOCK TABLES `status_update` WRITE;
/*!40000 ALTER TABLE `status_update` DISABLE KEYS */;
/*!40000 ALTER TABLE `status_update` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `system_settings`
--

DROP TABLE IF EXISTS `system_settings`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `system_settings` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",

  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `system_settings`
--

LOCK TABLES `system_settings` WRITE;
/*!40000 ALTER TABLE `system_settings` DISABLE KEYS */;
/*!40000 ALTER TABLE `system_settings` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_group`
--

DROP TABLE IF EXISTS `user_group`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_group` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `owner_id` bigint(20) NOT NULL,
  `require_join_confirmation` bit(1) NOT NULL,
  `uuid` varchar(255) NOT NULL,
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",

  PRIMARY KEY (`id`),
  KEY `FK72A9010BD1B1E9A7` (`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_group`
--

LOCK TABLES `user_group` WRITE;
/*!40000 ALTER TABLE `user_group` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_group` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_group_uzer`
--

DROP TABLE IF EXISTS `user_group_uzer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_group_uzer` (
  `user_group_group_members_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",
  KEY `FK9C06ACC665CB398F` (`user_id`),
  KEY `FK9C06ACC6123E3354` (`user_group_group_members_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_group_uzer`
--

LOCK TABLES `user_group_uzer` WRITE;
/*!40000 ALTER TABLE `user_group_uzer` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_group_uzer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_list`
--

DROP TABLE IF EXISTS `user_list`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_list` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `description` varchar(255) DEFAULT NULL,
  `name` varchar(255) NOT NULL,
  `owner_id` bigint(20) NOT NULL,
  `uuid` varchar(255) NOT NULL,
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",

  PRIMARY KEY (`id`),
  KEY `FK14392492D1B1E9A7` (`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_list`
--

LOCK TABLES `user_list` WRITE;
/*!40000 ALTER TABLE `user_list` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_list` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_list_uzer`
--

DROP TABLE IF EXISTS `user_list_uzer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_list_uzer` (
  `user_list_members_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL,
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",
  KEY `FK1472A59F65CB398F` (`user_id`),
  KEY `FK1472A59F5C57180C` (`user_list_members_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_list_uzer`
--

LOCK TABLES `user_list_uzer` WRITE;
/*!40000 ALTER TABLE `user_list_uzer` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_list_uzer` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_stream`
--

DROP TABLE IF EXISTS `user_stream`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `user_stream` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT,
  `version` bigint(20) NOT NULL,
  `date_created` datetime NOT NULL,
  `defined_by` varchar(255) NOT NULL,
  `name` varchar(255) NOT NULL,
  `owner_id` bigint(20) NOT NULL,
  `uuid` varchar(255) NOT NULL,
    `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",

  PRIMARY KEY (`id`),
  KEY `FKF70E9674D1B1E9A7` (`owner_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_stream`
--

LOCK TABLES `user_stream` WRITE;
/*!40000 ALTER TABLE `user_stream` DISABLE KEYS */;
/*!40000 ALTER TABLE `user_stream` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `uzer`
--

DROP TABLE IF EXISTS `uzer`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `uzer` (
  `id` bigint(20) NOT NULL,
  `version` bigint(20) NOT NULL,
  `current_status_id` bigint(20) DEFAULT NULL,
  `date_created` datetime NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `first_name` varchar(255) NOT NULL,
  `full_name` varchar(255) NOT NULL,
  `last_name` varchar(255) NOT NULL,
  `profile_id` bigint(20) DEFAULT NULL,
  `user_id` varchar(20) NOT NULL,
  `uuid` varchar(255) NOT NULL,
  `_SP_del` bool default false, 
  `_SP_ts` int default 0,
  `_SP_clock` varchar(100) default "0-0",

  PRIMARY KEY (`id`),
  UNIQUE KEY `user_id` (`user_id`),
  KEY `FK370612AC44CB52` (`current_status_id`),
  KEY `FK370612A2139300` (`profile_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `uzer`
--

LOCK TABLES `uzer` WRITE;
/*!40000 ALTER TABLE `uzer` DISABLE KEYS */;
/*!40000 ALTER TABLE `uzer` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2012-04-25  6:17:51
