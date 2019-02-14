--创建数据库
create database if not exists intelligent_everything;
--创建数据库 1.删除已经存在的表 2.创建数据库 file_index
drop table if exists file_index;
create table  if not exists  file_index(
  name        varchar(256)  not null comment '文件名称',
  path        varchar(1024) not null comment '文件路径',
  depth       int           not null comment '文件路径深度'
  file_type   varchar(32)   not null comment '文件类型'
);


--name varchar(256) not null comment '文件名称'
--path varchar(1024) not null comment '文件路径'
--depth int not null comment '文件路径深度'
--file_type varchar(32) not null comment "文件类型"
--scott数据库 用户名 密码
