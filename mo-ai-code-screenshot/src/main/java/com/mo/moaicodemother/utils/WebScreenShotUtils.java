package com.mo.moaicodemother.utils;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.RandomUtil;
import cn.hutool.core.util.StrUtil;
import com.mo.moaicodemother.exception.BusinessException;
import com.mo.moaicodemother.exception.ErrorCode;
import io.github.bonigarcia.wdm.WebDriverManager;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.time.Duration;
import java.util.UUID;

@Slf4j
public class WebScreenShotUtils {

    //第一步，初始化驱动，需要注意避免重复初始化驱动程序

    //1,在静态代码块里初始化驱动，确保整个应用生命周期内只初始化一次

    //2,默认使用已经初始化好的驱动实例

    //3,在项目停止前正确销毁驱动，释放资源
    private static final WebDriver webDriver;

    // 全局静态初始化，避免重复初始化驱动程序：
    static {
        final int DEFAULT_WIDTH = 1600;
        final int DEFAULT_HEIGHT = 900;
        webDriver = initChromeDriver(DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    /**
     * 退出时销毁
     */
    @PreDestroy
    public void destroy() {
        webDriver.quit();
    }

    public static String saveWebPageScreenshot(String webUrl){
        //非空校验
        if (StrUtil.isBlank(webUrl)){
            log.error("网页加载失败,webUrl不能为空");
            return null;
        }
        try {


            //创建临时目录
            String rootPath = System.getProperty("user.dir") + "/tmp/screenshots/" + UUID.randomUUID().toString().substring(0, 8);
            FileUtil.mkdir(rootPath);
            //图片加载
            final String IMG_SUFFIX = ".png";
            String imgPath = rootPath + File.separator + RandomUtil.randomNumbers(5) + IMG_SUFFIX;
            //访问网页
            webDriver.get(webUrl);
            //等待页面加载完成
            waitForPageLoad(webDriver);
            //截图
            byte[] screenshotBytes = ((TakesScreenshot) webDriver).getScreenshotAs(OutputType.BYTES);
            //保存原始图片
            saveImage(screenshotBytes, imgPath);
            log.info("原始图片保存成功：{}", imgPath);
            //压缩图片
            final String COMPRESS_SUFFIX = "_compress.jpg";
            String compressImgPath = rootPath + File.separator + RandomUtil.randomNumbers(5) + COMPRESS_SUFFIX;
            compressImage(imgPath, compressImgPath);
            log.info("压缩图片保存成功：{}", compressImgPath);
            //删除原始图片
            FileUtil.del(imgPath);
            return compressImgPath;
        } catch (Exception e) {
            log.error("网页截图失败：{}", webUrl, e);
            return null;
        }
    }

    /**
     * 初始化 Chrome 浏览器驱动
     */
    private static WebDriver initChromeDriver(int width, int height) {
        try {
            // 自动管理 ChromeDriver
            WebDriverManager.chromedriver().setup();
            // 配置 Chrome 选项
            ChromeOptions options = new ChromeOptions();
            // 无头模式
            options.addArguments("--headless");
            // 禁用GPU（在某些环境下避免问题）
            options.addArguments("--disable-gpu");
            // 禁用沙盒模式（Docker环境需要）
            options.addArguments("--no-sandbox");
            // 禁用开发者shm使用
            options.addArguments("--disable-dev-shm-usage");
            // 设置窗口大小
            options.addArguments(String.format("--window-size=%d,%d", width, height));
            // 禁用扩展
            options.addArguments("--disable-extensions");
            // 设置用户代理
            options.addArguments("--user-agent=Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
            // 创建驱动
            WebDriver driver = new ChromeDriver(options);
            // 设置页面加载超时
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));
            // 设置隐式等待
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
            return driver;
        } catch (Exception e) {
            log.error("初始化 Chrome 浏览器失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "初始化 Chrome 浏览器失败");
        }
    }

    /**
     * 保存图片文件
     * @param imgBytes
     * @param imgPath
     */
    private static void saveImage(byte[] imgBytes, String imgPath){
        try{
            FileUtil.writeBytes(imgBytes, imgPath);
        }catch (Exception e){
            log.error("保存图片失败：{}",imgPath, e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "保存图片失败");
        }
    }

    /**
     * 压缩图片
     * @param originImgPath
     * @param compressImgPath
     */
    private static void compressImage(String originImgPath, String compressImgPath){
        final float COMPRESS_QUALITY = 0.3f;
        try{
            ImgUtil.compress(
                    FileUtil.file(originImgPath),
                    FileUtil.file(compressImgPath),
                    COMPRESS_QUALITY
            );
        }catch (Exception e){
            log.error("图片压缩失败：{} -> {}",originImgPath, compressImgPath,e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "图片压缩失败");
        }
    }

    /**
     * 等待页面加载完成
     * @param webDriver
     */
    private static void waitForPageLoad(WebDriver webDriver){
        try {
            //创建等待页面加载对象
            WebDriverWait wait = new WebDriverWait(webDriver, Duration.ofSeconds(10));
            //等待 document.readyState === 'complete'
            wait.until(driver -> ((JavascriptExecutor) driver)
                    .executeScript("return document.readyState")
                    .equals("complete")
            );
            //额外等待一段时间，确保动态资源内容加载完成
            Thread.sleep(5000);
            log.info("页面加载完成");
        } catch (Exception e) {
            log.error("等待页面加载失败,继续执行截图", e);
        }
    }
}
