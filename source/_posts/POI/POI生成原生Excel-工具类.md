---
title: POI生成原生Excel-工具类
date: 2018-09-01 14:46:03
tags:
 - POI
categories: 
 - Java
---
# POI生成原生Excel-工具类

```java
package com.pgc.diagnose.utils;

import org.apache.poi.xssf.usermodel.XSSFChart;
import org.apache.poi.xssf.usermodel.XSSFClientAnchor;
import org.apache.poi.xssf.usermodel.XSSFDrawing;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.openxmlformats.schemas.drawingml.x2006.chart.*;
import org.openxmlformats.schemas.drawingml.x2006.main.*;

import java.util.Iterator;
import java.util.List;

import static org.openxmlformats.schemas.drawingml.x2006.chart.STOrientation.MIN_MAX;
import static org.openxmlformats.schemas.drawingml.x2006.chart.STTickLblPos.NEXT_TO;

public class DrawUtils {

    public static void drawRadarChart(XSSFSheet sheet, Position start, Position end,
                                      List<String> dataRef) {
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 100, 50, start.getX(), start.getY(), end.getX(), end.getY());
        XSSFChart chart = drawing.createChart(anchor);

        CTChart ctChart = chart.getCTChart();
        CTPlotArea ctPlotArea = ctChart.addNewPlotArea();

        CTRadarChart ctRadarChart = ctPlotArea.addNewRadarChart();
        // 不允许自定义颜色、以及标记的形状
//        ctRadarChart.addNewRadarStyle().setVal(STRadarStyle.MARKER);
        ctRadarChart.addNewVaryColors().setVal(false);

        setAxIds(ctRadarChart);
        setCatAx(ctPlotArea);
        setRadarValAx(ctPlotArea);
        setDataLabel(ctRadarChart);
        paddingData(ctRadarChart, dataRef);
    }

    /**
     * 散点图
     */
    public static void drawScatterChart(XSSFSheet sheet, Position start, Position end,
                                        List<String> dataRef) {
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 100, 50, start.getX(), start.getY(), end.getX(), end.getY());

        XSSFChart chart = drawing.createChart(anchor);
        chart.setTitleText(sheet.getSheetName());

        CTChart ctChart = chart.getCTChart();
        CTPlotArea ctPlotArea = ctChart.getPlotArea();

        CTScatterChart scatterChart = ctPlotArea.addNewScatterChart();
        scatterChart.addNewVaryColors().setVal(true);

        setAxIds(scatterChart);
        setCatAx(ctPlotArea);
        setScatterValAx(ctPlotArea);
        setDataLabel(scatterChart);
        paddingData(scatterChart, dataRef);
        // 去掉连接线
        ctPlotArea.getScatterChartArray(0).getSerArray(0).addNewSpPr().addNewLn().addNewNoFill();
    }

    /**
     * 折线图
     */
    public static void drawCTLineChart(XSSFSheet sheet, Position start, Position end, List<String> xString, List<String> serTxName,
                                       List<String> dataRef) {
        // todo 折线图换成堆叠图
        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, start.getX(), start.getY(), end.getX(), end.getY());

        XSSFChart chart = drawing.createChart(anchor);

        CTChart ctChart = chart.getCTChart();
        CTPlotArea ctPlotArea = ctChart.getPlotArea();
        CTLineChart ctLineChart = ctPlotArea.addNewLineChart();

        ctLineChart.addNewVaryColors().setVal(true);

        // telling the Chart that it has axis and giving them Ids
        setAxIds(ctLineChart);
        // set cat axis
        setCatAx(ctPlotArea);
        // set val axis
        setValAx(ctPlotArea);
        // legend
        setLegend(ctChart);
        // set data lable
        setDataLabel(ctLineChart);
        // set chart title
        setChartTitle(chart, sheet.getSheetName());

        paddingData(ctLineChart, xString, serTxName, dataRef);
    }

    /*
     * @param position 图表坐标 起始行，起始列，终点行
     *
     * @param xString 横坐标
     *
     * @param serTxName 图形示例
     *
     * @param dataRef 柱状图数据范围 sheetName!$A$1:$A$12
     */
    public static void drawBarChart(XSSFSheet sheet, Position start, Position end, List<String> xString, List<String> serTxName,
                                    List<String> dataRef) {

        XSSFDrawing drawing = sheet.createDrawingPatriarch();
        XSSFClientAnchor anchor = drawing.createAnchor(0, 0, 0, 0, start.getX(), start.getY(), end.getX(), end.getY());

        XSSFChart chart = drawing.createChart(anchor);

        CTChart ctChart = chart.getCTChart();
        CTPlotArea ctPlotArea = ctChart.getPlotArea();
        CTBarChart ctBarChart = ctPlotArea.addNewBarChart();

        ctBarChart.addNewVaryColors().setVal(false);
        ctBarChart.addNewBarDir().setVal(STBarDir.COL);

        // telling the Chart that it has axis and giving them Ids
        setAxIds(ctBarChart);
        // set cat axis
        setCatAx(ctPlotArea);
        // set val axis
        setValAx(ctPlotArea);
        // add legend and set legend position
        setLegend(ctChart);
        // set data lable
        setDataLabel(ctBarChart);
        // set chart title
        setChartTitle(chart, sheet.getSheetName());
        // padding data to chart
        paddingData(ctBarChart, xString, serTxName, dataRef);
    }

    private static void paddingData(CTBarChart ctBarChart, List<String> xString, List<String> serTxName,
                                    List<String> dataRef) {
        Iterator<String> iterator = serTxName.iterator();
        for (int r = 0, len = dataRef.size(); r < len && iterator.hasNext(); r++) {
            CTBarSer ctBarSer = ctBarChart.addNewSer();

            ctBarSer.addNewIdx().setVal(r);
            // set legend value
            setLegend(iterator.next(), ctBarSer.addNewTx());
            // cat ax value
            setChartCatAxLabel(ctBarSer.addNewCat(), xString);
            // value range
            ctBarSer.addNewVal().addNewNumRef().setF(dataRef.get(r));
            // add border to chart
            ctBarSer.addNewSpPr().addNewLn().addNewSolidFill().addNewSrgbClr().setVal(new byte[]{0, 0, 0});
        }
    }

    private static void setLegend(String str, CTSerTx ctSerTx) {
        if (str.contains("$"))
            // set legend by str ref
            ctSerTx.addNewStrRef().setF(str);
        else
            // set legend by str
            ctSerTx.setV(str);
    }

    private static void paddingData(CTLineChart ctLineChart, List<String> xString, List<String> serTxName,
                                    List<String> dataRef) {
        Iterator<String> iterator = serTxName.iterator();
        for (int r = 0, len = dataRef.size(); r < len && iterator.hasNext(); r++) {
            CTLineSer ctLineSer = ctLineChart.addNewSer();
            ctLineSer.addNewIdx().setVal(r);
            setLegend(iterator.next(), ctLineSer.addNewTx());
            setChartCatAxLabel(ctLineSer.addNewCat(), xString);
            ctLineSer.addNewVal().addNewNumRef().setF(dataRef.get(r));
            ctLineSer.addNewSpPr().addNewLn().addNewSolidFill().addNewSrgbClr().setVal(new byte[]{0, 0, 0});
        }
    }

    private static void paddingData(CTScatterChart scatterChart, List<String> dataRef) {
        // 设置散点图内的信息
        CTScatterSer ctScatterSer = scatterChart.addNewSer();
        ctScatterSer.addNewIdx().setVal(0);
        ctScatterSer.addNewOrder().setVal(0);
        // 设置标记的样式
        CTMarker ctMarker = ctScatterSer.addNewMarker();
        ctMarker.addNewSymbol().setVal(STMarkerStyle.Enum.forInt(3));
        ctMarker.addNewSize().setVal((short) 8);
        CTShapeProperties ctShapeProperties1 = ctMarker.addNewSpPr();
        ctShapeProperties1.addNewSolidFill().addNewSchemeClr().setVal(STSchemeColorVal.Enum.forInt(5));
        CTLineProperties ctLineProperties1 = ctShapeProperties1.addNewLn();
        ctLineProperties1.setW(9525);
        ctLineProperties1.addNewSolidFill().addNewSchemeClr().setVal(STSchemeColorVal.Enum.forInt(5));

        CTNumDataSource ctNumDataSource = ctScatterSer.addNewYVal();
        CTNumRef ctNumRef = ctNumDataSource.addNewNumRef();
        ctNumRef.setF(dataRef.get(0));
    }

    private static void paddingData(CTRadarChart ctRadarChart, List<String> dataRef) {
        CTRadarSer ctRadarSer = ctRadarChart.addNewSer();
        ctRadarSer.addNewIdx().setVal(0);
        ctRadarSer.addNewOrder().setVal(0);
        CTLineProperties ctLineProperties = ctRadarSer.addNewSpPr().addNewLn();
        ctLineProperties.addNewRound();
        ctLineProperties.addNewSolidFill().addNewSchemeClr().setVal(STSchemeColorVal.ACCENT_1);
        // 渲染数据
        ctRadarSer.addNewCat().addNewStrRef().setF(dataRef.get(0));
        ctRadarSer.addNewVal().addNewNumRef().setF(dataRef.get(1));
    }

    private static void setChartCatAxLabel(CTAxDataSource cttAxDataSource, List<String> xString) {
        if (xString.size() == 1) {
            cttAxDataSource.addNewStrRef().setF(xString.get(0));
        } else {
            CTStrData ctStrData = cttAxDataSource.addNewStrLit();
            for (int m = 0, xlen = xString.size(); m < xlen; m++) {
                CTStrVal ctStrVal = ctStrData.addNewPt();
                ctStrVal.setIdx((long) m);
                ctStrVal.setV(xString.get(m));
            }
        }
    }

    private static void setDataLabel(CTBarChart ctBarChart) {
        setDLShowOpts(ctBarChart.addNewDLbls());
    }

    private static void setDataLabel(CTLineChart ctLineChart) {
        CTDLbls dlbls = ctLineChart.addNewDLbls();
        setDLShowOpts(dlbls);
        setDLPosition(dlbls, null);
    }

    /**
     * 设置散点图各图例的显示
     */
    private static void setDataLabel(CTScatterChart scatterChart) {
        CTDLbls ctdLbls = scatterChart.addNewDLbls();
        ctdLbls.addNewShowVal().setVal(true);
        ctdLbls.addNewShowLegendKey().setVal(false);
        ctdLbls.addNewShowSerName().setVal(false);
        ctdLbls.addNewShowCatName().setVal(false);
        ctdLbls.addNewShowPercent().setVal(false);
        ctdLbls.addNewShowBubbleSize().setVal(false);
    }

    private static void setDataLabel(CTRadarChart ctRadarChart) {
        // 分类标签等是否显示
        CTDLbls ctdLbls = ctRadarChart.addNewDLbls();
        ctdLbls.addNewShowLegendKey().setVal(false);
        ctdLbls.addNewShowVal().setVal(false);
        ctdLbls.addNewShowCatName().setVal(false);
        ctdLbls.addNewShowSerName().setVal(false);
        ctdLbls.addNewShowPercent().setVal(false);
        ctdLbls.addNewShowBubbleSize().setVal(false);
    }

    private static void setDLPosition(CTDLbls dlbls, STDLblPos.Enum e) {
        if (e == null)
            dlbls.addNewDLblPos().setVal(STDLblPos.T);
        else
            dlbls.addNewDLblPos().setVal(e);
    }

    private static void setDLShowOpts(CTDLbls dlbls) {
        // 添加图形示例的字符串
        dlbls.addNewShowSerName().setVal(false);
        // 添加x轴的坐标字符
        dlbls.addNewShowCatName().setVal(false);
        // 添加图形示例的图
        dlbls.addNewShowLegendKey().setVal(false);
        // 添加x对应y的---
        // dlbls.addNewShowVal().setVal(false);
    }

    private static void setAxIds(CTRadarChart ctRadarChart) {
        ctRadarChart.addNewAxId().setVal(123456);
        ctRadarChart.addNewAxId().setVal(123457);
    }

    private static void setAxIds(CTBarChart ctBarChart) {
        ctBarChart.addNewAxId().setVal(123456);
        ctBarChart.addNewAxId().setVal(123457);
    }

    private static void setAxIds(CTLineChart ctLineChart) {
        ctLineChart.addNewAxId().setVal(123456);
        ctLineChart.addNewAxId().setVal(123457);
    }

    private static void setAxIds(CTScatterChart scatterChart) {
        scatterChart.addNewAxId().setVal(123456);
        scatterChart.addNewAxId().setVal(123457);
    }

    private static void setLegend(CTChart ctChart) {
        CTLegend ctLegend = ctChart.addNewLegend();
        ctLegend.addNewLegendPos().setVal(STLegendPos.B);
        ctLegend.addNewOverlay().setVal(false);
    }

    private static void setCatAx(CTPlotArea ctPlotArea) {
        CTCatAx ctCatAx = ctPlotArea.addNewCatAx();
        ctCatAx.addNewAxId().setVal(123456); // id of the cat axis
        CTScaling ctScaling = ctCatAx.addNewScaling();
        ctScaling.addNewOrientation().setVal(STOrientation.MIN_MAX);
        ctCatAx.addNewDelete().setVal(false);
        ctCatAx.addNewAxPos().setVal(STAxPos.B);
        ctCatAx.addNewCrossAx().setVal(123457); // id of the val axis
        ctCatAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);
    }

    private static void setScatterValAx(CTPlotArea ctPlotArea) {
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
    }

    private static void setRadarValAx(CTPlotArea ctPlotArea) {
        CTValAx ctValAx = ctPlotArea.addNewValAx();
        ctValAx.addNewAxId().setVal(123457);
        ctValAx.addNewScaling().addNewOrientation().setVal(MIN_MAX);
        ctValAx.addNewDelete().setVal(false);
        ctValAx.addNewAxPos().setVal(STAxPos.L);
        ctValAx.addNewCrossAx().setVal(123456);
        ctValAx.addNewMajorTickMark().setVal(STTickMark.NONE);
        ctValAx.addNewMinorTickMark().setVal(STTickMark.NONE);
        ctValAx.addNewTickLblPos().setVal(NEXT_TO);
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
    }

    // 不要y轴的标签，或者y轴尽可能的窄
    private static void setValAx(CTPlotArea ctPlotArea) {
        CTValAx ctValAx = ctPlotArea.addNewValAx();
        ctValAx.addNewAxId().setVal(123457); // id of the val axis
        CTScaling ctScaling = ctValAx.addNewScaling();
        ctScaling.addNewOrientation().setVal(STOrientation.MIN_MAX);
        // 不现实y轴
//        ctValAx.addNewDelete().setVal(true);
        ctValAx.addNewAxPos().setVal(STAxPos.L);
        ctValAx.addNewCrossAx().setVal(123456); // id of the cat axis
        ctValAx.addNewTickLblPos().setVal(STTickLblPos.NEXT_TO);
    }

    // 图标标题
    private static void setChartTitle(XSSFChart xchart, String titleStr) {
        CTChart ctChart = xchart.getCTChart();

        CTTitle title = CTTitle.Factory.newInstance();
        CTTx cttx = title.addNewTx();
        CTStrData sd = CTStrData.Factory.newInstance();
        CTStrVal str = sd.addNewPt();
        str.setIdx(123459);
        str.setV(titleStr);
        cttx.addNewStrRef().setStrCache(sd);

        ctChart.setTitle(title);
    }
}

```

