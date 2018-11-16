---
title: POI之PPT转image,SmartArt解决方法
date: 2018-11-16 15:56:03
tags:
 - java
categories: 
 - Java成神之路
---
# POI之PPT转image,SmartArt解决方法

关于使用 POI 将 PPT 转为 图片，随便就能查到以下代码。

```java
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFSlide;

public class PpttoPNG {
   
   public static void main(String args[]) throws IOException {
      
      //creating an empty presentation
      File file = new File("addingimage.pptx");
      XMLSlideShow ppt = new XMLSlideShow(new FileInputStream(file));
      
      //getting the dimensions and size of the slide 
      Dimension pgsize = ppt.getPageSize();
      XSLFSlide[] slide = ppt.getSlides();
      
      for (int i = 0; i < slide.length; i++) {
         BufferedImage img = new BufferedImage(
            pgsize.width, pgsize.height,BufferedImage.TYPE_INT_RGB);
         Graphics2D graphics = img.createGraphics();

         //clear the drawing area
         graphics.setPaint(Color.white);
         graphics.fill(new Rectangle2D.Float(0, 0, pgsize.width, pgsize.height));

         //render
         slide[i].draw(graphics);
      }
      
      //creating an image file as output
      FileOutputStream out = new FileOutputStream("ppt_image.png");
      javax.imageio.ImageIO.write(img, "png", out);
      ppt.write(out);
      
      System.out.println("Image successfully created");
      out.close();	
   }
}
```

但是问题显然不会这么简单，经过实验发现。

通过POI导出的图片，出现以下几个问题：

1. 无法读取 SmartArt
2. 艺术字只能显示为普通文本
3. 图形阴影无法显示
4. 部分自定义几何图形显示异常

然后就出现了这种状况：
`PPT文件的内容：` ![PPT文件的内容](https://upload-images.jianshu.io/upload_images/13603359-ddc7e137e2ff31cf.png?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)
`导出的内容(这是什么鬼)：` ![image.png](https://upload-images.jianshu.io/upload_images/13603359-51403ce29c688f11.png)

然后上网查解决方法：

![stack overflow](https://images.gitee.com/uploads/images/2018/1116/111529_aa242626_1872936.png "屏幕截图.png")

![无解又是什么鬼](https://upload-images.jianshu.io/upload_images/13603359-0b2c169e961ec894.png)

## 解决方法

无解，所以我们抛弃了 POI，改用其他方式导出。

### 1.改用java调用Python

如果你不会python，可以将下面的py代码放到项目下，通过 `jython` 解释器来执行。

```python
import comtypes.client as client
import os

def init_powerpoint():
    powerpoint = client.CreateObject("Powerpoint.Application")
    powerpoint.Visible = 1
    return powerpoint

def ppt_to_pdf(powerpoint, inputFileName, outputFileName, formatType=32):
    if outputFileName[-3:] != 'pdf':
        outputFileName = outputFileName[0:-4] + "pdf"
    deck = powerpoint.Presentations.Open(inputFileName)
    deck.SaveAs(outputFileName, formatType)  # formatType = 32 for ppt to pdf
    deck.SaveAs(inputFileName.rsplit('.')[0] + '.jpg', 17)
    deck.Close()

def convert_files_in_folder(powerpoint, folder):
    files = os.listdir(folder)
    pptfiles = [f for f in files if f.endswith((".ppt", ".pptx"))]
    for pptfile in pptfiles:
        fullpath = os.path.join(cwd, pptfile)
        ppt_to_pdf(powerpoint, fullpath, fullpath)

if __name__ == "__main__":
    powerpoint = init_powerpoint()
    cwd = os.getcwd()
    convert_files_in_folder(powerpoint, cwd)
    powerpoint.Quit()
```

这种方法应该是最适合的了，导出的图片接近完美。

### 2.使用第三方服务

阿里云提供了一个“智能媒体管理”的服务，其中包含了许多的文档转换的API。省事

![收费标准](https://upload-images.jianshu.io/upload_images/13603359-0ebbce1f64d3e639.png)

![转换服务](https://upload-images.jianshu.io/upload_images/13603359-ab529c4e928c38f5.png)