---
title: 《常用的MySQL命令》
date: 2018-09-29 17:38:03
tags:
  - java
categories:
  - 工具函数
---

包含 MySQL 服务操作、数据库操作、表操作、数据操作、字符集编码、数据类型 等信息。

<!-- more -->

## 基本操作

```mysql
/* Windows服务 */
-- 启动 MySQL
	net start mysql
-- 创建Windows 服务
	sc create mysql binPath= mysqld_bin_path(等号与值之间有空格)
/* 连接与断开服务器 */
	mysql -h 地址 -P 端口 -u 用户名 -p 密码
-- 显示哪些线程正在运行
	SHOW PROCESSLIST
-- 显示系统变量信息
	SHOW VARIABLES
```

## 数据库操作

```mysql
/* 数据库操作 */
-- 查看当前数据库
	SELECT DATABASE();
-- 显示当前时间、用户名、数据库版本
	SELECT now(), user(), version();
-- 创建库
	CREATE DATABASE [IF NOT EXISTS] 数据库名 数据库选项
	数据库选项：
		CHARACTER SET charset_name
		COLLATE collation_name
-- 查看已有库
	SHOW DATABASES [ LIKE 'PATTERN' ]
-- 查看当前库信息
	SHOW CREATE DATABASE 数据库名
-- 修改库的选项信息
	ALTER DATABASE 数据库名 选项信息
-- 删除库
	DROP DATABASE [ IF EXISTS ] 数据库名
		同时删除数据库相关的目录及目录内容
```

## 表操作

```mysql
-- 创建表
	CREATE [TEMPORARY] TABLE [IF NOT EXISTS] [数据库名.]表名 (
    	表的结构定义
    ) [表选项]
    	- 每个字段必须有数据类型
    	- 最后一个字段后不能有都好
    	- TEMPORARY 临时表，会话结束时表自动消失
    	- 对于字段的定义：
    		字段名 数据类型 [NOT NULL|NULL] [DEFAULT default_value] [AUTO_INCREMENT] [UNIQUE [KEY] | PRIMARY [KEY]] [COMMENT 'string']
-- 表选项
	-- 字符集
		CHARSET = charset_name
		- 如果表没有设定，则使用数据库字符集
    -- 存储引擎
    	ENGINE = engine_name
    	- 表在管理数据时采用的不同的数据结构，结构不同会导致处理方式、提供的特性操作等不同
    	- 常见的引擎：InnoDB、MyISAM、Memory/Heap、BDB、Merge、Example、CSV、MaxDB、Archive
    	- 不同的引擎在保存表的结构和数据时采用不同的方式
    		- MyISAM表文件含义：.frm表定义，.MYD表数据，.MYI表索引
    		- InnoDB表文件含义：.frm表定义，表空间数据和日志文件
    	- 显示存储引擎的状态信息
    		SHOW ENGINES；
        - 显示存储引擎的日志或状态信息
        	SHOW ENGINE 引擎名 {LOGS|STATUS}
	-- 自增起始数
		AUTO_INCREMENT = 行数
	-- 数据文件目录
		DATA DIRECTORY = '目录'
    -- 索引文件目录
    	INDEX DIRECTORY = '目录'
    -- 表注释
    	COMMENT = 'string'
    -- 分区选项
    	PARTITION BY ...
-- 查看所有表
	SHOW TABLES [LIKE 'pattern']
	SHOW TABLES FROM 库名
-- 查看表结构
	SHOW CREATE TABLE 表名
	DESC 表名 / DESCRIBE 表名 / EXPLAIN 表名 / SHOW COLUMNS FROM 表名 [LIKE 'pattern']
	SHOW TABLE STATUS [FROM db_name] [like 'pattern']
-- 修改表
	-- 修改表本身的选项
		ALTER TABLE 表名 表的选项
		- eg: ALTER TABLE db_name ENGINE=MYISAM;
    -- 对表进行重命名
    	RENAME TABLE 原表名 TO 新表名
    	RENAME TABLE 原表名 TO 库名.表名
    	- RENAME 可以交换两个表名
    -- 修改表的字段结构
    	ALTER TABLE 表名 操作名
    	-- 操作名
    		ADD [COLUMN] 字段定义			   -- 增加字段
    		ADD [COLUMN] AFTER 字段名			-- 增加在该字段名后面
    		ADD [COLUMN] FIRST 				  -- 增加在第一个
    		ADD PRIMARY KEY(字段名)			-- 创建主键
    		ADD UNIQUE [索引名] (字段名)		  -- 创建唯一索引
    		ADD INDEX [索引名] (字段名)		  -- 创建普通索引
    		DROP [COLUMN] 字段名			 	-- 删除字段
    		MODIFY [COLUMN] 字段名 字段属性	 -- 支持对字段属性修改，不支持修改字段名
    		CHANGE [COLUMN] 原字段名 新字段名 属性 -- 支持对字段名修改
    		DROP PRIMARY KEY				  -- 删除主键(删除主键前，需要删除其 AUTO_INCREMENT 属性)
    		DROP INDEX 索引名					-- 删除索引
    		DROP FOREIGN KEY 外键				 -- 删除外键
-- 删除表
	DROP TABLE [IF EXISTS] 表名...
-- 清空表数据
	TRUNCATE [TABLE] 表名
-- 复制表结构
	CREATE TABLE 表名 LIKE 复制名
-- 复制表结构和数据
	CREATE TABLE 表名 [AS] SELECT * FROM 要复制的表名
-- 检查表是否有错误
	CHECK TABLE tb_name [,tb_name2] ... [option] ...
-- 优化表
	OPTIMIZE [LOCAL | NO_WRITE_TO_BINLOG] TABLE tb_name [,tb_name] ...
-- 修复表
	REPAIR [LOCAL | NO_WRITE_TO_BINLOG] TABLE tb_name [,tb_name] ... [QUICK] [EXTENDED] [USE_FRM]
-- 分析表
	ANALYZE [LOCAL | NO_WRITE_TO_BINLOG] TABLE tb_name [,tb_name]...
```

