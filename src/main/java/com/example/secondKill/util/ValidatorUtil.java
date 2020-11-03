package com.example.secondKill.util;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ValidatorUtil {
    private static final Pattern MOBILE_PATTERN = Pattern.compile("1\\d{10}");

    public static boolean isMobile(String str){
        System.err.println("调用函数" +str);
        if(StringUtils.isEmpty(str)) return false;
        Matcher matcher = MOBILE_PATTERN.matcher(str);
        return matcher.matches();
    }
}
