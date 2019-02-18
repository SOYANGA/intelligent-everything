-- 创建数据库
-- create database if not exists intelligent_everything;
-- H2创建数据库sql不需要执行原因是因为
-- 在我们创建数据源的时候已将数据库指定了，且H2数据库在嵌入模式下数据库其实就是在本地一个文件
-- 在创建数据源时H2会默认将url最后指定的文件名当作数据库去创建，之后存储的数据就会存储在这个文件里
-- dataSource.setUrl("jdbc:h2:" + workDir + File.separator + "intelligent_everything");
-- 1.删除已经存在的表 2.创建数据库 file_index
drop table if exists file_index;
create table if not exists file_index
(
  name      varchar(256)  not null comment '文件名称',
  path      varchar(1024) not null comment '文件路径',
  depth     int           not null comment '文件路径深度',
  file_type varchar(32)   not null comment '文件类型'
);
--scott数据库 用户名 密码