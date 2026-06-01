-- Neo4j 文物收藏：为 collect 表增加 objectId 与名称缓存
-- 在 muse 库执行一次即可（2026-05-31）

ALTER TABLE collect
  ADD COLUMN relic_object_id VARCHAR(64) NULL AFTER user_collect_id,
  ADD COLUMN relic_name VARCHAR(255) NULL AFTER relic_object_id;
