package com.github.soyanga.everything.core.interceptor.impl;

import com.github.soyanga.everything.core.common.FileConvertThing;
import com.github.soyanga.everything.core.dao.FileIndexDao;
import com.github.soyanga.everything.core.interceptor.FileInterceptor;
import com.github.soyanga.everything.core.model.Thing;

import java.io.File;


/**
 * @program: intelligent-everything
 * @Description: 对指定路径遍历的文件进行，转换并写入数据库
 * @Author: SOYANGA
 * @Create: 2019-02-15 22:02
 * @Version 1.0
 */
public class FileIndexInterceptor implements FileInterceptor {
    private final FileIndexDao fileIndexDao;

    public FileIndexInterceptor(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public void apply(File file) {
        Thing thing = FileConvertThing.convert(file);
        System.out.println("Thing ==>" + thing);
        fileIndexDao.insert(thing);
    }
}