## 数据操作

```mysql
-- 增
	INSERT [INTO] 表名 [(字段列表)] VALUES (值列表)[,(值列表),...]
		- 如果要插入的值列表包含所有字段并且顺序一致，则可以省略字段列表
		- 可同时插入多条数据记录
		- REPLACE 与 INSERT 完全一样，可互换
    INSERT [INTO] 表名 SET 字段名=值[,字段名=值,...]
-- 删
	DELETE FROM 表名 [删除条件子句]
		- 没有条件子句，会删除全部
-- 改
	UPDATE 表名 SET 字段名=值[,字段名=值,...] [更新条件]
-- 查
	SELECT 字段列表 FROM 表名 [其他子句]
		- 可来自多个表的多个字段
		- 其他子句可以不使用
		- 字段列表可以用*代替，表示所有字段
```

## 字符集编码

```mysql
-- MySQL、数据库、表、字段均可设置编码
-- 数据编码与客户端编码无需一致
-- 查看所有字符集编码项
SHOW VARIABLES LIKE 'character_set_%'
	character_set_client	-- 客户端向服务器发送数据时使用的编码
	character_set_results	-- 服务器端将结果返回给客户端所使用的编码
	character_set_connection-- 连接层编码
SET 变量名 = 变量值
	SET character_set_client = gbk;
	SET character_set_results = gbk;
	SET character_set_connection = gbk;
SET NAMES gbk; 	-- 以上三个配置
-- 校对集
	校对集用以排序
	-- 查看所有字符集
	SHOW CHARACTER SET [LIKE 'pattern'] / SHOW CHARSET [LIKE 'pattern']
	-- 查看所欲校对集
	SHOW COLLATION [LIKE 'pattern']
	-- 设置字符集编码
	CHARSET 字符集编码
	-- 设置校对集编码
	COLLATE 校对集编码
```

## 数据类型（列类型）

