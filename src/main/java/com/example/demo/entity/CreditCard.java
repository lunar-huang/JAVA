package com.example.demo.entity;

import java.io.Serializable;

public class CreditCard implements Serializable {
    private String benefits;
    private String cardName;
    private String cardLable;
    private String cardType;
    //卡的详细描述
    private String cardDesc;
    private String cardImageUrl;
    private String cardNo;

    public String getBenefits() {
        return benefits;
    }

    public void setBenefits(String benefits) {
        this.benefits = benefits;
    }

    public String getCardName() {
        return cardName;
    }

    public void setCardName(String cardName) {
        this.cardName = cardName;
    }

    public String getCardLable() {
        return cardLable;
    }

    public void setCardLable(String cardLable) {
        this.cardLable = cardLable;
    }

    public String getCardType() {
        return cardType;
    }

    public void setCardType(String cardType) {
        this.cardType = cardType;
    }

    public String getCardDesc() {
        return cardDesc;
    }

    public void setCardDesc(String cardDesc) {
        this.cardDesc = cardDesc;
    }

    public String getCardImageUrl() {
        return cardImageUrl;
    }

    public void setCardImageUrl(String cardImageUrl) {
        this.cardImageUrl = cardImageUrl;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    @Override
    public String toString() {
        return "CreditCard{" +
                "benefits='" + benefits + '\'' +
                ", cardName='" + cardName + '\'' +
                ", cardLable='" + cardLable + '\'' +
                ", cardType='" + cardType + '\'' +
                ", cardDesc='" + cardDesc + '\'' +
                ", cardImageUrl='" + cardImageUrl + '\'' +
                ", cardNo='" + cardNo + '\'' +
                '}';
    }
}
