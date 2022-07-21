package com.example.demo.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 身份证相关工具类
 */
public class IdCardUtil {

    IdCardUtil(){

    }

    /**
     * 是否有效身份证号
     *
     * @param idCard 身份证号，支持18位、15位和港澳台的10位
     */
    public static boolean isValidCard(String idCard) {
        idCard = idCard.trim();
        int length = idCard.length();
        switch (length) {
            // 18位身份证
            case 18:
                return isValidCard18(idCard);
            // 15位身份证
            case 15:
                return isValidCard15(idCard);
            default:
                return false;
        }
    }

    /**
     * 将15位身份证号码转换为18位
     *
     * @param idCard15 15位身份编码
     * @return 18位身份编码
     */
    public static String convertIdCardTo18(String idCard15) {
        StringBuilder idCard18;
        if (idCard15.length() != CHINA_ID_MIN_LENGTH) {
            return null;
        }
        if (isMatch(NUMBERS, idCard15)) {
            // 获取出生年月日
            String birthday = idCard15.substring(6, 12);
            Date birthDate = strToDate(birthday, YY_MM_DD);
            // 获取出生年
            int sYear = year(birthDate);
            // 理论上2000年之后不存在15位身份证，可以不要此判断
            if (sYear > 2000) {
                sYear -= 100;
            }
            idCard18 = new StringBuilder().append(idCard15, 0, 6).append(sYear).append(idCard15.substring(8));
            // 获取校验位
            char sVal = getCheckCode18(idCard18.toString());
            idCard18.append(sVal);
        } else {
            return null;
        }
        return idCard18.toString();
    }


    /**
     * 判断18位身份证的合法性
     *
     * 公民身份号码是特征组合码，由十七位数字本体码和一位数字校验码组成，排列顺序从左至右依次为：六位数字地址码，八位数字出生日期码，三位数字顺序码和一位数字校验码。
     * 顺序码: 表示在同一地址码所标识的区域范围内，对同年、同月、同 日出生的人编定的顺序号，顺序码的奇数分配给男性，偶数分配 给女性。
     * 第1、2位数字表示：所在省份的代码；第3、4位数字表示：所在城市的代码；第5、6位数字表示：所在区县的代码，第7~14位数字表示：出生年、月、日
     * 第15、16位数字表示：所在地的派出所的代码；第17位数字表示性别：奇数表示男性，偶数表示女性
     * 第18位数字是校检码，用来检验身份证的正确性。校检码可以是0~9的数字，有时也用x表示
     * 第十八位数字(校验码)的计算方法为：
     *
     * @param idCard 待验证的身份证
     * @return 是否有效的18位身份证
     */
    private static boolean isValidCard18(String idCard) {
        if (CHINA_ID_MAX_LENGTH != idCard.length()) {
            return false;
        }

        //校验生日
        if (!isBirthday(idCard.substring(6, 14))) {
            return false;
        }

        // 前17位
        String code17 = idCard.substring(0, 17);
        // 第18位
        char code18 = Character.toLowerCase(idCard.charAt(17));
        if (isMatch(NUMBERS, code17)) {
            // 获取校验位
            char val = getCheckCode18(code17);
            return val == code18;
        }
        return false;
    }

    /**
     * 验证15位身份编码是否合法
     *
     * @param idCard 身份编码
     * @return 是否合法
     */
    private static boolean isValidCard15(String idCard) {
        if (CHINA_ID_MIN_LENGTH != idCard.length()) {
            return false;
        }
        if (isMatch(NUMBERS, idCard)) {
            // 省份
            String proCode = idCard.substring(0, 2);
            if (null == cityCodes.get(proCode)) {
                return false;
            }
            //校验生日（两位年份，补充为19XX）
            return isBirthday("19" + idCard.substring(6, 12));
        } else {
            return false;
        }
    }


    /**
     * 获得18位身份证校验码
     * 计算方式：
     * 将前面的身份证号码17位数分别乘以不同的系数。从第一位到第十七位的系数分别为：7 9 10 5 8 4 2 1 6 3 7 9 10 5 8 4 2
     * 将这17位数字和系数相乘的结果相加
     * 用加出来和除以11，看余数是多少
     * 余数只可能有0 1 2 3 4 5 6 7 8 9 10这11个数字。其分别对应的最后一位身份证的号码为1 0 X 9 8 7 6 5 4 3 2
     * 通过上面得知如果余数是2，就会在身份证的第18位数字上出现罗马数字的Ⅹ。如果余数是10，身份证的最后一位号码就是2
     * @param code17 18位身份证号中的前17位
     * @return 第18位
     */
    private static char getCheckCode18(String code17) {
        int sum = getPowerSum(code17.toCharArray());
        return getCheckCode18(sum);
    }

    /**
     * 将power和值与11取模获得余数进行校验码判断
     *
     * @param iSum 加权和
     * @return 校验位
     */
    private static char getCheckCode18(int iSum) {
        switch (iSum % 11) {
            case 10:
                return '2';
            case 9:
                return '3';
            case 8:
                return '4';
            case 7:
                return '5';
            case 6:
                return '6';
            case 5:
                return '7';
            case 4:
                return '8';
            case 3:
                return '9';
            case 2:
                return 'x';
            case 1:
                return '0';
            case 0:
                return '1';
            default:
                return SPACE;
        }
    }

    /**
     * 将身份证的每位和对应位的加权因子相乘之后，再得到和值
     *
     * @param iArr 身份证号码的数组
     * @return 身份证编码
     */
    private static int getPowerSum(char[] iArr) {
        int iSum = 0;
        if (power.length == iArr.length) {
            for (int i = 0; i < iArr.length; i++) {
                iSum += Integer.valueOf(String.valueOf(iArr[i])) * power[i];
            }
        }
        return iSum;
    }

