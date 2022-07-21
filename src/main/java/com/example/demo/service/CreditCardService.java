package com.example.demo.service;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.CreditCard;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface CreditCardService {

    // 获取所有卡列表
    public String getAllCreditCardList(List<CreditCard> cardList);

    //筛选卡列表
    public String getSelectedCardList(JSONObject info,List<CreditCard> selectedCardList);

    // 获取筛选内容
    public String getSelectedColumnData(Map<String, Set<String>> column);
    String getSearchedCardList(JSONObject info, List<CreditCard> cardList);
}
