-- Neo4j 文物评论：为 comment_check 表增加 objectId 与名称缓存
-- 在 muse 库执行一次即可

ALTER TABLE comment_check
  ADD COLUMN relic_object_id VARCHAR(64) NULL AFTER relic_id,
  ADD COLUMN relic_name VARCHAR(255) NULL AFTER relic_object_id;
