-- 已有库升级：为 Multi-Agent 等 Generative 调用在租户上保存默认请求头（与 schema.sql 新装库一致）
-- 在 MySQL 上执行一次即可。

ALTER TABLE oci_user
    ADD COLUMN generative_openai_project VARCHAR(512) DEFAULT NULL,
    ADD COLUMN generative_conversation_store_id VARCHAR(512) DEFAULT NULL;
