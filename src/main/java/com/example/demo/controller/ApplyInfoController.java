package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.ApplyPhoneVerify;
import com.example.demo.entity.User;
import com.example.demo.service.ApplyInfoService;
import com.example.demo.service.UserService;
import com.example.demo.utils.CommonUtil;
import com.example.demo.utils.Consts;
import com.github.benmanes.caffeine.cache.Caffeine;
import org.apache.ibatis.mapping.CacheBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.cache.CacheProperties;
import org.springframework.cache.Cache;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;


@RestController
@Configuration
@EnableCaching
public class ApplyInfoController {

    private static final Logger logger = LoggerFactory.getLogger(ApplyInfoController.class);
    @Autowired
    private ApplyInfoService applyInfoService;
    @Autowired
    private ApplyInfoService infoService;
    @Autowired
    private UserService userService;

    Map<String, String> phoneMap = new HashMap();

    @ResponseBody
    @PostMapping("/submitAllUserInfo")
    public String submitAllUserInfo(@RequestBody JSONObject info) {
        logger.info("submitAllUserInfo received this\n {}",
                CommonUtil.desensitizeData(info));

        String homeAddress = info.getString("homeAd");
        String companyAddress = info.getString("address");
        String companyName = info.getString("companyName");
        String education = info.getString("education");
        String IDNo = info.getString("IDNo");
        String maritalStatus = info.getString("maritalStatus");
        String workingYears = info.getString("workingYears");
        String job = info.getString("job");

        if((homeAddress == null)||(homeAddress.trim().isEmpty())||(homeAddress.length()>75)) {
            logger.error("submitAllUserInfo: homeAddress is empty or too long.");
            return CommonUtil.getApplyReqMessage().get("E1003");
        }

        if((companyAddress == null)||(companyAddress.trim().isEmpty())||(companyAddress.length()>100)) {
            logger.error("submitAllUserInfo: companyAddress is empty or too long.");
            return CommonUtil.getApplyReqMessage().get("E1004");
        }

        if((companyName == null)||(companyName.trim().isEmpty())||(companyName.length()>50)) {
            logger.error("submitAllUserInfo: companyName is empty or too long.");
            return CommonUtil.getApplyReqMessage().get("E1005");
        }

        if((education == null)||(education.trim().isEmpty())||(!CommonUtil.isValidEducationBackground(education))) {
            logger.error("submitAllUserInfo: education is empty or invalid.");
            return CommonUtil.getApplyReqMessage().get("E1006");
        }

        if((IDNo == null)||(IDNo.trim().isEmpty())) {
            logger.error("submitAllUserInfo: IDNo is empty.");
            return CommonUtil.getApplyReqMessage().get("E1007");
        }

        if((maritalStatus == null)||(maritalStatus.trim().isEmpty())||(!CommonUtil.isValidMarriedStatus(maritalStatus))) {
            logger.error("submitAllUserInfo: maritalStatus is empty or invalid.");
            return CommonUtil.getApplyReqMessage().get("E1008");
        }

        if((workingYears == null)||(workingYears.trim().isEmpty())||(!CommonUtil.isValidWorkTime(workingYears))) {
            logger.error("submitAllUserInfo: workingYears is empty or invalid.");
            return CommonUtil.getApplyReqMessage().get("E1009");
        }

        if((job == null)||(job.trim().isEmpty())||(job.length()>50)) {
            logger.error("submitAllUserInfo: job is empty or too long.");
            return CommonUtil.getApplyReqMessage().get("E1010");
        }

        User user = userService.updateUserInfo(info);
        if(user==null) {
            return CommonUtil.getApplyReqMessage().get("E1002");
        }
        String res = applyInfoService.applyCard(info, user);
        res = CommonUtil.getApplyReqMessage().get(res);
        logger.info("submitAllUserInfo responsed: {}", res);
        return res;

    }

    @ResponseBody
    @RequestMapping("/getPhoneVerifyCode")
    public JSONObject getPhoneVerifyCode(@RequestBody JSONObject info){
        ApplyPhoneVerify verify = new ApplyPhoneVerify();
        String phoneNumber = infoService.getPhoneNumber(info);
        String code = infoService.getPhoneVerify(verify, phoneNumber);
        JSONObject res = new JSONObject();
        if ("C0001".equals(code)){
            res.put(Consts.CODE, '1');
            phoneMap.put(phoneNumber, verify.getMobilePhoneCode());
        } else{
            res.put(Consts.CODE, '0');
        }
        res.put(Consts.DATA,verify);
        res.put(Consts.MESSAGE, CommonUtil.getPhoneVerifyReqMessage().get(code));
        return res;
    }
}
