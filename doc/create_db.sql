/**
 * 创建库
 */
create database jigsaws default charset utf8;

/**
 * 选择数据库
 */
use jigsaws;

/**
 * 创建表 帐号
 */
DROP TABLE IF EXISTS t_account ;
CREATE TABLE t_account (
  device_id varchar(100) default null,
  plat varchar(100) default null,
  id_in_plat varchar(100) default null,
  name_in_plat varchar(100) default null,
  player_id bigint(20) UNSIGNED NOT NULL,
  channel varchar(100) default null,
  device varchar(100) default null,
  ct bigint(20) UNSIGNED DEFAULT '0',
  PRIMARY KEY (device_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/**
 * 玩家
 */
DROP TABLE IF EXISTS t_player ;
CREATE TABLE t_player (
  id bigint(20) UNSIGNED NOT NULL,
  level INT (5) UNSIGNED DEFAULT '0',
  exp INT (11) UNSIGNED DEFAULT '0',
  last_signin_time bigint(20) UNSIGNED DEFAULT '0',
  push_uri varchar(1000) default null,
  PRIMARY KEY (id)
) ENGINE = INNODB DEFAULT CHARSET = utf8 ;


/**
 * 图片
 */
DROP TABLE IF EXISTS t_image ;
CREATE TABLE t_image (
  id BIGINT(20) UNSIGNED NOT NULL,
  player_id BIGINT (20) UNSIGNED NOT NULL,
  url varchar(100) default null,
  good INT (11) UNSIGNED DEFAULT '0',
  bad INT (11) UNSIGNED DEFAULT '0',
  PRIMARY KEY (id)
) ENGINE = INNODB DEFAULT CHARSET = utf8 ;