```mysql
1. 数值类型
-- a.整型
	类型			字节		范围
	tinyint  	 1字节     -128~127 无符号位：0~255
	smallint 	 2字节 	 -32768~32767
	mediumint 	 3字节	 -8388608~8388607
	int 		 4字节
	bigint		 8字节
	int(M)		 M表示总位数
	- 默认存在符号位，unsigned 属性修改
	- 显示宽度，如果某个数不够定义字段时设置的位数，则前面以0补填，zerofill 属性修改
		例如：int(5) 插入'123',补填后为'00123'
	- 在满足要求的情况下，越小越好
	- 1表示bool真，0表示bool假。常用 tinyint(1) 表示布尔型
-- b.浮点型
	类型			字节		范围
	float		 4字节
	double		 8字节
	- 浮点型既支持符号位 unsigned，也支持显示宽度 zerofill
		- 不同于整型，前后均会补填0
	- 定义浮点型时，需指定总位数和小数位数
		- float(M,D)	double(M,D)
		- M表示总位数，D表示小数位数
		- M和D的大小会决定浮点数的范围，不同于整型的固定范围
		- M既表示总位数(不包括小数点和正负号)，也表示显示宽度(所有显示符号均包括)
		- 支持科学计数法表示
		- 浮点数表示近似值
-- c.定点数
	decimal  -- 可变长度
	decimal(M,D)  -- M表示总位数，D表示小数位数
	- 保存一个精确的数值，不会发生数据的改变，不同于浮点数的四舍五入
	- 将浮点数转换为字符串来保存，每9位数字保存为4个字节
2. 字符串类型
-- a.char，varchar
	char	定长字符串，速度快，但浪费空间
	varchar	变长字符串，速度慢，但节省空间
	- M表示能存储的最大长度，此长度为字符数，非字节数
	- 不同的编码，所占用的空间不同
	- char，最多255个字符，与编码无关
	- varchar，最多65535个字符，与编码有关
	- 一条有效记录最大不能超过 65535 个字节
	 - utf8 最大为21844个字符，gbk 最大为 32766个字符，latin 最大为65532个字符
	- varchar 是变长的，需要利用存储空间保存 varchar 的长度，如果数据小于255个字节，则采用一个字节来保存长度，反之需要两个字节来保存
	- varchar 的最大有效长度由最大行大小和使用的字符集确定。
	- 最大有效长度是 65532 字节，因为在varchar存字符串时，第一个字节是空的，不存在任何数据，然后还需两个字节来存放字符串的长度，所以有效长度是 64432-1-2=65532字节
	 - 例如：表 定义为 CREATE TABLE tb(c1 int,c2 char(30),c3 varchar(N)) charset=utf8；问N的最大值是多少？答：(65535-1-2-4-30*3)/3
-- b.blob,text
	blob	二进制字符串（字节字符串）
		tinyblob,blob,mediumblob,longblob
	text	非二进制字符串（字符字符串）
		tinytext,text,mediumtext,longtext
	- text 在定义时，不需要定义长度，也不会计算总长度
	- text 类型在定义时，不可给default值
-- c,binary,varbinary
	类似于 char 和 varchar，用于保存二进制字符串，也就是保存字节字符串而非字符字符串
	char、varchar、text 对应 binary、varbinary、blob
3. 日期时间类型
	一般用整型保存时间戳
	datetime		8字节	日期及时间
		YYYY-MM-DD hh:mm:ss
	date			3字节 日期
		YYYY-MM-DD
		YY-MM-DD
		YYYYMMDD
		YYMMDD
	timestamp		4字节 时间戳
		YY-MM-DD hh:mm:ss
		YYYYMMDDhhmmss
		YYMMDDhhmmss
	time			3字节 时间
		hh:mm:ss
		hhmmss
	year			1字节 年份
		YYYY
		YY
4. 枚举和集合
-- 枚举
	enum(val1,val2,val3...)
		- 在已知的值中进行单选，最大数量为65535
		- 枚举值在保存时，以2字节的整型保存，每个枚举值，按保存的位置顺序，从1开始逐一递增
		- 表现为字符串类型，存储却是整型
		- NULL值的索引是NULL
		- 空字符串错误值的索引值是0
-- 集合
	set(val1,val2,val3....)
		- CREATE TABLE tb(gener set('男','女','无'))
		- INSERT INTO tb values ('男，女')
		- 最多可以有64个不同成员，以bigint存储，共8字节，采取位运算的形式
		- 当创建表时，SET 成员值的尾部空格将自动删除
```

文章摘抄自 [掘金 - 一千行 MySQL 命令](https://juejin.im/post/5d6faf8f6fb9a06b0b1c9535?utm_source=gold_browser_extension) 。
