package com.mo.moaicodemother.agent;

import com.mo.moaicodemother.agent.dto.VueProjectGenerateRequest;
import com.mo.moaicodemother.agent.dto.VueProjectGenerateResponse;
import com.mo.moaicodemother.exception.BusinessException;
import com.mo.moaicodemother.exception.ErrorCode;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

/**
 * Client for calling Python FastAPI LangGraph agent.
 */
@Slf4j
@Component
public class AgentClient {

    private final RestClient restClient;

    @Value("${agent.base-url}")
    private String agentBaseUrl;

    public AgentClient(RestClient.Builder restClientBuilder) {
        this.restClient = restClientBuilder.build();
    }

    /**
     * Generate Vue project files by calling Python LangGraph agent.
     *
     * @param request generation request
     * @return generated Vue project response
     */
    public VueProjectGenerateResponse generateVueProject(VueProjectGenerateRequest request) {
        try {
            VueProjectGenerateResponse response = restClient.post()
                    .uri(agentBaseUrl + "/api/agent/vue-project/generate")
                    .body(request)
                    .retrieve()
                    .body(VueProjectGenerateResponse.class);

            if (response == null) {
                throw new BusinessException(ErrorCode.SYSTEM_ERROR, "Python Agent 返回为空");
            }

            if (!Boolean.TRUE.equals(response.getPassed())) {
                throw new BusinessException(
                        ErrorCode.SYSTEM_ERROR,
                        "Python Agent 生成失败：" + response.getErrorMessage()
                );
            }

            log.info("Python Agent 生成 Vue 项目成功，appId: {}, 文件数: {}",
                    response.getAppId(),
                    response.getFiles() == null ? 0 : response.getFiles().size());

            return response;
        } catch (BusinessException e) {
            throw e;
        } catch (Exception e) {
            log.error("调用 Python Agent 失败", e);
            throw new BusinessException(ErrorCode.SYSTEM_ERROR, "调用 Python Agent 失败：" + e.getMessage());
        }
    }
}