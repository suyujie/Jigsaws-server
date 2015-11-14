
/**
 * 创建库
 */
create database rulesgame_robot_log default charset utf8;

/**
 * 选择数据库
 */
use rulesgame_robot_log;


/**
 *登陆 日志
 */
DROP TABLE IF EXISTS t_sign_log ;
CREATE TABLE t_sign_log (
  id bigint(20) UNSIGNED NOT NULL,
  player_id bigint(20) unsigned NOT NULL,
  before_level int(11) unsigned DEFAULT null,
  before_cash int(11) unsigned DEFAULT null,
  before_gold int(11) unsigned DEFAULT null,
  after_level int(11) unsigned DEFAULT null,
  after_cash int(11) unsigned DEFAULT null,
  after_gold int(11) unsigned DEFAULT null,
  max_point_id int(11) unsigned DEFAULT null,
  sign_in_t bigint(20) unsigned DEFAULT null,
  sign_out_t bigint(20) unsigned DEFAULT null,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/**
 *支付日志
 */
DROP TABLE IF EXISTS t_pay_log ;
CREATE TABLE t_pay_log (
  player_id BIGINT(20) UNSIGNED NOT NULL,
  order_id VARCHAR(100) NOT NULL,
  pay_channel VARCHAR(20) NOT NULL,
  recharge_package_id VARCHAR(20) NOT NULL,
  coin_type VARCHAR(20) DEFAULT NULL,
  coin INT(11) DEFAULT NULL,
  info TEXT DEFAULT NULL,
  t BIGINT(20) UNSIGNED DEFAULT NULL,
  status int(11) UNSIGNED default null,
  PRIMARY KEY (player_id,pay_channel,order_id)
) ENGINE=INNODB DEFAULT CHARSET=utf8;


/**
 *gold 日志
 */
DROP TABLE IF EXISTS t_gold_log ;
CREATE TABLE t_gold_log (
  id bigint(20) UNSIGNED NOT NULL,
  player_id bigint(20) unsigned NOT NULL,
  coin_type varchar(20) DEFAULT null,
  coin int(11) DEFAULT null,
  before_gold int(11) unsigned DEFAULT null,
  change_gold int(11) DEFAULT null,
  after_gold int(11) unsigned DEFAULT null,
  t bigint(20) unsigned DEFAULT null,
  change_type int(1) DEFAULT null,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

/**
 *cash 日志
 */
DROP TABLE IF EXISTS t_cash_log ;
CREATE TABLE t_cash_log (
  id bigint(20) UNSIGNED NOT NULL,
  player_id bigint(20) unsigned NOT NULL,
  before_cash int(11) unsigned DEFAULT NULL,
  change_cash int(11) DEFAULT NULL,
  after_cash int(11) unsigned DEFAULT NULL,
  t bigint(20) unsigned DEFAULT NULL,
  change_type int(1) DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/**
 * pve 日志
 */
DROP TABLE IF EXISTS t_pve_log ;
CREATE TABLE t_pve_log (
  id bigint(20) UNSIGNED NOT NULL,
  player_id bigint(20) unsigned NOT NULL,
  point_id int(11) unsigned DEFAULT NULL,
  point_star int(1) unsigned DEFAULT NULL,
  win int(1) DEFAULT NULL,
  get_cash int(11) unsigned DEFAULT NULL,
  first_pass int(1) DEFAULT NULL,
  enter_time bigint(20) unsigned DEFAULT NULL,
  exit_time bigint(20) unsigned DEFAULT NULL,
  weapon varchar(100) default null,
  t bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/**
 * pvp 日志
 */
DROP TABLE IF EXISTS t_pvp_log ;
CREATE TABLE t_pvp_log (
  id bigint(20) UNSIGNED NOT NULL,
  attacker_id bigint(20) unsigned NOT NULL,
  attacker_level int(11) unsigned NOT NULL,
  defender_id bigint(20) unsigned NOT NULL,
  defender_level int(11) unsigned NOT NULL,
  attacker_win int(1) DEFAULT NULL,
  win_cash int(11) unsigned DEFAULT NULL,
  attacker_weapon varchar(100) default null,
  defender_weapon varchar(100) default null,
  begin_time bigint(20) unsigned DEFAULT NULL,
  end_time bigint(20) unsigned DEFAULT NULL,
  t bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/**
 * dailyJob 日志
 */
DROP TABLE IF EXISTS t_daily_job_log ;
CREATE TABLE t_daily_job_log (
  id bigint(20) UNSIGNED NOT NULL,
  player_id bigint(20) unsigned NOT NULL,
  job_id int(11) DEFAULT NULL,
  completed int(1) unsigned DEFAULT NULL,
  get_job_t bigint(20) unsigned DEFAULT NULL,
  t bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/**
 * egg 日志
 */
DROP TABLE IF EXISTS t_egg_log ;
CREATE TABLE t_egg_log (
  id bigint(20) UNSIGNED NOT NULL,
  player_id bigint(20) unsigned NOT NULL,
  all_num int(1) DEFAULT NULL,
  open_num int(1) DEFAULT NULL,
  get_egg_t bigint(20) unsigned DEFAULT NULL,
  t bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/**
 * level time
 */
DROP TABLE IF EXISTS t_level_time_log ;
CREATE TABLE t_level_time_log (
  id bigint(20) UNSIGNED NOT NULL,
  player_id bigint(20) unsigned NOT NULL,
  level int(1) DEFAULT NULL,
  all_time bigint(20) unsigned DEFAULT NULL,
  t bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/**
 * t_feedback 玩家反馈
 */
DROP TABLE IF EXISTS t_feedback ;
CREATE TABLE t_feedback (
  id bigint(20) UNSIGNED NOT NULL,
  player_id bigint(20) unsigned NOT NULL,
  msg text DEFAULT NULL,
  email varchar(50) DEFAULT NULL,
  t bigint(20) unsigned DEFAULT NULL,
  PRIMARY KEY (id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;


/**
 *尝试支付日志
 */
DROP TABLE IF EXISTS t_pay_try_log ;
CREATE TABLE t_pay_try_log (
  player_id BIGINT(20) UNSIGNED NOT NULL,
  pay_channel VARCHAR(20) NOT NULL,
  recharge_package_id VARCHAR(20) NOT NULL,
  t BIGINT(20) UNSIGNED DEFAULT NULL,
  PRIMARY KEY (player_id,pay_channel,t)
) ENGINE=INNODB DEFAULT CHARSET=utf8;





