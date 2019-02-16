package com.github.soyanga.everything.core;

import com.github.soyanga.everything.config.IntelligentEverythingConfig;
import com.github.soyanga.everything.core.dao.DataSourceFactory;
import com.github.soyanga.everything.core.dao.FileIndexDao;
import com.github.soyanga.everything.core.dao.impl.FileIndexDaoImpl;
import com.github.soyanga.everything.core.index.FileSacn;
import com.github.soyanga.everything.core.index.impl.FileSacnImpl;
import com.github.soyanga.everything.core.model.Condition;
import com.github.soyanga.everything.core.model.Thing;
import com.github.soyanga.everything.core.search.FileSearch;
import com.github.soyanga.everything.core.search.impl.FileSearchImpl;

import javax.sql.DataSource;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @program: intelligent-everything
 * @Description:
 * @Author: SOYANGA
 * @Create: 2019-02-16 09:19
 * @Version 1.0
 */
public class IntelligentEverythingManager {
    private static volatile IntelligentEverythingManager manager;
    private FileSearch fileSearch;
    private FileSacn fileSacn;

    /**
     * 线程池  ```
     */
    private ExecutorService executorService;

    private IntelligentEverythingManager() {
        this.initComponent();
    }

    /**
     * 初始化给调度器的准备
     */
    public void initComponent() {
        //准备输数据源对象
        DataSource dataSource = DataSourceFactory.dataSource();
        //数据库层得准备工作
        FileIndexDao fileIndexDao = new FileIndexDaoImpl(dataSource);
        //业务层的对象
        this.fileSacn = new FileSacnImpl();
        this.fileSearch = new FileSearchImpl(fileIndexDao);
    }

    /**
     * Manager单例对象得获取
     */
    public static IntelligentEverythingManager getInstance() {
        if (manager == null) {
            synchronized (IntelligentEverythingManager.class) {
                if (manager == null) {
                    manager = new IntelligentEverythingManager();
                }
            }
        }
        return manager;
    }


    /**
     * 检索
     */
    public List<Thing> search(Condition condition) {
        //NOTICE 扩展
        return fileSearch.search(condition);

    }

    /**
     * 索引
     */
    public void buildIndex() {
        //目录
        Set<String> directories = IntelligentEverythingConfig.getInstance().getIncludePath();
        if (this.executorService == null) {
            this.executorService = Executors.newFixedThreadPool(directories.size(), new ThreadFactory() {
                //原子类
                private final AtomicInteger threadId = new AtomicInteger(0);

                //创建线程
                @Override
                public Thread newThread(Runnable r) {
                    Thread thread = new Thread(r);
                    thread.setName("Thread-Scan-" + threadId.getAndIncrement());
                    return thread;
                }
            });
        }
        //为了能够让索引完成后再让当前线程进行打印索引建立完成使用CountDownLatch
        final CountDownLatch countDownLatch = new CountDownLatch(directories.size());
        System.out.println("Build index strat,please wait a moment...");
        for (String path : directories) {
            //线程池提交
            this.executorService.submit(new Runnable() {
                @Override
                public void run() {
                    IntelligentEverythingManager.this.fileSacn.index(path);
                    //当前任务完成，让CountDown值-1值-1
                    countDownLatch.countDown();
                }
            });
        }

        /**
         * 阻塞，直到任务完成，值0
         */
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //打印完成
        System.out.println("Build index complete!");
    }
}
