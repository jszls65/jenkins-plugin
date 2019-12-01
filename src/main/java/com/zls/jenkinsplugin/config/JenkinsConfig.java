package com.zls.jenkinsplugin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "jenkins")
public class JenkinsConfig {

    public static String url;
    public static String username;
    public static String password;

    @Value("${jenkins.url}")
    public void setUrl(String url){
        JenkinsConfig.url = url;
    }
    @Value("${jenkins.username}")
    public void setUsername(String username){
        JenkinsConfig.username = username;
    }
    @Value("${jenkins.password}")
    public void setPassword(String password){
        JenkinsConfig.password = password;
    }


    public static String getUrl(){
        return url;
    }

    public static String getUsername(){
        return username;
    }

    public static String getPassword(){
        return password;
    }
}
