package com.example.demo.controller;

import com.alibaba.excel.EasyExcel;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.ApplyGroup;
import com.example.demo.entity.ApplyInfo;
import com.example.demo.entity.PrintApplyGroup;
import com.example.demo.entity.User;
import com.example.demo.service.ApplyInfoService;
import com.example.demo.service.UserService;
import com.example.demo.utils.CommonUtil;
import com.example.demo.utils.Consts;
import com.example.demo.utils.IdCardUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.PropertySource;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@RestController
@Component
@PropertySource("classpath:/application.properties")
public class UserController {
    private static final String PHONE = "phone";
    private static final String NAME = "name";
    private static final String IDNO = "IDNo";

    private static final String CODES = "codes";

    @Autowired
    private UserService userService;
    @Autowired
    private ApplyInfoService applyInfoService;

    @Autowired
    private ApplyInfoController applyInfoController;

    @ResponseBody
    @PostMapping ("/insertUserBasicInfo")
    public JSONObject insertUserBasicInfo(@RequestBody JSONObject info){
        JSONObject res = new JSONObject();
        if(!info.containsKey(NAME) || !info.containsKey(IDNO) || !info.containsKey(PHONE)){
            CommonUtil.getResCodeAndMsg(res, "0", "基本信息中姓名、身份证号和电话关键字段不可为空！");
            return res;
        }
        String name = info.getString(NAME);
        String idNo = info.getString(IDNO);
        String phone = info.getString(PHONE);
        String codes = info.getString(CODES);
        if(phone == null || phone.length() == 0 || name == null || name.length() == 0
                || idNo == null || idNo.length() == 0){
            CommonUtil.getResCodeAndMsg(res, "0", "基本信息中姓名、身份证号和电话不可为空！");
            return res;
        }
        // 检验输入数据是否合法
        if(name.length() > 8 || name.length() < 2 || !CommonUtil.isValidateChineseName(name)){
            CommonUtil.getResCodeAndMsg(res, "0", "姓名必须为中文，且长度在2-8之间！");
            return res;
        }

        if(!IdCardUtil.isValidCard(idNo)){
            CommonUtil.getResCodeAndMsg(res, "0", "身份证号不合法！");
            return res;
        }

        if(!CommonUtil.isValidateMobile(phone)){
            CommonUtil.getResCodeAndMsg(res, "0", "手机号格式输入错误！");
            return res;
        }

        if (!codes.equals(applyInfoController.phoneMap.get(phone))){
            CommonUtil.getResCodeAndMsg(res, "0", "请重新确认验证码无误！");
            System.out.println(phone+applyInfoController.phoneMap.get(phone));
            return res;
        }
        User user = new User();
        user.setName(name);
        user.setIDNo(idNo);
        user.setPhone(phone);
        int i = userService.insertUserBasicInfo(user);
        if(i == 1){
            CommonUtil.getResCodeAndMsg(res, "1", "插入成功！");
        }else{
            if(i == 0){
                CommonUtil.getResCodeAndMsg(res, "0", "插入失败！");
            }else{
                CommonUtil.getResCodeAndMsg(res, "2", "数据库已存在该用户！");
            }
        }
        return res;
    }

    @ResponseBody
    @RequestMapping("/getUserBasicInfoByIDNo")
    public JSONObject getUserBasicInfoByIDNo(@RequestBody JSONObject info){
        JSONObject res = new JSONObject();
        String code = "0";
        String msg = "无关键字段！";
        if(!info.containsKey(IDNO) || info.getString(IDNO) == null
                || info.getString(IDNO).length() == 0) {
            CommonUtil.getResCodeAndMsg(res, code, msg);
            res.put(Consts.DATA, null);
            return res;
        }
        String idNo = info.getString(IDNO);
        if(IdCardUtil.isValidCard(idNo)){
            User user = userService.getUserBasicInfoByIDNo(idNo);
            if(user == null){
                code = "0";
                msg = "查询不到指定身份证号的用户！";
            }else{
                code = "1";
                msg = "查询成功！";
            }
            CommonUtil.getResCodeAndMsg(res, code, msg);
            res.put(Consts.DATA, user);
        }else{
            CommonUtil.getResCodeAndMsg(res, code, "身份证无效！");
            res.put(Consts.DATA, null);
        }
        return res;
    }

