package com.zls.jenkinsplugin.util;

import com.google.gson.Gson;
import com.qiniu.common.QiniuException;
import com.qiniu.http.Response;
import com.qiniu.storage.Configuration;
import com.qiniu.storage.Region;
import com.qiniu.storage.UploadManager;
import com.qiniu.storage.model.DefaultPutRet;
import com.qiniu.util.Auth;
import com.zls.jenkinsplugin.config.QiniuConfig;
import lombok.extern.slf4j.Slf4j;
/**
 * 七牛云工具类
 * @author zhangliansheng
 * @date 2019/11/27
 */
@Slf4j
public class QiniuUtil {

    /**
     * 上传文件
     * @param qiniuFileName 上传到七牛云上的文件名, 如果不指定, 则用hash值为文件名
     * @author zhangliansheng
     * @date 2019/11/27
     */
    public static String upload(String filePath, String qiniuFileName){
        //构造一个带指定 Region 对象的配置类
        Configuration cfg = new Configuration(Region.region2());
        //...其他参数参考类注释
        UploadManager uploadManager = new UploadManager(cfg);
        //...生成上传凭证，然后准备上传
        //如果是Windows情况下，格式是 D:\\qiniu\\test.png
        //默认不指定key的情况下，以文件内容的hash值作为文件名
        Auth auth = Auth.create(QiniuConfig.getAK(), QiniuConfig.getSK());
        String upToken = auth.uploadToken(QiniuConfig.getBucket());
        try {
            Response response = uploadManager.put(filePath, qiniuFileName, upToken);
            //解析上传成功的结果
            DefaultPutRet putRet = new Gson().fromJson(response.bodyString(), DefaultPutRet.class);
        } catch (QiniuException ex) {
            Response r = ex.response;
            System.err.println(r.toString());
            try {
                System.err.println(r.bodyString());
            } catch (QiniuException ex2) {
                //ignore
            }
            log.error("文件上传七牛云失败 ", ex);
            return "";
        }
        return getFileUrl(qiniuFileName);
    }

    /**
     * 公有空间文件链接
     * @author zhangliansheng
     * @date 2019/11/27
     */
    private static String getFileUrl(String fileName){
        String finalUrl = String.format("%s/%s", QiniuConfig.getDomainOfBucket(), fileName);
        System.out.println(finalUrl);
        return finalUrl;
    }
}
