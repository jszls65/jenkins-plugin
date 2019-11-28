package com.zls.jenkinsplugin.util;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.net.URL;

public class HtmlToImage {
    protected static void generateOutput() throws Exception {

        //load the webpage into the editor
        //JEditorPane ed = new JEditorPane(new URL("http://www.google.com"));
        JEditorPane ed = new JEditorPane(new URL("https://www.baidu.com/"));
        ed.setSize(2000,800);

        //create a new image
        BufferedImage image = new BufferedImage(ed.getWidth(), ed.getHeight(),
                BufferedImage.TYPE_INT_ARGB);

        //paint the editor onto the image
        SwingUtilities.paintComponent(image.createGraphics(),
                ed,
                new JPanel(),
                0, 0, image.getWidth(), image.getHeight());
        //save the image to file
        ImageIO.write((RenderedImage)image, "png", new File("html.png"));
    }
    public static void main(String[] args) {
        try {
            generateOutput();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
