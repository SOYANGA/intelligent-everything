package com.github.soyanga.everything.core.dao;

import com.github.soyanga.everything.core.model.Condition;
import com.github.soyanga.everything.core.model.Thing;

import java.util.List;

/**
 * @program: intelligent-everything
 * @Description: 业务层访问数据库的CRUD
 * @Author: SOYANGA
 * @Create: 2019-02-15 09:48
 * @Version 1.0
 */
public interface FileIndexDao {

    /**
     * 插入数据Thing
     * 插入没有结果
     *
     * @param thing 插入的数据
     */
    void insert(Thing thing);

    /**
     * 删除数据Thing
     *
     * @param thing 要删除的数据
     */
    void delete(Thing thing);

    /**
     * 检索数据
     *
     * @param connection 检索条件
     * @return 检索的结果
     */
    List<Thing> search(Condition connection);


}
