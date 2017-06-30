package com.LTVAnalysis.Common;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.google.gson.Gson;

public class Event {

	private String type;
	private String verb;
	private String key;
	private String event_time;
	
	public Event(String type, String verb, String key, String event_time) {
		this.type = type;
		this.verb = verb;
		this.key = key;
		this.event_time = event_time;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public String getVerb() {
		return verb;
	}
	
	public void setVerb(String verb) {
		this.verb = verb;
	}
	
	public String getKey() {
		return key;
	}
	
	public void setKey(String key) {
		this.key = key;
	}
	
	public String getEvent_time() {
		return event_time;
	}
	
	public void setEvent_time(String event_time) {
		this.event_time = event_time;
	}

	public Timestamp getTimeStamp() {
		Timestamp ts = null;
		String format = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
		
		SimpleDateFormat dateFormattter = null;
		Date dt = null;
		
		if(this.getEvent_time() != null) {
		    try {
		    	dateFormattter = new SimpleDateFormat(format);
		    	dt = dateFormattter.parse(this.getEvent_time());
		    	ts = new Timestamp(dt.getTime());
			} catch (ParseException e) {
				e.printStackTrace();
			}
		}
		
		return ts;
	}

	public String toJson() {
		Gson jsonBuilder = new Gson();
		return jsonBuilder.toJson(this);
	}
	
}
