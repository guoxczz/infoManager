package com.guoxc.info.dao;

import com.guoxc.info.bean.info.StockDayBean;

import java.util.List;

public interface StockDao {

    void insertStockDay(StockDayBean bean );

    void insertStockDayList( List beans );


}
