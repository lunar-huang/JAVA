package com.example.demo.entity;

import java.util.List;

public class ApplyGroup {
     private List<String> cardNo;
     private List<Integer> num;

     public void setCardNo(List<String> cardNo) {
          this.cardNo = cardNo;
     }
     public List<String> getCardNo(){ return cardNo;}

     public void setNum(List<Integer> num) {
          this.num = num;
     }
     public List<Integer> getNum(){ return num;}
}
