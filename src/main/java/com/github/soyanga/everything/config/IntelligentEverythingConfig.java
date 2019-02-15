package com.github.soyanga.everything.config;


import lombok.Getter;

import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * @program: intelligent-everything
 * @Description:
 * @Author: SOYANGA
 * @Create: 2019-02-15 11:32
 * @Version 1.0
 */
@Getter
public class IntelligentEverythingConfig {


    private static volatile IntelligentEverythingConfig config;

    /**
     * 建立索引路劲
     */
    private Set<String> includePath = new HashSet<>();


    /**
     * 排除索引路径
     */
    private Set<String> excludepath = new HashSet<>();

    private IntelligentEverythingConfig() {

    }

    public static IntelligentEverythingConfig getInstance() {
        if (config == null) {
            synchronized (IntelligentEverythingConfig.class) {
                if (config == null) {
                    config = new IntelligentEverythingConfig();
                    //1.获取文件系统
                    FileSystem fileSystem = FileSystems.getDefault();

                    //2.添加遍历目录
                    Iterable<Path> iterator = fileSystem.getRootDirectories();
                    iterator.forEach(path -> config.getIncludePath().add(path.toString()));

                    //3.排除的目录
                    //windows :C:\windows
                    //         C:\Program Files
                    //          C:\Program Files (x86)
                    //          C:\ProgramData
                    //linux: /temp  /etc

                    //3.1获取当前操作系统名称
                    String osName = System.getProperty("os.name");
                    //添加排除目录
                    if (osName.startsWith("Windows")) {
                        config.getExcludepath().add("C:\\windows");
                        config.getExcludepath().add("C:\\Program Files");
                        config.getExcludepath().add("C:\\Program Files (x86)");
                        config.getExcludepath().add("C:\\ProgramData");
                    } else {
                        config.getExcludepath().add("/temp");
                        config.getExcludepath().add("/etc");
                        config.getExcludepath().add("/root");
                    }
                }
            }
        }
        return config;
    }

    public static void main(String[] args) {
//        FileSystem fileSystem = FileSystems.getDefault();
//        Iterable<Path> iterator = fileSystem.getRootDirectories();
//        iterator.forEach(new Consumer<Path>() {
//            @Override
//            public void accept(Path path) {
//                System.out.println(path);
//            }
//        });
//        System.out.println(System.getProperty("os.name"));
        IntelligentEverythingConfig config = IntelligentEverythingConfig.getInstance();
        System.out.println(config.getIncludePath());
        System.out.println(config.getExcludepath());
//        System.out.println(config);
    }


}
