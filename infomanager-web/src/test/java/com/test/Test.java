package com.test;

import org.springframework.context.ApplicationContext;

import java.net.InetAddress;
import java.net.URLDecoder;
import java.net.UnknownHostException;

public class Test {

       public static void main(String[] args){

           try {
               String host = InetAddress.getLocalHost().getHostAddress();
               ApplicationContext context = null;

           } catch (UnknownHostException e) {
               e.printStackTrace();
           }
           System.out.println(  URLDecoder.decode(""));

    }

}
