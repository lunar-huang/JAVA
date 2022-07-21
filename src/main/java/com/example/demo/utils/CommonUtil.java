package com.example.demo.utils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class CommonUtil {

    private CommonUtil() {
    }

    public static CommonUtil getCommonUtil() {
        return (new CommonUtil());
    }
    private static final String SUCCESSCODE = "C0001";
    private static final String ERROR1 = "S1001";
    private static final String ERROR2 = "S1002";
    private static final String ERROR3 = "S1003";
    private static final String ERROR4 = "S1004";
    private static final String ERROR99 = "S9999";
    private static final String ERROREX = "E1001";
    private static final String ERROROTHERS = "E1002";

    private static final String ERRSTR1="系统ID校验失败，失败原因：系统ID为空或超长; ";

    private static final String ERRSTR2="流水号校验失败，失败原因：流水号为空或超长;";

    private static final String ERRSTR3="业务类型校验失败，失败原因：业务类型为空或超长;";

    private static final String ERRSTR4="请求时间校验失败，失败原因：请求时间为空或超长;";

    private static final String ERRSTR99="用户名或者密码错误";

    private static final String ERRSTREX="外部服务器错误";

    private static final String ERRSTROT="其他错误";

    public static Map<String, String> getCreditCardListReqMessage(){
        Map<String, String> message = new HashMap<>();
        message.put(SUCCESSCODE,"查询成功");
        message.put(ERROR1,ERRSTR1);
        message.put(ERROR2,ERRSTR2);
        message.put(ERROR3,ERRSTR3);
        message.put(ERROR4,ERRSTR4);
        message.put(ERROR99,ERRSTR99);
        message.put(ERROREX,ERRSTREX);
        message.put(ERROROTHERS,ERRSTROT);
        return message;
    }
    public static Map<String, String> getPhoneVerifyReqMessage(){
        Map<String, String> message = new HashMap<>();
        message.put(SUCCESSCODE,"查询成功");
        message.put(ERROR1,ERRSTR1);
        message.put(ERROR2,ERRSTR2);
        message.put(ERROR3,ERRSTR3);
        message.put(ERROR4,ERRSTR4);
        message.put("S9998","手机号格式不对");
        message.put(ERROR99,ERRSTR99);
        return message;
    }
    public static Map<String, String> getApplyReqMessage() {
        Map<String, String> message = new HashMap<>();
        message.put(SUCCESSCODE,"卡申请成功");
        message.put(ERROR1,ERRSTR1);
        message.put(ERROR2,ERRSTR2);
        message.put(ERROR3,ERRSTR3);
        message.put(ERROR4,ERRSTR4);
        message.put(ERROR99,ERRSTR99);
        message.put("S1005","请求手机号校验失败，失败原因：手机号为空或超长;");
        message.put("S1006","请求验证码校验失败，失败原因：验证码为空或超长;");
        message.put("S1007","请求验证码校验失败，失败原因：手机号与验证码不匹配或者验证码已过期(15min内有效);");
        message.put("S1035","申请失败，具体原因为A");
        message.put("S1036","申请失败，具体原因为B");
        message.put("S1037","申请失败，具体原因为....");
        message.put("E1003","错误：家庭住址为空或过长");
        message.put("E1004","错误：公司地址为空或过长");
        message.put("E1005","错误：公司名为空或过长");
        message.put("E1006","错误：最高学历为空或不合法");
        message.put("E1007","错误：身份证为空");
        message.put("E1008","错误：婚姻状况为空或不合法");
        message.put("E1009","错误：工作年限为空或不合法");
        message.put("E1010","错误：职位为空或过长");
        message.put(ERROREX,ERRSTREX);
        message.put(ERROROTHERS,ERRSTROT);
        return message;
    }

    /*
    判断名称中是否只有中文字符
     */
    public static Boolean isValidateChineseName(String name) {
        int n = 0;
        for(int i = 0; i < name.length(); i++) {
            n = name.charAt(i);
            if(!(19968 <= n && n <40869)) {
                return false;
            }
        }
        return true;
    }

    /*
    判断手机号是否合法
     */
    public static boolean isValidateMobile(String mobiles) {
        if ((mobiles != null) && (!mobiles.isEmpty())) {
            return Pattern.matches("^1[3-9]\\d{9}$", mobiles);
        }
        return false;
    }

    public static boolean isValidGender(String gender) {
        return gender.equals("m")||gender.equals("f");
    }

    public static boolean isValidAge(Integer age) {
        return age>=0&&age<120;
    }

    public static boolean isValidMarriedStatus(String marriedStatus) {
        return "未婚".equals(marriedStatus)||"已婚".equals(marriedStatus);
    }

    public static boolean isValidEducationBackground(String educationBackground) {
        return "高中或以下".equals(educationBackground)||"本科".equals(educationBackground)||"硕士".equals(educationBackground)||
                "博士".equals(educationBackground)||"大专".equals(educationBackground);
    }

    public static boolean isValidWorkTime(String workTime) {
        return "1年以内".equals(workTime)||"1-3年".equals(workTime)||"3-5年".equals(workTime)
                ||"5-10年".equals(workTime)||"10年以上".equals(workTime);
    }

    public static boolean isOnlyChinese(String str) {
        String regex = "^[\u4e00-\u9fa5]+$";
        return str.matches(regex);
    }

    public static JSONObject desensitizeData(JSONObject json) {
        if(json == null) return json;
        String temp  = json.toJSONString();
        JSONObject res = JSON.parseObject(temp);
        String phone = res.getString("phone");
        if(phone!=null) res.put("phone", phone.substring(0,3)+"*****");
        String name = res.getString("name");
        if(name!=null) res.put("name", name.charAt(0)+"**");
        String idNo = res.getString("IDNo");
        if(idNo!=null) res.put("IDNo", idNo.charAt(0)+"********");
        String homeAd = res.getString("homeAd");
        if(homeAd != null) res.put("homeAd", homeAd.charAt(0)+"*******");
        String sysId = res.getString("sysId");
        if(sysId != null) res.put("sysId", sysId.charAt(0)+"*****");
        if(res.containsKey("passwd")) {
            res.remove("passwd");
        }
        return res;
    }

    public static Boolean isEmptyStr(String str){
        return (str == null) || (str.length() == 0);
    }

    public static void getResCodeAndMsg(JSONObject res, String code, String msg){
        res.put(Consts.CODE, code);
        res.put(Consts.MESSAGE, msg);
    }
}
