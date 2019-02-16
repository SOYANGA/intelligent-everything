package com.github.soyanga.everything.core.index;

import com.github.soyanga.everything.core.interceptor.FileInterceptor;


/**
 * @program: intelligent-everything
 * @Description: 建立索引
 * @Author: SOYANGA
 * @Create: 2019-02-15 11:48
 * @Version 1.0
 */
public interface FileSacn {

    /**
     * 遍历path
     *
     * @param path
     */
    void index(String path);

    /**
     * 遍历的拦截器(处理)
     *
     * @param interceptor
     */
    void interceptor(FileInterceptor interceptor);

}
