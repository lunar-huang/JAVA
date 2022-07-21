package com.example.demo.controller;

import com.alibaba.fastjson.JSONObject;
import com.example.demo.entity.CreditCard;
import com.example.demo.service.CreditCardService;
import com.example.demo.utils.CommonUtil;
import com.example.demo.utils.Consts;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestController
public class CreditCardController {

    private static final Logger logger = LoggerFactory.getLogger(CreditCardController.class);

    @Autowired
    private CreditCardService cardService;

    private static final String SUCCESSCODE = "C0001";

    @ResponseBody
    @RequestMapping("/getAllCreditCard")
    public JSONObject getAllCreditCard(){

        List<CreditCard> cardList = new ArrayList<>();
        String code = cardService.getAllCreditCardList(cardList);
        JSONObject res = new JSONObject();
        if(SUCCESSCODE.equals(code)) {
            res.put(Consts.CODE, '1');
        } else{
            res.put(Consts.CODE, '0');
        }
        res.put(Consts.DATA,cardList);
        res.put(Consts.MESSAGE, CommonUtil.getCreditCardListReqMessage().get(code));
        return res;
    }

    @ResponseBody
    @RequestMapping("/getSelectedColumn")
    public JSONObject getSelectedColumn(){
        Map<String, Set<String>> map=new HashMap<>();
        String code = cardService.getSelectedColumnData(map);
        JSONObject res = new JSONObject();
        if(SUCCESSCODE.equals(code)) {
            res.put(Consts.CODE, '1');
        } else{
            res.put(Consts.CODE, '0');
        }
        res.put(Consts.DATA,map);
        res.put(Consts.MESSAGE, CommonUtil.getCreditCardListReqMessage().get(code));
        return res;
    }

    @ResponseBody
    @PostMapping("/getSelectedCardList")
    public JSONObject getSelectedCardList(@RequestBody JSONObject info) {
        List<CreditCard> selectedCardList = new ArrayList<>();
        String code = cardService.getSelectedCardList(info,selectedCardList);
        JSONObject res = new JSONObject();
        if(SUCCESSCODE.equals(code)) {
            res.put(Consts.CODE, '1');
        } else{
            res.put(Consts.CODE, '0');
        }
        res.put(Consts.DATA,selectedCardList);
        res.put(Consts.MESSAGE, CommonUtil.getCreditCardListReqMessage().get(code));
        return res;
    }


    @ResponseBody
    @RequestMapping("/getSearchRes")
    public JSONObject getSearchRes(@RequestBody JSONObject info) {
        List<CreditCard> cardList = new ArrayList<>();
        JSONObject res = new JSONObject();

        logger.info("getSearchRes received this request:\n {}", info);

        String code = cardService.getSearchedCardList(info, cardList);
        if(SUCCESSCODE.equals(code)) {
            res.put(Consts.CODE, '1');
        } else{
            res.put(Consts.CODE, '0');
        }
        res.put(Consts.DATA,cardList);
        res.put(Consts.MESSAGE, CommonUtil.getCreditCardListReqMessage().get(code));
        return res;
    }

}
