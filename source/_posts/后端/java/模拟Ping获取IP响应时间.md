---
title: 模拟Ping
date: 2018-09-29 17:38:03
tags:
 - java
categories: 
 - 工具函数
---
```java
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class IpContimeUtil {

    private static float ping(String ipAddress) {

        boolean isWindows = System.getProperty("os.name").toLowerCase().contains("win");
        String command = String.format("ping -%s 1 %s", isWindows ? "n" : "c", ipAddress);
        try {
            Process process = Runtime.getRuntime().exec(command);
            InputStream inputStream = process.getInputStream();
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "GBK");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line;
            Pattern pattern = Pattern.compile(".*?([\\d.]+)\\s?ms.*");

            while ((line = bufferedReader.readLine()) != null) {
                System.out.println(line);
                Matcher matcher = pattern.matcher(line);
                if (matcher.matches()) {
                    return Float.parseFloat(matcher.group(1));
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("响应超时");
        }
        throw new RuntimeException("响应异常");
    }

    private static List<Float> print(List<String> newList) throws Exception {
        return newList.stream().map(IpContimeUtil::ping).collect(Collectors.toList());
    }

    public static void main(String[] args) throws Exception {
        System.out.println("响应时间：" + IpContimeUtil.print(Collections.singletonList("101.132.162.159")));
    }
}

```

