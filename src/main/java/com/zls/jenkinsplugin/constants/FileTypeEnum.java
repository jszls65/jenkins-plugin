package com.zls.jenkinsplugin.constants;
/**
 * 文件类型
 * @author zhangliansheng
 * @date 2019/11/30
 */
public enum FileTypeEnum {
    TXT(1, "txt"),
    PIC(2, "jpg");

    private int code;
    private String value;
    private FileTypeEnum(int code, String value){
        this.code = code;
        this.value = value;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
