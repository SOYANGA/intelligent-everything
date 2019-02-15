package com.github.soyanga.everything.core.search;

import com.github.soyanga.everything.core.dao.DataSourceFactory;
import com.github.soyanga.everything.core.dao.impl.FileIndexDaoImpl;
import com.github.soyanga.everything.core.model.Condition;
import com.github.soyanga.everything.core.model.Thing;
import com.github.soyanga.everything.core.search.impl.FileSearchImpl;

import java.util.List;

/**
 * @program: intelligent-everything
 * @Description: 根据条件检索文件
 * @Author: SOYANGA
 * @Create: 2019-02-15 09:34
 * @Version 1.0
 */
public interface FileSearch {
    /**
     * 根据condition条件继续宁数据库的检索
     *
     * @param condition 检索的条件
     * @return 抽象的Thing(文件)集合
     */
    List<Thing> search(Condition condition);
}
