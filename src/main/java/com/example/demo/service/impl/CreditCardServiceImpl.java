package com.example.demo.service.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.CreditCard;
import com.example.demo.service.CreditCardService;
import com.example.demo.utils.RandomUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class CreditCardServiceImpl implements CreditCardService {

    @Autowired
    private RestTemplate restTemplate;
    private static  final  Logger logger=LoggerFactory.getLogger(CreditCardServiceImpl.class);
    private static final String SUCCESSCODE = "C0001";
    private static final String ERROREX = "E1001";
    private static final String ERROROTHERS = "E1002";
    private static final String CARDTYPE="cardType";
    private static final String CARDNAME= "cardName";
    private static final String CARDLABLE= "cardLable";
    private static final String RESULT ="result";
    private static final String CARDLIST="cardList";
    private static final String GETCARDAPI ="/creditCardTest/getCard";

    @Value("${url}")
    private String url ;
    @Value("${sysId}")
    private String sysId ;
    @Value("${passwd}")
    private String passwd ;

    public String postParams(){
        Map<String, Object> map = new HashMap<>();
        map.put("sysId", sysId);
        map.put("passwd", passwd);
        map.put("serialNo", RandomUtil.randomStr(16)); //生成16位随机字符串
        map.put("bizType","B0001");
        Date date = new Date();
        DateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr = sdf.format(date);
        map.put("reqTime",dateStr);
        map.put("cardNo","009");
        map.put(CARDTYPE,"Z");
        return JSON.toJSONString(map);
    }

    @Override
    public String getAllCreditCardList(List<CreditCard> cardList) {

        String res;
        try{
            res =sendPost(url,GETCARDAPI,JSON.parseObject(postParams()));
        }catch (Exception e) {
            logger.error("External API error {}",e.getMessage());
            return ERROREX;}


        JSONObject jsonObject;
        try {
            jsonObject = JSON.parseObject(res);
        } catch(Exception e) {
            logger.error("getAllCreditCardList fail to parser json {}", e.getMessage());
            return ERROROTHERS;
        }

        String code= jsonObject.getString("code");
        //判断当前是否访问成功
        if(SUCCESSCODE.equals(code)){
            logger.info("getAllCreditCardList successfully");
            // 转为CreditCard类型
            JSONArray cList = jsonObject.getJSONObject(RESULT).getJSONArray(CARDLIST);
            for (int i = 0; i < cList.size(); i++) {
                CreditCard card = JSON.toJavaObject(cList.getJSONObject(i),CreditCard.class);
                cardList.add(card);
            }
        }
        return code;
    }

    @Override
    public String getSelectedCardList(JSONObject info,List<CreditCard> selectedCardList) {
        String res;
        try {
            res = sendPost(url,GETCARDAPI,JSON.parseObject(postParams()));
        } catch (Exception e) {
            logger.error("external API error");
            return ERROREX;
        }

        JSONObject jsonObject;
        try {
            jsonObject = JSON.parseObject(res);
        } catch(Exception e) {
            logger.error("getSelectedCardList fail to parser json {}", e.getMessage());
            return ERROROTHERS;
        }

        if (info == null) {
            logger.error("getSelectedCardList front info is null:{}", info);
            return ERROROTHERS;
        }

        String nameT;
        String lableT;
        String typeT;
        String keyWord;
        try {
            //提取筛选标签
            nameT=info.getString(CARDNAME);
            lableT=info.getString(CARDLABLE);
            typeT=info.getString(CARDTYPE);
            keyWord = info.getString("keyWord").toLowerCase();
        } catch (Exception e) {
            logger.error("fail to parser json {}", e.getMessage());
            return ERROROTHERS;
        }
        if (nameT == null || lableT == null || typeT == null||keyWord==null ) {
            logger.error("some required key is null , cardName:{},cardLable:{},cardType:{},keyword:{}", nameT, lableT, typeT,keyWord);
            return ERROROTHERS;
        }

        String code= jsonObject.getString("code");
        //判断当前是否访问成功
        if(SUCCESSCODE.equals(code)){
            logger.info("getSelectedCardList successfully");
            JSONArray cList = jsonObject.getJSONObject(RESULT).getJSONArray(CARDLIST);
            String[] names= new String[]{"普卡", "白金卡", "金卡"};
            for (int i = 0; i < cList.size(); i++) {
                JSONObject card=(JSONObject) cList.get(i);
                String nameC=card.getString(CARDNAME);   //卡全名
                String name="";         //读取被筛选卡的标签
                for(String str:names){
                    if (nameC.contains(str)){
                        name=str;
                        break;
                    }
                }
                String lable=card.getString(CARDLABLE);
                String type=card.getString(CARDTYPE);

                //判断是否属于所选类型  若全选传空“”
                if((nameT.isEmpty()||nameT.equals(name))&&(lableT.isEmpty()||lableT.equals(lable))&&(typeT.isEmpty()||typeT.equals(type))&&(keyWord.isEmpty()||nameC.toLowerCase().contains(keyWord))){
                        CreditCard selectedcard = JSON.toJavaObject(cList.getJSONObject(i),CreditCard.class);
                        selectedCardList.add(selectedcard);
                }
            }
        }
        return code;
    }

    @Override
    public String getSelectedColumnData(Map<String,Set<String>> column) {
        String res;
        try {
            res = sendPost(url,GETCARDAPI,JSON.parseObject(postParams()));

        } catch (Exception e) {
            logger.error("external API error");
            return ERROREX;
        }

        JSONObject jsonObject;
        try {
            jsonObject = JSON.parseObject(res);
        } catch(Exception e) {
            logger.error("getSelectedCardList fail to parser json {}", e.getMessage());
            return ERROROTHERS;
        }

        //创建卡分类数组
        column.put(CARDNAME,new TreeSet<>());
        column.put(CARDLABLE,new TreeSet<>());
        column.put(CARDTYPE,new TreeSet<>());

        String code=jsonObject.getString("code");
        //判断当前是否访问成功
        if(SUCCESSCODE.equals(code)){
            logger.info("getSelectedColumnData successfully");
            // 确定每类卡数组中的元素
            JSONArray cList = jsonObject.getJSONObject(RESULT).getJSONArray(CARDLIST);
            for (int i = 0; i < cList.size(); i++) {
                JSONObject card=(JSONObject) cList.get(i);
                //方法二：匹配种类后加入筛选集合
                String name=card.getString(CARDNAME);
                String[] names= new String[]{"普卡", "白金卡", "金卡"};
                for(String str:names){
                    if (name.contains(str)){
                        column.get(CARDNAME).add(str);
                        break;
                    }
                }
                column.get(CARDLABLE).add(card.getString(CARDLABLE));
                column.get(CARDTYPE).add(card.getString(CARDTYPE));
            }
        }
        return code;
    }

    @Override
    public String getSearchedCardList(JSONObject info, List<CreditCard> cardList) {
        if(info==null||info.getString("keyWord")==null) {
            logger.error("front info is null at getSearchedCardList");
            return ERROROTHERS;
        }
        String keyWord = info.getString("keyWord");
        String res;
        try {
            res = sendPost(url,GETCARDAPI,JSON.parseObject(postParams()));

        } catch (Exception e) {
            logger.error("getSearchedCardList: external API error: {}\n", e.getMessage());
            return ERROREX;}
        JSONObject jsonObject = JSON.parseObject(res);
        keyWord = keyWord.toLowerCase();

        String code= jsonObject.getString("code");
        //判断当前是否访问成功
        if(SUCCESSCODE.equals(code)){
            JSONArray cList = jsonObject.getJSONObject(RESULT).getJSONArray(CARDLIST);
            for (int i = 0; i < cList.size(); i++) {
                CreditCard card = JSON.toJavaObject(cList.getJSONObject(i),CreditCard.class);
                if(card.getCardName().toLowerCase().contains(keyWord)) cardList.add(card);
            }
        }
        return code;}
    public String sendPost(String url, String path, JSONObject params) {
        String res;
        UriComponents uriComponents = UriComponentsBuilder.fromHttpUrl(url).
                path(path).build(true);
        URI uri = uriComponents.toUri();
        RequestEntity<JSONObject> requestEntity = RequestEntity.post(uri).
                header("requestheader","requestvalue").accept(MediaType.APPLICATION_JSON).
                contentType(MediaType.APPLICATION_JSON).body(params);
        ResponseEntity<JSONObject> responseEntity = restTemplate.exchange(requestEntity,JSONObject.class);
        JSONObject responseEntityBody = responseEntity.getBody();
        if(responseEntityBody != null) {res = responseEntityBody.toJSONString();}
        else {
            res= ERROROTHERS;
        }
        return  res;
    }

}
