	#SpringBoot的配置文件.properties
# Logging
	logging.config=classpath:logback-spring.xml
# Mysql Configuration
	spring.datasource.url=jdbc:mysql://localhost:3306/pgc  						#数据库地址
	spring.datasource.username=root												#账号
	spring.datasource.password=password											#密码
# Keep the connection alive if idle for a long time (needed in production)
	spring.datasource.testWhileIdle=true										#指定连接是否被空闲来连接回收器检验
	spring.datasource.validationQuery=SELECT 1									#验证从连接池取出的连接
# Show or not log for each sql query
	spring.jpa.show-sql=true													#运行时输出sql语句
# Hibernate ddl auto (save, save-drop, update)
	spring.jpa.hibernate.ddl-auto=update										#hibernate数据定义的规则，更新
# Set to true if we need to populate the database using 'data.sql'.
	spring.datasource.initialize=false											#指定初始化数据源，是否用data.sql来初始化，默认: true
# Naming strategy
	spring.jpa.hibernate.naming-strategy=org.hibernate.cfg.ImprovedNamingStrategy	#指定命名策略
# The SQL dialect makes Hibernate generate better SQL for the chosen database
	spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5Dialect	#hibernate方言
# Mongodb Configuration
	spring.data.mongodb.uri=mongodb://localhost:27000/pgc						#mongodb的uri
# OSS Configuration
	oss.endpoint=oss-cn-shanghai.aliyuncs.com									#oss阿里云的相关配置
	oss.access.key.id=LTAIxYKpaBqbAIK3
	oss.access.key.secret=WuTDXWS8PBqSdqMuMxrPfT5ryYmmd0
	oss.bucket.name=new-pgc
	spring.http.multipart.maxFileSize=100MB
	spring.http.multipart.maxRequestSize=100MB
# SSO Configuration
	security.enabled=true
#默认callback
	security.defaultCallback=/
#sso验证token地址
	security.ssoServerValidate=http://106.15.179.107:7127/api/c/sso/validate-token
#如果本地应用没有登陆就去验证
	security.ssoServerAuth=http://106.15.179.107:7127/api/c/sso/auth
#本地应用密码
	security.ssoKey=574163310
#本地应用地址, should be defined by the same field under each app
# security.appHost=http://localhost:8000
#gizp
	spring.resources.chain.gzipped=true

