package com.itheima.controller;

public class Result {
    private Object data;
    private Integer code;
    private String msg;


    public Result(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public Result(Object data, Integer code) {
        this.data = data;
        this.code = code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }
}
