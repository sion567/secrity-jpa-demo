DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` bigint NOT NULL AUTO_INCREMENT COMMENT '主键',
    `username` varchar(50) NOT NULL COMMENT '用户名',
    `password` varchar(64) COMMENT '密码',
    `mobile` varchar(20) COMMENT '手机号',
    `enabled` tinyint NOT NULL DEFAULT '1' COMMENT '用户是否可用',
    `roles` text COMMENT '用户角色，多个角色之间用逗号隔开',
    PRIMARY KEY (`id`),
    KEY `index_username`(`username`),
    KEY `index_mobile`(`mobile`)
) COMMENT '用户表';

DROP TABLE IF EXISTS `persistent_logins`;
CREATE TABLE `persistent_logins` (
     `username` varchar(64) NOT NULL,
     `series` varchar(64) PRIMARY KEY,
     `token` varchar(64) NOT NULL,
     `last_used` timestamp NOT NULL
);