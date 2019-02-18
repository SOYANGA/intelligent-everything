package com.github.soyanga.everything.core.monitor;

import com.github.soyanga.everything.core.common.HanderPath;

/**
 * @program: intelligent-everything
 * @Description: 文件监视器 监视用户配置的目录includePath，不监控用排除目录
 * @Author: SOYANGA
 * @Create: 2019-02-17 11:47
 * @Version 1.0
 */
public interface FileWatch {
    /**
     * 监听启动
     */
    void start();

    /**
     * 监听的目录  ：监视用户配置的目录includePath，不监控用排除目录
     */
    void monitor(HanderPath handerPath);

    /**
     * 监听结束
     */
    void stop();

}
