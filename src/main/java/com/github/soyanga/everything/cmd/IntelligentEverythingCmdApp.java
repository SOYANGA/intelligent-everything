package com.github.soyanga.everything.cmd;

import com.github.soyanga.everything.config.IntelligentEverythingConfig;
import com.github.soyanga.everything.core.IntelligentEverythingManager;
import com.github.soyanga.everything.core.common.ProFile;
import com.github.soyanga.everything.core.model.Condition;
import com.github.soyanga.everything.core.model.Thing;

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
    private static IntelligentEverythingConfig config = IntelligentEverythingConfig.getInstance();


    public static void main(String[] args) {
        loadConfig();
        System.out.println("这是intelligentEverything应用程序的命令行交互程序");
        //欢迎
        welcome();
        //配置参数
        configurationParameter();

        //通过用户参数进行解析
        //如果在配置参数中对程序参数已经进行解析的化，在这里就不对主方法参数进行参数解析
        //并且在接下来的交互中可以对参数进行重新分配
        //如果在这里进行解析，无法使用  默认true在交互中进行初始化配置参数
        if (!config.getAlterConfigflag()) {
            parseParams(args);
            System.out.println(IntelligentEverythingConfig.getInstance());
        }

        //创建统一调度器
        IntelligentEverythingManager manager = IntelligentEverythingManager.getInstance();

        //启动文件监控线程
        if (config.getFileSystemMonitorSwitch()) {
            manager.startFileSystemMonitor();
        }


        //启用后台清理线程
        if (config.getBackgroundClearThreadSwitch()) {
            manager.startBackgroundClearThread();

        }

        //启动后台记录history线程
        manager.startHistoryStoreThread();

        //3.交互式
        interactive(manager);
        //2.执行交互，实现help命令，添加最外层循环直到输入quit才正式，退出并打印一句话
    }


    /**
     * 对程序初始配置参数
     */
    private static void configurationParameter() {
        if (config.getIsFirstUse()) {
            System.out.println("->是否要对检索参数进行设置,如果不设置则会默认使用如下参数");
            System.out.println(config);
            if (config.getFileSystemMonitorSwitch()) {
                System.out.println("文件系统监控会对大文件进行监控时会花费很大的CPU资源，小文件则影响不大，根据检索路径自由改变");
            }
            System.out.println("->初始参数值一旦确认则在本次运行中无法对检索参数进行修改，建议进行设置，是否进行参数设置？\n");
            while (true) {
                System.out.println("->请回复: Y/N ");
                System.out.print("config->");
                String input = scanner.nextLine();
                if ("N".equals(input)) {
                    config.setAlterConfigflag(false);
                    return;
                }
                //默认开启参数可修改模式就是  true
                if ("Y".equals(input)) {
                    break;
                }

            }
            alterParam();
        } else {
            alterParam();
        }
    }

    private static void alterParam() {
        if (config.getIsFirstUse()) {
            System.out.println("->您可以输入confighelp查看参数设置规则，请您严格按照规则设置参数。");
            System.out.println("->为确保参数设置的正确性，一次只设置一个参数");
            System.out.println("->设置完毕你可以输入 quit 退出设置,配置完毕后自动进行初始化操作");
            System.out.println("\n" + "->当前配置如下:");
            showConfig();
            System.out.println("->设置规则如下");
            helpForConfig();
        } else {
            System.out.println("->系统监测已经初始化过,配置如下\n");
            showConfig();
            while (true) {
                System.out.println("是否要变更现有配置？Y/N");
                String input = scanner.nextLine();
                if ("N".equals(input)) {
                    return;
                }
                if ("Y".equals(input)) {
                    break;
                }
            }
        }
        while (true) {
            System.out.print("config->");
            String input = scanner.nextLine();
            if (input.startsWith("--")) {
                parseParams(input);
                continue;
            }
            if ("confighelp".equals(input)) {
                helpForConfig();
                continue;
            }
            if ("quit".equals(input)) {
                break;
            }
            if ("show".equals(input)) {
                showConfig();
                continue;
            } else {
                helpForConfig();
            }
        }

    }


    private static void interactive(IntelligentEverythingManager manager) {
        //2.执行交互，实现help命令，添加最外层循环直到输入quit才正式，退出并打印一句话
        if (config.getAlterIndexPathFlag()) {
            index(manager);
            config.setAlterIndexPathFlag(false);
        }
        while (true) {
            try {
                Thread.sleep(70);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            System.out.print("everything >>");
            String input = scanner.nextLine();

            //存储用户输入history
            startStoreHistory(manager, input);
            //优先处理search
            if (input.startsWith("search")) {
                analyticalSearch(input, manager);
                continue;
            }
            //falg为true（用户开启在程序中命令行修改程序参数模式）
            //flag为false（用户开启在程序运行前设置程序参数模式）
            switch (input) {
                case "help":
                    help();
                    break;
                case "index":
                    index(manager);
                    break;
                case "quit":
                    writeHistory(manager);
//                    stopWatch(manager);
                    WriteProFile();
                    quit();
                    break;
                case "history":
                    history(manager);
                    break;
                case "reinstall":
                    if (config.getAlterConfigflag()) {
                        alterParam();
                        index(manager);
                    } else {
                        help();
                    }
                    break;
                case "confighelp":
                    helpForConfig();
                    break;
                case "show":
                    showConfig();
                    break;
                default:
                    help();
            }
        }
    }


    /**
     * 用户查看要程序的配置信息
     */
    private static void showConfig() {
        System.out.println(config);
    }


    /**
     * 解析用户的查询指令
     *
     * @param input   用户输入
     * @param manager 调度器
     */
    private static void analyticalSearch(String input, IntelligentEverythingManager manager) {
        //search name [file_type]  search [“java 课件”] DOC
        String[] values = input.split(" ");
        if (values.length >= 2) {
            if (!"search".equals(values[0])) {
                help();
                return;
            }
            //将用户输入的正确指令 提取变成Condition条件
            Condition condition = new Condition();
            //判断文件夹中是否有空格
            if (values[1].startsWith("\"")) {
                int index = 0;
                for (String str : values) {
                    if (str.endsWith("\"")) {
                        break;
                    }
                    index++;
                }
                if (index > 1) {
                    StringBuilder fileNameSb = new StringBuilder();
                    values[1] = values[1].split("\"")[1];
                    values[index] = values[index].split("\"")[0];
                    for (int i = 1; i <= index; i++) {
                        fileNameSb.append(values[i] + " ");
                    }
                    String fileNmae = fileNameSb.toString().trim();
                    condition.setName(fileNmae);

                    if (values.length > index + 1) {
                        String fileType = values[index + 1];
                        condition.setFileType(fileType);
                    }
                    //limit orderBy 配置类
                    search(manager, condition);
                    return;
                }

            }
            String name = values[1];
            condition.setName(name);
            if (values.length >= 3) {
                String fileType = values[2];
                condition.setFileType(fileType.toUpperCase());
            }
            //limit orderBy 配置类
            search(manager, condition);
            return;
        } else {
            help();
            return;
        }
    }


    /**
     * 用户通过输入修改要检索的路径
     */
    private static void alterConfig(String alterPath, IntelligentEverythingManager manager) {


        index(manager);
    }


    /**
     * 在程序退出时将清理缓存文件，并将内存中的history写入到文件中
     *
     * @param manager
     */
    private static void writeHistory(IntelligentEverythingManager manager) {
        manager.writeFileToHistory();
    }

    /**
     * 将用户输入存入history中，并在首次写入时将history缓存文件中数据读取到内存中
     *
     * @param manager
     * @param input
     */
    private static void startStoreHistory(IntelligentEverythingManager manager, String input) {
        manager.historySoreQueue(input);
    }

    /**
     * 打印history历史
     *
     * @param manager
     */
    private static void history(IntelligentEverythingManager manager) {
        List<String> list = manager.printHistory();
        for (String history : list) {
            System.out.println(history);
        }
    }

    /**
     * 程序启动时从配置文件中读取配置信息
     */
    private static void loadConfig() {
        ProFile.ReadProFile();
    }

    /**
     * 退出时将配置写道配置文件中，供下次使用
     */
    private static void WriteProFile() {

        ProFile.flushProFile();
        ProFile.WriteProFile();
    }


    private static void index(IntelligentEverythingManager manager) {
        //统一调度器器中的buildIndex 构建索引
        new Thread(manager::buildIndex).start();
    }

    private static void search(IntelligentEverythingManager manager, Condition condition) {
        //统一调度器中的search 的Condition条件
        //name fileType limit orderByAsc
        condition.setLimit(config.getMaxReturn());
        condition.setOderByAsc(config.getDepthOrderAsc());

        List<Thing> thingList = manager.search(condition);
        for (Thing thing : thingList) {
            System.out.println(thing.getPath());
        }
    }


    /**
     * 默认初始化，以及程序主方法传参
     *
     * @param args
     */
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
        commonParse(paramStr, handindex);

    }

    /**
     * 命令行初始化参数解析
     *
     * @param pram 用户输入
     */
    public static void parseParams(String pram) {
        String paramStr = pram.trim();
        int handindex = paramStr.indexOf("=");
        //如果用户指定的参数不对，不再做解析直接使用默认值
        commonParse(paramStr, handindex);
    }

    public static void commonParse(String paramStr, int handindex) {
        if (handindex != -1 && handindex < paramStr.length()) {
            //操作参数
            String paramHand = paramStr.substring(0, handindex + 1);
            //赋值参数
            String argsHandle = paramStr.substring(handindex + 1);
            //赋值参数拆分成数组
            //一个一个字符串解析
            String[] paramList = argsHandle.split(";");

            String maxReturnParam = "--maxReturn=";
            if (maxReturnParam.equals(paramHand)) {
                try {
                    config.setMaxReturn(Integer.parseInt(paramList[0]));
                    if (!config.getIsStartLoad()) {
                        config.setAlterIndexPathFlag(true);
                    }
                    System.out.println("->maxReturn 设置成功");
                } catch (NullPointerException e) {
                    //用户输入错误就使用默认值
                }
            }

            String depthOrderAscParam = "--depthOrderByAsc=";
            if (depthOrderAscParam.equals(paramHand)) {
                config.setDepthOrderAsc(Boolean.parseBoolean(paramList[0]));
                if (!config.getIsStartLoad()) {
                    config.setAlterIndexPathFlag(true);
                }
                System.out.println("->depthOrderByAsc 设置成功");
            }

            String includePathParam = "--includePath=";
            if (includePathParam.equals(paramHand)) {
                if (paramList.length > 0) {
                    config.getIncludePath().clear();
                    if (!config.getIsStartLoad()) {
                        config.setAlterIndexPathFlag(true);
                    }
                    System.out.println("->includePath 设置成功");

                }
                for (String path : paramList) {
                    if (path != null) {
                        config.getIncludePath().add(path);
                    }
                }
            }
            String excludePathParam = "--excludePath=";
            if (excludePathParam.equals(paramHand)) {
                if (paramList.length > 0) {
                    config.getExcludepath().clear();
                    if (!config.getIsStartLoad()) {
                        config.setAlterIndexPathFlag(true);
                    }
                    System.out.println("->excludePath 设置成功");
                }
                for (String path : paramList) {
                    if (path != null) {
                        config.getExcludepath().add(path);
                    }
                }
            }
            if ("--fileSystemMonitor=true".equals(paramHand + paramList[0])) {
                config.setFileSystemMonitorSwitch(true);
                if (!config.getIsStartLoad()) {
                    config.setAlterIndexPathFlag(true);
                }
                System.out.println("文件系统监控开启");
            }
            if ("--backgroundClear=false".equals(paramHand + paramList[0])) {
                config.setBackgroundClearThreadSwitch(false);
                if (!config.getIsStartLoad()) {
                    config.setAlterIndexPathFlag(true);
                }
                System.out.println("后台清理关闭");
            }
            if (paramHand.contains("--isFirstUse=")) {
                config.setIsFirstUse(false);
            }
        }
    }


    private static void welcome() {
        System.out.println("欢迎使用Intelligent_Everything :)\n");
    }

    private static void help() {
        System.out.println("命令列表:");
        System.out.println("退出: quit");
        System.out.println("帮助: help");
        System.out.println("索引: index");
        System.out.println("历史指令: history");
        System.out.println("搜索: search <name> [<file-Type> img | doc | bin | archieve | other]");
        System.out.println("搜索文件特殊格式: search <\"name contain spacing \"> [<file-Type> img | doc | bin | archieve | other]");
        if (config.getAlterConfigflag()) {
            System.out.println("重新设置参数: reinstall");
        }
        System.out.println("查看当前参数设置: show");
        System.out.println("查看参数设置规则: confighelp");
    }


    /**
     * 对修改/初始化参数的帮助提示
     */
    private static void helpForConfig() {
        //如果开启可修改程序参数模式。
        System.out.println(" |修改参数，务必\"--\"开头 以分号\";\"结尾,配置包含多个文件路径信息时务必以\";\"为间隔");
        System.out.println(" |查看参数设置规则：                            confighelp");
        System.out.println(" |查看当前参数设置：                            show");
        System.out.println(" |退出设置参数：                                quit");
        System.out.println(" |修改要索引的文件路径：                         --includePath=path;path;");
        System.out.println(" |修改要排除索引的文件路径：                      --excludePath=path;path;");
        System.out.println(" |修改检索结果返回最大的数目：                    --maxReturn=number;");
        System.out.println(" |修改检索结果的排序 ture 为升序,false为降序：    --depthOrderByAsc=[true|false];");
        System.out.println(" |开启或关闭文件监控系统 ture 为开启,false为关闭： --fileSystemMonitor=[true|false];");
        System.out.println(" |开启或关闭后台清理 ture 为升序,false为降序：     --backgroundClear=[true|false];");
    }

    private static void quit() {
        System.out.println("感谢使用 :)");
        System.exit(0);
    }

    private static void stopWatch(IntelligentEverythingManager manager) {
        manager.stopFileSystemMonitor();
    }


}
