package com.zls.jenkinsplugin.util;

import com.zls.jenkinsplugin.config.JenkinsConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.httpclient.Credentials;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.UsernamePasswordCredentials;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * HttpClient工具类
 *
 * @author zhangliansheng
 * @date 2019/11/30
 */
@Slf4j
public class HttpClientUtil {

    public static HttpClient getHttpClient() {
        HttpClient client = new HttpClient();
        try {
            URL url = new URL(JenkinsConfig.getUrl());
            int port = url.getPort() == -1 ? 80 : url.getPort();
            Credentials credentials =
                    new UsernamePasswordCredentials(JenkinsConfig.getUsername(), JenkinsConfig.getPassword());
            AuthScope scope = new AuthScope(url.getHost(), port);
            client.getState().setCredentials(scope, credentials);
            client.getParams().setAuthenticationPreemptive(true);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return client;
    }


    public static void main(String[] args) {
        HttpClient client = new HttpClient();
        try {

            client.getParams().setAuthenticationPreemptive(true);

            GetMethod getMethod =
                    new GetMethod(
                            "https://gitlab.zhoupu123.com/api/v4/projects/8/repository/commits/294f5b5739b57d16c3e714d1d8890ada98e3815f/?private_token=x5CFUiEeMuDBVxXBmUVj");


            getMethod.addRequestHeader(new Header("Content-Type", "application/json;charset=utf-8"));

            client.getHttpConnectionManager().getParams().setSoTimeout(120 * 1000);
            client.getHttpConnectionManager().getParams().setConnectionTimeout(60000);
            int status = client.executeMethod(getMethod);
            System.out.println(getMethod.getResponseBodyAsString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


}
