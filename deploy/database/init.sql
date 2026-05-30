CREATE DATABASE IF NOT EXISTS muse DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE muse;

CREATE TABLE IF NOT EXISTS user (
  user_id INT AUTO_INCREMENT PRIMARY KEY,
  user_name VARCHAR(100) NOT NULL,
  user_password VARCHAR(100) NOT NULL,
  user_sex INT DEFAULT 0,
  user_tel VARCHAR(30),
  user_comment INT DEFAULT 1,
  user_login INT DEFAULT 1
);

CREATE TABLE IF NOT EXISTS cultural_relics_data (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  object_name VARCHAR(255) NOT NULL,
  cat1 VARCHAR(100),
  cat2 VARCHAR(100),
  cat3 VARCHAR(100),
  makers_name VARCHAR(100),
  img_url VARCHAR(1000),
  museum VARCHAR(255),
  time_period VARCHAR(100),
  label VARCHAR(255),
  medium VARCHAR(255),
  object_type VARCHAR(100),
  previous_owner VARCHAR(255),
  provenance VARCHAR(500),
  geography VARCHAR(255)
);

CREATE TABLE IF NOT EXISTS collect (
  id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  user_collect_id BIGINT NOT NULL
);

CREATE TABLE IF NOT EXISTS comment_check (
  user_comment_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  relic_id BIGINT NOT NULL,
  content TEXT NOT NULL,
  user_comment INT DEFAULT 1,
  created_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  modified_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS img_url_table (
  img_id VARCHAR(64) PRIMARY KEY,
  url VARCHAR(1000) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_logs (
  log_id INT AUTO_INCREMENT PRIMARY KEY,
  user_id INT NOT NULL,
  type VARCHAR(50) NOT NULL,
  description VARCHAR(500),
  time DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS museum (
  object_id VARCHAR(36) PRIMARY KEY,
  name VARCHAR(200) NOT NULL,
  name_cn VARCHAR(200),
  location VARCHAR(200),
  website VARCHAR(500)
);

CREATE TABLE IF NOT EXISTS artifact (
  object_id VARCHAR(36) PRIMARY KEY,
  title VARCHAR(500) NOT NULL,
  period VARCHAR(200),
  type VARCHAR(100),
  material VARCHAR(200),
  description TEXT,
  dimensions VARCHAR(300),
  museum_id VARCHAR(36),
  detail_url VARCHAR(1000) NOT NULL,
  image_url VARCHAR(1000) NOT NULL,
  image_path VARCHAR(500),
  credit_line VARCHAR(500),
  accession_number VARCHAR(100),
  crawl_date DATE NOT NULL,
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
  is_deleted TINYINT(1) DEFAULT 0,
  KEY idx_museum (museum_id),
  KEY idx_type (type),
  KEY idx_period (period)
);

INSERT INTO user (user_id, user_name, user_password, user_sex, user_tel, user_comment, user_login) VALUES
(1001, 'demo', '4DAA5D32203F6C48E54DC0FC51C64D3C', 1, '13800000000', 1, 1)
ON DUPLICATE KEY UPDATE user_name=VALUES(user_name);

INSERT INTO museum (object_id, name, name_cn, location, website) VALUES
('m-001', 'The British Museum', '大英博物馆', 'London, UK', 'https://www.britishmuseum.org'),
('m-002', 'The Metropolitan Museum of Art', '大都会艺术博物馆', 'New York, USA', 'https://www.metmuseum.org'),
('m-003', 'Musée du Louvre', '卢浮宫', 'Paris, France', 'https://www.louvre.fr'),
('m-004', 'Tokyo National Museum', '东京国立博物馆', 'Tokyo, Japan', 'https://www.tnm.jp')
ON DUPLICATE KEY UPDATE name=VALUES(name);

INSERT INTO cultural_relics_data (id, object_name, cat1, cat2, cat3, makers_name, img_url, museum, time_period, label, medium, object_type, previous_owner, provenance, geography) VALUES
(1, '商代青铜鼎', '青铜', '商代', '青铜器', '佚名', 'https://example.com/relic1.jpg', '大英博物馆', '商代', '礼器', '青铜', 'Bronze', '未知', '中国河南', '中国'),
(2, '唐代三彩骆驼', '陶瓷', '唐代', '陶俑', '佚名', 'https://example.com/relic2.jpg', '大都会艺术博物馆', '唐代', '雕塑', '陶', 'Ceramic', '未知', '中国陕西', '中国'),
(3, '宋代青瓷碗', '瓷', '宋代', '瓷器', '佚名', 'https://example.com/relic3.jpg', '卢浮宫', '宋代', '器皿', '瓷', 'Porcelain', '未知', '中国浙江', '中国'),
(4, '明代青花瓷瓶', '瓷', '明代', '瓷器', '佚名', 'https://example.com/relic4.jpg', '大英博物馆', '明代', '器皿', '瓷', 'Porcelain', '未知', '中国江西', '中国'),
(5, '清代珐琅彩碗', '珐琅', '清代', '瓷器', '佚名', 'https://example.com/relic5.jpg', '东京国立博物馆', '清代', '器皿', '珐琅', 'Enamel', '未知', '中国北京', '中国')
ON DUPLICATE KEY UPDATE object_name=VALUES(object_name);

INSERT INTO artifact (object_id, title, period, type, material, description, museum_id, detail_url, image_url, accession_number, crawl_date, is_deleted) VALUES
('r-001', '商代青铜鼎', '商代', '青铜器', '青铜', '商代晚期青铜祭祀用鼎', 'm-001', 'https://example.com/r1', 'https://example.com/relic1.jpg', 'BM-001', '2024-01-01', 0),
('r-002', '唐代三彩骆驼', '唐代', '陶俑', '陶瓷', '唐代三彩骆驼俑', 'm-002', 'https://example.com/r2', 'https://example.com/relic2.jpg', 'MET-002', '2024-01-01', 0),
('r-003', '宋代青瓷碗', '宋代', '瓷器', '瓷', '南宋龙泉窑青瓷碗', 'm-003', 'https://example.com/r3', 'https://example.com/relic3.jpg', 'LOUV-003', '2024-01-01', 0),
('r-004', '明代青花瓷瓶', '明代', '瓷器', '瓷', '明代青花缠枝莲纹瓶', 'm-001', 'https://example.com/r4', 'https://example.com/relic4.jpg', 'BM-004', '2024-01-01', 0),
('r-005', '清代珐琅彩碗', '清代', '瓷器', '珐琅', '清代珐琅彩花鸟纹碗', 'm-004', 'https://example.com/r5', 'https://example.com/relic5.jpg', 'TNM-005', '2024-01-01', 0)
ON DUPLICATE KEY UPDATE title=VALUES(title);

INSERT INTO img_url_table (img_id, url) VALUES
('default', 'https://example.com/default.jpg')
ON DUPLICATE KEY UPDATE url=VALUES(url);

INSERT INTO user_logs (user_id, type, description) VALUES
(1001, 'LOGIN', '用户登录系统'),
(1001, 'VIEW', '浏览商代青铜鼎')
ON DUPLICATE KEY UPDATE description=VALUES(description);
