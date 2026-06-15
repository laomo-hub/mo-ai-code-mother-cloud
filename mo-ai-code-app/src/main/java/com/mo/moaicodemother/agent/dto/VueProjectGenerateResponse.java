package com.mo.moaicodemother.agent.dto;

import lombok.Data;

import java.util.List;

/**
 * Response returned by Python LangGraph agent for Vue project generation.
 */
@Data
public class VueProjectGenerateResponse {

    /**
     * Application id.
     */
    private Long appId;

    /**
     * User id.
     */
    private Long userId;

    /**
     * Generation summary.
     */
    private String summary;

    /**
     * Whether generation passed quality check.
     */
    private Boolean passed;

    /**
     * Error message if generation failed.
     */
    private String errorMessage;

    /**
     * Generated files.
     */
    private List<GeneratedFileResponse> files;
}