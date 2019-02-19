package com.github.soyanga.everything.core.monitor.impl;

import com.github.soyanga.everything.core.common.FileConvertThing;
import com.github.soyanga.everything.core.common.HanderPath;
import com.github.soyanga.everything.core.dao.FileIndexDao;
import com.github.soyanga.everything.core.monitor.FileWatch;
import org.apache.commons.io.monitor.FileAlterationMonitor;
import org.apache.commons.io.monitor.FileAlterationObserver;

import java.io.File;

/**
 * @program: intelligent-everything
 * @Description: 文件监听器
 * @Author: SOYANGA
 * @Create: 2019-02-18 19:15
 * @Version 1.0
 */
public class FileWatchImpl implements FileWatch, org.apache.commons.io.monitor.FileAlterationListener {
    private FileIndexDao fileIndexDao;

    private org.apache.commons.io.monitor.FileAlterationMonitor monitor;

    private FileAlterationObserver observer;


    public FileWatchImpl(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
        this.monitor = new FileAlterationMonitor(1000);
    }

    /**
     * 监听器的启动
     */
    @Override
    public void start() {
        try {
            this.monitor.start();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    /**
     * 按照要排除文件要求求生成观察者
     *
     * @param handerPath
     */
    private void initObserver(HanderPath handerPath) {
        for (String path : handerPath.getIncludePath()) {
            this.observer = new FileAlterationObserver(
                    path, pathname -> {
                String currentPath = pathname.getName();
                for (String excludePath : handerPath.getExcludePath()) {
                    if (excludePath.startsWith(currentPath)) {
                        return false;
                    }
                }
                return true;
            }
            );
        }
    }

    /**
     * 监听器的监听
     *
     * @param handerPath
     */
    @Override
    public void monitor(HanderPath handerPath) {
        //在真实监听对象中将通知者添加进入，使得监听器有通知器通知
        //监控includePath集合内的目录，排除排除目录
        initObserver(handerPath);
        observer.addListener(this);
        this.monitor.addObserver(observer);
    }


    /**
     * 监听器的关闭
     */
    @Override
    public void stop() {
        try {
            this.monitor.stop();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onStart(FileAlterationObserver observer) {
        //在通知observer中添加监听器，通知监听开始，让通知器有通知的对象（监听者）
//        observer.addListener(this);
    }

    @Override
    public void onStop(FileAlterationObserver observer) {
        //在通知observer中添加监听器，通知监听结束
//        observer.removeListener(this);
    }

    @Override
    public void onDirectoryCreate(File file) {
        //目录创建
        System.out.println("FileCreate:" + file);
        this.fileIndexDao.insert(FileConvertThing.convert(file));
    }

    @Override
    public void onDirectoryChange(File file) {
        //目录改变
        System.out.println("DirectoryChange" + file);
    }

    @Override
    public void onDirectoryDelete(File file) {
        //目录删除
        System.out.println("DirectoryDelete:" + file);
        this.fileIndexDao.delete(FileConvertThing.convert(file));
    }

    @Override
    public void onFileCreate(File file) {
        //文件创建
        System.out.println("FileCreate:" + file);
        this.fileIndexDao.insert(FileConvertThing.convert(file));
    }

    @Override
    public void onFileChange(File file) {
        //文件改变
        System.out.println("FileChange" + file);
    }

    @Override
    public void onFileDelete(File file) {
        //文件删除
        System.out.println("FileDelete:" + file);
        this.fileIndexDao.delete(FileConvertThing.convert(file));
    }

}
