package com.zls.jenkinsplugin.util;

import com.alibaba.fastjson.JSONObject;
import com.zls.jenkinsplugin.common.ExecShellScript;
import com.zls.jenkinsplugin.entity.BuildInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * git 工具类
 * @author zhangliansheng
 * @date 2019/11/26
 */
@Slf4j
public class GitUtil {

    /**
     * 获取提交信息
     * @author zhangliansheng
     * @date 2019/11/26
     */
    public static String setCommitInfoById(String project, String jenkinsHome, String commitId){
        String cmd = "git log --pretty=format:\"作者:%an  时间:%cr  commit信息:%s\" {commitId} -1";
        cmd = cmd.replace("{commitId}", commitId);
        File dir = new File(String.format("%s/workspace/%s/",jenkinsHome, project));
        return ExecShellScript.execCmd(cmd, dir);
    }

    public static void setCommitInfoById(String project, String commitId, BuildInfo buildInfo){
        String projectId = getProjectId(project);
        String url = String.format("https://gitlab.zhoupu123.com/api/v4/projects/%s/repository/commits/%s/?private_token=x5CFUiEeMuDBVxXBmUVj"
        , projectId, commitId);

        HttpClient client = new HttpClient();
        try {

            client.getParams().setAuthenticationPreemptive(true);
            GetMethod getMethod = new GetMethod(url);
            getMethod.addRequestHeader(new Header("Content-Type", "application/json;charset=gb2312"));

            client.getHttpConnectionManager().getParams().setSoTimeout(120 * 1000);
            client.getHttpConnectionManager().getParams().setConnectionTimeout(60000);
            int status = client.executeMethod(getMethod);
            if(status != 200){
                log.error("获取gitlab commit信息失败, 状态码: status: {}", status);
                throw new Exception(String.format("获取gitlab commit信息失败, 状态码: status: %d", status));
            }
            String responseStr = getMethod.getResponseBodyAsString();
            responseStr = new String(getMethod.getResponseBody(), "UTF-8");
            JSONObject jsonObject = JSONObject.parseObject(responseStr);
            buildInfo.setCommitAuthor(jsonObject.getString("committer_name"));
            SimpleDateFormat oldSdf =
                    new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS");
            SimpleDateFormat newSdf = new SimpleDateFormat("dd号hh点mm分");
            String commitDateStr = jsonObject.getString("committed_date")
                    .replace("T"," ")
                    .replace("+08:00", "");
            Date commitDate = oldSdf.parse(commitDateStr);

            buildInfo.setCommitDate(newSdf.format(commitDate));
            buildInfo.setCommitEmail(jsonObject.getString("committer_email"));
            buildInfo.setCommitTitle(jsonObject.getString("title"));
            buildInfo.setCommitMessage(jsonObject.getString("message"));
            buildInfo.setHasCommitInfo(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String getProjectId(String project) {
        Map<String, String> projectMap = new HashMap(){{
            put("saas", "8");
            put("cattle", "101");
        }};
        return projectMap.get(project.toLowerCase());
    }
}

