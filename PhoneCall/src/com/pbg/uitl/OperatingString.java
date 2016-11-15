package com.pbg.uitl;

import com.pbg.bluetooth.BluetoothInstance;

import xu.ye.R.string;

public class OperatingString {
    public boolean ASCII = false;
    
    
    /**
     * 得到16进制字符串，过滤掉不正确的字符
     *
     * @return 字符串
     */
    public static String getHexString(String s) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            if (('0' <= c && c <= '9') || ('a' <= c && c <= 'f') ||
                    ('A' <= c && c <= 'F')) {
                sb.append(c);
            }
        }
        if ((sb.length() % 2) != 0) {
            sb.deleteCharAt(sb.length());
        }
        return sb.toString();
    }
    
    /**
     * 在getHexString基础上每两个字符后加空格
     * @return 格式化后的字符串
     */
    private String getFormattedString(String s) {
   //    String s = getHexString();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < s.length() - 1; i += 2) {
            sb.append(s.substring(i, i + 2));
            sb.append(' ');
        }
        return sb.toString();
    }
}
