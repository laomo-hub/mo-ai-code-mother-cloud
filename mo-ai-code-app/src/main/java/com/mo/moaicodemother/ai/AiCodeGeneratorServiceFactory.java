package com.mo.moaicodemother.ai;


import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.mo.moaicodemother.AiCodeGeneratorService;
import com.mo.moaicodemother.guardrail.PromptSafetyInputGuardrail;
import com.mo.moaicodemother.tools.*;
import com.mo.moaicodemother.exception.BusinessException;
import com.mo.moaicodemother.exception.ErrorCode;
import com.mo.moaicodemother.enums.CodeGenTypeEnum;
import com.mo.moaicodemother.service.ChatHistoryService;
import com.mo.moaicodemother.utils.SpringContextUtil;
import dev.langchain4j.community.store.memory.chat.redis.RedisChatMemoryStore;
import dev.langchain4j.data.message.ToolExecutionResultMessage;
import dev.langchain4j.memory.chat.MessageWindowChatMemory;
import dev.langchain4j.model.chat.ChatModel;
import dev.langchain4j.model.chat.StreamingChatModel;
import dev.langchain4j.service.AiServices;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

/**
 * AI服务创建工厂
 */
@Configuration
@Slf4j
public class AiCodeGeneratorServiceFactory {

    @Resource(name = "openAiChatModel")
    private ChatModel chatModel;

    @Resource
    private RedisChatMemoryStore redisChatMemoryStore;

    @Resource
    private ChatHistoryService chatHistoryService;

    @Resource
    private ToolManager toolManager;

    @Resource
    private FileWriteTool fileWriteTool;

    @Resource
    private FileReadTool fileReadTool;

    @Resource
    private FileModifyTool fileModifyTool;

    @Resource
    private FileDeleteTool fileDeleteTool;

    @Resource
    private FileDirReadTool fileDirReadTool;

    /**
     * AI 服务实例缓存
     * 缓存策略：
     * - 最大缓存 1000 个实例
     * - 写入后 30 分钟过期
     * - 访问后 10 分钟过期
     */
    private final Cache<String, AiCodeGeneratorService> serviceCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(Duration.ofMinutes(30))
            .expireAfterAccess(Duration.ofMinutes(10))
            .removalListener((key, value, cause) -> {
                log.debug("AI 服务实例被移除，缓存键: {}, 原因: {}", key, cause);
            })
            .build();


    /**
     * 获取AI代码生成器服务(为了兼容老逻辑)
     * @param appId
     * @return
     */
    public AiCodeGeneratorService getAiCodeGeneratorService(long appId) {
        return getAiCodeGeneratorService(appId, CodeGenTypeEnum.HTML);
    }


    /**
     * 获取AI代码生成器服务
     * @param appId
     * @param codeGenType
     * @return
     */
    public AiCodeGeneratorService getAiCodeGeneratorService (long appId, CodeGenTypeEnum codeGenType) {
        String cacheKey = buildCacheKey(appId, codeGenType);
        return serviceCache.get(cacheKey,key->createAiCodeGeneratorService(appId,codeGenType));
    }

    /**
     * 创建AI代码生成器服务
     * @param appId
     * @param codeGenType
     * @return
     */
    private AiCodeGeneratorService createAiCodeGeneratorService(long appId, CodeGenTypeEnum codeGenType){
        log.info("为appId: {} 创建AI代码生成器服务",appId);
        MessageWindowChatMemory chatMemory=MessageWindowChatMemory.builder()
                .id(appId)
                .chatMemoryStore(redisChatMemoryStore)
                .maxMessages(20)
                .build();
        chatHistoryService.loadChatHistoryToMemory(appId,chatMemory,20);
        return switch (codeGenType){
            //Vue项目生成，使用工具调用和推理模型
            case VUE_PROJECT -> {
                // 使用多例模式的 StreamingChatModel 解决并发问题
                StreamingChatModel reasoningStreamingChatModel = SpringContextUtil.getBean("reasoningStreamingChatModelPrototype", StreamingChatModel.class);
                yield AiServices.builder(AiCodeGeneratorService.class)
                        .chatModel(chatModel)
                        .streamingChatModel(reasoningStreamingChatModel)
                        .chatMemory(chatMemory)
                        .chatMemoryProvider(memoryId->chatMemory)
                        .tools(fileWriteTool, fileReadTool, fileModifyTool, fileDeleteTool, fileDirReadTool)
                        //处理工具调用幻觉问题
                        .hallucinatedToolNameStrategy(toolExecutionRequest ->
                                ToolExecutionResultMessage.from(toolExecutionRequest,
                                        "Error: there is no tool called"+toolExecutionRequest.name())
                        )
                        .maxSequentialToolsInvocations(20)  // 最多连续调用 20 次工具
                        .inputGuardrails(new PromptSafetyInputGuardrail()) // 添加输入护轨
//                      .outputGuardrails(new RetryOutputGuardrail()) // 添加输出护轨，为了流式输出，这里不使用
                        .build();
            }
            case HTML, MULTI_FILE -> {
                // 使用多例模式的 StreamingChatModel 解决并发问题
                StreamingChatModel openAiStreamingChatModel = SpringContextUtil.getBean("streamingChatModelPrototype", StreamingChatModel.class);
                yield AiServices.builder(AiCodeGeneratorService.class)
                        .chatModel(chatModel)
                        .streamingChatModel(openAiStreamingChatModel)
                        .chatMemory(chatMemory)
                        .inputGuardrails(new PromptSafetyInputGuardrail()) // 添加输入护轨
//                      .outputGuardrails(new RetryOutputGuardrail()) // 添加输出护轨，为了流式输出，这里不使用
                        .build();
            }
            default ->
                    throw new BusinessException(ErrorCode.SYSTEM_ERROR,"不支持的代码生成类型："+codeGenType.getValue());
        };
    }


    /**
     * 创建AI代码生成器服务
     * @return
     */
    @Bean
    public AiCodeGeneratorService aiCodeGeneratorService(){
        return getAiCodeGeneratorService(0);
    }

    /**
     * 构建缓存键
     * @param appId
     * @param codeGenType
     * @return
     */
    private String buildCacheKey(long appId, CodeGenTypeEnum codeGenType){
        return appId+"_"+codeGenType.getValue();
    }
}
