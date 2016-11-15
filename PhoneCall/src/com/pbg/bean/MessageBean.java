package com.pbg.bean;

public class MessageBean{

	private int _id;
	private String name;
	private String date;
	private String text;
	private int layoutID;
	private String address;
	private String threadId;
	
	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getThreadId() {
		return threadId;
	}

	public void setThreadId(String threadId) {
		this.threadId = threadId;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

	public MessageBean(){
	}
	
	public MessageBean(int _id, String name, String date, String text,
			int layoutID) {
		super();
		this._id = _id;
		this.name = name;
		this.date = date;
		this.text = text;
		this.layoutID = layoutID;
	}


	
	public String getName(){
		return name;
	}

	public void setName(String name){
		this.name = name;
	}

	public String getDate(){
		return date;
	}

	public void setDate(String date){
		this.date = date;
	}

	public String getText(){
		return text;
	}

	public void setText(String text){
		this.text = text;
	}

	public int getLayoutID(){
		return layoutID;
	}

	public void setLayoutID(int layoutID){
		this.layoutID = layoutID;
	}
}
