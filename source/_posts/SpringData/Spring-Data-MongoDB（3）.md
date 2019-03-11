---
title: SpringDataMongoDB(三)
date: 2018-09-01 14:46:03
tags:
 - Spring Data
categories: 
 - Java
---
# Spring Data MongoDB （三） 自定义converter

```java
package com.pgc.diagnose.config;

import com.mongodb.MongoClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.mongodb.config.AbstractMongoConfiguration;
import org.springframework.data.mongodb.core.convert.CustomConversions;

import java.util.ArrayList;
import java.util.List;

@Configuration
//@EnableAutoConfiguration(exclude = {EmbeddedMongoAutoConfiguration.class})
//@Profile("!testing")
public class MongoConfig extends AbstractMongoConfiguration {

    @Value("${spring.data.mongodb.uri}")
    private String host;

    @Override
    public MongoClient mongoClient() {
        return new MongoClient("127.0.0.1", 27017);
    }

    @Override
    protected String getDatabaseName() {
        return "ch_node";
    }

    @Override
    public CustomConversions customConversions() {
        List<Converter<?, ?>> converters = new ArrayList<>();
        converters.add(new StringToPointConverter2());
        return new CustomConversions(converters);
    }
}

```



```java
package com.pgc.diagnose.config;

import com.pgc.common.exception.BadRequestException;
import com.pgc.diagnose.model.Point;
import org.springframework.core.convert.ConverterNotFoundException;
import org.springframework.core.convert.TypeDescriptor;
import org.springframework.core.convert.converter.ConditionalGenericConverter;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.Set;

@Component
public class StringToPointConverter implements ConditionalGenericConverter {

    @Override
    public boolean matches(TypeDescriptor sourceType, TypeDescriptor targetType) {
        return sourceType.getType().equals(String.class) && targetType.getType().equals(Point.class);
    }

    @Override
    public Set<ConvertiblePair> getConvertibleTypes() {
        return Collections.singleton(new ConvertiblePair(String.class, Point.class));
    }

    @Override
    public Object convert(Object source, TypeDescriptor sourceType, TypeDescriptor targetType) {
        String from = (String) source;

        if (from != null){
            String[] strings = from.split("#");

            if (strings.length == 0 || strings.length > 2)
                throw new BadRequestException("String 转 Point 失败！");

            if (strings.length == 1)
                return Point.build(Point.Track.valueOf(strings[0]));

            return Point.build(Point.Track.valueOf(strings[0]), Point.Industry.valueOf(strings[1]));
        }

        throw new ConverterNotFoundException(sourceType, targetType);
    }
}

```



```java
package com.pgc.diagnose.config;

import com.pgc.common.exception.BadRequestException;
import com.pgc.diagnose.model.Point;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class StringToPointConverter2 implements Converter<String, Point> {
    @Override
    public Point convert(String from) {

        if (from != null){
            String[] strings = from.split("#");

            if (strings.length == 0 || strings.length > 2)
                throw new BadRequestException("String 转 Point 失败！");

            if (strings.length == 1)
                return Point.build(Point.Track.valueOf(strings[0]));

            return Point.build(Point.Track.valueOf(strings[0]), Point.Industry.valueOf(strings[1]));
        }

        return null;
    }
}

```



```java
package com.pgc.diagnose.config;

import com.pgc.common.config.WebConfig;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DiagnoseAppWebConfig extends WebConfig {

}

```

