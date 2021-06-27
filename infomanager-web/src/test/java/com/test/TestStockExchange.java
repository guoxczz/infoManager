package com.test;

import com.guoxc.info.exception.GeneralException;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.List;

public class TestStockExchange {

    public static void main(String[] args) throws Exception {
        List<String>  lines =  IOUtils.readLines( new FileInputStream( new File("E:\\stock\\data\\dayDetailwarn\\20210617\\603929.txt")),"utf-8");
       long buyVol = 0;
       long sellVol = 0;
       float price = 0;
        for(int i=0;i<lines.size();i++){
            String line = lines.get(i);
            long diff = 0;
            String type = null;
            if(line.indexOf("sell hidden exchange")>-1){
                String [] colArray  = line.split(" ");
                diff = Long.parseLong( colArray[4]);
                sellVol = sellVol+ Long.parseLong( colArray[4]) ;
                price = Float.valueOf(colArray[6].replace("curr_price:",""));
                type = "S";
            }else if(line.indexOf("buy hidden exchange")>-1){
                String [] colArray  = line.split(" ");
                buyVol = buyVol +Long.parseLong( colArray[5]) ;
                diff = Long.parseLong( colArray[5]);
                price = Float.valueOf(colArray[7].replace("curr_price:",""));
                type = "B";
            }
            if(type != null){
                System.out.println("buyVol: "+buyVol + " ;"+" sellVol: "+sellVol+"; "+type+" "+diff+"  ;price: "+price);
            }

        }


    }



    public static String readFileXml() throws GeneralException {
        try {
            InputStream is =  Thread.currentThread().getContextClassLoader().getResourceAsStream("test/xml.txt");
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

}
