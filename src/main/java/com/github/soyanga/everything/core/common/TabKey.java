package com.github.soyanga.everything.core.common;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.*;


/**
 * @program: intelligent-everything
 * @Description:
 * @Author: SOYANGA
 * @Create: 2019-02-20 22:33
 * @Version 1.0
 */
//TODO
public class TabKey {
    //TODO
    private static Set<String> set = new HashSet();
    private Button bt = new Button();

    private static Set<String> initSet() {
        String orderStr = "confighelp;show;quit;reinstall;search;cls;help;index;history;initialize;" +
                "--includePath=;--excludePath=;--maxReturn=;--depthOrderByAsc=;--fileSystemMonitor=;--backgroundClear=;--moniterFrequency=";
        String[] str = orderStr.split(";");
        set.addAll(Arrays.asList(str));
        return set;
    }


    public String complement(String str) {
        for (String order : set) {
            if (order.startsWith(str)) {
                return str;
            }
        }
        return null;
    }

}
