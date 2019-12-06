package com.zls.jenkinsplugin.controller;

import com.zls.jenkinsplugin.config.JenkinsConfig;
import com.zls.jenkinsplugin.constants.FileTypeEnum;
import com.zls.jenkinsplugin.entity.BuildInfo;
import com.zls.jenkinsplugin.entity.Result;
import com.zls.jenkinsplugin.service.DingtalkMsgService;
import com.zls.jenkinsplugin.common.JenkinsFilePath;
import com.zls.jenkinsplugin.service.message.MarkDownMessage;
import com.zls.jenkinsplugin.util.*;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;

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
    Environment environment;

    @Autowired
    private DingtalkMsgService dingtalkMsgService;
    @Autowired
    private JenkinsFilePath jenkinsFilePath;


    @ApiOperation(value = "发送信息到钉钉", notes = "发送信息到钉钉")
    @ResponseBody
    @Async
    @GetMapping("/sendMsg2Ddingtalk")
    public Result toDingtalkMsg(@RequestParam(name = "project") String project,
                                @RequestParam(name = "buildId", required = false) String buildId) throws Exception {
        BuildInfo buildInfo = new BuildInfo();
        log.info("主线程睡眠{}秒", threadSleep);
        Thread.sleep(threadSleep);
        // 校验结束
        // 获取构建版本号
        buildId = StringUtil.isBlank(buildId) ?  dingtalkMsgService.getLastBuilId(project) : buildId;
        buildInfo.setProject(project);
        buildInfo.setBuildId(buildId);

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
        buildInfo.setConsoleLogPicLocalUrl(JenkinsConfig.getLocalhost() +":"+ environment.getProperty("local.server.port") + "/dingtalk/console/" + project + "/" + buildId);

        // 从gitlab上获取commit信息
        GitUtil.setCommitInfoById(project, buildInfo.getCommitId(), buildInfo);

        dingtalkMsgService.sendMsg2Dingtalk(buildInfo, new MarkDownMessage());
        }

        return new Result(200);
    }

    @ApiOperation(value = "获取构建console图片", notes = "获取构建console图片")
    @RequestMapping(value = "/console/{project}/{buildId}", method = RequestMethod.GET, produces = MediaType.IMAGE_JPEG_VALUE)
    @ResponseBody
    public byte[] consolePic(@PathVariable("project") String project,
                             @PathVariable("buildId") String buildId) throws IOException {
        byte[] bytes = new byte[0];
        FileInputStream inputStream = null;
        try {
            File file = new File("./console-log-pic/" + project + "-" + buildId + ".jpg");
            inputStream = new FileInputStream(file);
            bytes = new byte[inputStream.available()];
            inputStream.read(bytes, 0, inputStream.available());
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            inputStream.close();
        }
        return bytes;
    }
}
