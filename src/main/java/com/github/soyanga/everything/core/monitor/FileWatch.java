package com.github.soyanga.everything.core.monitor;

/**
 * @program: intelligent-everything
 * @Description:
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
     * 监听的目录
     */
    void monitor();

    /**
     * 监听结束
     */
    void stop();

}
