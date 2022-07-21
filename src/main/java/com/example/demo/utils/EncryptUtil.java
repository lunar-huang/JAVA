package com.example.demo.utils;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.LoggerFactory;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * 加解密
 *
 */
public class EncryptUtil {
    private static  final org.slf4j.Logger logger= LoggerFactory.getLogger(EncryptUtil.class);

    private EncryptUtil() {
    }

    public static EncryptUtil getEncryptUtil() {
        return (new EncryptUtil());
    }

    /**
     * 加密
     */
    public static String encrypt(String src) {
        if(src==null) {
            return null;
        }
        if (StringUtils.isBlank(src)) {
            return null;
        }
        try {
            return Base64.getEncoder().encodeToString(src.getBytes(StandardCharsets.UTF_8));
        } catch (RuntimeException e) {
            logger.error("decrypt fail!");
            return null;
        }
    }

    /**
     * 解密
     */
    public static String decrypt(String src) {
        if (StringUtils.isBlank(src)) {
            return null;
        }
        try {
            byte[] asBytes = Base64.getDecoder().decode(src);
            return new String(asBytes, StandardCharsets.UTF_8);
        } catch (RuntimeException e) {
            logger.error("decrypt fail!");
            return null;
        }
    }

}
