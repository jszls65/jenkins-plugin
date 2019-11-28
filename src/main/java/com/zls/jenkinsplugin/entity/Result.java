package com.zls.jenkinsplugin.entity;

import lombok.Data;

@Data
public class Result {
    private int code;
    private String msg;
    private Object obj;


    public Result(int code, String msg){
        this.code = code;
        this.msg = msg;
    }

    public Result(int code, Object obj){
        this.code = code;
        this.obj = obj;
    }

    public Result(int code){
        this.code = code;
    }

    public static Result ok(int code, Object obj){
        return new Result(code, obj);
    }

}
