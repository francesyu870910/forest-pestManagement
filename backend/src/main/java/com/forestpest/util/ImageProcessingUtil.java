package com.forestpest.util;

import com.forestpest.exception.BusinessException;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

/**
 * 图片处理工具类
 */
public class ImageProcessingUtil {
    
    // 支持的图片格式
    private static final List<String> SUPPORTED_FORMATS = Arrays.asList(
        "image/jpeg", "image/jpg", "image/png", "image/gif", "image/bmp"
    );
    
    // 最大文件大小（10MB）
    private static final long MAX_FILE_SIZE = 10 * 1024 * 1024;
    
    // 缩略图尺寸
    private static final int THUMBNAIL_WIDTH = 200;
    private static final int THUMBNAIL_HEIGHT = 200;
    
    /**
     * 验证图片文件
     */
    public static void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BusinessException("图片文件不能为空");
        }
        
        // 验证文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new BusinessException("图片文件大小不能超过10MB");
        }
        
        // 验证文件格式
        String contentType = file.getContentType();
        if (contentType == null || !SUPPORTED_FORMATS.contains(contentType.toLowerCase())) {
            throw new BusinessException("不支持的图片格式，支持的格式：JPEG, PNG, GIF, BMP");
        }
        
        // 验证文件扩展名
        String originalFilename = file.getOriginalFilename();
        if (originalFilename != null) {
            String extension = getFileExtension(originalFilename).toLowerCase();
            if (!isValidImageExtension(extension)) {
                throw new BusinessException("不支持的图片文件扩展名");
            }
        }
    }
    
    /**
     * 获取文件扩展名
     */
    public static String getFileExtension(String filename) {
        if (filename == null || filename.isEmpty()) {
            return "";
        }
        
        int lastDotIndex = filename.lastIndexOf('.');
        if (lastDotIndex == -1 || lastDotIndex == filename.length() - 1) {
            return "";
        }
        
        return filename.substring(lastDotIndex + 1);
    }
    
    /**
     * 验证图片扩展名
     */
    private static boolean isValidImageExtension(String extension) {
        List<String> validExtensions = Arrays.asList("jpg", "jpeg", "png", "gif", "bmp");
        return validExtensions.contains(extension);
    }
    
    /**
     * 生成缩略图
     */
    public static byte[] generateThumbnail(MultipartFile file) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            throw new BusinessException("无法读取图片文件");
        }
        
        // 计算缩略图尺寸，保持宽高比
        int originalWidth = originalImage.getWidth();
        int originalHeight = originalImage.getHeight();
        
        double aspectRatio = (double) originalWidth / originalHeight;
        int thumbnailWidth, thumbnailHeight;
        
        if (aspectRatio > 1) {
            // 宽图
            thumbnailWidth = THUMBNAIL_WIDTH;
            thumbnailHeight = (int) (THUMBNAIL_WIDTH / aspectRatio);
        } else {
            // 高图
            thumbnailHeight = THUMBNAIL_HEIGHT;
            thumbnailWidth = (int) (THUMBNAIL_HEIGHT * aspectRatio);
        }
        
        // 创建缩略图
        BufferedImage thumbnailImage = new BufferedImage(
            thumbnailWidth, thumbnailHeight, BufferedImage.TYPE_INT_RGB);
        
        Graphics2D g2d = thumbnailImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, thumbnailWidth, thumbnailHeight, null);
        g2d.dispose();
        
        // 转换为字节数组
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(thumbnailImage, "jpg", baos);
        return baos.toByteArray();
    }
    
    /**
     * 获取图片信息
     */
    public static ImageInfo getImageInfo(MultipartFile file) throws IOException {
        BufferedImage image = ImageIO.read(file.getInputStream());
        if (image == null) {
            throw new BusinessException("无法读取图片文件");
        }
        
        ImageInfo info = new ImageInfo();
        info.setWidth(image.getWidth());
        info.setHeight(image.getHeight());
        info.setFileSize(file.getSize());
        info.setFormat(getImageFormat(file.getContentType()));
        info.setFileName(file.getOriginalFilename());
        
        return info;
    }
    
    /**
     * 获取图片格式
     */
    private static String getImageFormat(String contentType) {
        if (contentType == null) {
            return "unknown";
        }
        
        switch (contentType.toLowerCase()) {
            case "image/jpeg":
            case "image/jpg":
                return "JPEG";
            case "image/png":
                return "PNG";
            case "image/gif":
                return "GIF";
            case "image/bmp":
                return "BMP";
            default:
                return "unknown";
        }
    }
    
    /**
     * 压缩图片
     */
    public static byte[] compressImage(MultipartFile file, float quality) throws IOException {
        if (quality < 0.1f || quality > 1.0f) {
            throw new IllegalArgumentException("图片质量参数必须在0.1-1.0之间");
        }
        
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            throw new BusinessException("无法读取图片文件");
        }
        
        // 简化的压缩实现
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(originalImage, "jpg", baos);
        return baos.toByteArray();
    }
    
    /**
     * 调整图片尺寸
     */
    public static byte[] resizeImage(MultipartFile file, int targetWidth, int targetHeight) throws IOException {
        BufferedImage originalImage = ImageIO.read(file.getInputStream());
        if (originalImage == null) {
            throw new BusinessException("无法读取图片文件");
        }
        
        BufferedImage resizedImage = new BufferedImage(
            targetWidth, targetHeight, BufferedImage.TYPE_INT_RGB);
        
        Graphics2D g2d = resizedImage.createGraphics();
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.drawImage(originalImage, 0, 0, targetWidth, targetHeight, null);
        g2d.dispose();
        
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(resizedImage, "jpg", baos);
        return baos.toByteArray();
    }
    
    /**
     * 生成安全的文件名
     */
    public static String generateSafeFileName(String originalFileName) {
        if (originalFileName == null || originalFileName.isEmpty()) {
            return System.currentTimeMillis() + ".jpg";
        }
        
        // 移除特殊字符
        String safeName = originalFileName.replaceAll("[^a-zA-Z0-9._-]", "_");
        
        // 添加时间戳避免重名
        String extension = getFileExtension(safeName);
        String nameWithoutExt = safeName.substring(0, safeName.lastIndexOf('.'));
        
        return nameWithoutExt + "_" + System.currentTimeMillis() + "." + extension;
    }
    
    /**
     * 图片信息类
     */
    public static class ImageInfo {
        private int width;
        private int height;
        private long fileSize;
        private String format;
        private String fileName;
        
        // Getters and Setters
        public int getWidth() {
            return width;
        }
        
        public void setWidth(int width) {
            this.width = width;
        }
        
        public int getHeight() {
            return height;
        }
        
        public void setHeight(int height) {
            this.height = height;
        }
        
        public long getFileSize() {
            return fileSize;
        }
        
        public void setFileSize(long fileSize) {
            this.fileSize = fileSize;
        }
        
        public String getFormat() {
            return format;
        }
        
        public void setFormat(String format) {
            this.format = format;
        }
        
        public String getFileName() {
            return fileName;
        }
        
        public void setFileName(String fileName) {
            this.fileName = fileName;
        }
        
        @Override
        public String toString() {
            return "ImageInfo{" +
                    "width=" + width +
                    ", height=" + height +
                    ", fileSize=" + fileSize +
                    ", format='" + format + '\'' +
                    ", fileName='" + fileName + '\'' +
                    '}';
        }
    }
}