package com.utils;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class StringUtil {

    //Java字符集名称常量定义
    public static final String CHARSET_UTF8 = "UTF-8";
    public static final String CHARSET_GBK = "GBK";
    public static final String CHARSET_UTF16 = "UTF-16";
    public static final String CHARSET_Unicode = "unicode";
    public static final String CHARSET_Iso8859_1 = "ISO8859-1";
    public static final String CHARSET_BIG5 = "BIG5";

    /**
     * 半角转换为全角
     *
     * @param input
     * @return
     */
    public static String ToDBC(String input) {
        char[] c = input.toCharArray();
        for (int i = 0; i < c.length; i++) {
            if (c[i] == 12288) {
                c[i] = (char) 32;
                continue;
            }
            if (c[i] > 65280 && c[i] < 65375)
                c[i] = (char) (c[i] - 65248);
        }
        return new String(c);
    }

    /**
     * 判断字符是否为空
     *
     * @param str
     * @return boolean
     */
    public static boolean isEmpty(String str) {
        if (TextUtils.isEmpty(str) || "".equals(str.trim()) || "null".equalsIgnoreCase(str))
            return true;
        else return false;
    }

    public static boolean isNotEmpty(String str) {
        return !isEmpty(str);
    }

    /**
     * 验证是否是 合法邮箱格式
     *
     * @param email
     * @return boolean
     */
    public static boolean isIegalEmail(String email) {
        return Pattern.compile("\\w+([-_.]\\w+)*@\\w+([-_.]\\w+)*\\.\\w+([-_.]\\w+)*").matcher(email).matches();
    }

    /**
     * 验证是否存在中文字符
     *
     * @param str
     * @return boolean
     */
    public static boolean isContainsChineseCharacter(String str) {
        return Pattern.compile("[.@\\w]*[\u4e00-\u9fa5]+[.@\\w]*").matcher(str).matches();
    }

    /**
     * <![CDATA[]]>
     *
     * @param s
     * @return
     */
    public static String getCDATA(String s) {
        StringBuilder sb = new StringBuilder();
        sb.append("<![CDATA[").append(s).append("]]>");
        return sb.toString();
    }

    /**
     * string 转int，吃掉exception
     *
     * @param s
     * @return
     */
    public static int formatInt(String s) {
        int id = -1;
        try {
            id = Integer.valueOf(s);
        } catch (NumberFormatException ex) {
            id = -1;
        }

        return id;
    }

    /**
     * 返回MB
     *
     * @param size
     * @return
     */
    public static String formatFileSize(long size) {
        try {
            double d = size / (1024 * 1024 * 1024);
            DecimalFormat df2 = new DecimalFormat("#,###,###,##0.00");
            double dd2dec = new Double(df2.format(d)).doubleValue();
            return String.valueOf(dd2dec);
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 替换指定字符串
     *
     * @param input
     * @param search
     * @param replacement
     * @return string
     */
    public static String replace(String input, String search, String replacement) {
        int pos = input.indexOf(search);
        if (pos != -1) {
            StringBuilder buffer = new StringBuilder();
            int lastPos = 0;
            do {
                buffer.append(input.substring(lastPos, pos)).append(replacement);
                lastPos = pos + search.length();
                pos = input.indexOf(search, lastPos);
            } while (pos != -1);
            buffer.append(input.substring(lastPos));
            input = buffer.toString();
        }
        return input;
    }

    /**
     * 汉字转码方法
     *
     * @param text 源字符串
     * @return 转码后的字符串
     */
    public static String encode(String text) {
        char[] utfBytes = text.toCharArray();
        StringBuilder result = new StringBuilder();
        for (int i = 0; i < utfBytes.length; i++) {
            if (utfBytes[i] == '&') {
                result.append("&amp;");
                continue;
            }
            if (utfBytes[i] == '<') {
                result.append("&lt;");
                continue;
            }
            if (utfBytes[i] == '>') {
                result.append("&gt;");
                continue;
            }
            if (utfBytes[i] == '\"') {
                result.append("&quot;");
                continue;
            }
            if (utfBytes[i] == '\'') {
                result.append("&apos;");
                continue;
            }
            String hexB = Integer.toHexString(utfBytes[i]);
            if (hexB.length() == 2 && utfBytes[i] > 127) {
                result.append("&#x").append("00").append(hexB).append(";");
            } else if (hexB.length() > 2) {
                result.append("&#x").append(hexB).append(";");
            } else {
                result.append(utfBytes[i]);
            }
        }
        if (result.length() == 0) {
            result.append(" ");
        }
        return result.toString();
    }

    public static byte[] getBytes(String str) {
        return getBytes(str, null);
    }

    public static byte[] getBytes(String str, String charsetName) {
        byte[] ret = null;
        if (isEmpty(str))
            return ret;
        try {
            if (!isEmpty(charsetName)) {
                ret = str.getBytes(charsetName);
            } else {
                ret = str.getBytes("utf-8");
            }
        } catch (UnsupportedEncodingException e) {
            ret = str.getBytes();
        }
        return ret;
    }

    public static boolean isEqual(String paramString1, String paramString2) {
        if ((isEmpty(paramString2)) || (isEmpty(paramString1))) {
            return false;
        }

        return paramString1.equals(paramString2);
    }

    /**
     * 去除字符串中的空格、回车、换行符、制表符
     *
     * @param str
     * @return
     */
    public static String replaceBlank(String str) {
        String dest = "";
        if (str != null) {
            Pattern p = Pattern.compile("\\s*|\t|\r|\n");
            Matcher m = p.matcher(str);
            dest = m.replaceAll("");
        }
        return dest;
    }

    public static boolean isMobileNO(String mobiles) {
        Pattern p = Pattern
                .compile("[1][358]\\d{9}");
        Matcher m = p.matcher(mobiles);
        System.out.println(m.matches() + "---");
        return m.matches();
    }

    public static boolean isCode(String str) {
        Pattern p = Pattern.compile("^[0-9]{6}$"); // 验证手机号
        Matcher m = p.matcher(str);
        boolean b = m.matches();
        return b;
    }
}
