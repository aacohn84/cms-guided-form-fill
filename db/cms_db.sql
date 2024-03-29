-- MySQL Script generated by MySQL Workbench
-- 10/30/14 21:05:29
-- Model: New Model    Version: 1.0
-- MySQL Workbench Forward Engineering

SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0;
SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0;
SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';

-- -----------------------------------------------------
-- Schema cms_db
-- -----------------------------------------------------

-- -----------------------------------------------------
-- Schema cms_db
-- -----------------------------------------------------
CREATE SCHEMA IF NOT EXISTS `cms_db` DEFAULT CHARACTER SET utf8 COLLATE utf8_general_ci ;
USE `cms_db` ;

-- -----------------------------------------------------
-- Table `cms_db`.`permission_level_ref`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cms_db`.`permission_level_ref` ;

CREATE TABLE IF NOT EXISTS `cms_db`.`permission_level_ref` (
  `level_id` VARCHAR(10) NOT NULL,
  PRIMARY KEY (`level_id`),
  UNIQUE INDEX `level_id_UNIQUE` (`level_id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cms_db`.`employee`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cms_db`.`employee` ;

CREATE TABLE IF NOT EXISTS `cms_db`.`employee` (
  `employee_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(64) NOT NULL,
  `password` VARCHAR(64) NOT NULL,
  `permission_level` VARCHAR(10) NOT NULL DEFAULT 'basic',
  UNIQUE INDEX `id_UNIQUE` (`employee_id` ASC),
  PRIMARY KEY (`name`),
  UNIQUE INDEX `username_UNIQUE` (`name` ASC),
  INDEX `permission_level_fk_idx` (`permission_level` ASC),
  CONSTRAINT `permission_level_fk`
    FOREIGN KEY (`permission_level`)
    REFERENCES `cms_db`.`permission_level_ref` (`level_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cms_db`.`patron`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cms_db`.`patron` ;

CREATE TABLE IF NOT EXISTS `cms_db`.`patron` (
  `patron_id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `name` VARCHAR(70) NOT NULL,
  `address` VARCHAR(256) NOT NULL,
  `phone` VARCHAR(45) NOT NULL,
  `email` VARCHAR(256) NULL,
  PRIMARY KEY (`patron_id`),
  UNIQUE INDEX `patron_id_UNIQUE` (`patron_id` ASC))
ENGINE = InnoDB;


-- -----------------------------------------------------
-- Table `cms_db`.`change_order`
-- -----------------------------------------------------
DROP TABLE IF EXISTS `cms_db`.`change_order` ;

CREATE TABLE IF NOT EXISTS `cms_db`.`change_order` (
  `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
  `employee_id` INT UNSIGNED NOT NULL,
  `date_created` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `date_modified` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  `change_order_type` VARCHAR(12) NULL,
  `patron_1_id` INT UNSIGNED NOT NULL,
  `name_1` VARCHAR(70) NULL,
  `patron_2_id` INT UNSIGNED NULL,
  `name_2` VARCHAR(70) NULL,
  `address` VARCHAR(256) NULL,
  `phone` VARCHAR(45) NULL,
  `email` VARCHAR(256) NULL,
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
  `decedents` VARCHAR(70) NULL,
  `place_final_disposition` VARCHAR(128) NULL,
  `reinterment_location` VARCHAR(45) NULL,
  `reinterment_cemetery` VARCHAR(45) NULL,
  `notary_signature` VARCHAR(3) NULL,
  `original_contract` VARCHAR(3) NULL,
  `death_certificate` VARCHAR(3) NULL,
  `cash_receipt` VARCHAR(3) NULL,
  `signed_notarized_release` VARCHAR(3) NULL,
  `donation_letter` VARCHAR(3) NULL,
  `evidence_of_burial` VARCHAR(3) NULL,
  `new_existing_contract` VARCHAR(3) NULL,
  PRIMARY KEY (`id`),
  UNIQUE INDEX `id_UNIQUE` (`id` ASC),
  INDEX `employee_id_fk_idx` (`employee_id` ASC),
  INDEX `patron_1_id_fk_idx` (`patron_1_id` ASC),
  INDEX `patron_2_id_fk_idx` (`patron_2_id` ASC),
  CONSTRAINT `employee_id_fk`
    FOREIGN KEY (`employee_id`)
    REFERENCES `cms_db`.`employee` (`employee_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `patron_1_id_fk`
    FOREIGN KEY (`patron_1_id`)
    REFERENCES `cms_db`.`patron` (`patron_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION,
  CONSTRAINT `patron_2_id_fk`
    FOREIGN KEY (`patron_2_id`)
    REFERENCES `cms_db`.`patron` (`patron_id`)
    ON DELETE NO ACTION
    ON UPDATE NO ACTION)
ENGINE = InnoDB;

USE `cms_db` ;

-- -----------------------------------------------------
-- procedure credentialsAreValid
-- -----------------------------------------------------

USE `cms_db`;
DROP procedure IF EXISTS `cms_db`.`credentialsAreValid`;

DELIMITER $$
USE `cms_db`$$
-- ---------------------------------------------------------------------
-- Returns all of the user's information if the given name and password
-- are correct. Otherwise, returns nothing.
-- ---------------------------------------------------------------------
CREATE PROCEDURE `credentialsAreValid` (
	IN `name_in` VARCHAR(64),
	IN `password_in` VARCHAR(64)
)
BEGIN
	SELECT
		*
	FROM
		`employee`
	WHERE
		`name` = `name_in` AND `password` = `password_in`;
END
$$

DELIMITER ;
SET SQL_MODE = '';
GRANT USAGE ON *.* TO cms_app;
 DROP USER cms_app;
SET SQL_MODE='TRADITIONAL,ALLOW_INVALID_DATES';
CREATE USER 'cms_app' IDENTIFIED BY 'lEoyuHgAig9l';

GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE `cms_db`.`change_order` TO 'cms_app';
GRANT SELECT ON TABLE `cms_db`.`employee` TO 'cms_app';
GRANT INSERT, SELECT, UPDATE, DELETE ON TABLE `cms_db`.`patron` TO 'cms_app';

SET SQL_MODE=@OLD_SQL_MODE;
SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS;
SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS;

-- -----------------------------------------------------
-- Data for table `cms_db`.`permission_level_ref`
-- -----------------------------------------------------
START TRANSACTION;
USE `cms_db`;
INSERT INTO `cms_db`.`permission_level_ref` (`level_id`) VALUES ('basic');
INSERT INTO `cms_db`.`permission_level_ref` (`level_id`) VALUES ('admin');
INSERT INTO `cms_db`.`permission_level_ref` (`level_id`) VALUES ('super');

COMMIT;

