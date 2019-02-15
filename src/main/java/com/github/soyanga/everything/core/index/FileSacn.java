package com.github.soyanga.everything.core.index;

import com.github.soyanga.everything.core.dao.DataSourceFactory;
import com.github.soyanga.everything.core.dao.impl.FileIndexDaoImpl;
import com.github.soyanga.everything.core.index.impl.FileSacnImpl;
import com.github.soyanga.everything.core.interceptor.FileInterceptor;
import com.github.soyanga.everything.core.interceptor.impl.FileIndexInterceptor;
import com.github.soyanga.everything.core.interceptor.impl.FilePrintInterceptor;

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

    public static void main(String[] args) {
        FileSacnImpl sacn = new FileSacnImpl();
        FileInterceptor filePrintInterceptor = new FilePrintInterceptor();
        FileInterceptor fileIndexInterceptor = new FileIndexInterceptor(new FileIndexDaoImpl(DataSourceFactory.dataSource()));
        sacn.interceptor(filePrintInterceptor);
        sacn.interceptor(fileIndexInterceptor);
        sacn.index("D:\\图片");
    }
}
