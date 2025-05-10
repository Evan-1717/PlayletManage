package com.rabbiter.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static String calculateMD5(String input) {
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            byte[] bytes = digest.digest(input.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static String getFileMD5(File file) throws IOException, NoSuchAlgorithmException {
        // 创建MessageDigest实例，指定MD5算法
        MessageDigest digest = MessageDigest.getInstance("MD5");

        // 使用FileInputStream读取文件内容
        try (FileInputStream fis = new FileInputStream(file)) {
            byte[] byteArray = new byte[1024]; // 定义一个缓冲区
            int bytesCount;

            // 读取文件内容，并更新摘要
            while ((bytesCount = fis.read(byteArray)) != -1) {
                digest.update(byteArray, 0, bytesCount);
            }
        }

        // 获取文件的MD5值，并转换为十六进制字符串
        byte[] bytes = digest.digest();
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(Integer.toString((b & 0xff) + 0x100, 16).substring(1));
        }
        return sb.toString();
    }

    public static String transitionTime(Long date, String pattern) {
        SimpleDateFormat sdf = new SimpleDateFormat(pattern);
        return sdf.format(new Date(date));
    }

    public static String getTime9() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss SSS");
        return sdf.format(new Date());
    }

    public static String getTime6() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return sdf.format(new Date());
    }

    public static String getTime6_s() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd-HHmmss");
        return sdf.format(new Date());
    }

    public static String getTime3() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        return sdf.format(new Date());
    }

    public static String decodeUnicode(String input) {
        Pattern pattern = Pattern.compile("\\\\u([0-9a-fA-F]{4})");
        Matcher matcher = pattern.matcher(input);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            String hex = matcher.group(1);
            char ch = (char) Integer.parseInt(hex, 16);
            matcher.appendReplacement(sb, String.valueOf(ch));
        }
        matcher.appendTail(sb);
        return sb.toString();
    }
}
