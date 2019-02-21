package com.github.soyanga.everything.core.common;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @program: intelligent-everything
 * @Description:
 * @Author: SOYANGA
 * @Create: 2019-02-20 22:33
 * @Version 1.0
 */

public class TabKey {

    private static Set set = new HashSet();

    private static Queue<String> queue = new ArrayBlockingQueue(10);

    private static Iterator iterator = queue.iterator();
}
