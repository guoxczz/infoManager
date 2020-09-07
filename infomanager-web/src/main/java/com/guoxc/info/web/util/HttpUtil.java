package com.guoxc.info.web.util;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import com.guoxc.info.exception.GeneralException;
import javax.servlet.ServletException;
import javax.servlet.ServletInputStream;
import javax.servlet.ServletRequest;

import com.guoxc.info.utils.StringUtil;
import com.guoxc.info.web.control.StockControl;
import org.apache.commons.httpclient.Header;
import org.apache.commons.httpclient.HeaderElement;
import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.HttpVersion;
import org.apache.commons.httpclient.NameValuePair;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.commons.httpclient.methods.InputStreamRequestEntity;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.multipart.ByteArrayPartSource;
import org.apache.commons.httpclient.methods.multipart.FilePart;
import org.apache.commons.httpclient.methods.multipart.MultipartRequestEntity;
import org.apache.commons.httpclient.methods.multipart.Part;
import org.apache.commons.httpclient.methods.multipart.StringPart;
import org.apache.commons.httpclient.params.HttpConnectionManagerParams;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.apache.commons.io.IOUtils;

/**
 * HttpUtil
 *
 * @author 陆晓明
 *
 */
public class HttpUtil {
    private final static Logger log = LoggerFactory.getLogger(HttpUtil.class);
    private String protocol;
    private String host;
    private int port;
    private String dir;
    private String uri;
    private final static int DefaultPort = 80;
    private final static String ProtocolSeparator = "://";
    private final static String PortSeparator = ":";
    private final static String HostSeparator = "/";
    private final static String DirSeparator = "/";



    public static String sendHttpPostEntity(String sendUrl, String content) throws GeneralException {
        log.info("***sendHttpPostEntity sendUrl= " + sendUrl);
        String result = doHttpPostInter(sendUrl, null, content, false, 30);
        //      log.info("***sendHttpPostEntity result= " + result);
        return result;
    }

    public static String sendHttpPostEntityNolog(String sendUrl, String content) throws GeneralException {
        log.info("***sendHttpPostEntity sendUrl= " + sendUrl);
        String result = doHttpPostInter(sendUrl, null, content, false, 30);
        //      log.info("***sendHttpPostEntity result= " + result);

        return result;
    }

    public static String sendHttpPostEntityNolog(String sendUrl, String content, int timeOut) throws GeneralException {
        log.info("***sendHttpPostEntity sendUrl= " + sendUrl);
        String result = doHttpPostInter(sendUrl, null, content, false, timeOut);
        //      log.info("***sendHttpPostEntity result= " + result);

        return result;
    }

    public static String sendHttpNameValuePair(String sendUrl, HashMap param) throws GeneralException {
        log.info("***sendHttpNameValuePair sendUrl= " + sendUrl);
        // log.info("***sendHttpNameValuePair param= "+param);
        List<NameValuePair> list = new ArrayList();
        if (param != null) {
            for (Iterator it = param.keySet().iterator(); it.hasNext();) {
                String key = (String)it.next();
                NameValuePair data = new NameValuePair(key, (String)param.get(key));
                list.add(data);
            }
        }
        NameValuePair[] datas = list.toArray(new NameValuePair[]{});

        String result = doHttpPostInter(sendUrl, datas, null, true, 60); // 需要大于inter// 工程的超时时间，以免出现// 断开的管道
        // log.info("***sendHttpNameValuePair result= " + result);
        return result;

    }

    // public static String doHttpPostInter(String sUrl,String key, String
    // sContent, String outUrl) throws GeneralException {
    // return doHttpPostInter(key, sContent, sUrl, true, 60,outUrl, null, null);
    //
    // }

    public static String sendHttpNameValuePair(String sendUrl, HashMap param, int timeOut) throws GeneralException {
        log.info("***sendHttpNameValuePair sendUrl= " + sendUrl);
        // log.info("***sendHttpNameValuePair param= "+param);
        List<NameValuePair> list = new ArrayList();
        if (param != null) {
            for (Iterator it = param.keySet().iterator(); it.hasNext();) {
                String key = (String)it.next();
                NameValuePair data = new NameValuePair(key, (String)param.get(key));
                list.add(data);
            }
        }
        NameValuePair[] datas = list.toArray(new NameValuePair[]{});
        log.info("util_timeOut =" + timeOut);
        String result = doHttpPostInter(sendUrl, datas, null, true, timeOut); // 需要大于inter
        // 工程的超时时间，以免出现
        // 断开的管道
        // log.info("***sendHttpNameValuePair result= " + result);
        return result;

    }

