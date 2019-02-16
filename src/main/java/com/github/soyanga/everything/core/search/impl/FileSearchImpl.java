package com.github.soyanga.everything.core.search.impl;

import com.github.soyanga.everything.core.dao.FileIndexDao;
import com.github.soyanga.everything.core.model.Condition;
import com.github.soyanga.everything.core.model.Thing;
import com.github.soyanga.everything.core.search.FileSearch;

import java.util.ArrayList;
import java.util.List;

/**
 * @program: intelligent-everything
 * @Description: 业务层代码
 * @Author: SOYANGA
 * @Create: 2019-02-15 09:35
 * @Version 1.0
 */
public class FileSearchImpl implements FileSearch {

    /**
     * 业务层想要使用数据库持久化层的代码就必须使用此类FileIndexDao
     */
    private final FileIndexDao fileIndexDao;

    /**
     * @param fileIndexDao 数据库层的检索实现
     */
    public FileSearchImpl(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public List<Thing> search(Condition condition) {
        if (condition == null) {
            return new ArrayList<>();
        }
        return fileIndexDao.search(condition);
    }
}
