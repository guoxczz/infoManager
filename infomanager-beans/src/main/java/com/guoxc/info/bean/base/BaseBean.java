package com.guoxc.info.bean.base;

import com.alibaba.fastjson.JSON;

import java.io.Serializable;

public class BaseBean   implements  Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;


	@Override
	public String toString() {
		return JSON.toJSONString(this);
	}


}
