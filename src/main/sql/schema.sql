-- 数据库初始化脚本

--创建数据库
CREATE DATABASE seckill;
--使用数据库
USE seckill;
--创建秒杀库存表
CREATE TABLE seckill(
'seckill_id' BIGINT not null AUTO_INCREMENT COMMENT '商品库存id',
'name' VARCHAR(120) not null COMMENT '商品名称',
'number' int not null COMMENT '商品数量',
'start_time' timestamp not null COMMENT '秒杀开启时间',
'end_time' timestamp not null COMMENT '秒杀结束时间',
'create_time' timestamp not null default current_timestamp COMMENT '创建时间',/*default current_timestamp(mysql系统当前时间)*/
PRIMARY key (seckill_id),
key idx_start_time(start_time),
key idx_end_time(end_time),
key idx_create_time(create_time)
)ENGINE=InnoDB AUTO_INCREMENT=1000 DEFAULT CHARSET=UTF8 COMMENT='秒杀库存表'

--初始化数据
insert into
    seckill(name,number,start_time,end_time)
values
    ('1000元秒杀iphone8',100,'2019-8-8 00:00:00','2019-8-9 00:00:00');
    ('800元秒杀Nokia7x',200,'2019-8-8 00:00:00','2019-8-9 00:00:00');
    ('500元秒杀小米6',300,'2019-8-8 00:00:00','2019-8-9 00:00:00');
    ('200元秒杀老人机sgw',400,'2019-8-8 00:00:00','2019-8-9 00:00:00');

--秒杀成功明细表
--用户登录认证相关的信息
create table success_seckill(
'seckill_id' bigint not null COMMENT '秒杀商品id',
'user_phone' bitint not null COMMENT '用户手机号',
'state' tinyint not null default -1 COMMENT '状态标示:-1:无效 0:成功 1:已付款 2:已发货',
'create_time' timestamp not null COMMENT '创建时间',
PRIMARY key (seckill_id,user_phone),/*联合主建:作用:唯一性，同一个用户对同一个产品不能做重复的秒杀*/
key idx_create_time(create_time)
)ENGINE=InnoDB DEFAULT CHARSET=UTF8 COMMENT='秒杀成功明细表'

--连接数据库的控制台
mysql -uroot -123456

--为什么手写DDL
--记录每次上线的DDL修改
--上线v1.1
ALTER table seckill
    drop Index idx_create_time,
    add Index idx_c_s(start_time,create_time)