# 数据库初始化

-- 创建库
create database if not exists init_db;

-- 切换数据库
use init_db;

-- 用户表
create table if not exists user
(
    id              bigint auto_increment                     comment 'id主键' primary key,
    userAccount     varchar(256)                              not null comment '用户账号',
    userPassword    varchar(256)                              not null comment '用户密码',
    userName        varchar(256)                              null     comment '用户昵称',
    userAvatar      varchar(1024)                             null     comment '用户头像',
    userProfile     varchar(512)                              null     comment '用户简介',
    userRole        varchar(256) default 'user'               not null comment '用户角色   user/admin/ban',
    createTime      datetime default CURRENT_TIMESTAMP        not null comment '创建时间',
    updateTime      datetime default CURRENT_TIMESTAMP        not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete        tinyint  default 0                        not null comment '逻辑删除 0未删除 1已删除'
) comment '用户表' collate = utf8mb4_unicode_ci;


-- 文章表
create table if not exists article
(
    id         bigint auto_increment comment 'id' primary key,
    title      varchar(512)                       null comment '标题',
    content    text                               null comment '内容',
    tags       varchar(1024)                      null comment '标签列表（json 数组）',
    userId     bigint                             not null comment '创建用户 id',
    createTime datetime default CURRENT_TIMESTAMP not null comment '创建时间',
    updateTime datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
    isDelete   tinyint  default 0                 not null comment '是否删除',
    index idx_userId (userId)
) comment '文章' collate = utf8mb4_unicode_ci;