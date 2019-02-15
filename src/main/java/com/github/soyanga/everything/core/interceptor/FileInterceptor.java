package com.github.soyanga.everything.core.interceptor;

import java.io.File;

/**
 * @program: intelligent-everything
 * @Description:拦截器--函数式接口
 * @Author: SOYANGA
 * @Create: 2019-02-15 12:061
 * @Version 1.0
 */
@FunctionalInterface
public interface FileInterceptor {
    void apply(File file);
}
