USE `valorant_dev`;

ALTER TABLE valorant_riot_account ADD `account_no` BIGINT UNIQUE KEY NOT NULL AUTO_INCREMENT     COMMENT '账户编号' AFTER user_id;
ALTER TABLE valorant_riot_account CHANGE `social_name` `email` VARCHAR(100)     DEFAULT NULL       COMMENT '初始邮箱';
ALTER TABLE valorant_riot_account CHANGE `social_tag` `email_pwd` VARCHAR(500)     DEFAULT NULL    COMMENT '初始邮箱密码';

