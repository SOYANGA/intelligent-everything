package com.github.soyanga.everything.core.model;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * @program: intelligent-everything
 * @Description: 这是我们的文件类型
 * @Author: SOYANGA
 * @Create: 2019-02-14 11:47
 * @Version 1.0
 */
public enum FileType {
    /**
     * 对应了文件的图片，文档，可执行，压缩，其他文件类型
     */
    IMG("png", "jpeg", "jpe", "gif"),
    DOC("ppt", "pptx", "doc", "docx", "pdf"),
    BIN("exe", "sh", "jar", "msi"),
    ARCHIVE("zip", "rar"),
    OTHER("*");

    /**
     * 对应文件类型的扩展名集合
     */
    private Set<String> extend = new HashSet<>();

    FileType(String... extend) {
        this.extend.addAll(Arrays.asList(extend));
    }

    /**
     * 将对应文件类型抽象成枚举类（FileType）
     * 根据文件的扩展名返回文件类型
     *
     * @param extend（需要查找的文件类型（字符串形式））
     * @return Filetype枚举类型（文件类型）
     */
    public static FileType lookup(String extend) {

        for (FileType filetype : FileType.values()) {
            if (filetype.extend.contains(extend)) {
                return filetype;
            }
        }
        return FileType.OTHER;
    }

    /**
     * 根据文件类型名(String) 获取文件类型对象
     *
     * @param name 数据库中对用文件类型
     * @return thing.filetype -java抽象的文件类(Thing)对应的文件类型
     */
    public static FileType lookupByName(String name) {
        for (FileType filetype : FileType.values()) {
            if (filetype.name().equals(name)) {
                return filetype;
            }
        }
        return FileType.OTHER;
    }

}
