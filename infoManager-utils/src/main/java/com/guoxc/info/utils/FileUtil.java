package com.guoxc.info.utils;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileUtil {


    public static ArrayList readFileXml(File file,String charset)
    {
        ArrayList list = new ArrayList();

        try{


        InputStream is =  new FileInputStream(file);

        InputStreamReader reader = new InputStreamReader(is, charset);
        BufferedReader rFile = new BufferedReader(reader );
        String line = rFile.readLine();
        while(line != null )
        {
            list.add(line);

            line = rFile.readLine();

        }

        rFile.close();

        }catch(Exception e){
            e.printStackTrace();;
        }

        return list;

    }


}
