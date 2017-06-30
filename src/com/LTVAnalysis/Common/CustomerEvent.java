package com.LTVAnalysis.Common;

public class CustomerEvent extends Event {

	private String last_name;
	private String adr_city;
	private String adr_state;
	
	public CustomerEvent(String type, String verb, String key, String event_time) {
		super(type,verb,key,event_time);
	}
	
	public String getLast_name() {
		return last_name;
	}
	
	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}
	
	public String getAdr_city() {
		return adr_city;
	}
	
	public void setAdr_city(String adr_city) {
		this.adr_city = adr_city;
	}
	
	public String getAdr_state() {
		return adr_state;
	}
	
	public void setAdr_state(String adr_state) {
		this.adr_state = adr_state;
	}
	
}
