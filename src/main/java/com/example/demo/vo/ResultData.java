package com.example.demo.vo;

import lombok.Getter;

@Getter
public class ResultData<T> {
    private T data;
    private int code;
    private String msg;

    /**
     * 若没有数据返回，默认状态码为0，提示信息为：操作成功！
     */
    public ResultData() {
        this.code = 0;
        this.msg = "发布成功！";
    }

    /**
     * 若没有数据返回，可以人为指定状态码和提示信息
     */
    public ResultData(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    /**
     * 有数据返回时，状态码为0，默认提示信息为：操作成功！
     */
    public ResultData(T data) {
        this.data = data;
        this.code = 0;
        this.msg = "发布成功！";
    }

    /**
     * 有数据返回，状态码为0，人为指定提示信息
     */
    public ResultData(T data, String msg) {
        this.data = data;
        this.code = 0;
        this.msg = msg;
    }
}
