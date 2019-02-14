package com.github.soyanga.everything.core.model;

import lombok.Data;

/**
 * @program: intelligent-everything
 * @Description:文件属性信息索引后的记录
 * @Author: SOYANGA
 * @Create: 2019-02-14 11:57
 * @Version 1.0
 */
@Data  //getter setter 生成完成
public class Thing {

    /**
     * 文件名 D:/a/b/hello.text   hello.text
     */
    private String name;

    /**
     * 文件路径
     */
    private String path;

    /**
     * 文件递归深度
     */
    private Integer depth;

    /**
     * 文件类型
     */
    private FileType filetype;
}
