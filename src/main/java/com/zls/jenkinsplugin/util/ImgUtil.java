package com.zls.jenkinsplugin.util;

import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.List;

/**
 * 文本转图片
 *
 * @author zhangliansheng
 * @date 2019/11/26
 */
public class ImgUtil {
    /**
     * 根据str,font的样式等生成图片
     * @param strArr
     * @throws Exception
     */
    public static void createImage(String filePath, List<String> strArr) throws Exception {
        Font font = new Font("宋体", Font.PLAIN, 22);
        int[] wh = getWidthAndHeight(strArr, font);
        int imageWidth = wh[0];
        int imageHeight = wh[1];

        File outFile = new File(filePath);
        // 创建图片
        BufferedImage image = new BufferedImage(imageWidth, imageHeight,
                BufferedImage.TYPE_INT_BGR);
        Graphics g = image.getGraphics();
        g.setClip(0, 0, imageWidth, imageHeight);
        // 背景色白色
        g.setColor(Color.white);
        g.fillRect(0, 0, imageWidth, imageHeight);
        //  字体颜色黑色
        g.setColor(Color.black);
        // 设置画笔字体
        g.setFont(font);
        // 每张多少行，当到最后一张时判断是否填充满
        int i = 1;
        g.drawString(strArr.get(0), 0, 0);
        boolean startErrorLine = false;
        for (String s : strArr) {
            if(s.startsWith("[ERROR]")){
                startErrorLine = true;
            }
            // 只设置一次
            if(startErrorLine){
                g.setColor(Color.red);
                g.setFont(font);
            }
            g.drawString(s, 0, wh[2] * i);
            i++;
        }
        g.dispose();
        // 输出png图片
        ImageIO.write(image, "jpg", outFile);
    }

    private static int[] getWidthAndHeight(List<String> lines, Font font) {
        int unitHeight = 0;
        int width = 0;
        for (String line : lines) {
            Rectangle2D r = font.getStringBounds(line, new FontRenderContext(
                    AffineTransform.getScaleInstance(1, 1), false, false));
            unitHeight = (int) Math.floor(r.getHeight());
            // 获取整个str用了font样式的宽度这里用四舍五入后+1保证宽度绝对能容纳这个字符串作为图片的宽度
            int lineWidth = (int) Math.round(r.getWidth()) + 1;
            width = width < lineWidth ? lineWidth : width;
        }
        // 把单个字符的高度+3保证高度绝对能容纳字符串作为图片的高度
        int height = unitHeight;
        height = height * lines.size();
        return new int[]{width, height, unitHeight};
    }
}
