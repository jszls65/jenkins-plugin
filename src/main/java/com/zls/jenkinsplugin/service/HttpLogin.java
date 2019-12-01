package com.zls.jenkinsplugin.service;

import org.apache.commons.httpclient.*;
import org.apache.commons.httpclient.auth.AuthScope;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;

public class HttpLogin {
    public static void main(String[] args) {
        String name = "admin";
        String password = "admin";
        String jenkinsHost = "http://localhost:8080";

        try {
            HttpClient client = new HttpClient();
            URL url = new URL(jenkinsHost);
            int port = url.getPort() == -1 ? 80 : url.getPort();
            Credentials credentials = new UsernamePasswordCredentials(name, password);
            AuthScope scope = new AuthScope(url.getHost(), port);
            client.getState().setCredentials(scope, credentials);
            client.getParams().setAuthenticationPreemptive(true);

            GetMethod getMethod = new GetMethod("http://localhost:8080/job/test-cms/5/consoleText");
            getMethod.addRequestHeader(new Header("Content-Type", "application/json;charset=utf-8"));

            client.getHttpConnectionManager().getParams().setSoTimeout(120 * 1000);
            client.getHttpConnectionManager().getParams().setConnectionTimeout(60000);
            int status = client.executeMethod(getMethod);



            PostMethod postMethod = new PostMethod("http://localhost:8080/job/test-cms/buildHistory/ajax");
            postMethod.addRequestHeader(new Header("Content-Type", "application/json;charset=utf-8"));

            client.getHttpConnectionManager().getParams().setSoTimeout(120 * 1000);
            client.getHttpConnectionManager().getParams().setConnectionTimeout(60000);
            client.executeMethod(postMethod);

            System.out.println("----------------------------------------------------------------\n");
            System.out.println(postMethod.getResponseBodyAsString());



        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (HttpException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

}