    /**
     * 根据日期获取年
     *
     * @param date 日期
     * @return 年的部分
     */
    public static int year(Date date) {
        Calendar ca = Calendar.getInstance();
        ca.setTime(date);
        return ca.get(Calendar.YEAR);
    }

    /**
     * 验证是否为生日<br>
     * 只支持以下几种格式：
     * <ul>
     * <li>yyyyMMdd</li>
     * <li>yyyy-MM-dd</li>
     * <li>yyyy/MM/dd</li>
     * <li>yyyy.MM.dd</li>
     * <li>yyyy年MM月dd日</li>
     * </ul>
     *
     * @param value 值
     * @return 是否为生日
     */
    public static boolean isBirthday(CharSequence value) {
        if (isMatch(BIRTHDAY, value)) {
            Matcher matcher = BIRTHDAY.matcher(value);
            if (matcher.find()) {
                int year = Integer.parseInt(matcher.group(1));
                int month = Integer.parseInt(matcher.group(3));
                int day = Integer.parseInt(matcher.group(5));
                return isBirthday(year, month, day);
            }
        }
        return false;
    }

    /**
     * 验证是否为生日
     *
     * @param year  年，从1900年开始计算
     * @param month 月，从1开始计数
     * @param day   日，从1开始计数
     * @return 是否为生日
     */
    public static boolean isBirthday(int year, int month, int day) {
        // 验证年
        int thisYear = year(new Date());
        if (year < 1900 || year > thisYear) {
            return false;
        }

        // 验证月
        if (month < 1 || month > 12) {
            return false;
        }

        // 验证日
        if (day < 1 || day > 31) {
            return false;
        }
        // 检查几个特殊月的最大天数
        if (day == 31 && (month == 4 || month == 6 || month == 9 || month == 11)) {
            return false;
        }
        if (month == 2) {
            // 在2月，非闰年最大28，闰年最大29
            return day < 29 || (day == 29 && isLeapYear(year));
        }
        return true;
    }

    /**
     * 是否闰年
     *
     * @param year 年
     * @return 是否闰年
     */
    private static boolean isLeapYear(int year) {
        return new GregorianCalendar().isLeapYear(year);
    }

    /**
     * 将字符串转换成指定格式的日期
     *
     * @param str        日期字符串.
     * @param dateFormat 日期格式. 如果为空，默认为:yyyy-MM-dd HH:mm:ss.
     * @return
     */
    private static Date strToDate(final String str, String dateFormat) {
        if (str == null || str.trim().length() == 0) {
            return null;
        }
        try {
            if (dateFormat == null || dateFormat.length() == 0) {
                dateFormat = DATE_FORMAT;
            }
            DateFormat fmt = new SimpleDateFormat(dateFormat);
            return fmt.parse(str.trim());
        } catch (Exception ex) {
            return null;
        }
    }

    /**
     * 给定内容是否匹配正则
     *
     * @param pattern 模式
     * @param content 内容
     * @return 正则为null或者""则不检查，返回true，内容为null返回false
     */
    private static boolean isMatch(Pattern pattern, CharSequence content) {
        if (content == null || pattern == null) {
            // 提供null的字符串为不匹配
            return false;
        }
        return pattern.matcher(content).matches();
    }


    /**
     * 中国公民身份证号码最小长度。
     */
    private static final int CHINA_ID_MIN_LENGTH = 15;
    /**
     * 中国公民身份证号码最大长度。
     */
    private static final int CHINA_ID_MAX_LENGTH = 18;
    /**
     * 每位加权因子
     */
    private static final int[] power = {7, 9, 10, 5, 8, 4, 2, 1, 6, 3, 7, 9, 10, 5, 8, 4, 2};

    /**
     * 省市代码表
     */
    private static Map<String, String> cityCodes = new HashMap<>();

    /**
     * 默认日期模板
     */
    private static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    private static final String YY_MM_DD = "yyMMdd";
    public static final char SPACE = ' ';
    /**
     * 数字
     */
    public static final Pattern NUMBERS = Pattern.compile("\\d+");
    /**
     * 生日
     */
    public static final Pattern BIRTHDAY = Pattern.compile("^(\\d{2,4})([/\\-.年]?)(\\d{1,2})([/\\-.月]?)(\\d{1,2})日?$");

    static {
        cityCodes.put("11", "北京");
        cityCodes.put("12", "天津");
        cityCodes.put("13", "河北");
        cityCodes.put("14", "山西");
        cityCodes.put("15", "内蒙古");
        cityCodes.put("21", "辽宁");
        cityCodes.put("22", "吉林");
        cityCodes.put("23", "黑龙江");
        cityCodes.put("31", "上海");
        cityCodes.put("32", "江苏");
        cityCodes.put("33", "浙江");
        cityCodes.put("34", "安徽");
        cityCodes.put("35", "福建");
        cityCodes.put("36", "江西");
        cityCodes.put("37", "山东");
        cityCodes.put("41", "河南");
        cityCodes.put("42", "湖北");
        cityCodes.put("43", "湖南");
        cityCodes.put("44", "广东");
        cityCodes.put("45", "广西");
        cityCodes.put("46", "海南");
        cityCodes.put("50", "重庆");
        cityCodes.put("51", "四川");
        cityCodes.put("52", "贵州");
        cityCodes.put("53", "云南");
        cityCodes.put("54", "西藏");
        cityCodes.put("61", "陕西");
        cityCodes.put("62", "甘肃");
        cityCodes.put("63", "青海");
        cityCodes.put("64", "宁夏");
        cityCodes.put("65", "新疆");
        cityCodes.put("71", "台湾");
        cityCodes.put("81", "香港");
        cityCodes.put("82", "澳门");
        cityCodes.put("91", "国外");
    }
}

