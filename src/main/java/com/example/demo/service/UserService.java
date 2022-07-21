package com.example.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.ApplyGroup;
import com.example.demo.entity.ApplyInfo;
import com.example.demo.entity.User;

import java.util.Date;
import java.util.List;

public interface UserService {
    //插入
    public int insertUserBasicInfo(User user);

    public User getUserBasicInfoByIDNo(String idNo);
    // 根据id更改用户基本信息
    public int updateUserBasicInfoById(User user);

    User updateUserInfo(JSONObject info);

    // 打印所有用户信息作为报表
    public List<User> ReportPrintUser(List<Integer> userId);

    public List<User> ReportPrintUserNoApply();

    // excel表名
    public List<List<String>> UserReportColumn();

    public List<List<String>> ApplyInfoReportColumn();

    public List<String> getFilename(Date date);
    public List<String> getStringDateShort(Date date);

    public ApplyGroup GroupByCard(List<ApplyInfo> applyInfo);

    public List<List<String>> GroupColumn();
}
