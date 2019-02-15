package com.github.soyanga.everything.core.interceptor.impl;

import com.github.soyanga.everything.core.interceptor.FileInterceptor;

import java.io.File;

/**
 * @program: intelligent-everything
 * @Description: 对路径筛选遍历的文件进行打印操作
 * @Author: SOYANGA
 * @Create: 2019-02-15 12:08
 * @Version 1.0
 */
public class FilePrintInterceptor implements FileInterceptor {

    @Override
    public void apply(File file) {
        System.out.println(file.getAbsolutePath());
    }
}
