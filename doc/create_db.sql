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
 * 统计
 */
DROP TABLE IF EXISTS t_statistics ;
CREATE TABLE t_statistics (
  player_id BIGINT (20) UNSIGNED NOT NULL,
  game_success INT (11) UNSIGNED DEFAULT '0',
  game_failure INT (11) UNSIGNED DEFAULT '0',
  game_giveup INT (11) UNSIGNED DEFAULT '0',
  upload_num INT (11) UNSIGNED DEFAULT '0',
  upload_be_good INT (11) UNSIGNED DEFAULT '0',
  upload_be_bad INT (11) UNSIGNED DEFAULT '0',
  comment_good INT (11) UNSIGNED DEFAULT '0',
  comment_bad INT (11) UNSIGNED DEFAULT '0',
  PRIMARY KEY (player_id)
) ENGINE = INNODB DEFAULT CHARSET = utf8 ;


/**
 * 图片
 */
DROP TABLE IF EXISTS t_jigsaw ;
CREATE TABLE t_jigsaw (
  id BIGINT(20) UNSIGNED NOT NULL,
  player_id BIGINT (20) UNSIGNED NOT NULL,
  url varchar(100) default null,
  good INT (11) UNSIGNED DEFAULT '0',
  bad INT (11) UNSIGNED DEFAULT '0',
  PRIMARY KEY (id)
) ENGINE = INNODB DEFAULT CHARSET = utf8 ;






