package com.guoxc.info.dao;

import com.guoxc.info.bean.info.BsStaticDataBean;
import org.apache.ibatis.annotations.Param;

public interface BsStaticDataDao {

    public  String getCodeValue(@Param("codeName") String codeName);

    public  int updateCodeValue(BsStaticDataBean bean );
}
