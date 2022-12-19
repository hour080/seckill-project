package com.example.seckill.utils;


import org.apache.commons.codec.digest.DigestUtils;

/**
 * MD5工具类，实现对用户密码的加密
 * @author hourui
 * @version 1.0
 * @Description
 * @date 2022/12/18 10:23
 */
public class MD5Util {

    private static final String SALT = "1a2b3c4d";

    public static String md5(String src){
        return DigestUtils.md5Hex(src);
    }
    /**
     * 第一次客户端MD5加密   MD5(input + salt)
     * @param inputPass
     * @author hourui
     * @date 2022/12/18 10:33
     * @return java.lang.String
     */
    public static String passToServerPass(String inputPass){
        String str = "" + SALT.charAt(4) + SALT.charAt(0) + inputPass + SALT.charAt(2) + SALT.charAt(5);
        return md5(str);
    }

    /**
     * 第二次服务器端加密  MD5(from + salt) from = MD5(input + salt)
     * @param fromPass
     * @param salt
     * @author hourui
     * @date 2022/12/18 10:34
     * @return java.lang.String
     */
    public static String serverToDBPass(String fromPass, String salt){
        String str = "" + salt.charAt(0) + salt.charAt(2) + fromPass + salt.charAt(4) + salt.charAt(5);
        return md5(str);
    }

    public static String inputToDBPass(String inputPass, String salt){
        String fromPass = passToServerPass(inputPass);
        return serverToDBPass(fromPass, salt);
    }

    public static void main(String[] args) {
        //659f3ac88180c0ac6a5ad1870e0275d0
        System.out.println(passToServerPass("123456"));
        //7d24ee5cb6fe6409eef371052ef29db3
        System.out.println(serverToDBPass("42a16487dc8d31f7b318caf71bdfe021", "favourite"));
        //7d24ee5cb6fe6409eef371052ef29db3
        System.out.println(inputToDBPass("123456", "favourite"));
    }


}
