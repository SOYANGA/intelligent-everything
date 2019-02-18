package com.github.soyanga.everything.core.common;

import lombok.Data;

import java.util.Set;

/**
 * @program: intelligent-everything
 * @Description: 给文件监视器配置要监控的目录
 * @Author: SOYANGA
 * @Create: 2019-02-18 19:12
 * @Version 1.0
 */
@Data
public class HanderPath {
    private Set<String> includePath;
    private Set<String> excludePath;
}
