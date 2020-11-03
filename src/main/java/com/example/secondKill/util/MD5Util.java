package com.example.secondKill.util;

import org.apache.commons.codec.digest.DigestUtils;

public class MD5Util {
    private final static String SALT = "1a2b3c4d";

    public static String md5(String str) {
        return DigestUtils.md5Hex(str);
    }

    /**
     * 将用户输入的密码进行一次MD5加密
     * FormPass = MD5(inputPass + 固定salt);
     *
     * @param inputPass
     * @return
     */
    public static String inputPassToFormPass(String inputPass) {
        String str = "" + SALT.charAt(0) + SALT.charAt(2) + inputPass + SALT.charAt(5) + SALT.charAt(4);
        return md5(str);
    }

    /**
     * 将加密过一次的密码进行二次MD5加密
     * FormPass = MD5(inputPass + 固定salt);
     *
     * @param
     * @return
     */
    public static String formPassToDbPass(String FormPass, String SALT) {
        String str = "" + SALT.charAt(0) + SALT.charAt(2) + FormPass + SALT.charAt(5) + SALT.charAt(4);
        return md5(str);
    }

    public static String inputPassToDbPass(String inputPass, String SALT) {
        return formPassToDbPass(inputPassToFormPass(inputPass), SALT);
    }

    public static void main(String[] args) {
        System.err.println(inputPassToDbPass("123456", SALT));
        System.err.println(inputPassToFormPass("123456"));//761e0c4f27fff7766114a80b1bdbf74e
        //d3b1294a61a07da9b49b6e22b2cbd7f9
    }
}
