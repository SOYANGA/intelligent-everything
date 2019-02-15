package com.github.soyanga.everything.core.common;

import com.github.soyanga.everything.core.model.FileType;
import com.github.soyanga.everything.core.model.Thing;

import java.io.File;

/**
 * @program: intelligent-everything
 * @Description: 辅助工具类，将File对象->Thing对象
 * @Author: SOYANGA
 * @Create: 2019-02-15 20:48
 * @Version 1.0
 */
public final class FileConvertThing {

    private FileConvertThing() {

    }

    public static Thing convert(File file) {
        Thing thing = new Thing();
        thing.setName(file.getName());
        //路径->绝对路径
        thing.setPath(file.getAbsolutePath());
        //计算深度
        thing.setDepth(computeFileDepth(file));
        thing.setFiletype(computeFileType(file));
        return thing;
    }


    /**
     * 计算文件深度
     *
     * @param file 文件
     * @return 文件深度
     */
    private static int computeFileDepth(File file) {
        int depth = 0;
        String[] segments = file.getAbsolutePath().split("\\\\");
        depth = segments.length;
        return depth;
    }

    /**
     * 处理文件类型 将File的文件名进行字符串处理拆分文件类型，并将结果变为Thing的fileType属性
     *
     * @param file 文件
     * @return FileType(Thing 的fileType属性 ）
     */
    private static FileType computeFileType(File file) {
        if (file.isDirectory()) {
            return FileType.OTHER;
        }
        String filename = file.getName();
        int index = filename.lastIndexOf(".");
        if (index != -1 && index < file.length() - 1) {
            //防止文件名是：h.
            String extend = filename.substring(index + 1);
            return FileType.lookup(extend);
        } else {
            return FileType.OTHER;
        }
    }
}
