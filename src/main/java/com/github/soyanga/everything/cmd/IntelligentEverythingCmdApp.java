package com.github.soyanga.everything.cmd;

import com.github.soyanga.everything.core.IntelligentEverythingManager;
import com.github.soyanga.everything.core.model.Condition;

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
        //1.欢迎
        welcome();
        //2.创建统一调度器
        IntelligentEverythingManager manager = IntelligentEverythingManager.getInstance();
        //3.交互式
        interactive(manager);
        //2.执行交互，实现help命令，添加最外层循环直到输入quit才正式，退出并打印一句话
    }

    private static void interactive(IntelligentEverythingManager manager) {
        //2.执行交互，实现help命令，添加最外层循环直到输入quit才正式，退出并打印一句话
        while (true) {
            System.out.print("everything >>");
            String input = scanner.nextLine();
            //优先处理search
            if (input.startsWith("search")) {
                //search name [file_type]
                String[] values = input.split(" ");
                if (values.length >= 2) {
                    if (!values[0].equals("search")) {
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
                    break;
                case "quit":
                    quit();
                    break;
                default:
                    help();
            }
        }
    }


    private static void index(IntelligentEverythingManager manager) {
        //统一调度器器中的buildIndex 构建索引
        new Thread(new Runnable() {
            @Override
            public void run() {
                manager.buildIndex();
            }
        }).start();


    }

    private static void search(IntelligentEverythingManager manager, Condition condition) {
        //TODO
        System.out.println("检索功能");
        //统一调度器中的search
        //name fileType limit orderByAsc
        manager.search(condition);
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
