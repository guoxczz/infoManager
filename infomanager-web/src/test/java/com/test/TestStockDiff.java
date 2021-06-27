package com.test;

import com.guoxc.info.web.common.StockCalucate;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.*;

public class TestStockDiff {
   private static String space ="\t   ";
    public static void main(String[] args){

        Map detailInfo = new HashMap();
        List<String>  lines = null;
        try {
            lines = IOUtils.readLines( new FileInputStream( new File("E:\\stock\\data\\dayDetail\\20210625\\sh603929.txt")),"utf-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        String lastLine = lines.get(1);
        for(int i=2;i< lines.size(); i++){
            StockCalucate.detailCompare(lastLine,lines.get(i),detailInfo);
            lastLine =  lines.get(i);
       }

    }

}
