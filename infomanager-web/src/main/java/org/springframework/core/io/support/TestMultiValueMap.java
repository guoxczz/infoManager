package org.springframework.core.io.support;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;

public class TestMultiValueMap {
 public static final String FACTORIES_RESOURCE_LOCATION ="META-INF/spring.factories";

    public static void main( String[] args) {


        try {


            Enumeration<URL> urls = Thread.currentThread().getContextClassLoader().getResources(FACTORIES_RESOURCE_LOCATION)  ;

            while(urls.hasMoreElements()){
               URL url =   urls.nextElement();
               System.out.println("aa**"+url);
            }


            Enumeration<URL> urls2 = ClassLoader.getSystemResources("META-INF/spring.factories");
            while(urls2.hasMoreElements()){
                URL url =   urls2.nextElement();
                System.out.println(url);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println( TestMultiValueMap.class.getResource(""));
        System.out.println(TestMultiValueMap.class.getResource("/"));

//        MultiValueMap<String,String> stringMultiValueMap = new LinkedMultiValueMap<>();
//
//        stringMultiValueMap.add("早班 9:00-11:00", "周一");
//        stringMultiValueMap.add("早班 9:00-11:00", "周二");
//        stringMultiValueMap.add("中班 13:00-16:00", "周三");
//        stringMultiValueMap.add("早班 9:00-11:00", "周四");
//        stringMultiValueMap.add("测试1天2次 09:00 - 12:00", "周五");
//        stringMultiValueMap.add("测试1天2次 09:00 - 12:00", "周六");
//        stringMultiValueMap.add("中班 13:00-16:00", "周日");
//        //打印所有值
//        Set<String> keySet = stringMultiValueMap.keySet();
//        for (String key : keySet) {
//            List<String> values = stringMultiValueMap.get(key);
//            System.out.println(StringUtils.join(values.toArray()," ")+":"+key);
//        }
    }
}
