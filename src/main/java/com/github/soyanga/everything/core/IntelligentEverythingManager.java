package com.github.soyanga.everything.core;

import com.github.soyanga.everything.config.IntelligentEverythingConfig;
import com.github.soyanga.everything.core.common.History;
import com.github.soyanga.everything.core.dao.DataSourceFactory;
import com.github.soyanga.everything.core.dao.FileIndexDao;
import com.github.soyanga.everything.core.dao.impl.FileIndexDaoImpl;
import com.github.soyanga.everything.core.index.FileSacn;
import com.github.soyanga.everything.core.index.impl.FileSacnImpl;
import com.github.soyanga.everything.core.interceptor.impl.FileIndexInterceptor;
import com.github.soyanga.everything.core.interceptor.impl.ThingClearInterceptor;
import com.github.soyanga.everything.core.model.Condition;
import com.github.soyanga.everything.core.model.Thing;
import com.github.soyanga.everything.core.search.FileSearch;
import com.github.soyanga.everything.core.search.impl.FileSearchImpl;

import javax.sql.DataSource;
import java.io.File;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

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
     * 线程池
     */
    private ExecutorService executorService;

    /**
     * 清理删除文件
     */
    private ThingClearInterceptor thingClearInterceptor;
    private Thread backgroundClearThread;
    private AtomicBoolean backgroundClearThreadStatus = new AtomicBoolean(false);

    /**
     * history记录功能
     */
    private History history;
    private Thread historyStoreThread;
    private AtomicBoolean historyStoreThreadStatus = new AtomicBoolean(false);

    private IntelligentEverythingManager() {
        this.initComponent();
    }

    /**
     * 初始化给调度器的准备
     */
    public void initComponent() {
        //准备输数据源对象
        DataSource dataSource = DataSourceFactory.dataSource();

        //初始化并重新索引数据库
        initOrResetDatabase();

        //数据库层得准备工作
        FileIndexDao fileIndexDao = new FileIndexDaoImpl(dataSource);
        //业务层的对象
        this.fileSacn = new FileSacnImpl();
        this.fileSearch = new FileSearchImpl(fileIndexDao);
        //测试使用
//        this.fileSacn.interceptor(new FilePrintInterceptor());
        //执行数据处理的操作
        this.fileSacn.interceptor(new FileIndexInterceptor(fileIndexDao));

        //清理线程设置-守护线程
        thingClearInterceptor = new ThingClearInterceptor(fileIndexDao);
        this.backgroundClearThread = new Thread(this.thingClearInterceptor);
        this.backgroundClearThread.setName("Thread-Thing-Clear");
        this.backgroundClearThread.setDaemon(true);
        //记录历史线程-守护线程
        history = new History();
        this.historyStoreThread = new Thread(this.history);
        this.historyStoreThread.setName("Thread-Thing-History");
        this.historyStoreThread.setDaemon(true);
    }


    private void initOrResetDatabase() {
        DataSourceFactory.initDataSource();
    }


    /**
     * Manager单例对象得获取
     */
    public static IntelligentEverythingManager getInstance() {
        if (manager == null) {
            synchronized (IntelligentEverythingManager.class) {
                if (manager == null) {
                    manager = new IntelligentEverythingManager();
                    manager.initComponent();
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
        //流式处理，后处理，先查再删除
        return fileSearch.search(condition)
                .stream()
                .filter(thing -> {
                    String path = thing.getPath();
                    File f = new File(path);
                    boolean flag = f.exists();
                    if (!flag) {
                        //删除
                        thingClearInterceptor.apply(thing);
                    }
                    return flag;
                }).collect(Collectors.toList());

    }

    /**
     * 索引
     * 当重新执行index的时候会删除原有的表重新进行新的创建表
     */
    public void buildIndex() {
        //重新构建表
        initOrResetDatabase();
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
            this.executorService.submit(() -> {
                IntelligentEverythingManager.this.fileSacn.index(path);
                //当前任务完成，让CountDown值-1值-1
                countDownLatch.countDown();
            });
        }

        //阻塞，直到任务完成，值0
        try {
            countDownLatch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //打印完成
        System.out.println("Build index complete!");
    }

    /**
     * 存储用户输入history to queue
     *
     * @param string 用户输入history
     */
    public void historySoreQueue(String string) {
        history.storeHistoryQueue(string);
    }

    /**
     * 用户返回一个history List
     *
     * @return historyList
     */
    public List<String> printHistory() {
        return history.getHistory();
    }

    public void writeFileToHistory() {
        history.cleanHistoryFile();
        history.writeHistorytoFile();
    }

    /**
     * 启动清理线程：利用一个（原子）标志位+原子CAS操作进行
     */
    public void startBackgroundClearThread() {
        if (this.backgroundClearThreadStatus.compareAndSet(false, true)) {
            this.backgroundClearThread.start();
        } else {
            System.out.println("Can not restart BackgroundClearThread ");
        }
    }

    /**
     * 启动记录history用户输入线程
     */
    public void startHistoryStoreThread() {
        if (this.historyStoreThreadStatus.compareAndSet(false, true)) {
            this.historyStoreThread.start();
        } else {
            System.out.println("Can not restart BackgroundClearThread ");
        }
    }
}
