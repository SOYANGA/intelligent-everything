package com.github.soyanga.everything.core.monitor;

import com.github.soyanga.everything.core.common.HanderPath;
import org.apache.commons.io.monitor.FileAlterationMonitor;

/**
 * @program: intelligent-everything
 * @Description: 文件监视器 监视用户配置的目录includePath，不监控用排除目录
 * @Author: SOYANGA
 * @Create: 2019-02-17 11:47
 * @Version 1.0
 */
public interface FileWatch {
    /**
     * 监听启动
     */
    void start();

    /**
     * 监听的目录  ：监视用户配置的目录includePath，不监控用排除目录
     */
    void monitor(HanderPath handerPath);

    /**
     * 监听结束
     */
    void stop();

}

////////////////////////////////////////////////////////////////////////////////////////////////////////

//以上是之前写的
//以下是常林兄的moniter，性能提升不少~
//package com.github.soyanga.everything.core.monitor;
//
//import com.github.soyanga.everything.core.common.HanderPath;
//import org.apache.commons.io.monitor.FileAlterationMonitor;
//
///**
// * @program: intelligent-everything
// * @Description: 文件监视器 监视用户配置的目录includePath，不监控用排除目录 一下是常林兄的moniter，性能提升不少~
// * @Author: zouchanglin
// * @Create: 2019-02-17 11:47
// * @Version 1.3.0
// */
//public interface FileWatch {
//    /**
//     * 监听的目录
//     */
//    void monitor(HanderPath handlePath);
//
//    /**
//     * 监听结束
//     */
//    void stop();
//
//    /**
//     * 监听启动
//     */
//    void start();
//
////    FileAlterationMonitor getFileAlterationMonitor();
//}
