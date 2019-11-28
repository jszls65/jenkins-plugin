package com.zls.jenkinsplugin.common;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class JenkinsFilePath {

    @Value("${jenkins.home}")
    private String jenkinsHome;

    /**
     * 获取build.xml路径
     *
     * @param
     * @return
     * @author zhangliansheng
     * @date 2019/11/3
     */
    public String getBuildXmlPath(String project, String buildId) {
        return String.format(jenkinsHome + "/jobs/%s/builds/%s/build.xml", project, buildId);
    }

    /**
     * 获取log日志文件路径
     * @author zhangliansheng
     * @date 2019/11/17
     */
    public String getLogFilePath(String project, String buildId){
        return String.format(jenkinsHome + "/jobs/%s/builds/%s/log", project, buildId);
    }



}
