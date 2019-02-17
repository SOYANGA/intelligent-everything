package com.github.soyanga.everything.cmd;

import com.github.soyanga.everything.config.IntelligentEverythingConfig;
import com.github.soyanga.everything.core.IntelligentEverythingManager;
import com.github.soyanga.everything.core.model.Condition;
import com.github.soyanga.everything.core.model.Thing;
import lombok.ToString;

import java.util.List;
import java.util.Scanner;

/**
 * @program: intelligent-everything
 * @Description:
 * @Author: SOYANGA
 * @Create: 2019-02-14 10:37
 * @Version 1.0
 */
public class IntelligentEverythingCmdApp {
    private static Scanner scanner = new Scanner(System.in);


    public static void main(String[] args) {
        System.out.println("这是intelligentEverything应用程序的命令行交互程序");
        //通过用户参数进行解析
        parseParams(args);
        System.out.println(IntelligentEverythingConfig.getInstance());
        //1.欢迎
        welcome();
        //2.创建统一调度器
        IntelligentEverythingManager manager = IntelligentEverythingManager.getInstance();

        //启用后台清理线程
        manager.startBackgroundClearThread();

        //启动后台记录history线程
        manager.startHistoryStoreThread();

        //3.交互式
        interactive(manager);
        //2.执行交互，实现help命令，添加最外层循环直到输入quit才正式，退出并打印一句话
    }


    private static void interactive(IntelligentEverythingManager manager) {
        //2.执行交互，实现help命令，添加最外层循环直到输入quit才正式，退出并打印一句话
        while (true) {
            System.out.print("everything >>");
            String input = scanner.nextLine();
            //存储用户输入history
            startStoreHistory(manager, input);
            //优先处理search
            if (input.startsWith("search")) {
                //search name [file_type]
                String[] values = input.split(" ");
                if (values.length >= 2) {
                    if (!"search".equals(values[0])) {
                        help();
                        continue;
                    }
                    //将用户输入的正确指令 提取变成Condition条件
                    Condition condition = new Condition();
                    String name = values[1];
                    condition.setName(name);
                    if (values.length >= 3) {
                        String fileType = values[2];
                        condition.setFileType(fileType.toUpperCase());
                    }
                    //limit orderBy 配置类
                    search(manager, condition);
                    continue;
                } else {
                    help();
                    continue;
                }
            }

            switch (input) {
                case "help":
                    help();
                    break;
                case "index":
                    index(manager);
                    try {
                        Thread.sleep(100);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    break;
                case "quit":
                    writeHistory(manager);
                    quit();
                    break;
                case "history":
                    history(manager);
                    break;
                default:
                    help();
            }
        }
    }

    private static void writeHistory(IntelligentEverythingManager manager) {
        manager.writeFileToHistory();
    }

    private static void startStoreHistory(IntelligentEverythingManager manager, String input) {
            manager.historySoreQueue(input);
    }

    private static void history(IntelligentEverythingManager manager) {
        List<String> list = manager.printHistory();
        for (String history : list) {
            System.out.println(history);
        }
    }


    private static void index(IntelligentEverythingManager manager) {
        //统一调度器器中的buildIndex 构建索引
        new Thread(manager::buildIndex).start();
    }

    private static void search(IntelligentEverythingManager manager, Condition condition) {
        //统一调度器中的search 的Condition条件
        //name fileType limit orderByAsc
        condition.setLimit(IntelligentEverythingConfig.getInstance().getMaxReturn());
        condition.setOderByAsc(IntelligentEverythingConfig.getInstance().getDepthOrderAsc());

        List<Thing> thingList = manager.search(condition);
        for (Thing thing : thingList) {
            System.out.println(thing.getPath());
        }
    }


    private static void parseParams(String[] args) {

        //为了避免文件中有空格，重新拼接字符串
        StringBuilder paramStrbuilder = new StringBuilder();
        for (String s : args) {
            paramStrbuilder.append(" ").append(s);
        }
        //重新拼接的字符串
        String paramStr = paramStrbuilder.toString().trim();
        int handindex = paramStr.indexOf("=");
        //如果用户指定的参数不对，不再做解析直接使用默认值
        if (handindex != -1 && handindex < paramStr.length()) {
            //操作参数
            String paramHand = paramStr.substring(0, handindex + 1);
            //赋值参数
            String argsHandle = paramStr.substring(handindex + 1);
            //赋值参数拆分成数组
            //一个一个字符串解析
            String[] paramList = argsHandle.split(";");

            IntelligentEverythingConfig config = IntelligentEverythingConfig.getInstance();

            String maxReturnParam = "--maxReturn=";
            if (maxReturnParam.equals(paramHand)) {
                config.setMaxReturn(Integer.parseInt(paramList[0]));
            }

            String depthOrderAscParam = "--depthOrderByAsc=";
            if (depthOrderAscParam.equals(paramHand)) {
                config.setDepthOrderAsc(Boolean.parseBoolean(paramList[0]));
            }
            String includePathParam = "--includePath=";
            if (includePathParam.equals(paramHand)) {
                if (paramList.length > 0) {
                    config.getIncludePath().clear();
                }
                for (String path : paramList) {
                    if (path != null) {
                        config.getIncludePath().add(path);
                    }
                }
            }
            String excludePathParam = "--excludePath=";
            if (excludePathParam.startsWith(paramHand)) {
                if (paramList.length > 0) {
                    config.getExcludepath().clear();
                }
                for (String path : paramList) {
                    if (path != null) {
                        config.getExcludepath().add(path);
                    }
                }
            }
        }
    }

    private static void welcome() {
        System.out.println("welcome to use,IntelligentEverything");
    }

    private static void help() {
        System.out.println("命令列表:");
        System.out.println("退出: quit");
        System.out.println("帮助: help");
        System.out.println("索引: index");
        System.out.println("搜索: search <name> [<file-Type> img | doc | bin | archieve | other]");
    }

    private static void quit() {
        System.out.println("再见");
        System.exit(0);
    }


}
