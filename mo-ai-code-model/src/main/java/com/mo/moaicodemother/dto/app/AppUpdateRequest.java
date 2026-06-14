package com.mo.moaicodemother.dto.app;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 应用更新请求（用户）
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppUpdateRequest implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * id
     */
    private Long id;

    /**
     * 应用名称（用户只允许修改名称）
     */
    private String appName;
}