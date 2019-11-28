package com.zls.jenkinsplugin.service;

import com.dingtalk.api.DefaultDingTalkClient;
import com.dingtalk.api.DingTalkClient;
import com.dingtalk.api.response.OapiRobotSendResponse;
import com.taobao.api.ApiException;
import com.zls.jenkinsplugin.entity.BuildInfo;
import com.zls.jenkinsplugin.service.message.Message;
import com.zls.jenkinsplugin.util.StringUtil;
import com.zls.jenkinsplugin.util.XmlUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.dom4j.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

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
        Map<String, String> permalinksMap = new HashMap();
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
     * 根据build xml设置Msg信息
     *
     * @param
     * @return
     * @author zhangliansheng
     * @date 2019/11/3
     */
    public void setBuildInfo2Msg(String path, BuildInfo msg) {
        if (StringUtil.isBlank(path) || msg == null) {
            return;
        }
        try {
            Element root = XmlUtil.getRootElement(path);

            String userId = root.element("actions").element("hudson.model.CauseAction").element("causeBag").element("entry").element("hudson.model.Cause_-UserIdCause")
                    .element("userId").getTextTrim();
            // branch
            String codeBranch = "";
            try{
                codeBranch = root.element("actions").element("hudson.plugins.git.GitTagAction")
                        .element("tags").element("entry").element("string").getTextTrim();
            }catch (Exception e){
                log.error("获取代码分支名称失败, {}", e);
            }
            if (codeBranch.lastIndexOf("/") != -1) {
                codeBranch = codeBranch.substring(codeBranch.lastIndexOf("/") + 1);
            }
            // startTime
            Long timeStamp = Long.valueOf(root.element("startTime").getTextTrim());
            String startTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(timeStamp);

            msg.setUserId(userId);
            msg.setCodeBranch(codeBranch);
            msg.setStartTime(startTime);
            msg.setDuration(root.element("duration").getTextTrim());

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
        }
    }



    /**
     * 根据log文件中的内容设置覆盖率信息和单元测试信息
     *
     * @param
     * @return
     * @author zhangliansheng
     * @date 2019/11/3
     */
    public void setTestInfo2Msg(String logPath, BuildInfo msg) {
        // 设置覆盖率信息
        File logFile = new File(logPath);
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(logFile);
            br = new BufferedReader(fr);
            String line = "";
            while ((line = br.readLine()) != null) {
                //Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
                if (line.contains("Tests run")) {
                    Map<String, String> testCase = StringUtil.formatStrToMap(line, 0);
                    msg.setHasTestCase(Boolean.TRUE);
                    msg.setTestTotal(testCase.get("Tests run"));
                    msg.setTestErrorTotal(testCase.get("Errors"));
                    msg.setTestFailTotal(testCase.get("Failures"));
                    msg.setTestSkipTotal(testCase.get("Skipped"));
                }
                //Overall coverage: class: 7, method: 2, line: 2, branch: 1, instruction: 5
                if (line.contains("Overall coverage")) {
                    Map<String, String> testCase = StringUtil.formatStrToMap(line, "class");
                    msg.setHasCoverage(Boolean.TRUE);
                    msg.setCoverageClass(testCase.get("class"));
                    msg.setCoverageMethod(testCase.get("method"));
                    msg.setCoverageLine(testCase.get("line"));
                    msg.setCoverageBranch(testCase.get("branch"));
                    msg.setCoverageInstruction(testCase.get("instruction"));
                }

                // Checking out Revision b40a0f2f136e8a19e4bece56a5e51313971195dc (refs/remotes/origin/in-tomcat)
                if(line.contains("Checking out Revision")){
                    msg.setHasCommitInfo(Boolean.TRUE);
                    String[] lineSplitStr = line.split(" ");
                    if(lineSplitStr.length >=3){
                        msg.setCommitId(lineSplitStr[3]);
                    }
                }
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

    /**
     * 从log文件中获取异常信息
     * @author zhangliansheng
     * @date 2019/11/27
     */
    public void setExcepitonInfo2MsgFromLogFile(String logPath, BuildInfo buildInfo) {
        // 设置覆盖率信息
        File logFile = new File(logPath);
        FileReader fr = null;
        BufferedReader br = null;
        try {
            fr = new FileReader(logFile);
            br = new BufferedReader(fr);
            String line = "";
            if(CollectionUtils.isEmpty(buildInfo.getConsoleLogLines())){
                buildInfo.setConsoleLogLines(new ArrayList<String>());
            }
            while ((line = br.readLine()) != null) {
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
}
