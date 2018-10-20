DROP DATABASE IF EXISTS db;
CREATE DATABASE db;

USE db;
CREATE TABLE Users (
    userID INT(11) PRIMARY KEY AUTO_INCREMENT,
    fullName VARCHAR(50) NOT NULL,
    email VARCHAR(50) NOT NULL,
    hashPass VARCHAR(50) NOT NULL
);

CREATE TABLE Files (
    fileID INT(11) PRIMARY KEY AUTO_INCREMENT,
    rawData Text(64000) NOT NULL,
    fileName VARCHAR(50) NOT NULL,
    lastUpdate DateTime NOT NULL
);

CREATE TABLE Access (
	userID INT(11) NOT NULL,
    fileID INT(11) NOT NULL,
    accessID INT(11) NOT NULL PRIMARY KEY
);