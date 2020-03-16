---
title: 使用POI结合Excel的行列生成雷达图
date: 2018-09-01 14:46:03
tags:
 - POI
categories: 
 - Java
---
# 使用POI结合Excel的行列生成雷达图

## 一、先放最后输出的结果图：

![雷达图](https://koral-home.oss-cn-beijing.aliyuncs.com/blog/RadarChart.png)

## 二、代码

```java
    @Test
    public void createRadar() throws IOException {
        final String sheetName = "RadarChart";
        try (XSSFWorkbook workbook = new XSSFWorkbook()) {
            XSSFSheet sheet = workbook.createSheet(sheetName);

            for (int i = 0; i < 16; i++) {
                XSSFRow row = sheet.createRow(i);
                XSSFCell cell = row.createCell(0);
                cell.setCellValue(i + "行");
                XSSFCell cell1 = row.createCell(1);
                cell1.setCellValue(RandomUtils.nextInt(0, 20));
            }

            XSSFDrawing drawing = sheet.createDrawingPatriarch();
            XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, 2, 0, 7, 16);
            XSSFChart chart = drawing.createChart(anchor);

            CTChart ctChart = chart.getCTChart();
            ctChart.addNewAutoTitleDeleted().setVal(false);
//            ctChart.addNewPlotVisOnly().setVal(true);
//            ctChart.addNewDispBlanksAs().setVal(STDispBlanksAs.GAP);
//            ctChart.addNewShowDLblsOverMax().setVal(false);

            CTPlotArea ctPlotArea = ctChart.addNewPlotArea();
            ctPlotArea.addNewLayout();
//            CTShapeProperties ctShapeProperties = ctPlotArea.addNewSpPr();
//            ctShapeProperties.addNewNoFill();
//            ctShapeProperties.addNewLn().addNewNoFill();
//            ctShapeProperties.addNewEffectLst();

            CTRadarChart ctRadarChart = ctPlotArea.addNewRadarChart();
            // 这两个值应该对应着catAx和valAx
            ctRadarChart.addNewAxId().setVal(123456);
            ctRadarChart.addNewAxId().setVal(123457);
            // 分类标签等是否显示
            CTDLbls ctdLbls = ctRadarChart.addNewDLbls();
            ctdLbls.addNewShowLegendKey().setVal(false);
            ctdLbls.addNewShowVal().setVal(false);
            ctdLbls.addNewShowCatName().setVal(false);
            ctdLbls.addNewShowSerName().setVal(false);
            ctdLbls.addNewShowPercent().setVal(false);
            ctdLbls.addNewShowBubbleSize().setVal(false);
            // 不允许自定义颜色、以及标记的形状
            ctRadarChart.addNewRadarStyle().setVal(STRadarStyle.MARKER);
            ctRadarChart.addNewVaryColors().setVal(false);

            CTRadarSer ctRadarSer = ctRadarChart.addNewSer();
            ctRadarSer.addNewIdx().setVal(0);
            ctRadarSer.addNewOrder().setVal(0);
            CTLineProperties ctLineProperties = ctRadarSer.addNewSpPr().addNewLn();
            ctLineProperties.addNewRound();
            ctLineProperties.addNewSolidFill().addNewSchemeClr().setVal(STSchemeColorVal.ACCENT_1);
            // 渲染数据
            ctRadarSer.addNewCat().addNewStrRef().setF(sheetName + "!$A$1:$A$6");
            ctRadarSer.addNewVal().addNewNumRef().setF(sheetName + "!$B$2:$B$6");

            CTCatAx ctCatAx = ctPlotArea.addNewCatAx();
            ctCatAx.addNewAxId().setVal(123456);
            ctCatAx.addNewScaling().addNewOrientation().setVal(MIN_MAX);
            ctCatAx.addNewDelete().setVal(false);
            ctCatAx.addNewAxPos().setVal(STAxPos.B);
            ctCatAx.addNewCrossAx().setVal(123457);
            ctCatAx.addNewMajorTickMark().setVal(STTickMark.NONE);
            ctCatAx.addNewMinorTickMark().setVal(STTickMark.NONE);
            ctCatAx.addNewTickLblPos().setVal(NEXT_TO);
//            ctCatAx.addNewCrosses().setVal(STCrosses.AUTO_ZERO);
//            ctCatAx.addNewAuto().setVal(true);
//            ctCatAx.addNewLblAlgn().setVal(STLblAlgn.CTR);
//            ctCatAx.addNewLblOffset().setVal(100);
//            ctCatAx.addNewNoMultiLvlLbl().setVal(false);
            // spPr
//            CTShapeProperties ctShapeProperties = ctCatAx.addNewSpPr();
//            ctShapeProperties.addNewNoFill();
//            ctShapeProperties.addNewEffectLst();
//            CTLineProperties ctLineProperties1 = ctShapeProperties.addNewLn();
//            ctLineProperties1.setW(9525);
//            ctLineProperties1.setCap(STLineCap.FLAT);
//            ctLineProperties1.setCmpd(STCompoundLine.SNG);
//            ctLineProperties1.setAlgn(STPenAlignment.CTR);
//            CTSchemeColor ctSchemeColor = ctLineProperties1.addNewSolidFill().addNewSchemeClr();
//            ctSchemeColor.setVal(STSchemeColorVal.TX_1);
//            ctSchemeColor.addNewLumMod().setVal(15000);
//            ctSchemeColor.addNewLumOff().setVal(85000);
            // txPr
//            CTTextBody ctTextBody = ctCatAx.addNewTxPr();
//            CTTextCharacterProperties ctTextCharacterProperties = ctTextBody.addNewP().addNewPPr().addNewDefRPr();
//            ctTextCharacterProperties.setU(STTextUnderlineType.NONE);
//            ctTextCharacterProperties.setStrike(STTextStrikeType.NO_STRIKE);
//            ctTextCharacterProperties.setSz(900);
//            ctTextCharacterProperties.setB(false);
//            ctTextCharacterProperties.setI(false);
//            ctTextCharacterProperties.setBaseline(0);
//            ctTextCharacterProperties.setKern(1200);
//            CTSchemeColor ctSchemeColor = ctTextCharacterProperties.addNewSolidFill().addNewSchemeClr();
//            ctSchemeColor.setVal(STSchemeColorVal.TX_1);
//            ctSchemeColor.addNewLumMod().setVal(65000);
//            ctSchemeColor.addNewLumOff().setVal(35000);


            CTValAx ctValAx = ctPlotArea.addNewValAx();
            ctValAx.addNewAxId().setVal(123457);
            ctValAx.addNewScaling().addNewOrientation().setVal(MIN_MAX);
            ctValAx.addNewDelete().setVal(false);
            ctValAx.addNewAxPos().setVal(STAxPos.L);
            ctValAx.addNewCrossAx().setVal(123456);
            ctValAx.addNewMajorTickMark().setVal(STTickMark.NONE);
            ctValAx.addNewMinorTickMark().setVal(STTickMark.NONE);
            ctValAx.addNewTickLblPos().setVal(NEXT_TO);
//            ctValAx.addNewCrosses().setVal(STCrosses.AUTO_ZERO);
//            ctValAx.addNewCrossBetween().setVal(STCrossBetween.BETWEEN);
            // 设置网格线
            CTShapeProperties ctShapeProperties = ctValAx.addNewMajorGridlines().addNewSpPr();
            CTLineProperties ctLineProperties1 = ctShapeProperties.addNewLn();
            ctLineProperties1.setW(9525);
            ctLineProperties1.setCap(STLineCap.FLAT);
            ctLineProperties1.setCmpd(STCompoundLine.SNG);
            ctLineProperties1.setAlgn(STPenAlignment.CTR);
            // 值的纵坐标线，不显示
            ctValAx.addNewSpPr().addNewLn().addNewNoFill();
            // 值的纵坐标值，不显示
            ctValAx.addNewTxPr().addNewP().addNewPPr().addNewDefRPr().addNewNoFill();

            System.out.println(ctChart);

            FileOutputStream outputStream =
                    new FileOutputStream("C:\\Users\\user\\Desktop\\out.xlsx");
            workbook.write(outputStream);
            outputStream.flush();
            outputStream.close();
        }
    }
```

## 三、XML结构

以上生成的图表，归根结底是属于XML的一种，通过 `RadarChart` 的继承关系可以看到，它其实是 `XmlObject`的一种实现。

```xml
<xml-fragment xmlns:char="http://schemas.openxmlformats.org/drawingml/2006/chart" xmlns:main="http://schemas.openxmlformats.org/drawingml/2006/main">
  <char:autoTitleDeleted val="false"/>
  <char:plotArea>
    <char:layout/>
  </char:plotArea>
  <char:plotArea>
    <char:layout/>
    <char:radarChart>
      <char:radarStyle val="marker"/>
      <char:varyColors val="false"/>
      <char:ser>
        <char:idx val="0"/>
        <char:order val="0"/>
        <char:spPr>
          <main:ln>
            <main:solidFill>
              <main:schemeClr val="accent1"/>
            </main:solidFill>
            <main:round/>
          </main:ln>
        </char:spPr>
        <char:cat>
          <char:strRef>
            <char:f>RadarChart!$A$1:$A$6</char:f>
          </char:strRef>
        </char:cat>
        <char:val>
          <char:numRef>
            <char:f>RadarChart!$B$2:$B$6</char:f>
          </char:numRef>
        </char:val>
      </char:ser>
      <char:dLbls>
        <char:showLegendKey val="false"/>
        <char:showVal val="false"/>
        <char:showCatName val="false"/>
        <char:showSerName val="false"/>
        <char:showPercent val="false"/>
        <char:showBubbleSize val="false"/>
      </char:dLbls>
      <char:axId val="123456"/>
      <char:axId val="123457"/>
    </char:radarChart>
    <char:catAx>
      <char:axId val="123456"/>
      <char:scaling>
        <char:orientation val="minMax"/>
      </char:scaling>
      <char:delete val="false"/>
      <char:axPos val="b"/>
      <char:majorTickMark val="none"/>
      <char:minorTickMark val="none"/>
      <char:tickLblPos val="nextTo"/>
      <char:crossAx val="123457"/>
    </char:catAx>
    <char:valAx>
      <char:axId val="123457"/>
      <char:scaling>
        <char:orientation val="minMax"/>
      </char:scaling>
      <char:delete val="false"/>
      <char:axPos val="l"/>
      <char:majorGridlines>
        <char:spPr>
          <main:ln w="9525" cap="flat" cmpd="sng" algn="ctr"/>
        </char:spPr>
      </char:majorGridlines>
      <char:majorTickMark val="none"/>
      <char:minorTickMark val="none"/>
      <char:tickLblPos val="nextTo"/>
      <char:spPr>
        <main:ln>
          <main:noFill/>
        </main:ln>
      </char:spPr>
      <char:txPr>
        <main:p>
          <main:pPr>
            <main:defRPr>
              <main:noFill/>
            </main:defRPr>
          </main:pPr>
        </main:p>
      </char:txPr>
      <char:crossAx val="123456"/>
    </char:valAx>
  </char:plotArea>
  <char:plotVisOnly val="true"/>
</xml-fragment>
```

## 四、工具类，我放在 [POI生成原生图表-工具类](www.baidu.com) 中