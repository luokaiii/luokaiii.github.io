

坑：

1. 当我们从 `cas脚手架`上下载来代码之后，在其 `POM` 文件中并没有 `spring-boot` 的依赖。

2. 虽然项目中有测试代码，但是 `POM` 中并没有导入 `JUnit` 的包。

3. 因为项目中只有一个初始环境，所以项目启动成功也什么都没有，完全找不到教程上的登录页。

4. 因为 `cas脚手架` 需要翻墙才能使用，因此我们在 `gradle.properties` 中设置代理

5. ```properties
   systemProp.https.proxyPort=1080
   systemProp.http.proxyHost=127.0.0.1
   systemProp.https.proxyHost=127.0.0.1
   systemProp.http.proxyPort=1080
   ```

6. 
