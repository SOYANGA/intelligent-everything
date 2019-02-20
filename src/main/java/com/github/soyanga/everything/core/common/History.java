package com.github.soyanga.everything.core.common;


import com.github.soyanga.everything.config.IntelligentEverythingConfig;
import java.io.*;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * @program: intelligent-everything
 * @Description:
 * @Author: SOYANGA
 * @Create: 2019-02-17 20:44
 * @Version 1.0
 */
public class History implements Runnable {
    private int capacity = 10;
    private int showNumber = 10;
    private String historyPath = IntelligentEverythingConfig.getInstance().getHistoryPath();
    private Queue<String> queue = new ArrayBlockingQueue<>(capacity);
    private File historyFile;


    /**
     * 将队列中要存储的history先存放到队列中取用，一旦队列满了就将内容存放到文件中
     * 在结束使用时将queue中的数据存放到文件中
     */
    @Override
    public void run() {
        historyFile = new File(historyPath);
        if (!historyFile.exists()) {
            try {
                historyFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        while (true) {
            if (queue.size() >= showNumber) {
                String history = queue.poll();
                try {
//                    historyPath = IntelligentEverythingConfig.getInstance().getHistoryPath();
                    FileWriter fileWriter = new FileWriter(historyFile, true);
                    fileWriter.write(history + "\n");
                    fileWriter.flush();
                    Thread.sleep(100);
                } catch (IOException | InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public void storeHistoryQueue(String history) {
        if (queue.isEmpty()) {
            leadHistory(capacity - 1);
        }
        this.queue.add(history);
    }

    /**
     * 当队列为空时在文件中取，当队列不为空时直接在队列中取
     *
     * @return 返回一个history集合
     */
    public List<String> getHistory() {
        List<String> list = new ArrayList<>();
        int size = queue.size();
        for (int i = size >= showNumber ? showNumber : size; i > 0; i--) {
            String history = queue.poll();
            list.add(history);
            queue.add(history);
        }
        return list;
    }

    public void leadHistory(int i) {
        if (queue.isEmpty()) {
            //Class.getClassLoader.getResourceAsStream(String name) ：默认则是从ClassPath根下获取，path不能以’/'开头，最终是由ClassLoader获取资源。
            try (FileInputStream in = new FileInputStream(historyFile)) {
                Scanner scanner = new Scanner(in);
                while (scanner.hasNext() && i > 0) {
                    queue.add(scanner.nextLine());
                    i--;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void writeHistorytoFile() {
        while (!queue.isEmpty()) {
            String history = queue.poll();
            try {
                FileWriter fileWriter = new FileWriter(historyFile, true);
                fileWriter.write(history + "\n");
                fileWriter.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void cleanHistoryFile() {
        try {
            FileWriter fileWriter = new FileWriter(historyFile);
            fileWriter.write("");
            fileWriter.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}


