package com.zls.jenkinsplugin.entity;

import com.zls.jenkinsplugin.util.StringUtil;
import lombok.Data;

import java.util.List;

@Data
public class BuildInfo {
    //项目名称
    private String project;
    //构建id
    private String buildId;
    //代码分支
    private String codeBranch;

    private String userId;
    //构建结果，成功还是失败
    private Boolean success;
    //开始时间
    private String startTime;
    // 耗时
    private String duration;
    // 测试用例总数
    private String testTotal;
    // 测试用例失败次数
    private String testFailTotal;
    // 测试用例错误次数
    private String testErrorTotal;
    // 测试用例被跳过总数
    private String testSkipTotal;

    //Overall coverage: class: 7, method: 2, line: 2, branch: 1, instruction: 5
    private String coverageClass;
    private String coverageMethod;
    private String coverageLine;
    private String coverageBranch;
    private String coverageInstruction;
    // git提交id
    private String commitId;
    // 提交人的Pinyin name
    private String commitAuthor;
    private String commitEmail;
    private String commitTitle;
    private String commitMessage;
    private String commitDate;
    private String commitBranch;
    private Boolean hasTestCase;
    private Boolean hasCoverage;
    private Boolean hasCommitInfo;
    // 按行保存log日志.
    private List<String> consoleLogLines;
    // 上传到七牛云上的图片链接
    private String consoleLogPicUrl;
    private String remark;

    @Override
    public String toString() {
        return "DingtalkMsg{" +
                "project='" + project + '\'' +
                ", buildId='" + buildId + '\'' +
                ", codeBranch='" + codeBranch + '\'' +
                ", userId='" + userId + '\'' +
                ", success='" + success + '\'' +
                ", startTime='" + startTime + '\'' +
                ", duration=" + duration +
                ", testTotal=" + testTotal +
                ", testFailTotal=" + testFailTotal +
                ", testErrorTotal=" + testErrorTotal +
                ", testSkipTotal=" + testSkipTotal +
                ", coverageClass=" + coverageClass +
                ", coverageMethod=" + coverageMethod +
                ", coverageLine=" + coverageLine +
                ", coverageBranch=" + coverageBranch +
                ", coverageInstruction=" + coverageInstruction +
                '}';
    }

}
