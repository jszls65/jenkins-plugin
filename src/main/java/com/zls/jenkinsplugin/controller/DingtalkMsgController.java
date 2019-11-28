package com.zls.jenkinsplugin.controller;

import com.zls.jenkinsplugin.entity.BuildInfo;
import com.zls.jenkinsplugin.entity.Result;
import com.zls.jenkinsplugin.service.DingtalkMsgService;
import com.zls.jenkinsplugin.common.JenkinsFilePath;
import com.zls.jenkinsplugin.service.message.MarkDownMessage;
import com.zls.jenkinsplugin.util.QiniuUtil;
import com.zls.jenkinsplugin.util.Text2ImgBase64Util;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.*;
import java.util.*;
@Api(tags = "Jenkins构建消息发送至钉钉")
@Controller
@RequestMapping("/dingtalk")
@Slf4j
public class DingtalkMsgController {

    @Value("${jenkins.home}")
    private String jenkinsHome;
    @Value("${jenkins.threadSleep}")
    private Long threadSleep;

    @Autowired
    private DingtalkMsgService dingtalkMsgService;
    @Autowired
    private JenkinsFilePath jenkinsFilePath;


    @ApiOperation(value = "发送信息到钉钉", notes = "发送信息到钉钉")
    @ApiImplicitParam(name = "project", value = "项目名称: cattle", paramType = "query", required = true, dataType = "string")
    @ResponseBody
    @Async
    @GetMapping("/toMsg")
    public Result toDingtalkMsg(@RequestParam("project") String project) throws Exception{
        BuildInfo buildInfo = new BuildInfo();
        log.info("主线程睡眠{}秒", threadSleep);
        Thread.sleep(threadSleep);
        // 校验结束
        // 获取jenkins构建结果对象
        Map<String, String> permalinksMap = dingtalkMsgService.getPermalinksMap(project);
        // 获取构建版本号
        String buildId = permalinksMap.get("lastCompletedBuild");
        String logPath = jenkinsFilePath.getLogFilePath(project, buildId);
        //判断构建成功还是失败
        if(permalinksMap.get("lastCompletedBuild").equals(permalinksMap.get("lastSuccessfulBuild"))){
            //本次构建成功
            buildInfo.setResult("成功");

            dingtalkMsgService.setTestInfo2Msg(logPath, buildInfo);
        }else{
            //本次构建失败
            buildInfo.setResult("失败");
            dingtalkMsgService.setExcepitonInfo2MsgFromLogFile(logPath, buildInfo);
            // 根据consolelog生成jpg图片
            String fileName = buildInfo.getProject() + "-" + buildInfo.getBuildId() + ".jpg";
            String filePath = "console-log-pic/" + fileName;
            Text2ImgBase64Util.createImage(filePath, buildInfo.getConsoleLogLines());
            // 上传七牛云
            String url = QiniuUtil.upload(filePath, fileName);
            buildInfo.setConsoleLogPicUrl(url);
        }
        // 从build.xml中获取内容：1.构建人 2.开始时间， 3.耗时
        String buildXmlPath = jenkinsFilePath.getBuildXmlPath(project, buildId);
        buildInfo.setProject(project);
        buildInfo.setBuildId(buildId);
        dingtalkMsgService.setBuildInfo2Msg(buildXmlPath, buildInfo);
        System.out.println(buildInfo.toString());
        dingtalkMsgService.sendMsg2Dingtalk(buildInfo, new MarkDownMessage());
        return new Result(200);
    }
    @ResponseBody
    @GetMapping("/{project}/{buildId}")
    public Result getConsoleLog(@PathVariable("project") String project,
                                @PathVariable("buildId") String buildId){

        String logFilePath = jenkinsFilePath.getLogFilePath(project, buildId);
        FileReader fr = null;
        BufferedReader br = null;
        List<String> lines = new ArrayList<>();
        try {
            fr = new FileReader(logFilePath);
            br = new BufferedReader(fr);
            String line = "";
            while ((line  = br.readLine()) != null){
                lines.add(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try {
                if(br != null){
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if(fr != null){
                    fr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
        return Result.ok(200, lines) ;
    }
}
