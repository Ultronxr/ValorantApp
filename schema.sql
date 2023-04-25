CREATE USER `valorant`@`%` IDENTIFIED WITH caching_sha2_password BY 'xxx';
GRANT Alter, Alter Routine, Create, Create Routine, Create Temporary Tables, Create View, Delete, Drop, Event, Execute, Grant Option, Index, Insert, Lock Tables, References, Select, Show View, Trigger, Update ON `valorant\_dev`.* TO `valorant`@`%`;
GRANT Alter, Alter Routine, Create, Create Routine, Create Temporary Tables, Create View, Delete, Drop, Event, Execute, Grant Option, Index, Insert, Lock Tables, References, Select, Show View, Trigger, Update ON `valorant\_dev\_new`.* TO `valorant`@`%`;
GRANT Alter, Alter Routine, Create, Create Routine, Create Temporary Tables, Create View, Delete, Drop, Event, Execute, Grant Option, Index, Insert, Lock Tables, References, Select, Show View, Trigger, Update ON `valorant\_prod`.* TO `valorant`@`%`;


CREATE DATABASE `valorant_dev` DEFAULT CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci';
CREATE DATABASE `valorant_dev_new` DEFAULT CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci';
CREATE DATABASE `valorant_prod` DEFAULT CHARACTER SET 'utf8mb4' COLLATE 'utf8mb4_general_ci';

### 详细表结构及数据查看 /exe/mysql-8.0.28-winx64/valorant_dev.sql

