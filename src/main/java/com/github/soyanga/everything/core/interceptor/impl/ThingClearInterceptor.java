package com.github.soyanga.everything.core.interceptor.impl;

import com.github.soyanga.everything.core.dao.FileIndexDao;
import com.github.soyanga.everything.core.interceptor.ThingInterceptor;
import com.github.soyanga.everything.core.model.Thing;

import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @program: intelligent-everything
 * @Description: 删除容器（生产消费者），多线程
 * @Author: SOYANGA
 * @Create: 2019-02-16 22:26
 * @Version 1.0
 */
public class ThingClearInterceptor implements ThingInterceptor, Runnable {

    private Queue<Thing> queue = new ArrayBlockingQueue<>(1024);

    private final FileIndexDao fileIndexDao;

    public ThingClearInterceptor(FileIndexDao fileIndexDao) {
        this.fileIndexDao = fileIndexDao;
    }

    @Override
    public void apply(Thing thing) {
        this.queue.add(thing);
    }

    /**
     * 将要删除的数据，从阻塞队列中取出，不为空并删除
     * 队列没有值的时候，取值一直为空
     */
    @Override
    public void run() {
        System.out.println("后台清理线程开启");
        while (true) {
            Thing thing = this.queue.poll();
            if (thing != null) {
                fileIndexDao.delete(thing);
            }
            //1.优化 批量删除,把delete升级为批量删除
            //List<Thing> thingList = new ArrayList<>();
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
