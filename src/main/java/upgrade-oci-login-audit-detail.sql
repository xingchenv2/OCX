-- 登录审计扩展详情（JSON）；已有库执行一次。若列已存在会报错，可忽略。
ALTER TABLE oci_login_audit
    ADD COLUMN login_detail MEDIUMTEXT NULL COMMENT 'JSON: 访问入口、网络与链路、客户端与能力' AFTER user_agent;
