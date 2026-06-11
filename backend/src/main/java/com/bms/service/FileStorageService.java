/**
 * 文件存储服务 - 支持本地存储和阿里云OSS
 */
package com.bms.service;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.OSSException;
import com.aliyun.oss.model.ObjectMetadata;
import com.aliyun.oss.model.PutObjectRequest;
import com.bms.config.OssConfig;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * 文件存储服务
 * 支持本地存储和阿里云OSS对象存储
 */
@Service
@Slf4j
public class FileStorageService {

    @Autowired
    private OssConfig ossConfig;

    @Value("${file.storage.local-path:uploads}")
    private String localStoragePath;

    private OSS ossClient;

    @PostConstruct
    public void init() {
        if (ossConfig.isEnabled()) {
            try {
                ossClient = new OSSClientBuilder().build(
                        ossConfig.getEndpoint(),
                        ossConfig.getAccessKeyId(),
                        ossConfig.getAccessKeySecret()
                );
                log.info("阿里云OSS初始化成功，Bucket: {}", ossConfig.getBucketName());
            } catch (Exception e) {
                log.error("阿里云OSS初始化失败，将使用本地存储: {}", e.getMessage());
                ossConfig.setEnabled(false);
            }
        } else {
            log.info("OSS未启用，使用本地文件存储，路径: {}", localStoragePath);
        }
    }

    @PreDestroy
    public void destroy() {
        if (ossClient != null) {
            ossClient.shutdown();
            log.info("阿里云OSS客户端已关闭");
        }
    }

    /**
     * 上传图片文件
     *
     * @param file     MultipartFile文件
     * @param fileType 文件类型（如 book、avatar 等）
     * @return 文件访问URL
     */
    public String uploadImage(MultipartFile file, String fileType) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("上传文件不能为空");
        }

        // 验证文件类型
        String originalFilename = file.getOriginalFilename();
        String extension = getFileExtension(originalFilename);
        if (!isImageFile(extension)) {
            throw new RuntimeException("只能上传图片文件（jpg、jpeg、png、gif、webp）");
        }

        // 验证文件大小（最大5MB）
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new RuntimeException("图片大小不能超过5MB");
        }

        // 生成唯一文件名
        String newFileName = generateFileName(extension);

        try {
            if (ossConfig.isEnabled()) {
                return uploadToOss(file.getInputStream(), newFileName, fileType);
            } else {
                return uploadToLocal(file.getInputStream(), newFileName, fileType);
            }
        } catch (IOException e) {
            log.error("文件上传失败: {}", e.getMessage());
            throw new RuntimeException("文件上传失败: " + e.getMessage());
        }
    }

    /**
     * 上传到阿里云OSS
     */
    private String uploadToOss(InputStream inputStream, String fileName, String fileType) {
        try {
            // 创建目录路径：books/2024/01/
            String datePath = new SimpleDateFormat("yyyy/MM/").format(new Date());
            String objectName = ossConfig.getBasePath() + datePath + fileName;

            // 创建PutObjectRequest
            PutObjectRequest putObjectRequest = new PutObjectRequest(
                    ossConfig.getBucketName(),
                    objectName,
                    inputStream
            );

            // 设置ContentType
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentType(getContentType(fileName));
            putObjectRequest.setMetadata(metadata);

            // 上传文件
            ossClient.putObject(putObjectRequest);

            // 返回访问URL
            String url = ossConfig.getFileUrlPrefix() + objectName;
            log.info("文件上传到OSS成功: {}", url);
            return url;

        } catch (OSSException e) {
            log.error("OSS上传失败: {}", e.getErrorMessage());
            throw new RuntimeException("OSS上传失败: " + e.getErrorMessage());
        }
    }

    /**
     * 上传到本地存储
     */
    private String uploadToLocal(InputStream inputStream, String fileName, String fileType) {
        try {
            // 创建目录路径
            String datePath = new SimpleDateFormat("yyyy/MM/").format(new Date());
            java.nio.file.Path dirPath = java.nio.file.Paths.get(localStoragePath, fileType, datePath);
            java.nio.file.Files.createDirectories(dirPath);

            // 保存文件
            java.nio.file.Path filePath = dirPath.resolve(fileName);
            java.nio.file.Files.copy(inputStream, filePath);

            // 返回访问URL（相对路径）
            String url = "/" + fileType + "/" + datePath + fileName;
            log.info("文件上传到本地成功: {}", url);
            return url;

        } catch (IOException e) {
            log.error("本地上传失败: {}", e.getMessage());
            throw new RuntimeException("本地上传失败: " + e.getMessage());
        }
    }

    /**
     * 删除文件
     */
    public boolean deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return false;
        }

        try {
            if (ossConfig.isEnabled() && fileUrl.contains(ossConfig.getBucketName())) {
                // 从OSS删除
                String objectName = extractObjectName(fileUrl);
                ossClient.deleteObject(ossConfig.getBucketName(), objectName);
                log.info("从OSS删除文件成功: {}", objectName);
                return true;
            } else if (fileUrl.startsWith("/")) {
                // 从本地删除
                java.nio.file.Path filePath = java.nio.file.Paths.get(localStoragePath, fileUrl.substring(1));
                java.nio.file.Files.deleteIfExists(filePath);
                log.info("从本地删除文件成功: {}", filePath);
                return true;
            }
        } catch (Exception e) {
            log.error("删除文件失败: {}", e.getMessage());
        }
        return false;
    }

    /**
     * 生成唯一文件名
     */
    private String generateFileName(String extension) {
        return UUID.randomUUID().toString().replace("-", "") + "." + extension;
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "jpg";
        }
        return filename.substring(filename.lastIndexOf(".") + 1).toLowerCase();
    }

    /**
     * 判断是否为图片文件
     */
    private boolean isImageFile(String extension) {
        return "jpg".equals(extension) || "jpeg".equals(extension) ||
                "png".equals(extension) || "gif".equals(extension) ||
                "webp".equals(extension);
    }

    /**
     * 获取ContentType
     */
    private String getContentType(String filename) {
        String extension = getFileExtension(filename);
        switch (extension) {
            case "jpg":
            case "jpeg":
                return "image/jpeg";
            case "png":
                return "image/png";
            case "gif":
                return "image/gif";
            case "webp":
                return "image/webp";
            default:
                return "application/octet-stream";
        }
    }

    /**
     * 从URL中提取OSS对象名
     */
    private String extractObjectName(String url) {
        // https://bucket.endpoint/path/to/object.jpg -> path/to/object.jpg
        if (url.contains(ossConfig.getBucketName() + "." + ossConfig.getEndpoint())) {
            int index = url.indexOf(ossConfig.getBucketName() + "." + ossConfig.getEndpoint()) +
                    (ossConfig.getBucketName() + "." + ossConfig.getEndpoint()).length() + 1;
            return url.substring(index);
        }
        return url;
    }
}