    @ResponseBody
    @RequestMapping("/updateUserBasicInfoById")
    public JSONObject updateUserBasicInfoById(@RequestBody JSONObject info){
        // 根据id/idcard更新姓名或者电话
        JSONObject res = new JSONObject();
        String name = null;
        String phone = null;

        if(!info.containsKey("id") || info.get("id") == null){
            CommonUtil.getResCodeAndMsg(res, "0", "没有关键字段");
            return res;
        }
        if(info.containsKey(NAME)){
            name = info.getString(NAME);
        }
        if(info.containsKey(PHONE)){
            phone = info.getString(PHONE);
        }
        if(CommonUtil.isEmptyStr(name) && CommonUtil.isEmptyStr(phone)){
            CommonUtil.getResCodeAndMsg(res, "0", "要更新的字段为空！");
            return res;
        }
        // 检验输入数据是否合法
        if(name != null && name.length() > 0 && (!CommonUtil.isValidateChineseName(name)
                || name.length() > 8 || name.length() < 2)){
            CommonUtil.getResCodeAndMsg(res, "0", "姓名必须为中文，且长度在2-8之间！");
            return res;
        }
        if(phone != null && phone.length() != 0 && !CommonUtil.isValidateMobile(phone)){
            CommonUtil.getResCodeAndMsg(res, "0", "手机号格式输入错误！");
            return res;

        }
        User user = new User();
        user.setId((Integer) info.get("id"));
        user.setName(name);
        user.setPhone(phone);
        int i = userService.updateUserBasicInfoById(user);
        CommonUtil.getResCodeAndMsg(res, "i", i == 1 ? "更新成功！":"更新失败！");
        return res;
    }


    @Scheduled(cron ="${cron}")
    public int ReportPrint() {
        Date date = new Date(new Date().getTime());
        List<String> FileName = userService.getFilename(date);

        String fileNameUser = FileName.get(0);
        String fileNameApplyInfo = FileName.get(1);
        String fileNameGroup = FileName.get(2);
        String fileNameNoApply = FileName.get(3);

        List<ApplyInfo> applyInfo = applyInfoService.ReportPrintApplyInfo(date);
        List<List<String>> applyInfoList = userService.ApplyInfoReportColumn();
        EasyExcel.write(fileNameApplyInfo).head(applyInfoList).sheet("ApplyInfo").doWrite(applyInfo);

        // 获取新增的申请信息操作人id
        List<Integer> userId = new ArrayList<>();
        for(int i=0;i<applyInfo.size();i++){
            int temp =applyInfo.get(i).getUserId();
            if(!userId.contains(temp)){ userId.add(temp); }
        }

        // 打印进行操作的申请人信息
        List<User> user = userService.ReportPrintUser(userId);
        List<List<String>> userList = userService.UserReportColumn();
        EasyExcel.write(fileNameUser).head(userList).sheet("user").doWrite(user);

        // 打印没有详细信息的申请人信息
        List<User> userNoApply = userService.ReportPrintUserNoApply();
        EasyExcel.write(fileNameNoApply).head(userList).sheet("user").doWrite(userNoApply);

        // 按照卡类型分类申请信息
        List<List<String>> Grouplist = userService.GroupColumn();
        ApplyGroup applyGroup = userService.GroupByCard(applyInfo);
        List<PrintApplyGroup> PrintGroup = new ArrayList<>();
        for(int i=0;i<applyGroup.getNum().size();i++){
            PrintApplyGroup temp = new PrintApplyGroup();
            temp.setNum(applyGroup.getNum().get(i));
            temp.setCardNo(applyGroup.getCardNo().get(i));
            PrintGroup.add(temp);
        }
        Collections.sort(PrintGroup);
        Collections.reverse(PrintGroup);
        EasyExcel.write(fileNameGroup).head(Grouplist).sheet("GroupByCardNo").doWrite(PrintGroup);

        return 0;
    }
}
