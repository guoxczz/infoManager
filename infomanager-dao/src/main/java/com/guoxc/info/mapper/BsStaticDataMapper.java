package com.guoxc.info.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface BsStaticDataMapper {

    @Select("select t.code_value  from BS_STATIC_DATA t where t.code_name= #{codeName}  ")
    public  String getCodeValue(String codeName);
}
