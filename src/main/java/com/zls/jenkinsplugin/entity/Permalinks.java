package com.zls.jenkinsplugin.entity;

import lombok.Data;
/**
 * Jenkins构建记录对象
 * @author zhangliansheng
 * @date 2019/11/2
 */
@Data
public class Permalinks {

    private Long lastCompletedBuild;
    private Long lastFailedBuild;
    private Long lastStableBuild;
    private Long lastSuccessfulBuild;
    private Long lastUnstableBuild;
    private Long lastUnsuccessfulBuild;
}

