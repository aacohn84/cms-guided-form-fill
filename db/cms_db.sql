-- MySQL Script generated by MySQL Workbench
-- 10/06/14 21:33:13
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema cmsdb
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema cmsdb
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `cmsdb` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `cmsdb` ;

-- -----------------------------------------------------
-- Table `cmsdb`.`patron`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cmsdb`.`patron` ;

CREATE TABLE IF NOT EXISTS `cmsdb`.`patron` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(70) NOT NULL,
  `address` VARCHAR(256) NOT NULL,
  `phone` VARCHAR(45) NOT NULL,
  `email` VARCHAR(256) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cmsdb`.`permission_level_ref`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cmsdb`.`permission_level_ref` ;

CREATE TABLE IF NOT EXISTS `cmsdb`.`permission_level_ref` (
  `level_id` VARCHAR(10) NOT NULL,
  PRIMARY KEY (`level_id`),
  UNIQUE INDEX `level_id_UNIQUE` (`level_id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cmsdb`.`employee`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cmsdb`.`employee` ;

CREATE TABLE IF NOT EXISTS `cmsdb`.`employee` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `username` VARCHAR(64) NOT NULL,
  `password` VARCHAR(64) NOT NULL,
  `permission_level` VARCHAR(10) NOT NULL DEFAULT 'basic',
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  PRIMARY KEY (`username`),
  INDEX `permission_level_fk_idx` (`permission_level` ASC),
  UNIQUE INDEX `username_UNIQUE` (`username` ASC),
  CONSTRAINT `permission_level_fk`
    FOREIGN KEY (`permission_level`)
    REFERENCES `cmsdb`.`permission_level_ref` (`level_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cmsdb`.`change_order`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cmsdb`.`change_order` ;

CREATE TABLE IF NOT EXISTS `cmsdb`.`change_order` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `patron_id` INT UNSIGNED NOT NULL,
  `employee_id` INT UNSIGNED NOT NULL,
  `is_complete` TINYINT(1) NOT NULL COMMENT 'Flag true if form has been filled completely.',
  `date_created` TIMESTAMP NOT NULL,
  `date_modified` TIMESTAMP NOT NULL,
  `change_order_type` VARCHAR(12) NULL,
  `cemetery` VARCHAR(45) NULL,
  `location` VARCHAR(45) NULL,
  `orig_contract_num` VARCHAR(45) NULL,
  `contract_amount` DECIMAL(9,2) NULL,
  `contract_balance` DECIMAL(9,2) NULL,
  `reason` VARCHAR(320) NULL,
  `item_code_1` VARCHAR(45) NULL,
  `item_code_2` VARCHAR(45) NULL,
  `item_code_3` VARCHAR(45) NULL,
  `item_code_4` VARCHAR(45) NULL,
  `item_code_5` VARCHAR(45) NULL,
  `description_1` VARCHAR(45) NULL,
  `description_2` VARCHAR(45) NULL,
  `description_3` VARCHAR(45) NULL,
  `description_4` VARCHAR(45) NULL,
  `description_5` VARCHAR(45) NULL,
  `extended_price_1` DECIMAL(9,2) NULL,
  `extended_price_2` DECIMAL(9,2) NULL,
  `extended_price_3` DECIMAL(9,2) NULL,
  `extended_price_4` DECIMAL(9,2) NULL,
  `extended_price_5` DECIMAL(9,2) NULL,
  `gift_amount` DECIMAL(9,2) NULL,
  `admin_return_fees` DECIMAL(9,2) NULL,
  `credits_discounts` DECIMAL(9,2) NULL,
  `total_to_be_returned` DECIMAL(9,2) NULL,
  `apply_credit` VARCHAR(17) NULL,
  `total_deductions` DECIMAL(9,2) NULL,
  `credit_balance` DECIMAL(9,2) NULL,
  `property_assignment` VARCHAR(21) NULL,
  `donation_amount` DECIMAL(9,2) NULL,
  `assignee_name_1` VARCHAR(70) NULL,
  `assignee_name_2` VARCHAR(70) NULL,
  `assignee_address` VARCHAR(128) NULL,
  `assignee_phone` VARCHAR(45) NULL,
  `assignee_email` VARCHAR(128) NULL,
  `decedents` VARCHAR(70) NULL COMMENT 'contains a person\'s first and last name.',
  `place_final_disposition` VARCHAR(128) NULL COMMENT 'probably an address',
  `reinterment_location` VARCHAR(45) NULL,
  `reinterment_cemetery` VARCHAR(45) NULL,
  `notary_signature` TINYINT(1) NULL COMMENT 'checkbox',
  `original_contract` TINYINT(1) NULL,
  `death_certificate` TINYINT(1) NULL,
  `cash_receipt` TINYINT(1) NULL,
  `signed_notarized_release` TINYINT(1) NULL,
  `donation_letter` TINYINT(1) NULL,
  `evidence_of_burial` TINYINT(1) NULL,
  `new_existing_contract` TINYINT(1) NULL,
  `decisions_filename` VARCHAR(45) NULL COMMENT 'Reference to a file that stores decisions made in the Guided Forms application.',
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `client_id_fk_idx` (`patron_id` ASC),
  INDEX `employee_id_fk_idx` (`employee_id` ASC),
  CONSTRAINT `client_id_fk`
    FOREIGN KEY (`patron_id`)
    REFERENCES `cmsdb`.`patron` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `employee_id_fk`
    FOREIGN KEY (`employee_id`)
    REFERENCES `cmsdb`.`employee` (`id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

USE `cmsdb` ;

-- -----------------------------------------------------
-- procedure credentialsAreValid
-- -----------------------------------------------------

USE `cmsdb`;
DROP procedure IF EXISTS `cmsdb`.`credentialsAreValid`;

DELIMITER $$
USE `cmsdb`$$
CREATE PROCEDURE `credentialsAreValid` (
	IN `username_in` VARCHAR(64),
	IN `password_in` VARCHAR(64)
)
BEGIN
	SELECT
		TRUE
	FROM
		`employee`
	WHERE
		`username` = `username_in` AND `password` = `password_in`;
END
$$

DELIMITER ;

-- -----------------------------------------------------
-- procedure getPermissionLevel
-- -----------------------------------------------------

USE `cmsdb`;
DROP procedure IF EXISTS `cmsdb`.`getPermissionLevel`;

DELIMITER $$
USE `cmsdb`$$
CREATE PROCEDURE `getPermissionLevel` (
	IN `username_in` VARCHAR(64)
)
BEGIN
	SELECT `permission_level` FROM `employee` WHERE `username` = `username_in`;
END$$

DELIMITER ;
SET SQL_MODE = '';
GRANT USAGE ON *.* TO application;
 DROP USER application;
SET SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';
CREATE USER 'application' IDENTIFIED BY 'lEoyuHgAig9l';


SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Data for table `cmsdb`.`permission_level_ref`
-- -----------------------------------------------------
START TRANSACTION;
USE `cmsdb`;
INSERT INTO `cmsdb`.`permission_level_ref` (`level_id`) VALUES ('basic');
INSERT INTO `cmsdb`.`permission_level_ref` (`level_id`) VALUES ('admin');
INSERT INTO `cmsdb`.`permission_level_ref` (`level_id`) VALUES ('super');

COMMIT;
