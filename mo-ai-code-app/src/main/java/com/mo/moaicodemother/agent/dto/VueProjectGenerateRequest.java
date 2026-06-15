package com.mo.moaicodemother.agent.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request sent to Python LangGraph agent for Vue project generation.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class VueProjectGenerateRequest {

    /**
     * Application id.
     */
    private Long appId;

    /**
     * User id.
     */
    private Long userId;

    /**
     * User prompt.
     */
    private String prompt;
}
