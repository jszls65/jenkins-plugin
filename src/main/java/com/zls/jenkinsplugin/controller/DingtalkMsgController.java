package com.zls.jenkinsplugin.controller;

import com.zls.jenkinsplugin.constants.FileTypeEnum;
import com.zls.jenkinsplugin.entity.BuildInfo;
import com.zls.jenkinsplugin.entity.Result;
import com.zls.jenkinsplugin.service.DingtalkMsgService;
import com.zls.jenkinsplugin.common.JenkinsFilePath;
import com.zls.jenkinsplugin.service.message.MarkDownMessage;
import com.zls.jenkinsplugin.util.FileUtil;
import com.zls.jenkinsplugin.util.GitUtil;
import com.zls.jenkinsplugin.util.QiniuUtil;
import com.zls.jenkinsplugin.util.ImgUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

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
//        Thread.sleep(threadSleep);
        // 校验结束
        // 获取构建版本号
        String buildId = dingtalkMsgService.getLastBuilId(project);

        String logPath = dingtalkMsgService.genLogFile(project, buildId);
        // 设置builInfo
        dingtalkMsgService.setBuildInfoFromConsoleLogFile(logPath, buildInfo);
        //判断构建成功还是失败`
        if(!buildInfo.getSuccess()){
            // 根据consolelog生成jpg图片
            String picFilePath = FileUtil.getFilePath(project, buildId, FileTypeEnum.PIC);
            ImgUtil.createImage(picFilePath, buildInfo.getConsoleLogLines());
            // 上传七牛云
            String url = QiniuUtil.upload(picFilePath, picFilePath);
            buildInfo.setConsoleLogPicUrl(url);

            // 从gitlab上获取commit信息
            GitUtil.setCommitInfoById(project, buildInfo.getCommitId(), buildInfo);
            
            dingtalkMsgService.sendMsg2Dingtalk(buildInfo, new MarkDownMessage());
        }

        return new Result(200);
    }

}
