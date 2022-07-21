package com.example.demo.utils;

/**
 * 常量工具类
 */
public class Consts {

    private Consts() {
    }

    public static Consts getConsts() {
        return (new Consts());
    }

    // 返回码，1表示数据请求正常，0表示数据请求不正常
    public static final String CODE = "code";
    // 返回信息
    public static final String MESSAGE = "msg";
    // 返回数据
    public static final String DATA = "data";
}
