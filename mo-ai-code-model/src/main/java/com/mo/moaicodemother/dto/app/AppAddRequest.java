package com.mo.moaicodemother.dto.app;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 应用创建请求
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppAddRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 应用名称
     */
    private String appName;

    /**
     * id
     */
    private Long id;

    /**
     * 应用封面
     */
    private String cover;

    /**
     * 应用初始化的 prompt
     */
    private String initPrompt;

    /**
     * 代码生成类型
     */
    private String codeGenType;

    /**
     * 优先级
     */
    private Integer priority;
}