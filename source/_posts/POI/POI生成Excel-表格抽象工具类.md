# POI生成Excel工具类

该工具类中，包含：创建表格（带默认样式）、自定义样式（通过重写 setDefaultStyle和setFirstCellStyle）、合并单元格、宽度自适应等。

```java
/**
 * Excel操作工具类
 */
public class ExcelTools {

    /**
     * 默认的样式， 垂直水平居中、默认换行
     */
    private XSSFCellStyle defaultStyle;

    /**
     * 默认的行首样式
     */
    private XSSFCellStyle firstCellStyle;

    private static SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd hh:mm");

    public XSSFWorkbook createWorkbook() {
        XSSFWorkbook workbook = new XSSFWorkbook();

        XSSFFont defaultFont = workbook.createFont();
        defaultFont.setColor(IndexedColors.BLACK.getIndex());
        defaultFont.setFontHeight(11);
        defaultFont.setFontName("微软雅黑 Light");

        XSSFCellStyle defaultStyle = workbook.createCellStyle();
        defaultStyle.setWrapText(true);
        defaultStyle.setAlignment(HorizontalAlignment.CENTER);
        defaultStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        defaultStyle.setFont(defaultFont);

        XSSFFont font = workbook.createFont();
        font.setColor(IndexedColors.WHITE.getIndex());
        font.setFontHeight(11);
        font.setFontName("微软雅黑 Light");

        XSSFCellStyle firstCellStyle = workbook.createCellStyle();
        firstCellStyle.setAlignment(HorizontalAlignment.CENTER);
        firstCellStyle.setVerticalAlignment(VerticalAlignment.CENTER);
        firstCellStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        firstCellStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        firstCellStyle.setFont(font);
        firstCellStyle.setBorderBottom(BorderStyle.THIN);
        firstCellStyle.setBottomBorderColor(IndexedColors.LIGHT_BLUE.getIndex());
        firstCellStyle.setLeftBorderColor(IndexedColors.LIGHT_BLUE.getIndex());
        firstCellStyle.setRightBorderColor(IndexedColors.LIGHT_BLUE.getIndex());

        setDefaultStyle(defaultStyle);
        setFirstCellStyle(firstCellStyle);

        return workbook;
    }

    // 创建页
    public XSSFSheet createSheet(XSSFWorkbook workbook, String sheetName) {
        return workbook.createSheet(sheetName);
    }

    /**
     * 创建表格
     *
     * @param sheet           页 @NotNull
     * @param titles          首行标题 @NotNull
     * @param content         填充的内容 @NotNull
     * @param rowNumber       开始的行号 @NotNull
     * @param colNumber       开始的列号 @NotNull
     * @param customRowHeight 自定义行高
     * @param cellRangeList   需要合并的单元格
     * @param customRowStyle  自定义样式,key 对应行，value 对应样式
     * @param customColWidth  自定义宽度自适应
     */
    @SuppressWarnings("unchecked")
    public void createTable(XSSFSheet sheet, String[] titles, Object[][] content,
                            Integer rowNumber, Integer colNumber,
                            boolean customRowHeight,
                            List<CellRange> cellRangeList,
                            Map<Integer, XSSFCellStyle> customRowStyle,
                            Map<Integer, Integer> customColWidth) {
        // 首行填充
        setFirstRow(sheet, titles, getFirstCellStyle(), rowNumber, colNumber);
        rowNumber++;
        // 设置单元格宽度
        autoWidth(sheet, titles, colNumber, customColWidth);
        // 填充数据
        for (int i = 0; i < content.length; i++) {
            Object[] rowContent = content[i];
            XSSFRow row = sheet.getRow(rowNumber + i) == null ? sheet.createRow(rowNumber + i) : sheet.getRow(rowNumber + i);
            row.setHeight(customRowHeight ? (short) 800 : -1);
            XSSFCellStyle cellStyle = customRowStyle == null || customRowStyle.get(rowNumber + i) == null ?
                    getDefaultStyle() : customRowStyle.get(rowNumber + i);
            for (int j = 0; j < rowContent.length; j++) {
                Object colContent = rowContent[j];
                XSSFCell cell = row.createCell(colNumber + j);
                // 如果是基本数据类型，则转换为字符串填充; 日期转换输出; List转String输出
                if (colContent instanceof Integer || colContent instanceof Double
                        || colContent instanceof String || colContent instanceof Float
                        || colContent instanceof Character || colContent instanceof Byte
                        || colContent instanceof Short || colContent instanceof Boolean
                        || colContent instanceof Long)
                    cell.setCellValue(String.valueOf(colContent));
                else if (colContent instanceof Date)
                    cell.setCellValue(sdf.format(colContent));
                else if (colContent instanceof Iterable)
                    cell.setCellValue(new XSSFRichTextString(String.join(" \n ", (Iterable) colContent)));
                else
                    cell.setCellValue(" - ");
                // 单元格样式
                cell.setCellStyle(cellStyle);
            }
        }

        // 合并单元格
        mergeRegion(sheet, cellRangeList);
    }

    /**
     * 合并单元格
     */
    public static void mergeRegion(XSSFSheet sheet, List<CellRange> cellRangeList) {
        if (cellRangeList == null) return;
        for (CellRange cellRange : cellRangeList) {
            sheet.addMergedRegion(new CellRangeAddress(cellRange.getFirstRow(), cellRange.getLastRow(), cellRange.getFirstCol(), cellRange.getLastCol()));
        }
    }

    /**
     * 无需定制样式和宽度的表格
     */
    public void createTable(XSSFSheet sheet, String[] titles, Object[][] content,
                            Integer rowNumber, Integer colNumber) {
        createTable(sheet, titles, content, rowNumber, colNumber, true, null, null, null);
    }


    /**
     * 填充首行数据
     * 默认按照标题的长度设置单元格宽度
     *
     * @param sheet       页
     * @param cellTitles  标题
     * @param headerStyle 首行样式
     * @param rowNum      起始行
     * @param colNum      起始列
     */
    public static void setFirstRow(XSSFSheet sheet, String[] cellTitles, XSSFCellStyle headerStyle, int rowNum, int colNum) {
        final XSSFRow row = sheet.getRow(rowNum) == null ? sheet.createRow(rowNum) : sheet.getRow(rowNum);
        row.setHeight((short) 800);

        for (String cellName : cellTitles) {
            final Cell cell = row.createCell(colNum++);
            cell.setCellValue(cellName);
            cell.setCellStyle(headerStyle);
        }
    }

    /**
     * 宽度自适应
     *
     * @param sheet       页
     * @param strings     行首
     * @param colNumber   起始列
     * @param customWidth 自定义宽度
     */
    public static void autoWidth(Sheet sheet, String[] strings, Integer colNumber, Map<Integer, Integer> customWidth) {
        for (int s = 0; s < strings.length; s++) {
            int col = colNumber + s;
            if (customWidth != null && customWidth.keySet().contains(col)) {
                sheet.setColumnWidth(col, customWidth.get(col));
            } else {
                sheet.autoSizeColumn(col, true);
                sheet.setColumnWidth(col, sheet.getColumnWidth(col) * 17 / 10);
            }
        }
    }

    public XSSFCellStyle getDefaultStyle() {
        return defaultStyle;
    }

    public void setDefaultStyle(XSSFCellStyle defaultStyle) {
        this.defaultStyle = defaultStyle;
    }

    public XSSFCellStyle getFirstCellStyle() {
        return firstCellStyle;
    }

    public void setFirstCellStyle(XSSFCellStyle firstCellStyle) {
        this.firstCellStyle = firstCellStyle;
    }
}
```

合并单元格的对象：

```java
/**
 * 需要合并的单元格
 */
@Setter
@Getter
public class CellRange {
    private Integer firstRow;
    private Integer lastRow;
    private Integer firstCol;
    private Integer lastCol;

    private CellRange(Integer firstRow, Integer lastRow, Integer firstCol, Integer lastCol) {
        this.firstRow = firstRow;
        this.lastRow = lastRow;
        this.firstCol = firstCol;
        this.lastCol = lastCol;
    }

    public static CellRange build(Integer firstRow, Integer lastRow, Integer firstCol, Integer lastCol) {
        return new CellRange(firstRow, lastRow, firstCol, lastCol);
    }
}
```

