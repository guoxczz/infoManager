package com.test;

import com.alibaba.fastjson.JSON;
import com.guoxc.info.utils.DateUtil;
import gui.ava.html.image.generator.HtmlImageGenerator;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.ApplicationContext;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.net.InetAddress;
import java.net.URL;
import java.net.URLDecoder;
import java.net.UnknownHostException;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

import com.tencentyun.TLSSigAPIv2;
import org.xhtmlrenderer.swing.Java2DRenderer;

import javax.imageio.ImageIO;

public class Test {
private static long diffTime = 3*24*60*60*1000;
       public static void main(String[] args){


           try {
                String HtmlTemplateStr =

                       "<style type=\"text/css\">"+

                               "body {background-color: yellow}"+

                               "h1 {background-color: #00ff00}"+

                               "h2 {background-color: transparent}"+

                               "p {background-color: rgb(250,0,255)}"+

                               "p.no2 {background-color: gray; padding: 20px;}"+

                               "</style>"+



                               "<img src=\"http://p1.img.cctvpic.com/photoAlbum/page/performance/img/2018/8/21/1534782617583_424.jpg\" width=\"660\" height=\"380\" alt=\"cctv.com图片\" />"+



                               "<h1>我是h1标题</h1>"+



                               "<div id=\"\" class=\"\">"+

                               "<img src=\"http://115.182.9.166/cportal/photoAlbum/page/performance/img/2017/5/26/1495792113125_553.jpg\" width=\"480\" height=\"300\" alt=\"cportal图片\" />"+

                               "<h1>这是标题 1</h1>"+

                               "<h2>这是标题 2</h2>"+

                               "<p>这是段落</p>"+

                               "<p class=\"no2\">这个段落设置了内边距。</p>"+

                               "<input type=\"button\" value=\"1\" onclick=\"点我\" />"+

                               "<input type=\"text\" id=\"ww\" name=\"\" value=\"hahahah\"/>"+

                               "</div>"+



                               "<table border=\"1\" background=\"http://www.w3school.com.cn/i/eg_bg_06.gif\">"+

                               "<tr>"+

                               "<th>Month</th>"+

                               "<th>Savings</th>"+

                               "</tr>"+

                               "<tr>"+

                               "<td>January</td>"+

                               "<td><p>这是第一行</p></td>"+

                               "</tr>"+

                               "<tr>"+

                               "<td><img src=\"http://p1.img.cctvpic.com/photoAlbum/page/performance/img/2018/8/21/1534782617583_424.jpg\" width=\"660\" height=\"380\" alt=\"cctv.com图片\" /></td>"+

                               "<td><h1>这是第二行</h1></td>"+

                               "</tr>"+

                               "</table>"+



                               "<p>有序列表：</p>"+

                               "<ol>"+

                               "<li>打开冰箱门</li>"+

                               "<li>把大象放进去</li>"+

                               "<li>关上冰箱门</li>"+

                               "</ol>"+



                               "<p>无序列表：</p>"+

                               "<ul>"+

                               "<li>雪碧</li>"+

                               "<li>可乐</li>"+

                               "<li>凉茶</li>"+

                               "</ul>";
               HtmlImageGenerator htmlImage = new HtmlImageGenerator();
//               String content =  IOUtils.toString(new URL("https://www.baidu.com"));
               System.out.println(HtmlTemplateStr);
               htmlImage.loadHtml(HtmlTemplateStr);
               Dimension DEFAULT_SIZE = new Dimension(800, 800);
               htmlImage.setSize(DEFAULT_SIZE);
               htmlImage.saveAsImage(new File ("D:/test/baidu.jpg"));


//               Java2DRenderer d = new Java2DRenderer("https://www.tencent.com/en-us",800,900);
//
//               ImageIO.write(d.getImage(),"jpg",new File("D:/test/baidu.jpg"));

               StringUtils.equals("a","b");
               System.out.println( StringUtils.stripStart("yabcx  ", "xyz")) ;
               Map param = new HashMap();
               param.put("sz002726_lastPrice","7.49");
               param.put("sz002726_avg20","379855");
               System.out.println( JSON.toJSONString(param));

//               String key = "asdfasdfasdfzcxvaaa";
//
//               System.out.println(key.length());
//               String aa = AesEncryptApache.encrypt("mcbcd", key);
//               System.out.println(aa);
//               String bb = AesEncryptApache.decrypt("cX4M6uwluo7SPL6ZQGIM/FPhUeV34WnUDHDDNx0WW1E=", key);
//               System.out.println(bb);

           } catch (Throwable e) {
               e.printStackTrace();
           }




//           try {
//              File file = new File("E:\\stock\\tdx");
//               printRecentFile(file);
//
//
//           } catch (Exception e) {
//               e.printStackTrace();
//           }
//           System.out.println(  URLDecoder.decode(""));

    }




    private static void printRecentFile(    File file ){
           if(file.isDirectory()){
               File[] subFile = file.listFiles();
               for(int i=0;i<subFile.length;i++){
                   printRecentFile(subFile[i]);
               }
           }else{
             long modifyTime =   file.lastModified();
             long currTime =  System.currentTimeMillis();


             if(currTime-modifyTime<diffTime){
                 String filePath = file.getAbsolutePath();
                 if(filePath.indexOf("vipdoc\\sz\\lday\\")>-1 || filePath.indexOf("vipdoc\\sh\\lday\\")>-1){
                     return;
                 }
                 System.out.println(file.getAbsolutePath() +" "+DateUtil.format(new Timestamp(modifyTime),"yyyy-MM-dd HH:mm:ss"));

             }

           }


    }


}
