-- =============================================
-- 图书管理系统 - 完整数据库初始化脚本
-- 根据实际数据库结构生成
-- =============================================

-- 创建数据库
CREATE DATABASE IF NOT EXISTS bms CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE bms;

-- =============================================
-- 1. 用户表
-- =============================================
DROP TABLE IF EXISTS borrow_record;
DROP TABLE IF EXISTS operation_log;
DROP TABLE IF EXISTS announcement;
DROP TABLE IF EXISTS book;
DROP TABLE IF EXISTS user;

CREATE TABLE `user` (
    id INT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password VARCHAR(100) NOT NULL,
    role VARCHAR(20) DEFAULT 'READER',
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    enabled TINYINT(1) DEFAULT 1
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 注意：用户数据由后端应用启动时自动初始化
-- 初始账号：admin/librarian/reader，密码均为 123456

-- =============================================
-- 2. 图书表
-- =============================================
CREATE TABLE `book` (
    id INT AUTO_INCREMENT PRIMARY KEY,
    isbn VARCHAR(50),
    title VARCHAR(100) NOT NULL,
    author VARCHAR(50),
    publisher VARCHAR(100),
    publish_date DATETIME,
    category VARCHAR(50),
    price DECIMAL(10,2),
    stock INT DEFAULT 0,
    description TEXT,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书表';

-- 初始化图书数据
INSERT INTO `book` (`id`, `isbn`, `title`, `author`, `publisher`, `publish_date`, `category`, `price`, `stock`, `description`, `create_time`, `update_time`) VALUES
(1, '9787532781876', '活着', '余华', '上海文艺出版社', '2012-08-01 00:00:00', '文学小说', 39.00, 5, '讲述了一个人一生的故事，这是一个历尽世间沧桑和磨难老人的人生感言。', '2026-04-30 14:00:00', '2026-04-30 14:00:00'),
(2, '9787506382368', '三体', '刘慈欣', '重庆出版社', '2008-01-01 00:00:00', '科幻小说', 68.00, 3, '地球文明向宇宙发出的第一声啼鸣，以太阳为天线，向宇宙深处呐喊。', '2026-04-30 14:05:00', '2026-04-30 14:05:00'),
(3, '9787530217743', '百年孤独', '加西亚·马尔克斯', '南海出版公司', '2011-06-01 00:00:00', '文学小说', 55.00, 4, '魔幻现实主义文学的代表作，描写了布恩迪亚家族七代人的传奇故事。', '2026-04-30 14:10:00', '2026-04-30 14:10:00'),
(4, '9787115428577', 'Python编程：从入门到实践', 'Eric Matthes', '人民邮电出版社', '2016-07-01 00:00:00', '计算机编程', 89.00, 6, '针对所有层次Python读者的经典编程入门书籍。', '2026-04-30 14:15:00', '2026-04-30 14:15:00'),
(5, '9787111603746', '深入理解计算机系统', 'Randal E.Bryant', '机械工业出版社', '2016-01-01 00:00:00', '计算机教材', 129.00, 2, '从程序员的视角详细阐述计算机系统的本质概念。', '2026-04-30 14:20:00', '2026-04-30 14:20:00'),
(6, '9787532769942', '围城', '钱钟书', '上海文艺出版社', '2018-08-01 00:00:00', '文学小说', 42.00, 5, '被誉为"新儒林外史"的经典长篇讽刺小说。', '2026-04-30 14:25:00', '2026-04-30 14:25:00'),
(7, '9787115333657', 'JavaScript高级程序设计', 'Nicholas C.Zakas', '人民邮电出版社', '2012-02-01 00:00:00', '计算机编程', 129.00, 3, 'JavaScript技术经典名著，全面深入地介绍了JavaScript语言。', '2026-04-30 14:30:00', '2026-04-30 14:30:00'),
(8, '9787508647387', '人类简史', '尤瓦尔·赫拉利', '中信出版社', '2017-02-01 00:00:00', '历史人文', 68.00, 4, '从十万年前有生命迹象开始到21世纪资本、科技交织的人类发展史。', '2026-04-30 14:35:00', '2026-04-30 14:35:00'),
(9, '9787544291505', '解忧杂货店', '东野圭吾', '南海出版公司', '2014-05-01 00:00:00', '文学小说', 39.50, 6, '日本著名作家东野圭吾的温情悬疑佳作。', '2026-04-30 14:40:00', '2026-04-30 14:40:00'),
(10, '9787111544937', 'Java编程思想', 'Bruce Eckel', '机械工业出版社', '2015-10-01 00:00:00', '计算机编程', 149.00, 2, 'Java学习必读经典，深受全球程序员欢迎。', '2026-04-30 14:45:00', '2026-04-30 14:45:00'),
(11, '9787532767727', '红楼梦', '曹雪芹', '上海文艺出版社', '2018-08-01 00:00:00', '古典文学', 198.00, 3, '中国古典四大名著之一，清代长篇小说巅峰之作。', '2026-05-01 09:00:00', '2026-05-01 09:00:00'),
(12, '9787506364697', '平凡的世界', '路遥', '作家出版社', '2012-06-01 00:00:00', '文学小说', 108.00, 4, '全景式地表现中国当代城乡社会生活的长篇小说。', '2026-05-01 09:05:00', '2026-05-01 09:05:00'),
(13, '9787544287644', '小王子', '圣埃克苏佩里', '南海出版公司', '2013-01-01 00:00:00', '童话寓言', 29.00, 8, '献给曾经是孩子的大人们。', '2026-05-01 09:10:00', '2026-05-01 09:10:00'),
(14, '9787111407010', '设计模式：可复用面向对象软件的基础', 'Erich Gamma', '机械工业出版社', '2010-10-01 00:00:00', '计算机编程', 69.00, 3, '软件工程领域的经典著作，深入讲解23种设计模式。', '2026-05-01 09:15:00', '2026-05-01 09:15:00'),
(15, '9787508658068', '原则', '瑞·达利欧', '中信出版社', '2018-01-01 00:00:00', '成功励志', 128.00, 4, '华尔街投资大神的人生经验之作。', '2026-05-01 09:20:00', '2026-05-01 09:20:00');

-- =============================================
-- 3. 借阅记录表
-- =============================================
CREATE TABLE borrow_record (
    id INT AUTO_INCREMENT PRIMARY KEY,
    book_id INT NOT NULL,
    user_id INT NOT NULL,
    borrow_date DATETIME DEFAULT CURRENT_TIMESTAMP,
    return_date DATETIME,
    status VARCHAR(20) DEFAULT 'BORROWING',
    remark VARCHAR(200),
    operator_id INT,
    renewal_count INT DEFAULT 0,
    due_date DATETIME,
    overdue_days INT DEFAULT 0,
    fine DECIMAL(10,2) DEFAULT 0.00,
    FOREIGN KEY (book_id) REFERENCES `book`(id),
    FOREIGN KEY (user_id) REFERENCES user(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='借阅记录表';

-- =============================================
-- 4. 公告表
-- =============================================
CREATE TABLE announcement (
    id INT AUTO_INCREMENT PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    content TEXT NOT NULL,
    enabled TINYINT(1) DEFAULT 1,
    sort_order INT DEFAULT 0,
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    update_time DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='公告表';

-- 初始化公告数据
INSERT INTO announcement (`id`, `title`, `content`, `enabled`, `sort_order`, `create_time`, `update_time`) VALUES
(1, '图书馆暑期开放时间调整通知', '尊敬的各位读者：\n\n暑期将至，为更好地服务广大读者，图书馆将于2026年7月1日至8月31日调整开放时间：\n\n- 周一至周五：8:30 - 17:30\n- 周六周日：9:00 - 16:00\n\n请各位读者合理安排借阅时间，祝大家暑期愉快！\n\n图书馆管理处\n2026年6月20日', 1, 1, '2026-06-20 10:00:00', '2026-06-20 10:00:00'),
(2, '新书推荐：《三体》三部曲', '《三体》三部曲是刘慈欣创作的系列长篇科幻小说，由《三体》《三体II·黑暗森林》《三体III·死神永生》组成。作品讲述了地球文明向宇宙发出的第一声啼鸣，以及由此引发的一系列惊心动魄的故事。\n\n馆藏位置：科幻小说区 A-3排\n欢迎广大读者借阅！', 1, 2, '2026-05-01 14:00:00', '2026-05-01 14:00:00'),
(3, '借阅规则调整说明', '为提高图书流通效率，自2026年5月1日起，借阅规则调整如下：\n\n1. 借阅期限：普通读者从30天调整为45天\n2. 续借次数：从1次调整为2次\n3. 逾期罚款：每天0.2元，封顶10元\n\n感谢各位读者的理解与支持！', 1, 3, '2026-04-25 09:00:00', '2026-04-25 09:00:00');

-- =============================================
-- 5. 操作日志表
-- =============================================
CREATE TABLE operation_log (
    id INT AUTO_INCREMENT PRIMARY KEY,
    user_id INT,
    username VARCHAR(50) NOT NULL,
    operation_type VARCHAR(50) NOT NULL,
    module VARCHAR(50) NOT NULL,
    target_id INT,
    description VARCHAR(500),
    ip_address VARCHAR(50),
    create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_module (`module`),
    INDEX idx_create_time (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- =============================================
-- 完成提示
-- =============================================
SELECT '数据库初始化完成！' AS result;
SELECT '图书表记录数:', COUNT(*) FROM `book`;
SELECT '公告表记录数:', COUNT(*) FROM announcement;