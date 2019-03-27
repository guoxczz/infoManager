package com.guoxc.info.utils;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * 字符串工具类
 */
public final class StringUtil {

    /**
     * Private Constructor
     **/
    private StringUtil() {
    }

    /**
     * @return
     * @Description: 生成UUID
     * @ReturnType String
     * @author: qinzheng
     * @Created 2015年8月31日 上午11:31:30
     */
    public static String uuid() {
        return UUID.randomUUID().toString();
    }

    /**
     * int字符串装int值
     *
     * @param intstr
     * @return
     * @Description:
     * @ReturnType int
     * @author: qinzheng
     * @Created 2015年8月21日 上午10:44:01
     */
    public static int toInt(String intstr) {
        try {
            return Integer.parseInt(intstr);
        } catch (Exception e) {
            return 0;
        }
    }
    
   	/**
   	 * 判断字符串是否为空或""
   	 * 
   	 * @param str
   	 * @return boolean
   	 */
   	public static boolean isNull(String str){
   	    if(str==null||str.trim().equals("")||str.toLowerCase().equals("null")){
   	        return true;	   	     
   	    } else {
   	        return false;
   	    }
   	}

    /**
     * 判断字符串是否非null && 非空字符
     *
     * @param param
     * @return
     */
    public static boolean isNotEmpty(String param) {
        return param != null && !"".equals(param.trim());
    }

    /**
     * 判断字符串是否为null || 空字符串
     *
     * @param param
     * @return
     */
    public static boolean isEmpty(String param) {
        return param == null || "".equals(param.trim());
    }

    /**
     * 判断字符串是否为null || 空字符串 || null字符串
     *
     * @param param
     * @return
     */
    public static boolean isBlank(String param) {
        return param == null || "".equals(param.trim()) || "null".equals(param.trim().toLowerCase());
    }

    /**
     * 判断字符串是否为非null && 非空字符串 && 非null字符串
     *
     * @param param
     * @return
     */
    public static boolean isNotBlank(String param) {
        return param != null && !"".equals(param.trim()) && !"null".equals(param.trim().toLowerCase());
    }

    /**
     * 判断是否为数字
     *
     * @param str
     * @return True为数字
     */
    public static boolean isNum(String str) {
        String regex = "^(-?\\d+)(\\.\\d+)?$";
        return matchRegex(str, regex);
    }

    public static String trim2Empty(String str) {
        return isEmpty(str) ? "" : str.trim();
    }

    private static boolean matchRegex(String value, String regex) {
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(value);
        return matcher.matches();
    }

    /**
     * @param str
     * @param encoding
     * @param toEncoding
     * @return
     * @throws Exception
     * @Description: 字符串字符转换
     * @ReturnType String
     * @author: qinzheng
     * @Created 2015年9月10日 上午9:52:45
     */
    public static String convertCharacter(String str, String encoding, String toEncoding) throws Exception {
        return new String(str.getBytes(encoding), toEncoding);
    }

    /**
     * author 高利强
     * <p>
     * 2016年7月1日 下午5:28:43
     * <p>
     * 判断逗号分隔的字符串是否包含特定数字字符
     *
     * @param subStr
     * @parame str
     */
    public static boolean dealStr(String str, String subStr) {
        boolean flag = false;
        String[] array = str.split(",");
        for (String element : array) {
            if (element.equals(subStr)) {
                flag = true;
            }
            ;
        }
        return flag;
    }

    /**
     * author 高利强
     * <p>
     * 2016年7月11日 上午10:50:14
     * <p>
     * 岗位类型特殊处理
     */
    public static String getPostType(String postType) {

        if (postType.length() < 1) {
            return postType;
        }

        String postTypeStr = "";
        String[] postTypeAarry = postType.split(",");
        for (String element : postTypeAarry) {
            if ("03".equals(element)) {
                postTypeStr = "03";
                break;
            }
        }
        return postTypeStr;
    }


    /**
     * 手机号验证
     *
     * @param str
     * @return 验证通过返回true
     */
    public static boolean isMobile(String str) {
        Pattern p;
        Matcher m;
        boolean b = false;
        if (StringUtil.isEmpty(str)) {
            return b;
        }
        p = Pattern.compile("^[1][3,4,5,7,8][0-9]{9}$"); // 验证手机号
        m = p.matcher(str);
        b = m.matches();
        return b;
    }

    /**
     * @return
     * @throws Exception
     * @Description: 输出身份证前6后4中间星号
     * @ReturnType String
     * @author: lvchong
     * @Created 2016年9月27日 上午1:50:45
     */
    public static String dealIdCard(String idCard) throws Exception {
        String first = idCard.substring(0, 6);
        String last = idCard.substring(idCard.length() - 4, idCard.length());
        StringBuilder sb = new StringBuilder();
        sb.append(first);
        sb.append(last);
        int d = idCard.length() - sb.length();
        sb.delete(sb.length() - 4, sb.length());
        for (int i = 0; i < d; i++) {
            sb.append("*");
        }
        sb.append(last);
        return sb.toString();
    }

    /**
     * author 高利强
     * <p>
     * 2016年12月19日 下午5:48:39
     * <p>
     * 在线渠道编码自增1后前面自动补零
     */
    public static String frontCompWithZore(String currentNum) {
        AtomicInteger num = new AtomicInteger(Integer.parseInt(currentNum));
        return String.format("%07d", num.incrementAndGet());
    }

    public static String getAndCheckNullFromMap(Map<String, String> params, String keyStr, String errMsg)
            throws Exception {

        if (StringUtil.isEmpty(params.get(keyStr))) {
            throw new Exception(errMsg);
        }
        return params.get(keyStr);
    }

    /***
     *
     * @Description: 字符集
     * @ClassName ENCODING
     * @author: qinzheng
     * @Created 2015年9月10日 上午9:48:26
     */
    public interface ENCODING {
        String UTF_8 = "UTF-8";
        String GBK = "GBK";
        String ISO_8858_1 = "ISO-8859-1";
        String GB2312 = "GB2312";
    }

    public static String replaceHtml(String str) {
        String tmpstr = str;
        tmpstr = replace(tmpstr, "<br>", "\n");
        tmpstr = replace(tmpstr, "&lt;", "<");
        tmpstr = replace(tmpstr, "&gt;", ">");
        tmpstr = replace(tmpstr, "&amp;", "&");
        tmpstr = replace(tmpstr, "&apos;", "\'");
        tmpstr = replace(tmpstr, "&quot;", "\"");
        tmpstr = replace(tmpstr, "&nbsp;", " ");
        return tmpstr;
    }

    public static String replace(String text, String repl, String with) {
        return replace(text, repl, with, -1);
    }

    public static String replace(String text, String repl, String with, int max) {
        if (text == null || isEmpty(repl) || with == null || max == 0) {
            return text;
        }
        StringBuffer buf = new StringBuffer(text.length());
        int start = 0;
        for (int end = 0; (end = text.indexOf(repl, start)) != -1;) {
            buf.append(text.substring(start, end)).append(with);
            start = end + repl.length();
            if (--max == 0) {
                break;
            }
        }
        buf.append(text.substring(start));
        return buf.toString();
    }

    public static String null2Empty(String s) {
        if ("null".equals(s) || null == s) {
            return "";
        }
        return s;
    }
}
