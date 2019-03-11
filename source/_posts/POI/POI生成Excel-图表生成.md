---
title: POI+XChart图形报告
date: 2018-09-01 14:46:03
tags:
 - POI
categories: 
 - Java
---
# POI+XChart图形报告

## 一、基础示例，写入Excel

生成一个图形，并将其写入 Excel 中。

```java
@Test
public void generate() throws IOException {
    String filePath = "C:\\Users\\user\\Desktop\\first.xlsx";
    double[] xData = new double[]{0.0, 1.0, 2.0};
    double[] yData = new double[]{2.0, 1.0, 0.0};

    // Create Chart
    XYChart chart = QuickChart.getChart("Sample Chart", "X", "Y", "y(x)", xData, yData);
    // 将图表转换为byte数组
    byte[] bytes = BitmapEncoder.getBitmapBytes(chart, BitmapEncoder.BitmapFormat.PNG);

    try (XSSFWorkbook workbook = new XSSFWorkbook();
         ByteArrayInputStream inputStream = new ByteArrayInputStream(bytes);
         FileOutputStream outputStream = new FileOutputStream(filePath)) {
        // 创建Excel
        XSSFSheet sheet = workbook.createSheet("图表");

        // 写入Excel的指定位置
        XSSFDrawing shapes = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = new XSSFClientAnchor(0, 0, 0, 0, 0, 0, 6, 10);
        shapes.createPicture(anchor, sheet.getWorkbook().addPicture(inputStream, HSSFWorkbook.PICTURE_TYPE_PNG));

        // 将Excel写到文件中
        workbook.write(outputStream);
    }
}
```



