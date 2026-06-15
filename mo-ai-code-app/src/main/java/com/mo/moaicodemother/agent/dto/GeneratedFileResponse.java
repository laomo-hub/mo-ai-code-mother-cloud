package com.mo.moaicodemother.agent.dto;

import lombok.Data;

/**
 * Generated file returned by Python LangGraph agent.
 */
@Data
public class GeneratedFileResponse {

    /**
     * Relative file path.
     */
    private String path;

    /**
     * File content.
     */
    private String content;
}