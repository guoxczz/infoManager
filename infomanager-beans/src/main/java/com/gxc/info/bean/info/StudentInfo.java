package com.gxc.info.bean.info;

import java.util.Date;

import com.gxc.info.bean.base.BaseBean;

@SuppressWarnings("serial")
public class StudentInfo  extends  BaseBean{
	private Long id; // Ö÷¼üID
    private String name; 
    private String sex; 
    private Date birthday; 
    private String classId; 
    private String phone;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSex() {
		return sex;
	}
	public void setSex(String sex) {
		this.sex = sex;
	}
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	public String getClassId() {
		return classId;
	}
	public void setClassId(String classId) {
		this.classId = classId;
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	} 
	
	

}
