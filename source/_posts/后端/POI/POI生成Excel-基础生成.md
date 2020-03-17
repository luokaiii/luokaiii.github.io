---
title: 使用POI导出Excel
date: 2018-09-01 14:46:03
tags:
  - POI
  - hide
categories:
  - 后端
  - POI
---

## 一、引入 POI4.0 的包

```xml
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi</artifactId>
    <version>4.0.1</version>
</dependency>
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>4.0.1</version>
</dependency>
```

## 二、普通的 Excel 生成

```java
@Test
public void generate() {
    String filePath = "C:\\Users\\user\\Desktop\\first.xlsx";
     try(XSSFWorkbook workbook = new XSSFWorkbook();
            FileOutputStream outputStream = new FileOutputStream(filePath)){
        // 创建一个 10*5 的表格
        for (int i = 0; i < 10; i++) {
            XSSFRow row = sheet.createRow(i);
            for (int j = 0; j < 5; j++) {
                XSSFCell cell = row.createCell(j);
                cell.setCellValue(String.format("第%s行，第%s列", i + 1, j + 1));
            }
        }
     }
}
```

结果如下：

![正常导出的Excel](https://images.gitee.com/uploads/images/2019/0121/161334_ee08a1e8_1872936.png)

> 这里返回的结果可能挤在一起了，这是因为我们没有设置单元格的宽高。

## 三、加上行首、样式

我们先取首行，填充行首，并为其设置样式（如居中、加粗等）。

```java
@Test
public void generate() throws IOException {
    String filePath = "C:\\Users\\user\\Desktop\\first.xlsx";
    try(XSSFWorkbook workbook = new XSSFWorkbook();
        FileOutputStream outputStream = new FileOutputStream(filePath)){
        XSSFSheet sheet = workbook.createSheet("普通表");

		// 自定义样式：居中、蓝色、加粗
        final XSSFFont font = workbook.createFont();
        font.setBold(true);
        font.setColor(IndexedColors.BLUE.getIndex());
        XSSFCellStyle cellStyle = workbook.createCellStyle();
        cellStyle.setFont(font);
        cellStyle.setAlignment(HorizontalAlignment.CENTER);

		// 设置首行
        String[] titles = new String[]{"行首1","行首2","行首3","行首4","行首5"};
        XSSFRow first = sheet.createRow(0);
        for (int i = 0; i < titles.length; i++) {
            XSSFCell cell = first.createCell(i);
            cell.setCellValue(titles[i]);
            cell.setCellStyle(cellStyle);
        }

		// 填充数据
        for (int i = 1; i < 10; i++) {
            XSSFRow row = sheet.createRow(i);
            for (int j = 0; j < 5; j++) {
                XSSFCell cell = row.createCell(j);
                cell.setCellValue(String.format("第%s行，第%s列", i + 1, j + 1));
            }
        }

        // 设置宽度自适应
        for (int o = 0; o < 5; o++) {
           sheet.autoSizeColumn(o, true);
           sheet.setColumnWidth(o, sheet.getColumnWidth(o) * 17 / 10);
        }
        workbook.write(outputStream);
    }
}
```

效果如下：

![带样式的Excel](https://images.gitee.com/uploads/images/2019/0121/174309_564e9d53_1872936.png)

具体单元格的样式，我们可以在 `cellStyle` 中设置，如果需要多种样式组合的话，可以通过 `XSSFWorkbook` 创建多个，在指定位置进行设置。

> 通过以上代码可以看出，POI 操作 Excel 其实就是在操作 Row(行) 和 Col(列)，其数据可以抽象为一个 `二维数组`，只需要对二维数组进行遍历，即可渲染 Excel 数据。

具体的方法工具类，[贴在这里](www.baidu.com)
