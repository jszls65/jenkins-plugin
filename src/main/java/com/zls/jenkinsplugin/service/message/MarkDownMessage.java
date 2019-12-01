package com.zls.jenkinsplugin.service.message;

import com.dingtalk.api.request.OapiRobotSendRequest;
import com.zls.jenkinsplugin.entity.BuildInfo;
import com.zls.jenkinsplugin.util.GitUtil;
import org.springframework.stereotype.Component;

@Component
public class MarkDownMessage implements Message {
    @Override
    public OapiRobotSendRequest getRequest(BuildInfo buildInfo, String jenkinsHome, String consoleLogUrl) throws Exception{
        OapiRobotSendRequest request = new OapiRobotSendRequest();
        request.setMsgtype("markdown");
        OapiRobotSendRequest.Markdown markdown = new OapiRobotSendRequest.Markdown();
        markdown.setTitle("Jenkins构建完成");
        StringBuilder contentSb = new StringBuilder();
        String result = buildInfo.getSuccess()? "成功" : "失败";
        contentSb.append(String.format("%s  #%s  构建%s", buildInfo.getProject(), buildInfo.getBuildId(), result)).append("\n");
        contentSb.append(String.format("开始时间：%s  共耗时：%s ms", buildInfo.getStartTime(), buildInfo.getDuration())).append("\n");
        if(buildInfo.getHasTestCase()){
            contentSb.append(String.format("测试用例： 总数：%s，失败：%s，错误：%s，跳过：%s ", buildInfo.getTestTotal(),
                    buildInfo.getTestFailTotal(), buildInfo.getTestErrorTotal(), buildInfo.getTestSkipTotal())).append("\n");
        }

        if(buildInfo.getHasCoverage()){
            contentSb.append(String.format("覆盖率报告： 行：%s，类：%s，方法：%s，分支：%s，指令：%s", buildInfo.getCoverageLine(), buildInfo.getCoverageClass(),
                    buildInfo.getCoverageMethod(), buildInfo.getCoverageBranch(), buildInfo.getCoverageInstruction())).append("\n");
        }
        if(buildInfo.getHasCommitInfo()){
            String commitInfo = String.format("%s于%s往%s分支上提交了代码\n提交信息: %s\n",
                    buildInfo.getCommitAuthor(), buildInfo.getCommitDate(),
                    buildInfo.getCommitBranch(),
                    buildInfo.getCommitTitle());
            contentSb.append("\n").append(String.format("最后一次提交信息: %s", commitInfo)).append("\n");
        }

        contentSb.append(String.format("![screenshot](%s)\n", buildInfo.getConsoleLogPicUrl()));

        markdown.setText(contentSb.toString());
        request.setMarkdown(markdown);

        return request;
    }
}
