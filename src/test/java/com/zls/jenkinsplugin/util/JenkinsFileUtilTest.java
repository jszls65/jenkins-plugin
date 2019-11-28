package com.zls.jenkinsplugin.util;

import com.zls.jenkinsplugin.common.JenkinsFilePath;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
class JenkinsFileUtilTest {

    @Autowired
    private JenkinsFilePath jenkinsFilePath;
    @Test
    void getLogPath() {
        System.out.println(jenkinsFilePath.getBuildXmlPath("1","2"));
    }
}