![生成的基础图片](https://images.gitee.com/uploads/images/2019/0121/180421_34b56169_1872936.png)

>  上面我们演示了如何`将 XChart生成的图片导入 Excel`，下面的部分，都会将生成的图形导出为图片，如果需要导入Excel的话，只需要将chart套入第一章即可。

## 二、将生成的图形导出成为图片

获取随机数的方法也是通用的，这里先给出，具体的数据应该根据业务而定：

```java
/**
     * 获取随机数
     * @param numPoints 随机的数量
     * @return 随机数组
     */
    private double[] getRandomWalk(int numPoints) {

        double[] y = new double[numPoints];
        y[0] = 0;
        for (int i = 1; i < y.length; i++) {
            y[i] = y[i - 1] + Math.random() - .5;
        }
        return y;
    }
```

### 折线图

```java
@Test
public void generate() throws IOException {
    String filePath = "C:\\Users\\user\\Desktop\\first.png";

    XYChart chart = new XYChartBuilder().xAxisTitle("X").yAxisTitle("Y").width(600).height(400).build();
    chart.getStyler().setYAxisMin(-10.0);
    chart.getStyler().setYAxisMax(10.0);
    XYSeries series = chart.addSeries("新绿", null, getRandomWalk(200));
    series.setMarker(SeriesMarkers.NONE);

    BitmapEncoder.saveBitmap(chart, filePath, BitmapEncoder.BitmapFormat.PNG);
}
```

效果如下：

![输入图片说明](https://images.gitee.com/uploads/images/2019/0121/181356_e145341d_1872936.png "屏幕截图.png")

## 面积图

```java
@Test
public void generate() throws IOException {
    String filePath = "C:\\Users\\user\\Desktop\\first.png";

    // Create Chart
    XYChart chart = new XYChartBuilder().width(800).height(600).title(getClass().getSimpleName()).xAxisTitle("X").yAxisTitle("Y").build();

    // Customize Chart
    chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
    chart.getStyler().setAxisTitlesVisible(false);
    chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Area);

    // Series
    chart.addSeries("a", new double[] { 0, 3, 5, 7, 9 }, new double[] { -3, 5, 9, 6, 5 });
    chart.addSeries("b", new double[] { 0, 2, 4, 6, 9 }, new double[] { -1, 6, 4, 0, 4 });
    chart.addSeries("c", new double[] { 0, 1, 3, 8, 9 }, new double[] { -2, -1, 1, 0, 1 });

    BitmapEncoder.saveBitmap(chart, filePath, BitmapEncoder.BitmapFormat.PNG);
}
```

![面积图](https://images.gitee.com/uploads/images/2019/0121/182136_bba89ebf_1872936.png "屏幕截图.png")

## 饼状图

```java
@Test
public void generate() throws IOException {
    String filePath = "C:\\Users\\user\\Desktop\\first.png";

    PieChart chart = new PieChartBuilder().width(800).height(600).title(getClass().getSimpleName()).build();

    // Customize Chart
    Color[] sliceColors = new Color[] { new Color(224, 68, 14), new Color(230, 105, 62), new Color(236, 143, 110), new Color(243, 180, 159), new Color(246, 199, 182) };
    chart.getStyler().setSeriesColors(sliceColors);

    // Series
    chart.addSeries("Gold", 24);
    chart.addSeries("Silver", 21);
    chart.addSeries("Platinum", 39);
    chart.addSeries("Copper", 17);
    chart.addSeries("Zinc", 40);

    BitmapEncoder.saveBitmap(chart, filePath, BitmapEncoder.BitmapFormat.PNG);
}
```

![饼状图](https://koral-home.oss-cn-beijing.aliyuncs.com/bignzhuang.png)

## 散点图

```java
@Test
public void generate() throws IOException {
    String filePath = "C:\\Users\\user\\Desktop\\first.png";

    // Create Chart
    XYChart chart = new XYChartBuilder().width(800).height(600).build();

    // Customize Chart
    chart.getStyler().setDefaultSeriesRenderStyle(XYSeries.XYSeriesRenderStyle.Scatter);
    chart.getStyler().setChartTitleVisible(false);
    chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideSW);
    chart.getStyler().setMarkerSize(16);

    // Series
    List<Double> xData = new LinkedList<Double>();
    List<Double> yData = new LinkedList<Double>();
    Random random = new Random();
    int size = 1000;
    for (int i = 0; i < size; i++) {
        xData.add(random.nextGaussian() / 1000);
        yData.add(-1000000 + random.nextGaussian());
    }
    chart.addSeries("Gaussian Blob", xData, yData);


    BitmapEncoder.saveBitmap(chart, filePath, BitmapEncoder.BitmapFormat.PNG);
}
```



![散点图](https://koral-home.oss-cn-beijing.aliyuncs.com/sandian.png)

## 柱状图

```java
@Test
public void generate() throws IOException {
    String filePath = "C:\\Users\\user\\Desktop\\first.png";

    // Create Chart
    CategoryChart chart = new CategoryChartBuilder().width(800).height(600).title("Score Histogram").xAxisTitle("Score").yAxisTitle("Number").build();

    // Customize Chart
    chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
    chart.getStyler().setHasAnnotations(true);

    // Series
    chart.addSeries("test 1", Arrays.asList(0, 1, 2, 3, 4), Arrays.asList(4, 5, 9, 6, 5));

    BitmapEncoder.saveBitmap(chart, filePath, BitmapEncoder.BitmapFormat.PNG);
}
```

![柱状图](https://koral-home.oss-cn-beijing.aliyuncs.com/zhuzhuang.png)

## 堆叠图

```java
@Test
public void generate() throws IOException {
    String filePath = "C:\\Users\\user\\Desktop\\first.png";

    // Create Chart
    CategoryChart chart = new CategoryChartBuilder().width(800).height(600).title("Score Histogram").xAxisTitle("Mean").yAxisTitle("Count").build();

    // Customize Chart
    chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
    chart.getStyler().setAvailableSpaceFill(.96);
    chart.getStyler().setOverlapped(true);

    // Series
    Histogram histogram1 = new Histogram(getGaussianData(10000), 20, -20, 20);
    Histogram histogram2 = new Histogram(getGaussianData(5000), 20, -20, 20);
    chart.addSeries("histogram 1", histogram1.getxAxisData(), histogram1.getyAxisData());
    chart.addSeries("histogram 2", histogram2.getxAxisData(), histogram2.getyAxisData());

    BitmapEncoder.saveBitmap(chart, filePath, BitmapEncoder.BitmapFormat.PNG);
}

private List<Double> getGaussianData(int count) {
    List<Double> data = new ArrayList<Double>(count);
    Random r = new Random();
    for (int i = 0; i < count; i++) {
        data.add(r.nextGaussian() * 10);
    }
    return data;
}
```

![堆叠图](https://koral-home.oss-cn-beijing.aliyuncs.com/duidie.png)

## 百分比堆叠柱状图

这个其实是堆叠图的变种。

```java
@Test
public void generate() throws IOException {
    String filePath = "C:\\Users\\user\\Desktop\\first.png";

    CategoryChart chart = new CategoryChartBuilder().width(800).height(600).build();
    chart.getStyler().setChartBackgroundColor(Color.WHITE);
    chart.getStyler().setLegendBorderColor(Color.WHITE);
    chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNW);
    chart.getStyler().setAvailableSpaceFill(0.7);
    chart.getStyler().setStacked(true);

    List<String> xData = Arrays.asList("学院A", "学院B", "学院C","学院D","学院E");

    chart.addSeries("得分A", xData, getGaussianData(5));
    chart.addSeries("得分B", xData, getGaussianData(5));
    chart.addSeries("得分C", xData, getGaussianData(5));
    chart.addSeries("得分D", xData, getGaussianData(5));

    BitmapEncoder.saveBitmap(chart, filePath, BitmapEncoder.BitmapFormat.PNG);
}

private List<Double> getGaussianData(int count) {
    List<Double> data = new ArrayList<Double>(count);
    for (int i = 0; i < count; i++) {
        data.add(RandomUtils.nextDouble(0,1) * 10);
    }
    return data;
}
```

![百分比堆叠柱状图](https://koral-home.oss-cn-beijing.aliyuncs.com/baifenbi.png)

参考：

 (1) [XChart Demo](https://knowm.org/open-source/xchart/xchart-example-code/)

 (2) 自己





