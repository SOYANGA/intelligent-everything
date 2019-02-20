package com.github.soyanga.everything.core.dao;

import com.alibaba.druid.pool.DruidDataSource;
import com.github.soyanga.everything.config.IntelligentEverythingConfig;

import javax.sql.DataSource;
import java.io.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

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
    public static DataSource dataSource() {
        if (dataSource == null) {
            synchronized (DataSourceFactory.class) {
                if (dataSource == null) {
                    //实例化 ---Druid数据连接池（实例化数据源对象）
                    dataSource = new DruidDataSource();
                    //H2的Driver class接口的实现Jdbc API规范中的Driver（接口）的子类org.h2.Driver -->加载JDBC驱动
                    dataSource.setDriverClassName("org.h2.Driver");

                    //url,username,password
                    //采用H2嵌入式数据库，数据库以本地文件的方式存储(并不式mysql的在服务器上存储)，只需要提供url接口

                    //Jdbc规范中关于MySQL
                    //jdbc:mysql://ip:port/databaseName

                    //Jdbc规范中关于H2 jdbc:h2:filename ->存储到本地文件

                    //Jdbc规范中关于H2 jdbc:h2:~/filepath ->存储到当前用户的home目录

                    //jdbc规范中H2 jdbc:h2://ip:port/databaseName  ->存储到H2服务器
                    dataSource.setUrl("jdbc:h2:" + IntelligentEverythingConfig.getInstance().getH2IndexPath());

//                    dataSource.setUrl("jdbc:h2:file:" + IntelligentEverythingConfig.getInstance().getH2IndexPath() + ";AUTO_SERVER=TRUE;");

                    //Duirp数据库的连接池的可配置参数

                    //判断连接数据库受否开启超时断开连接
                    //第二种
                    dataSource.setTestWhileIdle(false);
                    //第一种
                    dataSource.setValidationQuery("select now()");
                }
            }
        }
        return dataSource;
    }


    /**
     * 初始化数据脚本
     * 通过数据库连接执行SQL
     * 为了方便关闭流我们使用 try-with-resource JDK1.7新特性 try(){}catch(IOExecption e){}
     */
    public static void initDataSource() {
        //1.获取数据源
        DataSource dataSource = DataSourceFactory.dataSource();
        try (//1.1建立连接
             Connection connection = dataSource.getConnection();
        ) {
            try (//2.获取SQL语句
                 //不采取绝对路径文件  名字写错就会变成null
                 InputStream in = DataSourceFactory.class.getClassLoader().getResourceAsStream("intelligent_everything.sql");
            ) {
                if (in == null) {
                    throw new RuntimeException("Not read init database scrip please check it");
                }
                try (//2.1将InputStream字节输入流->字节字符转换流->字符输入流
                     BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(in))
                ) {
                    //2.2将读取的字符输入流变为字符串供statement使用
                    StringBuilder str = new StringBuilder();
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        //不打印注释
                        if (!line.startsWith("--")) {
                            str.append(line);
                        }
                    }
                    //3.获取数据库的执行语句
                    String sql = str.toString();
                    //3.1创建命令，并预编译sql语句
                    try (PreparedStatement preparedStatement = connection.prepareStatement(sql);) {
                        //3.3执行sql语句
                        preparedStatement.executeUpdate();
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    //Scanner类将字节输入流变为打印流可以正常读取字符，将sql语句进行拼接
//    InputStream in = DataSourceFactory.class.getClassLoader().getResourceAsStream("intelligent_everything.sql");
//                 Scanner scanner = new Scanner(in);
//                while(scanner.hasNext()){
//                    str.append(scanner.nextLine());
//                }

    //创建命令并执行命令 -性能快于Statment 开发常用DataSource + PerparedStatement
//                PreparedStatement statement = connection.prepareStatement(sql); //预编译sql指令
//                ->在预编译中执行了sql语句
//                则在后面进行命令执行时就不需要将sql参数传给Statement.execute()
//                System.out.println(statement.execute(str.toString()));
//
    //为什么使用StringBuilder来进行sql语句的拼接，为什么不用StringBuffer（线程安全）
//               因为我们操作的sql变量在静态方法内（属于线程私有属性）->方法栈中
//               线程私有内存区域不会涉及多线程安全性问题，采用StringBuffer反而会让性能降低
//

    //IO第三方工具使用
//    public static void main(String[] args) {
//        //显示IO包的读文件
//
//        try (InputStream in = DataSourceFactory.class.getClassLoader().getResourceAsStream("intelligent_everything.sql");) {
//            //获得直接获得文件中的sql
////            String sql = IOUtils.toString(in);
////            System.out.println(sql);
//            IOUtils.readLines(in)
//                    .stream()
//                    .filter(new Predicate<String>() {
//                        /**
//                         *
//                         * @param line
//                         * @return 返回false：是要排除的内容
//                         */
//                        @Override
//                        public boolean test(String line) {
//                            return !line.startsWith("--");
//                        }
//                    })
//                    .forEach(line -> System.out.println(line));
//
//                //文件拷贝
////            FileUtils.copyFileToDirectory();
//
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }

}
