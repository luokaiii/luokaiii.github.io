```java
public static void main(String[] args) throws Exception {

?```
    XWPFDocument document = new XWPFDocument();

    XWPFParagraph paragraph = document.createParagraph();
    XWPFRun run =  paragraph.createRun();
    run.setText("Callout shape over text: Lorem ipsum semit dolor Lorem ipsum semit dolor Lorem ipsum semit dolor Lorem ipsum semit dolor Lorem ipsum semit dolor Lorem ipsum semit dolor Lorem ipsum semit dolor Lorem ipsum semit dolor Lorem ipsum semit dolor Lorem ipsum semit dolor Lorem ipsum semit dolor Lorem ipsum semit dolor Lorem ipsum semit dolor Lorem ipsum semit dolor Lorem ipsum semit dolor Lorem ipsum semit dolor Lorem ipsum semit dolor.");

    appendCalloutShape(run, "200pt", "0", "1in", "1in", "black", "#00FF00", "The callout\ntext...", false);

    paragraph = document.createParagraph();
    paragraph = document.createParagraph();
    paragraph = document.createParagraph();
    paragraph = document.createParagraph();
    run =  paragraph.createRun();
    run.setText("Callout shape:");

    appendCalloutShape(run, "1in", "-150px", "100px", "150px", "#0000FF", "yellow", "The callout\ntext...", true);
?```

?```
    FileOutputStream out = new FileOutputStream("C:\\Users\\user\\Desktop\\converter\\数据.docx");
    document.write(out);
    out.close();
    document.close();
}

public static void appendCalloutShape(XWPFRun run, String left, String top, String width, String height,
                                      String strokecolor, String fillcolor, String calloutext, boolean hashandles) throws Exception {
    CTGroup ctGroup = CTGroup.Factory.newInstance();

    CTShape ctShape = ctGroup.addNewShape();
    ctShape.setCoordsize("21600,21600");
    if (hashandles) { //is not Libreoffice Writer compatible
        ctShape.setAdj("" + 21600*1/3 + ",21600");
        CTFormulas cTFormulas = ctShape.addNewFormulas();
        cTFormulas.addNewF().setEqn("val #0");
        cTFormulas.addNewF().setEqn("val #1");
        ctShape.setPath2("m 1,1 l 1," + 21600*2/3 + ", " + 21600*1/3 + "," + 21600*2/3 + ", @0,@1, " + 21600*2/3 + "," + 21600*2/3 + ", 21600," + 21600*2/3 + ", 21600,1 x e");
        ctShape.addNewHandles().addNewH().setPosition("#0,#1");
    } else { // is Libreoffice Writer compatible
        ctShape.setPath2("m 1,1 l 1," + 21600*2/3 + ", " + 21600*1/3 + "," + 21600*2/3 + ", " + 21600*1/3 + ",21600, " + 21600*2/3 + "," + 21600*2/3 + ", 21600," + 21600*2/3 + ", 21600,1 x e");
    }

    ctShape.addNewPath().setTextboxrect("1,1,21600," + 21600*2/3);

    ctShape.setStyle("position:absolute;margin-left:" + left + ";margin-top:" + top + ";width:" + width + ";height:" + height + ";z-index:251659264;visibility:visible;rotation:0;");
    ctShape.setStrokecolor(strokecolor);
    ctShape.setFillcolor(fillcolor);

    CTTextbox cTTextbox = ctShape.addNewTextbox();

    CTTxbxContent ctTxbxContent = cTTextbox.addNewTxbxContent();
    XWPFParagraph textboxparagraph = new XWPFParagraph(ctTxbxContent.addNewP(), run.getDocument());
    XWPFRun textboxrun = null;
    String[] lines = calloutext.split("\n");
    for (int i = 0; i < lines.length; i++) {
        textboxrun = textboxparagraph.createRun();
        textboxrun.setText(lines[i]);
        textboxrun.addBreak();
    }

    Node ctGroupNode = ctGroup.getDomNode();
    CTPicture ctPicture = CTPicture.Factory.parse(ctGroupNode);
    CTR cTR = run.getCTR();
    cTR.addNewPict();
    cTR.setPictArray(0, ctPicture);
}
?```

https://stackoverflow.com/questions/53834805/how-to-create-a-wedgerectcallout-shape-in-word-using-apache-poi
```



```java

// 文本框中的內容很麻煩
        XmlObject object = paragraph.getCTP().getRArray(1);
        XmlCursor cursor = object.newCursor();

        // 可以打印XmlObject对象来查看当前xml文件内容
        // 也可通过XmlCursor的getName和getTextValue方法查看当前所在Node及其值
        // 我的第一个要修改的内容所在node为:注意index
        // <xml-fragment> -> <w:pict> -> <v:shape> -> <v:textbox> ->
        // <w:txbxContent> -> <w:p> -> <w:r> -> <w:t>
        cursor.toChild(1);
        cursor.toChild(0);
        cursor.toChild(3);
        cursor.toChild(0);

        cursor.toChild(0);
        cursor.toChild(3);
        cursor.toChild(1);
        cursor.setTextValue("First");

        // 我的第二个要修改的内容所在node为：注意index
        // <w:txbxContent> -> <w:p> -> <w:r> -> <w:t>
        cursor.toParent();
        cursor.toParent();
        cursor.toParent();
        cursor.toChild(1);
        cursor.toChild(3);
        cursor.toChild(1);
        cursor.setTextValue("Second");
```

通过 freemarker + flying-saucer-pdf ，将数据渲染到 html，再将html转成 pdf。问题在于：只能支持 宋体和黑体 两种字体