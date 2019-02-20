package com.github.soyanga.everything.core.common;

import com.github.soyanga.everything.cmd.IntelligentEverythingCmdApp;
import com.github.soyanga.everything.config.IntelligentEverythingConfig;


import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

/**
 * @program: intelligent-everything
 * @Description:
 * @Author: SOYANGA
 * @Create: 2019-02-20 05:38
 * @Version 1.0
 */
public class ProFile {
    private static IntelligentEverythingConfig config = IntelligentEverythingConfig.getInstance();
    private static String proFile = config.getProFile();
    private static File file = new File(proFile);


    public static void ReadProFile() {
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        try (FileReader fileReader = new FileReader(file);
             Scanner scanner = new Scanner(fileReader)) {
            System.out.println("开始导入配置文件...");
            while (scanner.hasNext()) {
                String congigStr = scanner.nextLine();
                IntelligentEverythingCmdApp.parseParams(congigStr);
            }
            System.out.println("配置文件导入完毕");
            config.setIsStartLoad(false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static void WriteProFile() {
        try (FileWriter fileWriter = new FileWriter(file, true);
        ) {
            //初始化标志位 默认时false不初始化，则会将”-isFirstUse=false;”写到配置文件中
            if(!config.getISinitialize()){
                fileWriter.write("--isFirstUse=false;\n");
                fileWriter.flush();
            }
            StringBuilder instr = new StringBuilder();
            for (String inconfig : config.getIncludePath()) {
                instr.append(inconfig).append(";");
            }
            fileWriter.write("--includePath=" + instr.toString() + "\n");
            fileWriter.flush();

            StringBuilder exstr = new StringBuilder();
            for (String exconfig : config.getExcludepath()) {
                exstr.append(exconfig).append(";");
            }
            fileWriter.write("--excludePath=" + exstr.toString() + "\n");
            fileWriter.flush();

            fileWriter.write("--depthOrderByAsc=" + config.getDepthOrderAsc().toString() + ";\n");
            fileWriter.flush();


            fileWriter.write("--maxReturn=" + config.getMaxReturn().toString() + ";\n");
            fileWriter.flush();

            fileWriter.write("--fileSystemMonitor=" + config.getFileSystemMonitorSwitch().toString() + ";\n");
            fileWriter.flush();


            fileWriter.write("--backgroundClear=" + config.getBackgroundClearThreadSwitch().toString() + ";\n");
            fileWriter.flush();


        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void flushProFile(){
        try(FileWriter fileWriter =new FileWriter(file)) {
             fileWriter.write("");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}

