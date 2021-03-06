package com.zls.jenkinsplugin.service.message;

import com.dingtalk.api.request.OapiRobotSendRequest;
import com.zls.jenkinsplugin.entity.BuildInfo;
import com.zls.jenkinsplugin.util.GitUtil;

public class TextMessage implements Message {
    @Override
    public OapiRobotSendRequest getRequest(BuildInfo msg, String jenkinsHome, String consoleLogUrl) throws Exception{
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("text");
        OapiRobotSendRequest.Text text = new OapiRobotSendRequest.Text();
        String commitInfo = GitUtil.setCommitInfoById(msg.getProject(), jenkinsHome, msg.getCommitId());
        String result = msg.getSuccess()? "成功" : "失败";
        String content = "Jenkins构建完成\n " +
                String.format("%s  #%s  构建%s\n", msg.getProject(), msg.getBuildId(), result) +
                String.format("测试用例： 总数：%s，失败：%s，错误：%s，跳过：%s \n", msg.getTestTotal(),
                        msg.getTestFailTotal(), msg.getTestErrorTotal(), msg.getTestSkipTotal()) +
                String.format("覆盖率报告： 行：%s，类：%s，方法：%s，分支：%s，指令：%s \n", msg.getCoverageLine(), msg.getCoverageClass(),
                        msg.getCoverageMethod(), msg.getCoverageBranch(), msg.getCoverageInstruction()) +
                String.format("构建日志链接：%s/%s/%s\n", consoleLogUrl, msg.getProject(), msg.getBuildId()) +
                String.format("最后一次提交信息: %s\n", commitInfo)
                ;

        text.setContent(content);
        request.setText(text);

        return request;
    }
}
