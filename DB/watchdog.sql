/*
Navicat MySQL Data Transfer

Source Server         : ads
Source Server Version : 50622
Source Host           : localhost:3306
Source Database       : watchdog

Target Server Type    : MYSQL
Target Server Version : 50622
File Encoding         : 65001

Date: 2015-04-27 14:46:20
*/

SET FOREIGN_KEY_CHECKS=0;

-- ----------------------------
-- Table structure for feed
-- ----------------------------
DROP TABLE IF EXISTS `feed`;
CREATE TABLE `feed` (
  `Id` int(11) NOT NULL,
  `EndPoint` varchar(1024) DEFAULT NULL,
  `LastUpdDate` datetime DEFAULT NULL,
  `LastUpdBy` varchar(255) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for feedparameters
-- ----------------------------
DROP TABLE IF EXISTS `feedparameters`;
CREATE TABLE `feedparameters` (
  `Id` int(11) NOT NULL,
  `Name` varchar(200) DEFAULT NULL,
  `Value` varchar(200) DEFAULT NULL,
  `FeedId` int(11) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for feedtype
-- ----------------------------
DROP TABLE IF EXISTS `feedtype`;
CREATE TABLE `feedtype` (
  `Id` int(11) NOT NULL,
  `Name` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for path
-- ----------------------------
DROP TABLE IF EXISTS `path`;
CREATE TABLE `path` (
  `Id` int(11) NOT NULL,
  `SourceFeedId` int(11) DEFAULT NULL,
  `DestinationFeedId` int(11) DEFAULT NULL,
  `Name` varchar(200) DEFAULT NULL,
  `LastUpdDate` datetime DEFAULT NULL,
  `LastUpdBy` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for pathqos
-- ----------------------------
DROP TABLE IF EXISTS `pathqos`;
CREATE TABLE `pathqos` (
  `RunId` int(11) NOT NULL,
  `PathId` int(11) DEFAULT NULL,
  `Value` float DEFAULT NULL,
  PRIMARY KEY (`RunId`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

-- ----------------------------
-- Table structure for run
-- ----------------------------
DROP TABLE IF EXISTS `run`;
CREATE TABLE `run` (
  `Id` int(11) NOT NULL,
  `StartDate` datetime DEFAULT NULL,
  `EndDate` datetime DEFAULT NULL,
  `LastUpdDate` datetime DEFAULT NULL,
  PRIMARY KEY (`Id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;
