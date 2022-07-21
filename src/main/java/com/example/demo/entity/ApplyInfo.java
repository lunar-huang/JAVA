package com.example.demo.entity;

import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;

public class ApplyInfo implements Serializable {
    private Integer id;
    private Integer userId;
    private String serialNo;
    @JsonFormat(pattern ="yyyy-MM-dd",timezone = "GMT+8")
//    @DateTimeFormat(pattern="yyyy-MM-dd")
    private String reqTime;

    //响应码
    private String code;
    private String cardNo;

    public String getSerialNo() {
        return serialNo;
    }

    public void setSerialNo(String serialNo) {
        this.serialNo = serialNo;
    }



    @JsonFormat(pattern ="yyyy-MM-dd",timezone = "GMT+8")
//    @DateTimeFormat(pattern="yyyy-MM-dd")
    private String updateTime;


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getReqTime() {
        return reqTime;
    }

    public void setReqTime(String reqTime) {
        this.reqTime = reqTime;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(String updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    @Override
    public String toString() {
        return "ApplyInfo{" +
                "id=" + id +
                ", userId=" + userId +
                ", serialNo= " + serialNo +
                ", reqTime=" + reqTime +
                ", code='" + code + '\'' +
                ", cardNo='" + cardNo + '\'' +
                ", updateTime=" + updateTime +
                '}';
    }

    public ApplyInfo(String reqTime, String cardNo, String serialNo, Integer userId) {
        this.reqTime = reqTime;
        this.cardNo = cardNo;
        this.serialNo = serialNo;
        this.userId = userId;
    }

    public ApplyInfo(){

    }
}
