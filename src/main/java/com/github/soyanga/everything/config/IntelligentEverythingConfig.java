package com.github.soyanga.everything.config;


import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Set;

/**
 * @program: intelligent-everything
 * @Description: 配置文件
 * @Author: SOYANGA
 * @Create: 2019-02-15 11:32
 * @Version 1.0
 */
@Getter
public class IntelligentEverythingConfig {


    private static volatile IntelligentEverythingConfig config;

    /**
     * 建立索引路径
     */
    @Setter
    private Set<String> includePath = new HashSet<>();


    /**
     * 排除索引路径
     */
    @Setter
    private Set<String> excludepath = new HashSet<>();


    //TODO 可配置的参数会在这里体现


    /**
     * 在交互中命令行设置参数flag = true;
     * 在程序运行前设置参数flag = false;
     */
    @Setter
    private Boolean alterConfigflag = true;


    /**
     * 文件系统监控开关 ture->开   false->关
     */
    @Setter
    private Boolean fileSystemMonitorSwitch = false;


    /**
     * 判断文件监控系统是否重复开启 true->重复开启，false->没有重复重复开启
     */
    @Setter
    private Boolean fileSystemIsRepeat = false;


    /**
     * 文件监控系统，监控刷新频率
     */
    @Setter
    private Integer moniterFrequency = 1000;


    /**
     * 后台清理开关 ture->开   false->关 后处理search时才进行后台处理
     */
    @Setter
    private Boolean backgroundClearThreadSwitch = true;


    /**
     * 判断 后台清理是否重复开启 true->重复开启，false->没有重复重复开启
     */
    @Setter
    private Boolean backgroundClearThreadIsRepeat = false;


    /**
     * 检索最大的返回值
     */
    @Setter
    private Integer maxReturn = 30;

    /**
     * 深度排序的规则，默认是升序
     * oder by depth asc limit 30 offese 0;
     */
    @Setter
    private Boolean depthOrderAsc = true;

    /**
     * 检查数据库索引目录是否变更
     * 变更则就重新索引
     */
    @Setter
    private Boolean alterIndexPathFlag = false;

    /**
     * 是否初次配置程序
     */
    @Setter
    private Boolean isFirstUse = true;


    /**
     * 是否开始导入配置文件
     */
    @Setter
    private Boolean isStartLoad = true;

    /**
     * 初始化标志为默认为false->不出初始化
     */
    @Setter
    private Boolean ISinitialize = false;

    /**
     * H2数据库文件路径
     */
    private String h2IndexPath = System.getProperty("user.dir") + File.separator + "intelligent_everything";

    /**
     * history缓存文件路径
     */
    private String historyPath = System.getProperty("user.dir") + File.separator + "historyFile.txt";


    /**
     * 配置文件，其中存储程序配置可更改的所有信息
     */
    private String proFile = System.getProperty("user.dir") + File.separator + "proFile.txt";

    private IntelligentEverythingConfig() {

    }

    public static IntelligentEverythingConfig getInstance() {
        if (config == null) {
            synchronized (IntelligentEverythingConfig.class) {
                if (config == null) {
                    config = new IntelligentEverythingConfig();
                    config.initDefaultPathsConfig();
                }
            }
        }
        return config;
    }


    private void initDefaultPathsConfig() {
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


    @Override
    public String toString() {
        return "  IntelligentEverythingConfig:\n"
                + "  isFirstUse=" + isFirstUse + "\n"
                + "  includePath=" + includePath + "\n"
                + "  excludepath=" + excludepath + "\n"
                + "  maxReturn=" + maxReturn + "\n"
                + "  depthOrderAsc=" + depthOrderAsc + "\n"
                + "  fileSystemMonitorSwitch=" + fileSystemMonitorSwitch + "\n"
                + "  moniterFrequency=" + moniterFrequency + "\n"
                + "  backgroundClearThreadSwitch=" + backgroundClearThreadSwitch + "\n";
    }
}
