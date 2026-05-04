USE bms;

-- 确保user表有enabled字段
ALTER TABLE `user` ADD COLUMN IF NOT EXISTS enabled TINYINT(1) DEFAULT 1;

-- 创建operation_log表
CREATE TABLE IF NOT EXISTS operation_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    username VARCHAR(50) NOT NULL,
    operation_type VARCHAR(50) NOT NULL,
    module VARCHAR(50) NOT NULL,
    target_id INT,
    description TEXT,
    ip_address VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;

