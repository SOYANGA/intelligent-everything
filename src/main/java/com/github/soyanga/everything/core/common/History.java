package com.github.soyanga.everything.core.common;


import com.github.soyanga.everything.core.dao.DataSourceFactory;


import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
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
    private Queue<String> queue = new ArrayBlockingQueue<>(capacity);


    /**
     * 将队列中要存储的history先存放到队列中取用，一旦队列满了就将内容存放到文件中
     * 在结束使用时将queue中的数据存放到文件中
     */
    @Override
    public void run() {
        while (true) {
            if (queue.size() >= showNumber) {
                String history = queue.poll();
                try {
                    URI url = Objects.requireNonNull(History.class.getClassLoader().getResource("historyFile.txt")).toURI();
                    FileWriter fileWriter = new FileWriter(url.getPath(), true);
                    fileWriter.write(history + "\n");
                    fileWriter.flush();
                    Thread.sleep(100);
                } catch (URISyntaxException | IOException | InterruptedException e) {
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
     * @return
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
            InputStream in = DataSourceFactory.class.getClassLoader().getResourceAsStream("historyFile.txt");

            Scanner scanner = new Scanner(in);
            while (scanner.hasNext() && i > 0) {
                queue.add(scanner.nextLine());
                i--;
            }
        }
    }

    public void writeHistorytoFile() {
        while (!queue.isEmpty()) {
            String history = queue.poll();
            try {
                URI url = Objects.requireNonNull(History.class.getClassLoader().getResource("historyFile.txt")).toURI();
                File file = new File(url);
                FileWriter fileWriter = new FileWriter(file, true);
                fileWriter.write(history + "\n");
                fileWriter.flush();
            } catch (URISyntaxException | IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void cleanHistoryFile() {
        try {
            URI url = Objects.requireNonNull(History.class.getClassLoader().getResource("historyFile.txt")).toURI();
            File file = new File(url);
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write("");
            fileWriter.flush();
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }
}

//    public static void main(String[] args) {
//        String history = "hahah";
//        try {
//            URI url = History.class.getClassLoader().getResource("historyFile.txt").toURI();
//            System.out.println();
//            File file = new File(url);
//            if (file.exists()) {
//                Writer fileWriter = new FileWriter(file, true);
//                fileWriter.write(history + "\n");
//                fileWriter.flush();
//            } else {
//                System.out.println(file);
//            }
//        } catch (URISyntaxException e) {
//            e.printStackTrace();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }


