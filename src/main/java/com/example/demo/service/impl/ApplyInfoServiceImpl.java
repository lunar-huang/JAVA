package com.example.demo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.ApplyInfo;
import com.example.demo.entity.ApplyPhoneVerify;
import com.example.demo.entity.User;
import com.example.demo.mapper.ApplyInfoMapper;
import com.example.demo.service.ApplyInfoService;
import com.example.demo.utils.CommonUtil;
import com.example.demo.utils.EncryptUtil;
import com.example.demo.utils.RandomUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.text.DateFormat;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class ApplyInfoServiceImpl implements ApplyInfoService {


    @Autowired
    private ApplyInfoMapper applyInfoMapper;
    @Autowired
    private RestTemplate restTemplate;

    private static final String SUCCESSCODE = "C0001";
    private static final String ERROREX = "E1001";
    private static final String ERROROTHERS = "E1002";
    private static final String CARDNO = "cardNo";
    private static final String MOBILEPHONECODE = "mobilePhoneCode";

    @Value("${url}")
    private String url ;
    @Value("${sysId}")
    private String sysId ;
    @Value("${passwd}")
    private String passwd ;

    private DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private static final Logger logger = LoggerFactory.getLogger(ApplyInfoServiceImpl.class);
    private static final String SNO = "serialNo";

    public Map<String, Object> basicParams(){
        Map<String, Object> map = new HashMap<>();
        map.put("sysId", sysId);
        map.put("passwd", passwd);
        map.put(SNO, RandomUtil.randomStr(16)); //生成16位随机字符串
        map.put("bizType","B0001");
        Date date = new Date();
        String dateStr = sdf.format(date);
        map.put("reqTime",dateStr);
        return map;
    }
    @Override
    public String applyCard(JSONObject info, User user) {
        if(info==null||user==null) {
            logger.error("json info is null at applyCard");
            return ERROROTHERS;
        }

        Map<String, Object> map = basicParams();
        Map<String, Object> omap = new HashMap<>();

        if((info.getString(CARDNO)==null)||(user.getName()==null)||(user.getIDNo()==null)||(user.getPhone()==null)
                ||(info.getString(MOBILEPHONECODE)==null)) {
            logger.error("some required key is null at applyCard");
            return ERROROTHERS;
        }

        omap.put("education", user.getEducation());
        omap.put("maritalStatus", user.getMaritalStatus());
        omap.put("workingYears", user.getWorkingYears());

        map.put(CARDNO,info.getString(CARDNO));
        map.put("name", EncryptUtil.decrypt(user.getName()));
        map.put("IDNo", EncryptUtil.decrypt(user.getIDNo()));
        map.put("phone", EncryptUtil.decrypt(user.getPhone()));
        map.put(MOBILEPHONECODE, info.getString(MOBILEPHONECODE));
        map.put("otherInfo", JSON.toJSONString(omap));
        String stringParams = JSON.toJSONString(map);

        ApplyInfo applyInfo = new ApplyInfo((String) map.get("reqTime"), info.getString(CARDNO), (String) map.get(SNO), user.getId());
        String resCode;
        applyInfoMapper.insertApplyInfo(applyInfo);

        JSONObject requestJSON = JSON.parseObject(stringParams);
        logger.info("applyCard sent to external API this:\n {}", CommonUtil.desensitizeData(requestJSON));

        String result;
        try {
            result = sendPost(url,"/creditCardTest/applyCard",requestJSON);

        } catch(Exception e) {
            logger.error("applyCard: exception from the external API: \n{}", e.getMessage());
            return ERROREX ;
        }

        JSONObject jsonObject;
        try {
            jsonObject = JSON.parseObject(result);
        } catch(Exception e) {
            logger.error("fail to parser json {} at applyCard", e.getMessage());
            return ERROROTHERS;
        }

        if(jsonObject==null || jsonObject.getString("code")==null) {
            logger.error("some required key is null at applyCard");
            return ERROROTHERS;
        }
        logger.info("applyCard received this response:\n {}", jsonObject);
        resCode = jsonObject.getString("code");
        Date date = new Date();
        String dateStr = sdf.format(date);
        applyInfoMapper.updateApplyInfo(user.getId(), (String) map.get(SNO), resCode, dateStr);

        return resCode;
    }

    public String sendPost(String url, String path, JSONObject params) {
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(url).
                path(path).build(true);
        URI uri = uriComponents.toUri();
        RequestEntity<JSONObject> requestEntity = RequestEntity.post(uri).
                header("requestheader","requestvalue").accept(MediaType.APPLICATION_JSON).
                contentType(MediaType.APPLICATION_JSON).body(params);
        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(requestEntity,JSONObject.class);
        JSONObject responseEntityBody = responseEntity.getBody();
        String result = "{";
        if(responseEntityBody != null) result = responseEntityBody.toJSONString();
        return  result;
    }

    public String postParams(String phoneNumber){
        Map<String, Object> map = basicParams();
        map.put("mobilePhone",phoneNumber);
        return JSON.toJSONString(map);
    }

    @Override
    public String getPhoneVerify(ApplyPhoneVerify verify, String phoneNumber){
        String res;
        try{
            res=sendPost(url,"/creditCardTest/getMobilePhoneCode",JSON.parseObject(postParams(phoneNumber)));
        }catch (Exception e) {
            logger.error("External API error");
            return ERROREX;
        }

        JSONObject jsonObject;
        try {
            jsonObject = JSON.parseObject(res);
        } catch(Exception e) {
            logger.error("getPhoneVerify fail to parser json {}", e.getMessage());
            return ERROROTHERS;
        }

        String code= jsonObject.getString("code");
        //判断当前是否访问成功
        if(SUCCESSCODE.equals(code)){
            //若申请验证码成功则返回六位数字验证码
            JSONObject jsonVerify = jsonObject.getJSONObject("result");
            ApplyPhoneVerify verifyT = JSON.toJavaObject(jsonVerify,ApplyPhoneVerify.class);
            verify.setMobilePhoneCode(verifyT.getMobilePhoneCode());
            verify.setMobilePhone(phoneNumber);
        }
        return jsonObject.get("code").toString();
    }

    @Override
    public String getPhoneNumber(JSONObject info){
        return info.getString("mobilePhone");
    }
    @Override
    public List<ApplyInfo> ReportPrintApplyInfo(Date date){

        List<ApplyInfo> applyInfo= applyInfoMapper.getAllApplyInfo();
        List<ApplyInfo> newApplyInfo = new ArrayList<>();
        // 24小时内新增的数据条目
        Date date0 = new Date(date.getTime()-24*60*60*1000);
        for(int i=applyInfo.size()-1;i>0;i--){
            Date dateReq = StringtoDate(applyInfo.get(i).getReqTime());
            if(dateReq.after(date0) && dateReq.before(date)){
                newApplyInfo.add(applyInfo.get(i));
            }
        }

        return newApplyInfo;
    }

    @Override
    public Date StringtoDate(String str) {
        Date date = null;
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        ParsePosition pos = new ParsePosition(0);
        date = formatter.parse(str, pos);
        return date;
    }
}
