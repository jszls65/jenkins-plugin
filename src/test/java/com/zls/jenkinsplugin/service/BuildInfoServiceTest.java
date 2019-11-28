package com.zls.jenkinsplugin.service;

import com.zls.jenkinsplugin.Application;
import com.zls.jenkinsplugin.entity.BuildInfo;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@Slf4j
class BuildInfoServiceTest {
    @Autowired
    private DingtalkMsgService dingtalkMsgService;
    @Test
    void sendMsg2Dingtalk() {
        BuildInfo msg = new BuildInfo();
        msg.setProject("cattle");
        msg.setBuildId("33");
        msg.setCodeBranch("dev");
        msg.setUserId("admin");
        msg.setResult("success");
        msg.setStartTime("2019-11-03 18:03");
        msg.setDuration("300");
        msg.setTestTotal("3");
        msg.setTestFailTotal("2");
        msg.setTestErrorTotal("0");
        msg.setTestSkipTotal("0");
        msg.setCoverageClass("7");
        msg.setCoverageMethod("2");
        msg.setCoverageLine("2");
        msg.setCoverageBranch("1");
        msg.setCoverageInstruction("4");

    }
}