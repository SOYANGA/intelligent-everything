package com.github.soyanga.everything.core.dao.impl;

import com.github.soyanga.everything.core.dao.FileIndexDao;
import com.github.soyanga.everything.core.model.Condition;
import com.github.soyanga.everything.core.model.FileType;
import com.github.soyanga.everything.core.model.Thing;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: intelligent-everything
 * @Description:
 * @Author: SOYANGA
 * @Create: 2019-02-15 09:50
 * @Version 1.0
 */
public class FileIndexDaoImpl implements FileIndexDao {
    /**
     * final修饰的元素有3种初始化方式 直接赋值，构造方法，构造快
     */
    private final DataSource dataSource;


    /**
     * 这里我们要这样获取数据源是为了解耦，提升代码灵活性,而不使用DataSouceFactory中的静态方法
     *
     * @param dataSource 数据源
     */
    public FileIndexDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }


    /**
     * 数据库处理逻辑
     * 创建连接->创建命令->准备sql语句->执行sql语句-> //结果返回->处理结果->包装成Things
     * @param thing 插入的数据
     */
    @Override
    public void insert(Thing thing) {
        Connection connection = null;
        PreparedStatement statement = null;
        try {
            //1.获取数据源连接
            connection = dataSource.getConnection();
            //2.准备SQL语句
            String sql = "insert into file_index(name,path,depth,file_type) values (?,?,?,?)";

            //3.准备命令
            statement = connection.prepareStatement(sql);
            //4.设置参数-预编译命令 1，2，3，4
            statement.setString(1, thing.getName());
            statement.setString(2, thing.getPath());
            statement.setInt(3, thing.getDepth());
//            将枚举类的名称存储 FILE.DOC
            statement.setString(4, thing.getFiletype().name());
            //5.执行命令
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //6.关闭流
            releaseResource(null, statement, connection);
        }
    }

    /**
     * 数据库处理逻辑
     * 创建连接->创建命令->准备sql语句(从条件中取出condition的要查询的属性)
     * ->执行sql语句->结果返回->处理结果->包装成Things
     * @param condition
     * @return
     */
    @Override
    public List<Thing> search(Condition condition) {
        List<Thing> things = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            //1.获取数据源连接
            connection = dataSource.getConnection();
            //2.准备SQL语句

            //2.1拼接SQL语句添加模糊匹配
            //name : like '%%' 模糊匹配
            //fileType: =直接匹配
            //limit  :limit offset
            //OrderByAsc :order by
            //使用StringBuilder原因->因为这个方法代码执行时在虚拟机的本地方法栈(线程私有的)，不会被线程共享，不会出现线程安全问题。
            StringBuilder sqlBuilder = new StringBuilder();
            sqlBuilder.append(" select name,path,depth,file_type from file_index ");
            //name匹配原则：前模糊，后模糊，前后模糊原则需要的即可
            sqlBuilder.append(" where ")
                    .append(" name like '%")
                    .append(condition.getName())
                    .append("%' ");
            //fileType: =直接匹配
            if (condition.getFileType() != null) {
                sqlBuilder.append(" and file_type = '")
                        .append(condition.getFileType()
                                .toUpperCase()).append("' ");
            }
            //order by必选的
            sqlBuilder.append(" order by depth ")
                    .append(condition.getOderByAsc() ? "asc" : "desc");
            //limit offset
            sqlBuilder.append(" limit ")
                    .append(condition.getLimit())
                    .append(" offset 0 ");
            //3.准备命令
            System.out.println(sqlBuilder.toString());
            statement = connection.prepareStatement(sqlBuilder.toString());
            //4.设置参数-预编译命令
            //5.执行命令
            resultSet = statement.executeQuery();
            //6.处理结果
            while (resultSet.next()) {
                //将数据库中的行记录变成java中的对象
                Thing thing = new Thing();
                thing.setName(resultSet.getString("name"));
                thing.setPath(resultSet.getString("path"));
                thing.setDepth(resultSet.getInt("depth"));
                String fileType = resultSet.getString("file_type");
                thing.setFiletype(FileType.lookupByName(fileType));
                things.add(thing);
            }
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            //6.关闭流
            releaseResource(resultSet, statement, connection);
        }
        return things;
    }

    /**
     * 解决内部大量代码重复问题：重构
     *
     * @param resultSet  要关闭的结果集
     * @param statement  要关闭命令
     * @param connection 要关闭的书数据库连接
     */
    private void releaseResource(ResultSet resultSet, PreparedStatement statement, Connection connection) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
        if (statement != null) {
            try {
                statement.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

//    public static void main(String[] args) {
//        FileIndexDao fileIndexDao = new FileIndexDaoImpl(DataSourceFactory.dataSource());
//        Thing thing = new Thing();
//        thing.setName("哈哈");
//        thing.setPath("D:\\a\\test\\哈哈2.ppt");
//        thing.setDepth(3);
//        thing.setFiletype(FileType.DOC);
////        fileIndexDao.insert(thing);
//        Condition condition = new Condition();
//        condition.setName("哈哈");
//        condition.setFileType("DOC");
//        condition.setOderByAsc(true);
//        condition.setLimit(15);
//
//        List<Thing> things = fileIndexDao.search(condition);
//        for (Thing t : things) {
//            System.out.println(t);
//        }
//    }
}
