---
title: OSS上传
date: 2018-09-29 17:38:03
tags:
  - OSS
  - hide
categories:
  - 后端
  - java
---

# 通过 POI 将数据库中的数据上传至 OSS 对象存储

<center>
`我爱你，第一句是假的，第二句也是假的。`
</center>

![我爱你，第一句是假的，第二句也是假的。](https://upload-images.jianshu.io/upload_images/13603359-996a2c470135ea09.png)

<!-- More -->

表格的具体样式可以参考 [`第三章`](#三、设置Excel单元格样式)

我们以 aliyun 的 OSS 为例上传 [`上传方法`](#四、上传至OSS等对象存储)

## 一、准备工作

第一步：引入 Apache POI 的依赖

```java
<!-- Maven 方式 -->

<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi</artifactId>
    <version>4.0.0</version>
</dependency>
```

```java
<!-- Gradle 方式 -->

compile group: 'org.apache.poi', name: 'poi', version: '4.0.0'
```

第二步：引入 JUnit 测试，这里就不做赘述了。

## 二、讲解测试用例

### `Test`方法

我们先从测试方法着手，再逐步讲解测试中所用到的方法。

```java
@Autowired
private UserService userService;

@Test
public void upload(){
    List<User> users = userService.findAll(); // ①

    final ByteArrayOutputStream stream = exportProjectList(users); // ②

    String uri = uploadWorkbook(stream, "学生数据表.xlsx"); // ③

    System.out.println(uri); // ④
}
```

1. 从数据库中取出需要导入 Excel 的数据
2. 方法：[`exportProjectList`](#`exportProjectList`方法) 将数据存入 `POI` 生成的 Excel 中，并将 Excel 转换为字节流，用来向`对象存储`中上传
3. 方法：[`uploadWorkbook`](#`uploadWorkbook`方法) 上传流式文件，和文件名称
4. 输出 `aliyun` 返回的地址，用于业务的实现，比如 `保存至数据库` 等。

### `exportProjectList`方法

作用：接收从数据库查询出来的对象集合，将其插入到 Workbook 中，并生成二进制的输出流 `ByteArrayOutputStream`

表格的具体样式可以参考 [`第三章`](#三、设置Excel单元格样式)

```java
private ByteArrayOutputStream exportProjectList(User[] users) throws IOException {
        // Workbook 工作区
        final XSSFWorkbook workbook = new XSSFWorkbook();

        // 单字体居中样式
        final XSSFCellStyle centerStyle = workbook.createCellStyle();
        centerStyle.setAlignment(HorizontalAlignment.CENTER);

        // 字体居中加粗样式
        final XSSFCellStyle headerStyle = workbook.createCellStyle();
        headerStyle.setAlignment(HorizontalAlignment.CENTER);

        final XSSFFont font = workbook.createFont();
        font.setBold(true);
        headerStyle.setFont(font);

        // Sheet 分页
        final XSSFSheet sheet = workbook.createSheet("Sheet Name");

        final String[] strings = new String[]{"序号", "学号", "姓名", "年龄", "院系", "班级"};

        // 首行内容填充，以及设置样式
        setFirstRow(sheet, strings, headerStyle);

        int rowSign = 0;
        for (user user : users) {

            // Row 行
            final XSSFRow row = sheet.createRow(++rowSign);

            // Cell 单元格
            final XSSFCell firstCell = row.createCell(0);
            firstCell.setCellValue(rowSign);
            firstCell.setCellStyle(centerStyle);

            final XSSFCell secondCell = row.createCell(1);
            secondCell.setCellValue(user.getStuNo());
            secondCell.setCellStyle(centerStyle);

            final XSSFCell thirdCell = row.createCell(2);
            thirdCell.setCellValue(user.getName());
            thirdCell.setCellStyle(centerStyle);

            final XSSFCell fourthCell = row.createCell(3);
            fourthCell.setCellValue(user.getAge());
            fourthCell.setCellStyle(centerStyle);

            final XSSFCell fifthCell = row.createCell(4);
            fifthCell.setCellValue(user.getCollege());
            fifthCell.setCellStyle(centerStyle);

            final XSSFCell sixthCell = row.createCell(5);
            sixthCell.setCellValue(user.getClass());
            sixthCell.setCellStyle(centerStyle);
        }

        // 宽度自适应
        for (int s = 0; s < strings.length; s++) {
            sheet.autoSizeColumn(s);
            sheet.setColumnWidth(s, sheet.getColumnWidth(s) * 17 / 10);
        }

        final ByteArrayOutputStream stream = new ByteArrayOutputStream();

        workbook.write(stream);

        return stream;
    }
```

需要注意的是，`每一个单元格 (Cell) 都是不能为空的`。

我们将设置头标题的方法抽离出来，可供其他设置的方法使用。

```java
/**
 * 设置第一行的头标题
 */
private void setFirstRow(Sheet sheet, String[] cellTitles, XSSFCellStyle headerStyle) {
    final Row row = sheet.createRow(0);

    int cellSign = 0;

    for (String cellName : cellTitles) {
        final Cell cell = row.createCell(cellSign++);
        cell.setCellValue(cellName);
        cell.setCellStyle(headerStyle);
    }
}
```

在做完前面的工作之后，我们就可以开始写测试方法了

### `uploadWorkbook`方法

该方法用于：`接收 Workbook 生成的流，并将其保存至 OSS 上`，本方法适用于 `aliyun` 的 `OSS 对象存储`，其他的具体实现都可以从官方 API 中查到。

```java
private String uploadWorkbook(ByteArrayOutputStream stream, String fileId) throws IOException {
    final ByteArrayInputStream inputStream = new ByteArrayInputStream(stream.toByteArray()); // ①

    ObjectMetadata objectMetadata = new ObjectMetadata();
    objectMetadata.setContentLength(stream.size());
    objectMetadata.setContentEncoding("utf-8");
    objectMetadata.setCacheControl("no-cache");
    objectMetadata.setHeader("Pragma", "no-cache");
    objectMetadata.setContentDisposition("inline;filename=" + "学生表数据.xlsx"); // ②

    return ossCore.uploadByStream(fileId, objectMetadata, inputStream); // ③
}
```

1. 将输出流转换为输入流
2. 保存对象的媒体信息
3. 上传至 `aliyun` 的 `OSS` 对象存储，具体实现可以看 [`第四部分`](#四、上传至OSS等对象存储)

## 三、设置 Excel 单元格样式

创建 sheet

```java
HSSFCellStyle cellStyle = wb.createCellStyle();
```

一、设置背景色:

```java
cellStyle.setFillForegroundColor((short) 13);// 设置背景色
cellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
```

二、设置边框:

```java
cellStyle.setBorderBottom(HSSFCellStyle.BORDER_THIN); //下边框
cellStyle.setBorderLeft(HSSFCellStyle.BORDER_THIN);//左边框
cellStyle.setBorderTop(HSSFCellStyle.BORDER_THIN);//上边框
cellStyle.setBorderRight(HSSFCellStyle.BORDER_THIN);//右边框
```

三、设置居中:

```java
cellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 居中
```

四、设置字体:

```java
HSSFFont font = wb.createFont();
font.setFontName("黑体");
font.setFontHeightInPoints((short) 16);//设置字体大小

HSSFFont font2 = wb.createFont();
font2.setFontName("仿宋_GB2312");
font2.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);//粗体显示
font2.setFontHeightInPoints((short) 12);

cellStyle.setFont(font);//选择需要用到的字体格式
```

五、设置列宽:

```java
sheet.setColumnWidth(0, 3766); //第一个参数代表列id(从0开始),第2个参数代表宽度值  参考 ："2012-08-10"的宽度为2500
```

六、设置自动换行:

```java
cellStyle.setWrapText(true);//设置自动换行
```

七、合并单元格:

```java
Region region1 = new Region(0, (short) 0, 0, (short) 6);//参数1：行号 参数2：起始列号 参数3：行号 参数4：终止列号
//此方法在POI3.8中已经被废弃，建议使用下面一个
或者用

CellRangeAddress region1 = new CellRangeAddress(rowNumber, rowNumber, (short) 0, (short) 11);

//参数1：起始行 参数2：终止行 参数3：起始列 参数4：终止列
但应注意两个构造方法的参数不是一样的，具体使用哪个取决于POI的不同版本。


sheet.addMergedRegion(region1);
```

## 四、上传至 OSS 等对象存储

作用：`上传 stream流 至 OSS`

```java
public String uploadByStream(String fileId, ObjectMetadata metadata, InputStream in) {
        String ossUrl = null;
        OSSClient ossClient = new OSSClient(ENDPOINT, ACCESS_KEY_ID, ACCESS_KEY_SECRET);
        ossClientMap.put(fileId, ossClient);
        try {
            putUploadProgress(fileId, 0);
            PutObjectRequest req = new PutObjectRequest(OssConnectionUtil.getBucketName(), fileId, in, metadata);
            req.setProgressListener(new UploadProgressListener(req));
            PutObjectResult putResult = ossClient.putObject(req);
            if (putResult.getETag() != null) {
                ossUrl =  getOssURL(ossClient, fileId);
            }
            return ossUrl;
        } catch(ClientException ce) {
            return null;
        } catch (Exception e) {
            return null;
        } finally {
            ossClient.shutdown();
            ossClientMap.remove(fileId);
        }
    }
```
