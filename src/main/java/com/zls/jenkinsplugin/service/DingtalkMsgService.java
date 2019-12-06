package com.zls.jenkinsplugin.service;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;
import com.zls.jenkinsplugin.config.JenkinsConfig;
import com.zls.jenkinsplugin.constants.FileTypeEnum;
import com.zls.jenkinsplugin.entity.BuildInfo;
import com.zls.jenkinsplugin.service.message.Message;
import com.zls.jenkinsplugin.util.HttpClientUtil;
import com.zls.jenkinsplugin.util.StringUtil;
import com.zls.jenkinsplugin.util.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
@Slf4j
public class DingtalkMsgService {

    // 取配置参数
    @Value("${jenkins.url}")
    private String jenkinsUrl;
    @Value("${jenkins.home}")
    private String jenkinsHome;
    @Value("${jenkins.logUrl}")
    private String consoleLogUrl;
    @Value("${dingtalk.roboturl}")
    private String dingtalkUrl;
    @Value("${dingtalk.token}")
    private String dingtalkToken;

    /**
     * 解析文件获取PermalinksMap
     * @param project
     * @return
     * @author zhangliansheng
     * @date 2019/11/2
     */
    public Map<String, String> getPermalinksMap(String project) {
        List<String> fields = Arrays.asList("lastCompletedBuild", "lastFailedBuild", "lastStableBuild",
                "lastSuccessfulBuild", "lastUnstableBuild", "lastUnsuccessfulBuild");
        String path = String.format("%s/jobs/%s/builds/permalinks", jenkinsHome, project);
        FileReader fr = null;
        BufferedReader bf = null;
        HashMap permalinksMap = new HashMap();
        try {
            fr = new FileReader(path);
            bf = new BufferedReader(fr);
            String str;
            // 按行读取字符串
            while ((str = bf.readLine()) != null) {
                for (String field : fields) {
                    if (str.startsWith(field)) {
                        permalinksMap.put(field, str.split(" ")[1].trim());
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (bf != null) {
                try {
                    bf.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return permalinksMap;
    }



    /**
     * 根据log文件中的内容设置覆盖率信息和单元测试信息
     *
     * @param
     * @return
     * @author zhangliansheng
     * @date 2019/11/3
     */
    public void setBuildInfoFromConsoleLogFile(String logPath, BuildInfo buildInfo) {
        // 设置覆盖率信息
        File logFile = new File(logPath);
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(logFile);
            br = new BufferedReader(fr);
            String line = "";
            buildInfo.setSuccess(true);
            if(CollectionUtils.isEmpty(buildInfo.getConsoleLogLines())){
                buildInfo.setConsoleLogLines(new ArrayList<String>());
            }
            while ((line = br.readLine()) != null) {

                if(isIgnoreLine(line)){
                    continue;
                }
                //Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
                if (line.contains("Tests run")) {
                    Map<String, String> testCase = StringUtil.formatStrToMap(line, 0);
                    buildInfo.setHasTestCase(true);
                    buildInfo.setTestTotal(testCase.get("Tests run"));
                    buildInfo.setTestErrorTotal(testCase.get("Errors"));
                    buildInfo.setTestFailTotal(testCase.get("Failures"));
                    buildInfo.setTestSkipTotal(testCase.get("Skipped"));
                }
                //Overall coverage: class: 7, method: 2, line: 2, branch: 1, instruction: 5
                if (line.contains("Overall coverage")) {
                    Map<String, String> testCase = StringUtil.formatStrToMap(line, "class");
                    buildInfo.setHasCoverage(true);
                    buildInfo.setCoverageClass(testCase.get("class"));
                    buildInfo.setCoverageMethod(testCase.get("method"));
                    buildInfo.setCoverageLine(testCase.get("line"));
                    buildInfo.setCoverageBranch(testCase.get("branch"));
                    buildInfo.setCoverageInstruction(testCase.get("instruction"));
                }

                // Checking out Revision b40a0f2f136e8a19e4bece56a5e51313971195dc (refs/remotes/origin/in-tomcat)
                if(line.contains("Checking out Revision")){
                    buildInfo.setHasCommitInfo(true);
                    String[] lineSplitStr = line.split(" ");
                    if(lineSplitStr.length >=4){
                        buildInfo.setCommitId(lineSplitStr[3]);
                    }
                    if(lineSplitStr.length>=5){
                        String branchFullInfo = lineSplitStr[4];
                        String branchName =
                                branchFullInfo.substring(
                                        branchFullInfo.lastIndexOf("/") +1, branchFullInfo.indexOf(")")
                                );
                        buildInfo.setCommitBranch(branchName);
                    }
                }

                if(line.contains("Finished: FAILURE") || line.contains("BUILD FAILURE")){
                    buildInfo.setSuccess(false);
                }

                // 日志行写入列表中
                buildInfo.getConsoleLogLines().add(line);
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null) {
                    br.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 判断是否忽略该行
     */
    private boolean isIgnoreLine(String line) {
        if(StringUtils.isEmpty(line) || StringUtils.isEmpty(line.trim())){
            return true;
        }
        line = line.trim();
        return line.contains("[INFO] Downloading")
                || line.contains("[INFO] Downloaded")
                || line.contains("[INFO]");
    }

    /**
     * 发送消息给钉钉群机器人
     *
     * @param
     * @return
     * @author zhangliansheng
     * @date 2019/11/3
     */
    public void sendMsg2Dingtalk(BuildInfo msg, Message message) throws Exception{
        String url = String.format("%s?access_token=%s", dingtalkUrl, dingtalkToken);
        DingTalkClient client = new DefaultDingTalkClient(url);
        try {
            OapiRobotSendResponse response = client.execute(message.getRequest(msg, jenkinsHome, consoleLogUrl));
        } catch (ApiException e) {
            e.printStackTrace();
        }
    }

    public String getLastBuilId(String project) {
        HttpClient client = HttpClientUtil.getHttpClient();
        String result = "";
        try {
            PostMethod postMethod = new PostMethod(JenkinsConfig.getUrl() + "/job/"+ project +"/buildHistory/ajax");
            postMethod.addRequestHeader(new Header("Content-Type", "application/json;charset=utf-8"));

            client.getHttpConnectionManager().getParams().setSoTimeout(120 * 1000);
            client.getHttpConnectionManager().getParams().setConnectionTimeout(60000);
            int status = client.executeMethod(postMethod);
            if(status != 200){
                log.error("获取Jenkins构建历史, 失败: http status:{}", status);
                return result;
            }
            String responseString = postMethod.getResponseBodyAsString();
            if(StringUtil.isBlank(responseString)){
                return result;
            }
            String pattern = "/job/"+ project +"/-?[1-9]\\d*";
            Pattern r = Pattern.compile(pattern);
            Matcher m = r.matcher(responseString);
            Set<String> findLines = new TreeSet<>();
            while(m.find()){
                String findStr = m.group();
                if(findStr.endsWith("/")){
                    findStr = findStr.substring(0, findStr.length()-1);
                }
                if(StringUtil.isNotBlank(findStr) && findStr.lastIndexOf("/") != -1){
                    findLines.add(findStr.substring(findStr.lastIndexOf("/")+1));
                }
            }
            int size = findLines.size();
            int index = 0;
            for (String findLine : findLines) {
                if(index == size -1){
                    result = findLine;
                }
                index++;
            }
        } catch (IOException e) {
            log.error("获取Jenkins构建历史, 失败: {}", e);
        }finally {

        }
        if(StringUtil.isBlank(result)){
            log.error("获取最新构建Id失败");
        }
        return result;
    }

    /**
     * 生成log文件
     * @author zhangliansheng
     * @date 2019/11/30
     */
    public String genLogFile(String project, String buildId) {
        HttpClient client = HttpClientUtil.getHttpClient();
        String filePath = "console-log-pic/"+ project +"-" + buildId + ".txt";
        BufferedWriter bufferedWriter = null;
        try {
            GetMethod getMethod =
                    new GetMethod(JenkinsConfig.getUrl() + "/job/"+ project +"/"+ buildId +"/consoleText");
            getMethod.addRequestHeader(new Header("Content-Type", "application/json;charset=utf-8"));

            client.getHttpConnectionManager().getParams().setSoTimeout(120 * 1000);
            client.getHttpConnectionManager().getParams().setConnectionTimeout(60000);
            // 执行请求
            int status = client.executeMethod(getMethod);
            if(status != 200){
                log.error("获取Jenkins构建历史, 失败: http status:{}", status);
                throw new Exception("");
            }
            // 获取响应结果
            String responseString = getMethod.getResponseBodyAsString();
            if(StringUtil.isBlank(responseString)){
                throw new Exception("");
            }
            // 将响应结果写到文件中
            bufferedWriter = new BufferedWriter(new FileWriter(filePath));
            bufferedWriter.write(responseString);
            bufferedWriter.flush();
        } catch (IOException e) {
            log.error("获取Jenkins构建历史, 失败: {}", e);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if(bufferedWriter != null){
                    bufferedWriter.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return filePath;
    }


}
