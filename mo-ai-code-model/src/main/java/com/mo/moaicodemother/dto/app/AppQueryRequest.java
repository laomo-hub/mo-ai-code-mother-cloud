package com.mo.moaicodemother.dto.app;

import com.mo.moaicodemother.common.PageRequest;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 应用查询请求
 */
@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppQueryRequest extends PageRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 应用名称
     */
    private String appName;

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
     * 部署标识
     */
    private String deployKey;

    /**
     * 创建用户id
     */
    private Long userId;

    /**
     * 优先级
     */
    private Integer priority;
}