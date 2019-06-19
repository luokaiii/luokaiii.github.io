# NoSQLUnit

版本需求：JUnit 4.0 及以上、JDK5及以上。

## Maven 依赖

```xml
<dependency>
	<groupId>com.lordofthejars</groupId>
    <artifactId>nosqlunit-mongodb</artifactId>
    <version>${version.nosqlunit}</version>
</dependency>
```

## 数据集格式

```json
{
    "collection_name": [
        {
            "attr1": "value1",
            "attr2": "value2",
            "createDate": {
                "$date": "2019-06-17T10:06:36.210Z"
            }
        },
		{
            "attr1": "value1",
            "attr2": "value2"
        }
    ],
    "collection_name2": [
        {
            "attr1": "value1",
            "attr2": "value2"
        },
		{
            "attr1": "value1",
            "attr2": "value2"
        }
    ]
}
```

> 注：ISODate函数或其他js函数，请参考MongoDB java Driver。

## 负载策略

- INSERT 在执行任何测试方法之前插入已定义的数据集
- DELETE_ALL 在执行任何测试方法之前删除数据库中的所有元素
- CLEAN_ALL 删除数据库中的所有元素，并在执行测试方法之前插入已定义的数据集