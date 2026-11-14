-- =============================================
-- Spring Boot 3 整合 Shiro 演示库
-- 密码为 MD5 加密 1024 次后的密文（明文 mhxy1218）
-- =============================================
CREATE DATABASE IF NOT EXISTS shiro DEFAULT CHARACTER SET utf8mb4;
USE shiro;

DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(255) NOT NULL COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码（MD5 加密 1024 次的密文）',
    `phone` VARCHAR(11) NOT NULL COMMENT '手机号',
    `gender` TINYINT UNSIGNED NOT NULL COMMENT '性别（1-男；2-女）',
    `lock_state` TINYINT UNSIGNED NOT NULL DEFAULT 0 COMMENT '锁定状态：0-正常；1-锁定',
    `last_login_time` DATETIME DEFAULT NULL COMMENT '上一次登录时间（未登录默认为null）',
    PRIMARY KEY (`id`)
) ENGINE = InnoDB DEFAULT CHARACTER SET = utf8mb4 COMMENT = '用户表';

INSERT INTO `user` (`username`, `password`, `phone`, `gender`, `lock_state`, `last_login_time`) VALUES
('mumu', 'adfd5259371cd155896fbb30315593dd', '18888888888', 2, 0, NULL);
