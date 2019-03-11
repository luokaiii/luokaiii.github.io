# 使用POI结合Excel的行列生成原生散点图(XY图)

## 一、先放最后输出的结果图：

![雷达图](https://koral-home.oss-cn-beijing.aliyuncs.com/blog/ScatterChart.png)

## 二、代码

```java
@Test
public void createScatterChart() throws IOException {
    XSSFWorkbook wb = new XSSFWorkbook();
    XSSFSheet sheet = wb.createSheet("散点图");

    Row row;
    Cell cell;
    for (int r = 0; r < 105; r++) {
        row = sheet.createRow(r);
        cell = row.createCell(0);
        cell.setCellValue("S" + r);
        cell = row.createCell(1);
        cell.setCellValue(RandomUtils.nextInt(1,100));
    }

    XSSFDrawing drawing = sheet.createDrawingPatriarch();
    ClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 0, 0, 21, 40);

    XSSFChart chart = drawing.createChart(anchor);

    chart.setTitleText("预选赛项目得分分布图");
    chart.setAutoTitleDeleted(false);

    CTChart ctChart = chart.getCTChart();
    ctChart.addNewPlotVisOnly().setVal(true);
    ctChart.addNewDispBlanksAs().setVal(STDispBlanksAs.Enum.forInt(2));
    ctChart.addNewShowDLblsOverMax().setVal(false);

    // 创建一个散点图
    CTPlotArea ctPlotArea = ctChart.getPlotArea();

    CTScatterChart scatterChart = ctPlotArea.addNewScatterChart();
    scatterChart.addNewScatterStyle().setVal(STScatterStyle.LINE_MARKER);
    scatterChart.addNewVaryColors().setVal(false); // 不允许自定义颜色
    scatterChart.addNewAxId().setVal(123456);
    scatterChart.addNewAxId().setVal(123457);

    CTCatAx ctCatAx = ctPlotArea.addNewCatAx();
    ctCatAx.addNewAxId().setVal(123456);
    CTScaling ctScaling = ctCatAx.addNewScaling();
    ctScaling.addNewOrientation().setVal(MIN_MAX);
    ctCatAx.addNewDelete().setVal(false);
    ctCatAx.addNewAxPos().setVal(STAxPos.B);
    ctCatAx.addNewCrossAx().setVal(123457);
    ctCatAx.addNewTickLblPos().setVal(NEXT_TO);

    // 设置Y坐标
    CTValAx ctValAx = ctPlotArea.addNewValAx();
    ctValAx.addNewAxId().setVal(123457);
    CTScaling ctScaling1 = ctValAx.addNewScaling();
    ctScaling1.addNewOrientation().setVal(MIN_MAX);
    ctValAx.addNewDelete().setVal(false);
    ctValAx.addNewAxPos().setVal(STAxPos.B);
    ctValAx.addNewCrossAx().setVal(123456);
    // Y轴的对比线
    CTShapeProperties ctShapeProperties = ctValAx.addNewMajorGridlines().addNewSpPr();
    CTLineProperties ctLineProperties = ctShapeProperties.addNewLn();
    ctLineProperties.setW(9525);
    ctLineProperties.setCap(STLineCap.Enum.forInt(3));
    ctLineProperties.setCmpd(STCompoundLine.Enum.forInt(1));
    ctLineProperties.setAlgn(STPenAlignment.Enum.forInt(1));
    // 不显示Y轴上的坐标刻度线
    ctValAx.addNewMajorTickMark().setVal(STTickMark.NONE);
    ctValAx.addNewMinorTickMark().setVal(STTickMark.NONE);
    ctValAx.addNewTickLblPos().setVal(NEXT_TO);

    // 设置散点图内的信息
    CTScatterSer ctScatterSer = scatterChart.addNewSer();
    ctScatterSer.addNewIdx().setVal(0);
    ctScatterSer.addNewOrder().setVal(0);
    // 去掉连接线
    ctPlotArea.getScatterChartArray(0).getSerArray(0).addNewSpPr().addNewLn().addNewNoFill();

    // 设置散点图各图例的显示
    CTDLbls ctdLbls = scatterChart.addNewDLbls();
    ctdLbls.addNewShowVal().setVal(true);
    ctdLbls.addNewShowLegendKey().setVal(false);
    ctdLbls.addNewShowSerName().setVal(false);
    ctdLbls.addNewShowCatName().setVal(false);
    ctdLbls.addNewShowPercent().setVal(false);
    ctdLbls.addNewShowBubbleSize().setVal(false);
    // 设置标记的样式
    CTMarker ctMarker = ctScatterSer.addNewMarker();
    ctMarker.addNewSymbol().setVal(STMarkerStyle.Enum.forInt(3));
    ctMarker.addNewSize().setVal((short) 5);
    CTShapeProperties ctShapeProperties1 = ctMarker.addNewSpPr();
    ctShapeProperties1.addNewSolidFill().addNewSchemeClr().setVal(STSchemeColorVal.Enum.forInt(5));
    CTLineProperties ctLineProperties1 = ctShapeProperties1.addNewLn();
    ctLineProperties1.setW(9525);
    ctLineProperties1.addNewSolidFill().addNewSchemeClr().setVal(STSchemeColorVal.Enum.forInt(5));

    CTAxDataSource ctAxDataSource = ctScatterSer.addNewXVal();
    CTStrRef ctStrRef = ctAxDataSource.addNewStrRef();
    ctStrRef.setF("散点图!$A$1:$A$100");
    CTNumDataSource ctNumDataSource = ctScatterSer.addNewYVal();
    CTNumRef ctNumRef = ctNumDataSource.addNewNumRef();
    ctNumRef.setF("散点图!$B$1:$B$100");

    System.out.println(ctChart);

    FileOutputStream fileOut = new FileOutputStream("C:\\Users\\user\\Desktop\\out.xlsx");
    wb.write(fileOut);
    fileOut.close();
}
```

## 三、XML结构

以上生成的图表，归根结底是属于XML的一种，通过 `Chart` 的继承关系可以看到，它其实是 `XmlObject`的一种实现。

```xml
<xml-fragment xmlns:char="http://schemas.openxmlformats.org/drawingml/2006/chart" xmlns:main="http://schemas.openxmlformats.org/drawingml/2006/main">
  <char:title>
    <char:tx>
      <char:rich>
        <main:bodyPr/>
        <main:p>
          <main:r>
            <main:t>预选赛项目得分分布图</main:t>
          </main:r>
        </main:p>
      </char:rich>
    </char:tx>
  </char:title>
  <char:autoTitleDeleted val="false"/>
  <char:plotArea>
    <char:layout/>
    <char:scatterChart>
      <char:scatterStyle val="lineMarker"/>
      <char:varyColors val="false"/>
      <char:ser>
        <char:idx val="0"/>
        <char:order val="0"/>
        <char:spPr>
          <main:ln>
            <main:noFill/>
          </main:ln>
        </char:spPr>
        <char:marker>
          <char:symbol val="diamond"/>
          <char:size val="5"/>
          <char:spPr>
            <main:solidFill>
              <main:schemeClr val="accent1"/>
            </main:solidFill>
            <main:ln w="9525">
              <main:solidFill>
                <main:schemeClr val="accent1"/>
              </main:solidFill>
            </main:ln>
          </char:spPr>
        </char:marker>
        <char:xVal>
          <char:strRef>
            <char:f>散点图!$A$1:$A$100</char:f>
          </char:strRef>
        </char:xVal>
        <char:yVal>
          <char:numRef>
            <char:f>散点图!$B$1:$B$100</char:f>
          </char:numRef>
        </char:yVal>
      </char:ser>
      <char:dLbls>
        <char:showLegendKey val="false"/>
        <char:showVal val="true"/>
        <char:showCatName val="false"/>
        <char:showSerName val="false"/>
        <char:showPercent val="false"/>
        <char:showBubbleSize val="false"/>
      </char:dLbls>
      <char:axId val="123456"/>
      <char:axId val="123457"/>
    </char:scatterChart>
    <char:catAx>
      <char:axId val="123456"/>
      <char:scaling>
        <char:orientation val="minMax"/>
      </char:scaling>
      <char:delete val="false"/>
      <char:axPos val="b"/>
      <char:tickLblPos val="nextTo"/>
      <char:crossAx val="123457"/>
    </char:catAx>
    <char:valAx>
      <char:axId val="123457"/>
      <char:scaling>
        <char:orientation val="minMax"/>
      </char:scaling>
      <char:delete val="false"/>
      <char:axPos val="b"/>
      <char:majorGridlines>
        <char:spPr>
          <main:ln w="9525" cap="flat" cmpd="sng" algn="ctr"/>
        </char:spPr>
      </char:majorGridlines>
      <char:majorTickMark val="none"/>
      <char:minorTickMark val="none"/>
      <char:tickLblPos val="nextTo"/>
      <char:crossAx val="123456"/>
    </char:valAx>
  </char:plotArea>
  <char:plotVisOnly val="true"/>
  <char:plotVisOnly val="true"/>
  <char:dispBlanksAs val="gap"/>
  <char:showDLblsOverMax val="false"/>
</xml-fragment>
```

## 四、工具类，我放在 [POI生成原生图表-工具类](www.baidu.com) 中