package com.zls.jenkinsplugin.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "qiniu")
public class QiniuConfig {

    public static String AK;
    public static String SK;
    public static String bucket;
    public static String domainOfBucket;

    @Value("${qiniu.ak}")
    public void setAK(String AK){
        QiniuConfig.AK = AK;
    }
    @Value("${qiniu.sk}")
    public void setSK(String SK){
        QiniuConfig.SK = SK;
    }
    @Value("${qiniu.bucket}")
    public void setBucket(String bucket){
        QiniuConfig.bucket = bucket;
    }

    @Value("${qiniu.domainOfBucket}")
    public void setDomainOfBucket(String domainOfBucket){
        QiniuConfig.domainOfBucket = domainOfBucket;
    }

    public static String getAK(){
        return AK;
    }

    public static String getSK(){
        return SK;
    }

    public static String getBucket(){
        return bucket;
    }

    public static String getDomainOfBucket(){
        return domainOfBucket;
    }


}
