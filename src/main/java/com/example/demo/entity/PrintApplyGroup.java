package com.example.demo.entity;

public class PrintApplyGroup implements Comparable <PrintApplyGroup>{
    private String cardNo;
    private Integer num;

    public void setCardNo(String cardNo) { this.cardNo = cardNo; }
    public String getCardNo(){ return cardNo;}

    public void setNum(Integer num) {
        this.num = num;
    }
    public Integer getNum(){ return num;}


    @Override
    public int compareTo(PrintApplyGroup o) {
        return this.num.compareTo(o.getNum()) ;
    }
}
