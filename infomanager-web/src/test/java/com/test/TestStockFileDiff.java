package com.test;

import com.guoxc.info.web.common.StockCalucate;

import java.util.*;

public class TestStockFileDiff {
    private static String line =  "002835~11.59~11.22~11.66~12589~4909~7680~11.50~5~11.48~2~11.45~46~11.43~29~11.42~36~11.59~364~11.60~7~11.62~92~11.63~334~11.64~152~~20210607093006~\n" +
            "002835~11.63~11.22~11.66~14123~6443~7680~11.52~37~11.51~32~11.50~68~11.48~2~11.45~50~11.63~47~11.64~152~11.65~251~11.66~287~11.68~23~~20210607093009~";
   private static String space ="\t   ";
    public static void main(String[] args){




       String[] lines = line.split("\\n");
       String[] lastInfos = lines[0].split("~");
        String[] stockInfoCols = lines[1].split("~");
        System.out.println(lastInfos[0]+ " 当前："+ stockInfoCols[1]+"  上次："+ lastInfos[1]+"  今开："+ stockInfoCols[3]);
         List priceList = new ArrayList();

        Map<Float,Long> lastInfo = new HashMap();
        Map<Float,Long> currInfo = new HashMap();
        float currPirce = Float.valueOf(stockInfoCols[1]);

        // 记录成不同价位交量变化，跟成交量比较
        float[] lastBuyPrices = new  float[5];
        float[] lastSellPrices = new  float[5];
        long[] lastBuyVols = new  long[5];
        long[] lastSellVols = new  long[5];
        float[] buyPrices = new  float[5];
        float[] sellPrices = new  float[5];
        long[] buyVols = new  long[5];
        long[] sellVols = new  long[5];

        lastInfo.put(Float.valueOf(lastInfos[7]),Long.valueOf(lastInfos[8]));
        lastInfo.put(Float.valueOf(lastInfos[9]),Long.valueOf(lastInfos[10]));
        lastInfo.put(Float.valueOf(lastInfos[11]),Long.valueOf(lastInfos[12]));
        lastInfo.put(Float.valueOf(lastInfos[13]),Long.valueOf(lastInfos[14]));
        lastInfo.put(Float.valueOf(lastInfos[15]),Long.valueOf(lastInfos[16]));
        lastInfo.put(Float.valueOf(lastInfos[17]),Long.valueOf(lastInfos[18]));
        lastInfo.put(Float.valueOf(lastInfos[19]),Long.valueOf(lastInfos[20]));
        lastInfo.put(Float.valueOf(lastInfos[21]),Long.valueOf(lastInfos[22]));
        lastInfo.put(Float.valueOf(lastInfos[23]),Long.valueOf(lastInfos[24]));
        lastInfo.put(Float.valueOf(lastInfos[25]),Long.valueOf(lastInfos[26]));

        currInfo.put(Float.valueOf(stockInfoCols[7]),Long.valueOf(stockInfoCols[8]));
        currInfo.put(Float.valueOf(stockInfoCols[9]),Long.valueOf(stockInfoCols[10]));
        currInfo.put(Float.valueOf(stockInfoCols[11]),Long.valueOf(stockInfoCols[12]));
        currInfo.put(Float.valueOf(stockInfoCols[13]),Long.valueOf(stockInfoCols[14]));
        currInfo.put(Float.valueOf(stockInfoCols[15]),Long.valueOf(stockInfoCols[16]));
        currInfo.put(Float.valueOf(stockInfoCols[17]),Long.valueOf(stockInfoCols[18]));
        currInfo.put(Float.valueOf(stockInfoCols[19]),Long.valueOf(stockInfoCols[20]));
        currInfo.put(Float.valueOf(stockInfoCols[21]),Long.valueOf(stockInfoCols[22]));
        currInfo.put(Float.valueOf(stockInfoCols[23]),Long.valueOf(stockInfoCols[24]));
        currInfo.put(Float.valueOf(stockInfoCols[25]),Long.valueOf(stockInfoCols[26]));

        for(int i=7;i<26;i++){

            lastBuyPrices[0]=Float.valueOf(lastInfos[7]); //买1
            lastBuyVols[0]= Long.valueOf(lastInfos[8]);
            lastBuyPrices[1]=Float.valueOf(lastInfos[9]);
            lastBuyVols[1]= Long.valueOf(lastInfos[10]);
            lastBuyPrices[2]=Float.valueOf(lastInfos[11]);
            lastBuyVols[2]= Long.valueOf(lastInfos[12]);
            lastBuyPrices[3]=Float.valueOf(lastInfos[13]);
            lastBuyVols[3]= Long.valueOf(lastInfos[14]);
            lastBuyPrices[4]=Float.valueOf(lastInfos[15]);
            lastBuyVols[4]= Long.valueOf(lastInfos[16]);

            lastSellPrices[0]=Float.valueOf(lastInfos[17]); //卖1
            lastSellVols[0]= Long.valueOf(lastInfos[18]);
            lastSellPrices[1]=Float.valueOf(lastInfos[19]);
            lastSellVols[1]= Long.valueOf(lastInfos[20]);
            lastSellPrices[2]=Float.valueOf(lastInfos[21]);
            lastSellVols[2]= Long.valueOf(lastInfos[22]);
            lastSellPrices[3]=Float.valueOf(lastInfos[23]);
            lastSellVols[3]= Long.valueOf(lastInfos[24]);
            lastSellPrices[4]=Float.valueOf(lastInfos[25]);
            lastSellVols[4]= Long.valueOf(lastInfos[26]);
        }

        for(int i=9; i<28;i++){

            buyPrices[0]=Float.valueOf(stockInfoCols[7]); //买1
            buyVols[0]= Long.valueOf(stockInfoCols[8]);
            buyPrices[1]=Float.valueOf(stockInfoCols[9]);
            buyVols[1]= Long.valueOf(stockInfoCols[10]);
            buyPrices[2]=Float.valueOf(stockInfoCols[11]);
            buyVols[2]= Long.valueOf(stockInfoCols[12]);
            buyPrices[3]=Float.valueOf(stockInfoCols[13]);
            buyVols[3]= Long.valueOf(stockInfoCols[14]);
            buyPrices[4]=Float.valueOf(stockInfoCols[15]);
            buyVols[4]= Long.valueOf(stockInfoCols[16]);

            sellPrices[0]=Float.valueOf(stockInfoCols[17]); //卖1
            sellVols[0]= Long.valueOf(stockInfoCols[18]);
            sellPrices[1]=Float.valueOf(stockInfoCols[19]);
            sellVols[1]= Long.valueOf(stockInfoCols[20]);
            sellPrices[2]=Float.valueOf(stockInfoCols[21]);
            sellVols[2]= Long.valueOf(stockInfoCols[22]);
            sellPrices[3]=Float.valueOf(stockInfoCols[23]);
            sellVols[3]= Long.valueOf(stockInfoCols[24]);
            sellPrices[4]=Float.valueOf(stockInfoCols[25]);
            sellVols[4]= Long.valueOf(stockInfoCols[26]);
        }


        for(int i=0;i<5;i++ ){
            if(!priceList.contains(lastBuyPrices[i])){
                priceList.add(lastBuyPrices[i]) ;
            }
            if(!priceList.contains(lastSellPrices[i])){
                priceList.add(lastSellPrices[i]) ;
            }

            if(!priceList.contains(buyPrices[i])){
                priceList.add(buyPrices[i]) ;
            }
            if(!priceList.contains(sellPrices[i])){
                priceList.add(sellPrices[i]) ;
            }
        }
         Collections.sort(priceList);

        long mayDiffBuyVol = 0;
        long mayDiffSellVol=0;

        long tmpShowDiffBuyVol = 0;
        long tmpShowDiffSellVol=0;

        float tmpMinBuyExchange  = currPirce > buyPrices[0]?  buyPrices[0] :currPirce ;

        for(int i=0;i<lastBuyPrices.length;i++){
            if(lastBuyPrices[i]>tmpMinBuyExchange){
                mayDiffBuyVol = mayDiffBuyVol+ lastBuyVols[i];
            }else if(lastBuyPrices[i] ==tmpMinBuyExchange){
                if(lastBuyPrices[i] ==buyPrices[0]){
                    //买一价位和当前价位一样，成交量相减
                    if(lastBuyVols[i] >buyVols[0]){  //比较成交量增减
                        mayDiffBuyVol = mayDiffBuyVol+ lastBuyVols[i]-buyVols[0];
                    }else{  //买一存在追加
                        tmpShowDiffBuyVol = tmpShowDiffBuyVol+ buyVols[0]- lastBuyVols[i];
                    }
                }
            }else if(lastBuyPrices[i]>=buyPrices[4]){
                 Long tmp =  currInfo.get(lastBuyPrices[i]);
                 if(tmp == null){
                     tmpShowDiffBuyVol = tmpShowDiffBuyVol+ lastBuyVols[i];
                 }else{
                     tmpShowDiffBuyVol = tmpShowDiffBuyVol+ lastBuyVols[i]-tmp;
                 }
            }
        }


        float tmpMaxSellExchange  = currPirce > sellPrices[0]? currPirce: sellPrices[0]  ;
        for(int i=0;i<lastSellPrices.length;i++){
            if(lastSellPrices[i]<tmpMaxSellExchange){
                mayDiffSellVol = mayDiffSellVol+ lastSellVols[i];
            }else if(lastSellPrices[i] ==tmpMaxSellExchange){
                if(lastSellPrices[i] ==sellPrices[0]){
                    if(lastSellVols[i] >sellVols[0]){  //比较成交量增减
                        mayDiffSellVol = mayDiffSellVol+ lastSellVols[i]-sellVols[0];
                    }else{  //买一存在追加
                        tmpShowDiffSellVol = tmpShowDiffSellVol+ sellVols[0]- lastSellVols[i];
                    }
                }
            }else if(lastSellPrices[i] < sellPrices[0]){
                mayDiffSellVol = mayDiffSellVol+ lastSellVols[i];
            }
            else if(lastSellPrices[i]<=sellPrices[4]){
                Long tmp =  currInfo.get(lastSellPrices[i]);
                if(tmp == null){
                    tmpShowDiffSellVol = tmpShowDiffSellVol+ lastSellVols[i];
                }else{
                    tmpShowDiffSellVol = tmpShowDiffSellVol+ lastSellVols[i]-tmp;
                }
            }
        }


        StringBuilder sb = new StringBuilder();
        sb.append(" ").append("价格").append(space).append("last").append(space).append("当前").append(space).append("差量").append(space).append("说明").append("\n");

        for(int i=0;i<priceList.size();i++){
             float price = (float) priceList.get(i);
             if(lastInfo.containsKey(price)){  // 存在历史价位
                 if(currInfo.containsKey(price)){ // 存在历史价位 也存在当前价位
                     long tempDiff =  lastInfo.get(price) - currInfo.get(price);
                     sb.append(price).append(space).append(lastInfo.get(price)).append(space).append(currInfo.get(price)).append(space).append(  tempDiff).append(space).append("-").append(space).append("\n");


                 }else{ //不存在当前价位
                      if(price<=lastBuyPrices[0]){  //历史价位在买盘

                         if(price>buyPrices[4]){  // 代表成交了，或者撤单了
                             if(price <buyPrices[0]){
                                 sb.append(price).append(space).append(lastInfo.get(price)).append(space).append("-").append(space).append( lastInfo.get(price) ).append(space).append("撤单").append(space).append("\n");
                             }else{
                                 sb.append(price).append(space).append(lastInfo.get(price)).append(space).append("-").append(space).append( lastInfo.get(price) ).append(space).append("成交或撤单").append(space).append("\n");
                             }

                         }else{
                             sb.append(price).append(space).append(lastInfo.get(price)).append(space).append("-").append(space).append( lastInfo.get(price) ).append(space).append("隐藏").append(space).append("\n");
                         }


                      }else{ //历史价位在卖盘
                          if(price<sellPrices[4]){  // 代表成交了，或者撤单了
                              if(price>sellPrices[0]){
                                  sb.append(price).append(space).append(lastInfo.get(price)).append(space).append("-").append(space).append( lastInfo.get(price) ).append(space).append("撤单").append(space).append("\n");
                              }else{
                                  sb.append(price).append(space).append(lastInfo.get(price)).append(space).append("-").append(space).append( lastInfo.get(price) ).append(space).append("成交或撤单").append(space).append("\n");
                              }

                          }else{
                              sb.append(price).append(space).append(lastInfo.get(price)).append(space).append("-").append(space).append( lastInfo.get(price) ).append(space).append("隐藏").append(space).append("\n");
                          }

                      }

                 }


             }else{  //代表新增价位
                  if(price<=buyPrices[0] ){   //买盘新增价位
                      if(price>lastBuyPrices[4]){  //  代表中间新增价位， 新挂出来买单
                          sb.append(price).append(space).append("-").append(space).append(currInfo.get(price)).append(space).append(  currInfo.get(price)).append(space).append("新增买单").append(space).append("\n");
                      }else{  //底部新增价位， 可能是以前挂单
                          sb.append(price).append(space).append("-").append(space).append(currInfo.get(price)).append(space).append(  currInfo.get(price)).append(space).append("显现").append(space).append("\n");
                      }
                  }else{  //卖盘新增价位
                      if(price<lastSellPrices[4]){  //  代表中间新增价位， 新挂出来单子
                          sb.append(price).append(space).append("-").append(space).append(currInfo.get(price)).append(space).append(  currInfo.get(price) ).append("新增卖单").append(space).append("\n");
                      }else{  //底部新增价位， 可能是以前挂单
                          sb.append(price).append(space).append("-").append(space).append(currInfo.get(price)).append(space).append(  currInfo.get(price) ).append(space).append("显现").append(space).append("\n");
                      }
                  }

             }
            if(price == buyPrices[0]){
                sb.append("\n");
            }
            if(price == lastBuyPrices[0]){
                sb.append("\n");
            }


        }

        long buyExchange = (Long.valueOf(stockInfoCols[5])- Long.valueOf( lastInfos[5]));
        long sellExchange = (Long.valueOf(stockInfoCols[6])- Long.valueOf( lastInfos[6]));
        sb.append("成交量").append(space).append(Long.valueOf(stockInfoCols[4])- Long.valueOf( lastInfos[4])).append(space).append("\n")
          .append("主动买: ").append(space).append((Long.valueOf(stockInfoCols[5])- Long.valueOf( lastInfos[5]))).append(space).append(" 显示成交: ").append(mayDiffBuyVol).append(space).append("显示变动： ").append(tmpShowDiffBuyVol).append(space).append("\n")
          .append("主动卖:").append(space).append((Long.valueOf(stockInfoCols[6])- Long.valueOf( lastInfos[6]))).append(space).append("显示成交：").append(mayDiffSellVol).append(space).append("显示变动：").append(tmpShowDiffSellVol).append(space).append("\n") ;
        System.out.println(sb.toString());




    }

}
