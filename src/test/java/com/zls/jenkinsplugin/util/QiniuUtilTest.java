package com.zls.jenkinsplugin.util;

import com.zls.jenkinsplugin.Application;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
class QiniuUtilTest {


    @Test
    void upload() {
        String filePath = "C:\\Workspace\\pic\\zls.jpg";
        QiniuUtil.upload(filePath, "zls1.jpg");

    }

    @Test
    void getFileUrl() {
        /*String fileUrl = QiniuUtil.getFileUrl("zls1.jpg");
        Assert.assertEquals("http://q1lzmqffe.bkt.clouddn.com/zls1.jpg", fileUrl);*/
    }
}