CREATE TABLE valorant_riot_account (
    `user_id`               VARCHAR(100)     NOT NULL                   COMMENT '账户ID（从拳头RSO接口中获取）',
    `account_no`            BIGINT           NOT NULL AUTO_INCREMENT    COMMENT '账户编号',
    `username`              VARCHAR(100)     DEFAULT NULL               COMMENT '用户名（登录名）',
    `password`              VARCHAR(500)     DEFAULT NULL               COMMENT '密码',
    `email`                 VARCHAR(100)     DEFAULT NULL               COMMENT '初始邮箱',
    `email_pwd`             VARCHAR(500)     DEFAULT NULL               COMMENT '初始邮箱密码',
    `access_token`          VARCHAR(3000)    DEFAULT NULL               COMMENT 'API用户验证token（从拳头RSO接口中获取）',
    `entitlements_token`    VARCHAR(3000)    DEFAULT NULL               COMMENT 'API用户验证token（从拳头RSO接口中获取）',
    `multi_factor`          VARCHAR(3000)    DEFAULT NULL               COMMENT '两步验证信息',
    `is_verified`           TINYINT(1)       DEFAULT NULL               COMMENT '该账户信息是否通过验证：1-true; 0-false',
    `is_del`                TINYINT(1)       DEFAULT 0                  COMMENT '是否删除：1-true; 0-false',
    PRIMARY KEY(`user_id`),
    UNIQUE KEY(`account_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT 'valorant 模块 - 拳头账户信息';

-- 注：旧版本更新SQL
ALTER TABLE valorant_riot_account ADD `account_no` BIGINT UNIQUE KEY NOT NULL AUTO_INCREMENT     COMMENT '账户编号' AFTER user_id;
ALTER TABLE valorant_riot_account CHANGE `social_name` `email` VARCHAR(100)     DEFAULT NULL       COMMENT '初始邮箱';
ALTER TABLE valorant_riot_account CHANGE `social_tag` `email_pwd` VARCHAR(500)     DEFAULT NULL    COMMENT '初始邮箱密码';

CREATE TABLE valorant_cdk (
    `no`               BIGINT     NOT NULL AUTO_INCREMENT         COMMENT 'CDK编号',
    `content`              VARCHAR(100)     DEFAULT NULL     COMMENT 'CDK内容',
    `type`               VARCHAR(10)     NOT NULL         COMMENT 'CDK种类：onetime 一次性；reusable 可重复使用',
    `remaining_times`              INT     DEFAULT NULL     COMMENT 'CDK剩余可用次数',
    `is_used`                TINYINT(1)       DEFAULT 0        COMMENT '是否删除：1-true; 0-false',
    `is_del`                 TINYINT(1)       DEFAULT 0        COMMENT '是否删除：1-true; 0-false',
    PRIMARY KEY(`no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT 'valorant 模块 - CDKey信息';

CREATE TABLE valorant_cdk_history (
    `cdk_no`               BIGINT     NOT NULL AUTO_INCREMENT         COMMENT 'CDK编号',
    `account_no`              VARCHAR(100)     DEFAULT NULL     COMMENT 'CDK内容',
    `use_time`               VARCHAR(10)     NOT NULL         COMMENT 'CDK种类：onetime 一次性；reusable 可重复使用',
    `detail`              INT     DEFAULT NULL     COMMENT 'CDK剩余可用次数',
    PRIMARY KEY(`cdk_no`, `account_no`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT 'valorant 模块 - CDKey使用记录';

CREATE TABLE valorant_weapon (
    `uuid`                 VARCHAR(100)    NOT NULL         COMMENT '对象ID，主键',
    `asset_path`           VARCHAR(300)    DEFAULT NULL     COMMENT '游戏素材路径',
    `category`             VARCHAR(100)    DEFAULT NULL     COMMENT '分类',
    `default_skin_uuid`    VARCHAR(100)    DEFAULT NULL     COMMENT '默认皮肤ID',
    `display_name`         VARCHAR(100)    DEFAULT NULL     COMMENT '显示名称',
    `display_icon`         VARCHAR(300)    DEFAULT NULL     COMMENT '显示图标',
    `kill_stream_icon`     VARCHAR(300)    DEFAULT NULL     COMMENT '击杀信息中显示的图标',
    PRIMARY KEY(`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT 'valorant 模块 - 武器信息';

CREATE TABLE valorant_weapon_skin (
    `uuid`                  VARCHAR(100)    NOT NULL         COMMENT '对象ID，主键',
    `type`                  VARCHAR(10)     NOT NULL         COMMENT '皮肤类别（皮肤/升级/炫彩）：skin/level/chroma',
    `parent_weapon_uuid`    VARCHAR(100)    DEFAULT NULL     COMMENT '父武器ID（皮肤/升级/炫彩指向其父武器ID）',
    `parent_skin_uuid`      VARCHAR(100)    DEFAULT NULL     COMMENT '父皮肤ID（升级/炫彩指向其父皮肤ID）',
    `asset_path`            VARCHAR(300)    DEFAULT NULL     COMMENT '游戏素材路径',
    `content_tier_uuid`     VARCHAR(100)    DEFAULT NULL     COMMENT '皮肤等级（另外注）',
    `theme_uuid`            VARCHAR(100)    DEFAULT NULL     COMMENT '主题ID',
    `display_name`          VARCHAR(100)    DEFAULT NULL     COMMENT '显示名称',
    `swatch`                VARCHAR(300)    DEFAULT NULL     COMMENT '样品缩略图',
    `display_icon`          VARCHAR(300)    DEFAULT NULL     COMMENT '显示图标',
    `full_render`           VARCHAR(300)    DEFAULT NULL     COMMENT '完整图片',
    `wallpaper`             VARCHAR(300)    DEFAULT NULL     COMMENT '壁纸',
    `streamed_video`        VARCHAR(300)    DEFAULT NULL     COMMENT '演示视频',
    PRIMARY KEY(`uuid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT 'valorant 模块 - 武器皮肤（包括皮肤、升级、炫彩）';

-- 武器等级 content_tier_uuid
-- 12683d76-48d7-84a3-4e09-6985794f0445 等级一（蓝色圆圈）
-- 0cebb8be-46d7-c12a-d306-e9907bfc5a25 等级二（绿色圆角菱形）
-- 60bca009-4182-7998-dee7-b8a2558dc369 等级三（红色三角）
-- 411e4a55-4e59-7757-41f0-86a53f101bb5 等级四（金色菱形）
-- e046854e-406c-37f4-6607-19a9ba8426fc 等级五（橙色五边形）

CREATE TABLE valorant_store_front (
    `user_id`               VARCHAR(100)    NOT NULL         COMMENT '账户ID',
    `offer_id`              VARCHAR(100)    NOT NULL         COMMENT '供应ID（一般与 item_id 相同）',
    `date`                  VARCHAR(20)     NOT NULL         COMMENT '供应日期',
    `end_date`              VARCHAR(20)     DEFAULT NULL     COMMENT '供应结束日期',
    `is_direct_purchase`    TINYINT(1)      DEFAULT NULL     COMMENT '是否直售：1 - true / 0 - false',
    `currency_id`           VARCHAR(100)    DEFAULT NULL     COMMENT '游戏货币ID（VP:85ad13f7-3d1b-5128-9eb2-7cd8ee0b5741）',
    `cost`                  INT             DEFAULT NULL     COMMENT '原价',
    `item_type_id`          VARCHAR(100)    DEFAULT NULL     COMMENT '出售货物类别ID',
    `item_id`               VARCHAR(100)    DEFAULT NULL     COMMENT '出售货物ID',
    `quantity`              INT             DEFAULT NULL     COMMENT '出售数量',
    `discount_percent`      INT             DEFAULT NULL     COMMENT '打折百分比',
    `discount_cost`         INT             DEFAULT NULL     COMMENT '打折后的价格',
    `is_bonus`              TINYINT(1)      DEFAULT NULL     COMMENT '是否是Bonus商店（夜市）货物：1 - true / 0 - false',
    PRIMARY KEY(`user_id`, `offer_id`, `date`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci COMMENT 'valorant 模块 - 每日商店出售物品';
