package com.github.soyanga.everything.core.model;

import lombok.Data;

/**
 * @program: intelligent-everything
 * @Description:
 * @Author: SOYANGA
 * @Create: 2019-02-14 12:00
 * @Version 1.0
 */
@Data
public class Condition {

    private String name;

    //输入的是字符串，所以我们要是用String类
    private String fileType;
    /**
     * 筛选结果进行分页
     */
    private Integer limit;
    /**
     * 检索结果的文件信息depth的排序规则
     * 1.默认时true -> ASC
     * 2.false->desc
     */
    private Boolean oderByAsc;
}
