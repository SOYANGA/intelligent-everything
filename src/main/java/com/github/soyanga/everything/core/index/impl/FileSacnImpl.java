package com.github.soyanga.everything.core.index.impl;

import com.github.soyanga.everything.config.IntelligentEverythingConfig;
import com.github.soyanga.everything.core.index.FileSacn;
import com.github.soyanga.everything.core.interceptor.FileInterceptor;


import java.io.File;
import java.util.LinkedList;

/**
 * @program: intelligent-everything
 * @Description:
 * @Author: SOYANGA
 * @Create: 2019-02-15 11:48
 * @Version 1.0
 */
public class FileSacnImpl implements FileSacn {

    //DAO
    private IntelligentEverythingConfig config = IntelligentEverythingConfig.getInstance();

    private LinkedList<FileInterceptor> interceptors = new LinkedList<>();

    @Override
    public void index(String path) {
        File file = new File(path);
        if (file.isFile()) {
            //D:\a\b\abc.pdf  ->  D:\a\b
            if (config.getExcludepath().contains(file.getParent())) {
                return;
            }
        } else {
            if (config.getExcludepath().contains(path)) {
                return;
            } else {
                File[] files = file.listFiles();
                if (files != null) {
                    for (File f : files) {
                        index(f.getAbsolutePath());
                    }
                }
            }
            //file dirctory
        }
        //把文件变成thing->写入
        //File->Thing->Dao
        //将如将所有对文件处理都写在这里减少了代码的灵活度，处理操作应该分开，可以组合搭配修改,将操作单独写成一个interctor接口将子类写为实现操作
        for (FileInterceptor interceptor : interceptors) {
            interceptor.apply(file);
        }
    }


    @Override
    public void interceptor(FileInterceptor interceptor) {
        this.interceptors.add(interceptor);
    }
}
