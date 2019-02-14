package com.github.soyanga.everything.core.dao;

import com.alibaba.druid.pool.DruidDataSource;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * @program: intelligent-everything
 * @Description: 与H2数据库建立连接（数据源，以及执行SQL初始化数据库） --嵌入式模式
 * @Author: SOYANGA
 * @Create: 2019-02-14 12:20
 * @Version 1.0
 */
public class DataSourceFactory {
    /**
     * 数据源是唯一的所以使用单例设计模式-Double-Check
     */
    private static volatile DruidDataSource dataSource;

    /**
     * 构造方法私有化
     */
    private DataSourceFactory() {

    }

    /**
     * 单例数据源的创建
     *
     * @return 数据源
     */
    public static DataSource dataSourceFactory() {
        if (dataSource == null) {
            synchronized (DataSourceFactory.class) {
                if (dataSource == null) {
                    //实例化
                    dataSource = new DruidDataSource();
                    dataSource.setDriverClassName("org.h2.Driver");
                    //url,username,password
                    //采用H2嵌入式数据库，数据库以本地文件的方式存储，只需要提供url接口
                    //获取当前工作路径
                    String workDir = System.getProperty("user.dir");
                    dataSource.setUrl("jdbc:h2" + workDir + File.separator + "intelligent_everything");
                }
            }
        }
        return dataSource;
    }


    //初始化数据脚本
    public static void initDataSource() {
        //1.获取数据源
        DataSource dataSource = DataSourceFactory.dataSourceFactory();
        try (//建立连接
             Connection connection = dataSource.getConnection();
             //创建命令
             Statement statement = connection.createStatement();
        ) {

            try (//2.获取SQL语句
                 InputStream in = DataSourceFactory.class.getClassLoader().getResourceAsStream("intelligent_everything.sql");
                 //将InputStream字节输入流->字节字符转换流->字符输入流
                 BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in));
            ) {
                //将读取的字符输入流变为字符串供statement使用
                StringBuilder str = new StringBuilder();
                String line;
                while ((line = bufferedReader.readLine()) != null) {
                    str.append(line);
                }
                System.out.println(statement.execute(str.toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        //3.通过数据库连接执行SQL

    }


    public static void main(String[] args) {
//        System.out.println(System.getProperty("user.dir"));
        DataSourceFactory.initDataSource();
    }

}
