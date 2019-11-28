package com.zls.jenkinsplugin.service.message;

import com.dingtalk.api.request.OapiRobotSendRequest;
import com.zls.jenkinsplugin.entity.BuildInfo;

/**
 * 消息基类
 * @author zhangliansheng
 * @date 2019/11/27
 */
public interface Message {
    /**
     * 获取Request对象
     */
    OapiRobotSendRequest getRequest(BuildInfo msg, String jenkinsHome, String consoleLogUrl) throws Exception;

}
