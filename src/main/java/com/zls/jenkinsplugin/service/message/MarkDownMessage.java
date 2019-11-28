package com.zls.jenkinsplugin.service.message;

import com.dingtalk.api.request.OapiRobotSendRequest;
import com.zls.jenkinsplugin.entity.BuildInfo;
import com.zls.jenkinsplugin.util.GitUtil;
import org.springframework.stereotype.Component;

@Component
public class MarkDownMessage implements Message {
    @Override
    public OapiRobotSendRequest getRequest(BuildInfo msg, String jenkinsHome, String consoleLogUrl) throws Exception{
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("markdown");
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle("Jenkins构建完成");
        String commitInfo = GitUtil.getCommitInfoById(msg.getProject(), jenkinsHome, msg.getCommitId());
        StringBuilder contentSb = new StringBuilder();

        contentSb.append(String.format("%s  #%s  构建%s", msg.getProject(), msg.getBuildId(), msg.getResult())).append("\n");
        contentSb.append(String.format("开始时间：%s  共耗时：%s ms", msg.getStartTime(), msg.getDuration())).append("\n");
        if(msg.getHasTestCase()){
            contentSb.append(String.format("测试用例： 总数：%s，失败：%s，错误：%s，跳过：%s ", msg.getTestTotal(),
                    msg.getTestFailTotal(), msg.getTestErrorTotal(), msg.getTestSkipTotal())).append("\n");
        }

        if(msg.getHasCoverage()){
            contentSb.append(String.format("覆盖率报告： 行：%s，类：%s，方法：%s，分支：%s，指令：%s", msg.getCoverageLine(), msg.getCoverageClass(),
                    msg.getCoverageMethod(), msg.getCoverageBranch(), msg.getCoverageInstruction())).append("\n");
        }

        if("失败".equals(msg.getResult())){
            contentSb.append("\n").append(String.format("最后一次提交信息: %s", commitInfo)).append("\n");
            contentSb.append(String.format("![screenshot](%s)\n", msg.getConsoleLogPicUrl()));
        }

        markdown.setText(contentSb.toString());
        request.setMarkdown(markdown);

        return request;
    }
}
