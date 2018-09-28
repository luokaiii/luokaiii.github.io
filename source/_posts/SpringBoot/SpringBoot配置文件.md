	#SpringBoot�������ļ�.properties
# Logging
	logging.config=classpath:logback-spring.xml
# Mysql Configuration
	spring.datasource.url=jdbc:mysql://localhost:3306/pgc  						#���ݿ��ַ
	spring.datasource.username=root												#�˺�
	spring.datasource.password=password											#����
# Keep the connection alive if idle for a long time (needed in production)
	spring.datasource.testWhileIdle=true										#ָ�������Ƿ񱻿��������ӻ���������
	spring.datasource.validationQuery=SELECT 1									#��֤�����ӳ�ȡ��������
# Show or not log for each sql query
	spring.jpa.show-sql=true													#����ʱ���sql���
# Hibernate ddl auto (save, save-drop, update)
	spring.jpa.hibernate.ddl-auto=update										#hibernate���ݶ���Ĺ��򣬸���
# Set to true if we need to populate the database using 'data.sql'.
	spring.datasource.initialize=false											#ָ����ʼ������Դ���Ƿ���data.sql����ʼ����Ĭ��: true
# Naming strategy
	spring.jpa.hibernate.naming-strategy=org.hibernate.cfg.ImprovedNamingStrategy	#ָ����������
# The SQL dialect makes Hibernate generate better SQL for the chosen database
	spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL5Dialect	#hibernate����
# Mongodb Configuration
	spring.data.mongodb.uri=mongodb://localhost:27000/pgc						#mongodb��uri
# OSS Configuration
	oss.endpoint=oss-cn-shanghai.aliyuncs.com									#oss�����Ƶ��������
	oss.access.key.id=LTAIxYKpaBqbAIK3
	oss.access.key.secret=WuTDXWS8PBqSdqMuMxrPfT5ryYmmd0
	oss.bucket.name=new-pgc
	spring.http.multipart.maxFileSize=100MB
	spring.http.multipart.maxRequestSize=100MB
# SSO Configuration
	security.enabled=true
#Ĭ��callback
	security.defaultCallback=/
#sso��֤token��ַ
	security.ssoServerValidate=http://106.15.179.107:7127/api/c/sso/validate-token
#�������Ӧ��û�е�½��ȥ��֤
	security.ssoServerAuth=http://106.15.179.107:7127/api/c/sso/auth
#����Ӧ������
	security.ssoKey=574163310
#����Ӧ�õ�ַ, should be defined by the same field under each app
# security.appHost=http://localhost:8000
#gizp
	spring.resources.chain.gzipped=true

