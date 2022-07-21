package com.example.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.ApplyInfo;
import com.example.demo.entity.ApplyPhoneVerify;
import com.example.demo.entity.User;

import java.util.Date;
import java.util.List;

public interface ApplyInfoService {
    String applyCard(JSONObject info, User user);

    // 获取手机号验证码
    public String getPhoneVerify(ApplyPhoneVerify verify, String phoneNumber);
    //
    public String getPhoneNumber(JSONObject info);

    public List<ApplyInfo> ReportPrintApplyInfo(Date date);

    public Date StringtoDate(String Str);
}