    public static String sendHttpNameValuePair(String sendUrl, HashMap param, int connectTimeOut, int readTimeOut)
            throws GeneralException {
        log.info("***sendHttpNameValuePair sendUrl= " + sendUrl);
        // log.info("***sendHttpNameValuePair param= "+param);
        List<NameValuePair> list = new ArrayList();
        if (param != null) {
            for (Iterator it = param.keySet().iterator(); it.hasNext();) {
                String key = (String)it.next();
                NameValuePair data = new NameValuePair(key, (String)param.get(key));
                list.add(data);
            }
        }
        NameValuePair[] datas = list.toArray(new NameValuePair[]{});
        log.info("util_connectTimeOut =" + connectTimeOut);
        log.info("util_readTimeOut =" + readTimeOut);
        String result = doHttpPostInter(sendUrl, datas, null, true, connectTimeOut, readTimeOut); // 需要大于inter
        // 工程的超时时间，以免出现
        // 断开的管道
        // log.info("***sendHttpNameValuePair result= " + result);
        return result;

    }

    public static String sendRestRequestJSON(String url, String xml, String timeout) {
        String result = null;
        try {
            log.info("start post soap requst Url : " + url);
            // ByteArrayInputStream in = new
            // ByteArrayInputStream(xml.getBytes());
            // 127.0.0.1:8888/CenterProvideServlet? 211.138.17.45:8888
            // 10.87.30.24:27100/CenterProvideServlet
            // /127.0.0.1:8080/HomePerspect
            // log.info("soapXml="+soapXml);
            URL urlAddr = new URL(url);
            PostMethod httpPost = new PostMethod(urlAddr.toString());
            HttpClient client = new HttpClient();
            client.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
            client.getParams().setParameter("http.protocol.content-charset", "UTF-8");
            if (StringUtil.isNotBlank(timeout)) {
                client.getParams().setParameter("http.socket.timeout", new Integer(Integer.valueOf(timeout) * 1000));
            }

            httpPost.addRequestHeader("Content-Type", "application/json");
            httpPost.setRequestEntity(new InputStreamRequestEntity(new ByteArrayInputStream(xml.getBytes("UTF-8"))));
            client.executeMethod(httpPost);
            log.info("getStatusCode:" + httpPost.getStatusCode());
            if (httpPost.getStatusCode() == HttpStatus.SC_OK) {
                result = httpPost.getResponseBodyAsString();
            }
            httpPost.releaseConnection();

        } catch (Exception e) {
            log.error("调用接口失败", e);
        }

        return result;
    }

