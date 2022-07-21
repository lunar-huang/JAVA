package com.example.demo.utils;

import java.util.Random;

public class RandomUtil {

    /**
     * 将15位身份证号码转换为18位
     *
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    public  static String randomStr(int length){
        String str ="abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random ran=new Random(12);
        StringBuilder strb=new StringBuilder();
        for (int i=0;i<length;i++){
            int j=ran.nextInt(60);
            strb.append(str.charAt(j));
        }
        return strb.toString();
    }

}
