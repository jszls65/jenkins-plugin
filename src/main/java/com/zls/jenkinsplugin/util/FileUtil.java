package com.zls.jenkinsplugin.util;

import com.zls.jenkinsplugin.constants.FileTypeEnum;

public class FileUtil {
    public static String getFilePath(String project, String builId, FileTypeEnum fileTypeEnum){
        return String.format("console-log-pic/%s-%s.%s", project, builId, fileTypeEnum.getValue());
    }
}
