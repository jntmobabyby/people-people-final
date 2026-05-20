CREATE DATABASE IF NOT EXISTS lab_reservation DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE lab_reservation;

DROP TABLE IF EXISTS maintenance_ticket;
DROP TABLE IF EXISTS return_record;
DROP TABLE IF EXISTS reservation;
DROP TABLE IF EXISTS equipment;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  student_no VARCHAR(32) NOT NULL UNIQUE COMMENT '学号/工号',
  name VARCHAR(64) NOT NULL COMMENT '姓名',
  username VARCHAR(64) NOT NULL UNIQUE COMMENT '登录账号',
  password_hash VARCHAR(128) NOT NULL COMMENT '演示版密码字段',
  role VARCHAR(20) NOT NULL COMMENT 'ADMIN/STUDENT',
  status TINYINT NOT NULL DEFAULT 1 COMMENT '1启用，0停用'
) COMMENT '用户表';

CREATE TABLE equipment (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  asset_no VARCHAR(64) NOT NULL UNIQUE COMMENT '设备资产编号',
  name VARCHAR(128) NOT NULL COMMENT '设备名称',
  category VARCHAR(64) NOT NULL COMMENT '设备分类',
  location VARCHAR(128) NOT NULL COMMENT '存放位置',
  status TINYINT NOT NULL COMMENT '0停用，1可预约，2占用中，3维修中'
) COMMENT '设备表';

CREATE TABLE reservation (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  user_id BIGINT NOT NULL COMMENT '预约人ID',
  equipment_id BIGINT NOT NULL COMMENT '被预约设备ID',
  start_time DATETIME NOT NULL COMMENT '预约开始时间',
  end_time DATETIME NOT NULL COMMENT '预约结束时间',
  status TINYINT NOT NULL COMMENT '0待审批，1已通过，2已取消，3已完成，4已拒绝',
  review_comment VARCHAR(255) NULL COMMENT '审批意见',
  created_at DATETIME NOT NULL COMMENT '创建时间',
  INDEX idx_reservation_equipment_time (equipment_id, start_time, end_time),
  INDEX idx_reservation_user (user_id)
) COMMENT '预约表';

CREATE TABLE return_record (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  reservation_id BIGINT NOT NULL UNIQUE COMMENT '关联预约ID',
  return_time DATETIME NOT NULL COMMENT '实际归还时间',
  condition_note VARCHAR(255) NULL COMMENT '归还设备状况说明',
  penalty_amount DECIMAL(10,2) NOT NULL DEFAULT 0 COMMENT '超期或损坏费用'
) COMMENT '归还记录表';

CREATE TABLE maintenance_ticket (
  id BIGINT PRIMARY KEY AUTO_INCREMENT,
  equipment_id BIGINT NOT NULL COMMENT '故障设备ID',
  reporter_id BIGINT NULL COMMENT '上报人ID',
  fault_desc VARCHAR(500) NOT NULL COMMENT '故障描述',
  status TINYINT NOT NULL COMMENT '0待处理，1维修中，2已关闭',
  created_at DATETIME NOT NULL COMMENT '创建时间',
  INDEX idx_ticket_equipment (equipment_id)
) COMMENT '维修工单表';
