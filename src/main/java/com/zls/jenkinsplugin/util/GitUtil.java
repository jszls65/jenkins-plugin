package com.zls.jenkinsplugin.util;

import com.zls.jenkinsplugin.common.ExecShellScript;
import com.zls.jenkinsplugin.common.JenkinsFilePath;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import java.io.File;

/**
 * git 工具类
 * @author zhangliansheng
 * @date 2019/11/26
 */
public class GitUtil {

    /**
     * 获取提交信息
     * @author zhangliansheng
     * @date 2019/11/26
     */
    public static String getCommitInfoById(String project, String jenkinsHome, String commitId){
        String cmd = "git log --pretty=format:\"作者:%an  时间:%cr  commit信息:%s\" {commitId} -1";
        cmd = cmd.replace("{commitId}", commitId);
        File dir = new File(String.format("%s/workspace/%s/",jenkinsHome, project));
        return ExecShellScript.execCmd(cmd, dir);
    }
}

