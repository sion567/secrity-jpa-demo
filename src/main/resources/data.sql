-- 密码明文都为 123456
INSERT INTO `user` VALUES ('1', 'admin', '$2a$10$JNVWTh5Yq56kJtrCZkcDk.DL/L/i8g3KrTAshcHW3mFf8//lnfG56', '11111111111', '1', 'ROLE_ADMIN,ROLE_USER');
INSERT INTO `user` VALUES ('2', 'user', '$2a$10$JNVWTh5Yq56kJtrCZkcDk.DL/L/i8g3KrTAshcHW3mFf8//lnfG56', '22222222222', '1', 'ROLE_USER');