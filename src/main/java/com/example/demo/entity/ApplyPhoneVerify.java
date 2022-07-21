package com.example.demo.entity;


import java.io.Serializable;

public class ApplyPhoneVerify implements Serializable {
    private String mobilePhoneCode;
    private String mobilePhone;

    public String getMobilePhoneCode() {
        return mobilePhoneCode;
    }
    public String getMobilePhone() {
        return mobilePhone;
    }

    public void setMobilePhoneCode(String mobilePhoneCode) {
        this.mobilePhoneCode = mobilePhoneCode;
    }
    public void setMobilePhone(String mobilePhone) {
        this.mobilePhone = mobilePhone;
    }

    @Override
    public String toString(){
        return "ApplyPhoneVerify{" +
                "MobilePhone" + mobilePhone +
                "MobilePhoneCode" + mobilePhoneCode +
                '}';
    }
}
