package com.mo.moaicodemother.service.impl;

import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.UUID;
import cn.hutool.core.util.StrUtil;

import com.mo.moaicodemother.exception.ErrorCode;
import com.mo.moaicodemother.exception.ThrowUtils;
import com.mo.moaicodemother.manager.CosManager;
import com.mo.moaicodemother.service.ScreenshotService;
import com.mo.moaicodemother.utils.WebScreenShotUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.io.File;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@Slf4j
public class ScreenshotServiceImpl implements ScreenshotService {

    @Resource
    private CosManager cosManager;

    @Override
    public String generateAndUploadScreenshot(String webUrl) {
        long serviceStartTime = System.currentTimeMillis();
        log.info("===== ScreenshotService 开始执行 =====");
        
        //参数校验
        ThrowUtils.throwIf(StrUtil.isBlank(webUrl), ErrorCode.PARAMS_ERROR, "网页地址不能为空");
        log.info("开始截图：URL:{}", webUrl);
        
        //本地截图
        long screenshotStartTime = System.currentTimeMillis();
        log.info("[阶段1] 开始本地截图...");
        String localScreenshotPath = WebScreenShotUtils.saveWebPageScreenshot(webUrl);
        ThrowUtils.throwIf(StrUtil.isBlank(localScreenshotPath), ErrorCode.OPERATION_ERROR, "截图失败");
        log.info("[阶段1] 本地截图完成，耗时: {}ms", System.currentTimeMillis() - screenshotStartTime);
        
        //上传图片到COS
        try{
            long uploadStartTime = System.currentTimeMillis();
            log.info("[阶段2] 开始上传到COS...");
            String cosUrl = uploadScreenshotToCos(localScreenshotPath);
            ThrowUtils.throwIf(StrUtil.isBlank(cosUrl), ErrorCode.OPERATION_ERROR, "上传失败");
            log.info("[阶段2] COS上传完成，耗时: {}ms", System.currentTimeMillis() - uploadStartTime);
            log.info("上传成功：URL:{}", cosUrl);
            
            long totalServiceTime = System.currentTimeMillis() - serviceStartTime;
            log.info("===== ScreenshotService 执行完成，总耗时: {}ms ({}秒) =====", totalServiceTime, totalServiceTime / 1000.0);
            return cosUrl;
        } finally {
            //清理本地文件
            long cleanupStartTime = System.currentTimeMillis();
            cleanupLocalFile(localScreenshotPath);
            log.info("[阶段3] 清理本地文件完成，耗时: {}ms", System.currentTimeMillis() - cleanupStartTime);
        }
    }

    /**
     * 上传截图到对象存储
     *
     * @param localScreenshotPath 本地截图路径
     * @return 对象存储访问URL，失败返回null
     */
    private String uploadScreenshotToCos(String localScreenshotPath) {
        if (StrUtil.isBlank(localScreenshotPath)) {
            return null;
        }
        File screenshotFile = new File(localScreenshotPath);
        if (!screenshotFile.exists()) {
            log.error("截图文件不存在: {}", localScreenshotPath);
            return null;
        }
        // 生成 COS 对象键
        String fileName = UUID.randomUUID().toString().substring(0, 8) + "_compressed.jpg";
        String cosKey = generateScreenshotKey(fileName);
        return cosManager.uploadFile(cosKey, screenshotFile);
    }

    /**
     * 生成截图的对象存储键
     * 格式：/screenshots/2025/07/31/filename.jpg
     */
    private String generateScreenshotKey(String fileName) {
        String datePath = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy/MM/dd"));
        return String.format("/screenshots/%s/%s", datePath, fileName);
    }

    /**
     * 清理本地文件
     *
     * @param localFilePath 本地文件路径
     */
    private void cleanupLocalFile(String localFilePath) {
        File localFile = new File(localFilePath);
        if (localFile.exists()) {
            FileUtil.del(localFile);
            log.info("清理本地文件成功: {}", localFilePath);
        }
    }

}
