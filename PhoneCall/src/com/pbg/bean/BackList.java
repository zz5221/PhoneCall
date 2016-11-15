package com.pbg.bean;

import com.lidroid.xutils.db.annotation.Id;

public class BackList {
	@Id
	private int id;
	private String lxid;
	private String smsid;
	
	public String getSmsid() {
		return smsid;
	}
	public void setSmsid(String smsid) {
		this.smsid = smsid;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getLxid() {
		return lxid;
	}
	public void setLxid(String lxid) {
		this.lxid = lxid;
	}
	
}