    private static String doHttpPostInter(String sendUrl, NameValuePair[] data, String sContent,
        boolean isNameValuePair, int timeOut) throws GeneralException {

        String str = null;
        try {
            HttpClient httpclient = new HttpClient();
            // if (userName != null && passwd != null) {
            // Credentials defaultcreds = new
            // UsernamePasswordCredentials(userName, passwd);
            // httpclient.getState().setCredentials(AuthScope.ANY,
            // defaultcreds);
            // }
            httpclient.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
            httpclient.getParams().setParameter("http.protocol.content-charset", "UTF-8");
            HostConfiguration hostconfig = new HostConfiguration();
            if (timeOut > 0) {
                // httpclient.getParams().setParameter("http.socket.timeout",
                // new Integer(1));
                // httpclient.getParams().setIntParameter("http.socket.timeout",
                // 500);
                HttpConnectionManagerParams managerParams = httpclient.getHttpConnectionManager().getParams();
                // 设置连接超时时间(单位毫秒)
                managerParams.setConnectionTimeout(timeOut * 1000);
                managerParams.setSoTimeout(timeOut * 1000);

            }
            hostconfig.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_0);
            PostMethod httppost = new PostMethod(sendUrl);
            if (isNameValuePair) {
                httppost.setRequestBody(data);
            } else {
                httppost.setRequestEntity(new InputStreamRequestEntity(new ByteArrayInputStream(sContent.getBytes())));
            }
            try {
                httpclient.executeMethod(httppost);
                if (httppost.getStatusCode() == HttpStatus.SC_OK) {
                    str = httppost.getResponseBodyAsString();
                } else {
                    throw new GeneralException("9999", "Unexpected failure: " + httppost.getStatusLine().toString());
                }
            } finally {
                httppost.releaseConnection();
            }
        } catch (Exception e) {
            log.info("*** 接口调用失败 : ", e);
        } finally {
            log.info("end post requst content to : " + sendUrl);
        }
        return str;

    }

    // 可配置连接超时时间和读取超时时间
    private static String doHttpPostInter(String sendUrl, NameValuePair[] data, String sContent,
        boolean isNameValuePair, int connectTimeOut, int readTimeOut) throws GeneralException {

        String str = null;
        try {
            HttpClient httpclient = new HttpClient();
            // if (userName != null && passwd != null) {
            // Credentials defaultcreds = new
            // UsernamePasswordCredentials(userName, passwd);
            // httpclient.getState().setCredentials(AuthScope.ANY,
            // defaultcreds);
            // }
            httpclient.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
            httpclient.getParams().setParameter("http.protocol.content-charset", "UTF-8");
            HostConfiguration hostconfig = new HostConfiguration();
            if (connectTimeOut > 0) {
                // httpclient.getParams().setParameter("http.socket.timeout",
                // new Integer(1));
                // httpclient.getParams().setIntParameter("http.socket.timeout",
                // 500);
                HttpConnectionManagerParams managerParams = httpclient.getHttpConnectionManager().getParams();
                // 设置连接超时时间(单位毫秒)
                managerParams.setConnectionTimeout(connectTimeOut * 1000);
                // 读取超时时间
                if (readTimeOut > 0) {
                    managerParams.setSoTimeout(readTimeOut * 1000);
                } else {
                    managerParams.setSoTimeout(connectTimeOut * 1000);
                }

            }
            hostconfig.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_0);
            PostMethod httppost = new PostMethod(sendUrl);
            if (isNameValuePair) {
                httppost.setRequestBody(data);
            } else {
                httppost.setRequestEntity(new InputStreamRequestEntity(new ByteArrayInputStream(sContent.getBytes())));
            }
            try {
                httpclient.executeMethod(httppost);
                if (httppost.getStatusCode() == HttpStatus.SC_OK) {
                    str = httppost.getResponseBodyAsString();
                } else {
                    throw new GeneralException("9999", "Unexpected failure: " + httppost.getStatusLine().toString());
                }
            } finally {
                httppost.releaseConnection();
            }
        } catch (Exception e) {
            log.info("*** 接口调用失败 : ", e);
        } finally {
            log.info("end post requst content to : " + sendUrl);
        }
        return str;

    }

    public static String doHttpPostMultipart(String sendUrl, HashMap param, int timeOut) throws GeneralException {

        if (sendUrl == null) {
            throw new GeneralException("ACMS1004");
        }
        log.info("start post requst content to : " + sendUrl);

        List<StringPart> list = new ArrayList<StringPart>();
        if (param != null) {
            for (Iterator it = param.keySet().iterator(); it.hasNext();) {
                String key = (String)it.next();
                StringPart data = new StringPart(key, (String)param.get(key), "UTF-8");
                list.add(data);
            }
        }
        StringPart[] parts = list.toArray(new StringPart[0]);

        String str = null;
        try {
            HttpClient httpclient = new HttpClient();
            httpclient.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
            httpclient.getParams().setParameter("http.protocol.content-charset", "UTF-8");
            httpclient.getParams().setParameter("http.protocol.content-type", "multipart/form-data");
            if (timeOut > 0) {
                httpclient.getHttpConnectionManager().getParams().setConnectionTimeout(new Integer(timeOut * 1000));
                httpclient.getHttpConnectionManager().getParams().setSoTimeout(new Integer(timeOut * 1000));
            }
            PostMethod httppost = new PostMethod(sendUrl);

            MultipartRequestEntity requestEntity = new MultipartRequestEntity(parts, httpclient.getParams());
            // NameValuePair[] data = {new NameValuePair("xmlhead", sheader),new
            // NameValuePair("xmlbody", sContent)};
            httppost.setRequestEntity(requestEntity);

            try {

                httpclient.executeMethod(httppost);
                if (httppost.getStatusCode() == HttpStatus.SC_OK) {

                    str = httppost.getResponseBodyAsString();
                } else {
                    throw new GeneralException("9999", "Unexpected failure: " + httppost.getStatusLine().toString());
                }
            } finally {
                httppost.releaseConnection();
            }
        } catch (Exception e) {
            String sMsg = "Http协议post方法发送字符流时候出现异常：" + e.getMessage();
            log.error(sMsg, e);
            throw new GeneralException("9999", sMsg, e);
        } finally {
            log.info("end post requst content to : " + sendUrl);
        }
        return str;

    }

    public static String doHttpPost(String sendUrl, String key, String sContent) throws GeneralException {

        String str = null;
        try {
            PostMethod httpPost = new PostMethod(sendUrl);
            HttpClient client = new HttpClient();

            client.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
            client.getParams().setParameter("http.protocol.content-charset", "UTF-8");
            client.getParams().setParameter("http.socket.timeout", new Integer(200 * 1000));
            NameValuePair[] data = {new NameValuePair(key, sContent)};
            httpPost.setRequestBody(data);
            // httpPost.setRequestBody(in);
            // client.setTimeout(60000);
            // httpPost.setHttp11(false); // weblogic8.1的闭环测试需要此行代码
            client.executeMethod(httpPost);
            if (httpPost.getStatusCode() == HttpStatus.SC_OK) {
                String responseXml = httpPost.getResponseBodyAsString();
                httpPost.releaseConnection();
                str = responseXml;
                log.info("***return:\n " + responseXml);
                // log.info("***return:\n RspType= "+responseXml.substring(responseXml.indexOf("<Response>"),
                // responseXml.indexOf("</Response>")));
            } else {

                log.info("**httpPost.getStatusCode() =" + httpPost.getStatusCode());
            }

        } catch (Exception e) {
            throw new GeneralException("9999", e.getMessage(), e);
        }
        return str;

    }

    public static String doHttpPost(String sendUrl, String sContent) throws GeneralException {

        if (sendUrl == null || "".equals(sendUrl)) {

            return readFileXml();
        }

        //      log.info("start post requst content to : " + sendUrl);
        //      log.info("start post requst content  : " + sContent);
        String str = null;
        try {
            HttpClient httpclient = new HttpClient();
            httpclient.getParams().setParameter("http.protocol.version", HttpVersion.HTTP_1_1);
            httpclient.getParams().setParameter("http.protocol.content-charset", "GBK");
            httpclient.getParams().setParameter("http.socket.timeout", new Integer(30 * 1000));

            GetMethod httppost = new GetMethod(sendUrl);
//            if(StringUtil.isNotBlank(sContent)){
//                httppost.setRequestEntity(new InputStreamRequestEntity(new ByteArrayInputStream(sContent.getBytes())));
//            }
            try {
                httpclient.executeMethod(httppost);
                if (httppost.getStatusCode() == HttpStatus.SC_OK) {
                    str =   IOUtils.toString(httppost.getResponseBodyAsStream());
                } else {
                    throw new GeneralException("9999", "Unexpected failure: " + httppost.getStatusLine().toString());
                }
            } finally {
                httppost.releaseConnection();
            }
        } catch (Exception e) {
            log.error("*** 接口调用失败 : ", e);
            throw new GeneralException("9999", e.getMessage(), e);
        }
        //      log.info("end post requst content return = " + str);
        return str;

    }

    public static String readFileXml() throws GeneralException {
        try {
            InputStream is = Thread.currentThread().getContextClassLoader().getResourceAsStream("test/xml.txt");
            InputStreamReader reader = new InputStreamReader(is, "utf-8");
            BufferedReader rFile = new BufferedReader(reader);
            String line = rFile.readLine();
            // number = String.valueOf((Long.valueOf(line)+1));
            // RandomAccessFile rFile = new
            // RandomAccessFile("E:/bak/xml1.txt","rw");
            // rFile.seek(0);
            StringBuffer xml = new StringBuffer();
            // String line = rFile.readLine();
            while (line != null) {
                xml.append(line);
                line = rFile.readLine();
            }

            rFile.close();
            return xml.toString();
        } catch (Exception e) {
            throw new GeneralException("9999", e.getMessage(), e);
        }

    }

    public static String GBKtoUTF8(String inPara) {
        char temChr;

        int ascChr;

        int i;

        String retStr = new String("");

        if (inPara == null) {
            inPara = "";
        }

        for (i = 0; i < inPara.length(); i++) {
            temChr = inPara.charAt(i);
            ascChr = temChr + 0;
            if (ascChr >= 128) {
                retStr = retStr + "&#x" + Integer.toHexString(ascChr) + ";";
            } else {
                retStr = retStr + temChr;
            }
            if (i % 10000 == 0) {
                // MyLog.debugLog("inPara.length(i)"+i);
            }
        }

        // logger.info("retStr:"+retStr );

        return retStr;
    }

    /**
     * HNHttpServletUtils.getReceivedSoapXmlMessage(request); 从请求中获取报文文本.
     *
     * @param servletRequest
     *            收到的请求
     * @return 报文文本
     * @throws CBossException
     */
    public static String getRequestIntyFromRequest(ServletRequest servletRequest) throws ServletException {
        ServletInputStream inStream = null;
        ByteArrayOutputStream swapStream = null;
        String strReq = null;
        try {
            inStream = servletRequest.getInputStream();
            swapStream = new ByteArrayOutputStream();
            byte[] buff = new byte[1024];// buff用于存放循环读取的临时数据
            int rc = 0;
            while ((rc = inStream.read(buff, 0, 1024)) > 0) {
                swapStream.write(buff, 0, rc);
            }
            byte[] in_b = swapStream.toByteArray();// in_b为转换之后的结果
            strReq = new String(in_b, "UTF-8");
        } catch (Exception ex) {
            log.error("getRequestIntyFromRequest error:", ex);
        }
        return StringUtil.replaceHtml(strReq);
    }

    /**
     * 上传文件（包括图片），用StringPart代替NameValuePair，文件支持File和byte[]，超时时间默认为30S
     *
     * @param url
     *            请求URL
     * @param paramsMap
     *            参数和值
     * @return
     */
    public static String postUriEntity(String url, Map<String, Object> paramsMap, int timeOut)
            throws Exception {
        List<NameValuePair> nameValuePostBody = new LinkedList<NameValuePair>();
        List<Part> partList = new ArrayList<Part>();
        Part[] parts = null;
        String result = null;

        if (StringUtil.isBlank(url) || paramsMap.isEmpty()) {
            throw new Exception("ACMS1005");
        }
        try {
            String[] paramStr = url.split("[?]", 2);
            if (paramStr == null || paramStr.length != 2) {

            } else {
                String[] paramArray = paramStr[1].split("[&]");
                if (paramArray == null) {

                } else {
                    for (String param : paramArray) {
                        if (param == null || "".equals(param.trim())) {
                            continue;
                        }
                        String[] keyValue = param.split("[=]", 2);
                        if (keyValue == null || keyValue.length != 2) {
                            continue;
                        }
                        // hw.addNV(keyValue[0], keyValue[1]);
                        NameValuePair nvp = getNV(keyValue[0], keyValue[1]);
                        nameValuePostBody.add(nvp);
                    }
                }
            }
            Iterator<String> iterator = paramsMap.keySet().iterator();
            while (iterator.hasNext()) {
                String key = iterator.next();
                Object value = paramsMap.get(key);
                if (value instanceof File) {
                    FilePart filePart = new FilePart(key, (File)value);
                    filePart.setCharSet("UTF-8");
                    partList.add(filePart);
                } else if (value instanceof byte[]) {
                    byte[] byteVlue = (byte[])value;
                    ByteArrayPartSource btyePart = new ByteArrayPartSource(key, byteVlue);
                    FilePart filePart = new FilePart(key, btyePart);
                    filePart.setCharSet("UTF-8");
                    partList.add(filePart);
                } else {
                    if (value != null && !"".equals(value)) {
                        NameValuePair nvp = getNV(key, String.valueOf(value));
                        nameValuePostBody.add(nvp);
                    } else {
                        NameValuePair nvp = getNV(key, "");
                        nameValuePostBody.add(nvp);
                    }
                }
            }

            for (NameValuePair nameValuePair : nameValuePostBody) {
                StringPart stringPart = new StringPart(nameValuePair.getName(), nameValuePair.getValue(), "UTF-8");
                partList.add(stringPart);
            }
            parts = partList.toArray(new Part[0]);
            HttpClient client = new HttpClient();
            PostMethod method = null;
            method = new PostMethod(paramStr[0]);
            // 对于MIME类型的请求，httpClient建议全用MulitPartRequestEntity进行包装
            MultipartRequestEntity mre = new MultipartRequestEntity(parts, method.getParams());
            method.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
            method.getParams().setContentCharset("UTF-8");
            method.setRequestEntity(mre);
            if (timeOut > 0) {
                client.getHttpConnectionManager().getParams().setConnectionTimeout(timeOut * 1000);// 设置连接时间
                client.getHttpConnectionManager().getParams().setSoTimeout(timeOut * 1000);// 设置连接时间
            } else {
                client.getHttpConnectionManager().getParams().setConnectionTimeout(30 * 1000);// 设置连接时间
                client.getHttpConnectionManager().getParams().setSoTimeout(30 * 1000);// 设置连接时间
            }
            client.getHttpConnectionManager().getParams().setConnectionTimeout(50000);// 设置连接时间
            int status = 404;
            try {
                status = client.executeMethod(method);
                result = method.getResponseBodyAsString();
            } finally {
                method.releaseConnection();
            }
        } catch (Exception e) {
            log.error("*** 接口调用失败 : ", e);
        } finally {
            log.info("end post requst content to : " + url);
        }
        return result;
    }

    private static NameValuePair getNV(String name, String value) {
        NameValuePair nvp = new NameValuePair();
        nvp.setName(name);
        nvp.setValue(value);
        return nvp;
    }

    private void parseUrl(String url) {
        protocol = null;
        host = null;
        port = DefaultPort;
        dir = "/";
        uri = dir;

        if (url == null || url.length() == 0) {
            return;
        }
        String u = url.trim();
        boolean MeetProtocol = false;
        int pos = u.indexOf(ProtocolSeparator);
        if (pos > 0) {
            MeetProtocol = true;
            protocol = u.substring(0, pos);
            pos += ProtocolSeparator.length();
        }
        int posStartDir = 0;
        if (MeetProtocol) {
            int pos2 = u.indexOf(PortSeparator, pos);
            if (pos2 > 0) {
                host = u.substring(pos, pos2);
                pos2 = pos2 + PortSeparator.length();
                int pos3 = u.indexOf(HostSeparator, pos2);
                String PortStr = null;
                if (pos3 > 0) {
                    PortStr = u.substring(pos2, pos3);
                    posStartDir = pos3;
                } else {
                    int pos4 = u.indexOf("?");
                    if (pos4 > 0) {
                        PortStr = u.substring(pos2, pos4);
                        posStartDir = -1;
                    } else {
                        PortStr = u.substring(pos2);
                        posStartDir = -1;
                    }
                }
                try {
                    port = Integer.parseInt(PortStr);
                } catch (Exception e) {
                }
            } else {
                pos2 = u.indexOf(HostSeparator, pos);
                if (pos2 > 0) {
                    host = u.substring(pos, pos2);
                    posStartDir = pos2;
                } else {
                    host = u.substring(pos);
                    posStartDir = -1;
                }
            }

            pos = u.indexOf(HostSeparator, pos);
            pos2 = u.indexOf("?");
            if (pos > 0 && pos2 > 0) {
                uri = u.substring(pos, pos2);
            } else if (pos > 0 && pos2 < 0) {
                uri = u.substring(pos);
            }
        }

        if (posStartDir >= 0) {
            int pos2 = u.lastIndexOf(DirSeparator, posStartDir);
            if (pos2 > 0) {
                dir = u.substring(posStartDir, pos2 + 1);
            }
        }

    }

    private static String getResponseContentType(HttpMethod method) {
        Header contenttype = method.getResponseHeader("Content-Type");
        if (contenttype == null) {
            return null;
        }
        String ret = null;
        try {
            HeaderElement[] hes = contenttype.getElements();
            if (hes != null && hes.length > 0) {
                ret = hes[0].getName();
            }
        } catch (Exception e) {
        }
        return ret;
    }

    private static String getResponseContentTypeString(HttpMethod method) {
        Header contenttype = method.getResponseHeader("Content-Type");
        if (contenttype == null) {
            return null;
        }
        return contenttype.getValue();
    }

    private static byte[] getResponseBody(HttpMethod method) {
        byte[] input = null;

        InputStream is = null;
        ByteArrayOutputStream baos = null;
        try {
            is = method.getResponseBodyAsStream();
            baos = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int size = is.read(buffer);
            while (size > 0) {
                baos.write(buffer, 0, size);
                size = is.read(buffer);
            }
            input = baos.toByteArray();
            return input;
        } catch (Exception e) {
            return null;
        } finally {
            try {
                if (baos != null) {
                    baos.close();
                }
                if (is != null) {
                    is.close();
                }
            } catch (Exception e1) {
            }
        }
    }
}

