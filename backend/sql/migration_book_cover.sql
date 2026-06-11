-- ============================================
-- 图书图片功能迁移脚本
-- 添加图书封面图片字段
-- ============================================

-- 1. 给 book 表添加封面图片字段
ALTER TABLE book ADD COLUMN cover_image VARCHAR(500) DEFAULT NULL COMMENT '图书封面图片URL（阿里云OSS）';

-- 2. 添加索引（可选，用于按图片存在性查询）
ALTER TABLE book ADD INDEX idx_has_cover (cover_image);

-- 3. 添加备注说明
COMMENT ON COLUMN book.cover_image IS '图书封面图片URL，存储阿里云OSS对象地址，如：https://your-bucket.oss-cn-xxx.aliyuncs.com/books/xxx.jpg';
