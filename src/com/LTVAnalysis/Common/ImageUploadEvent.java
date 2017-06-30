package com.LTVAnalysis.Common;

public class ImageUploadEvent extends Event  {
	
	private String customer_id;
	private String camera_make;
	private String camera_model;
	
	public ImageUploadEvent(String type, String verb, String key, String event_time, String customer_id) {
		super(type,verb,key,event_time);
		this.customer_id = customer_id; 
	}
	
	public String getCustomer_id() {
		return customer_id;
	}
	
	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}
	
	public String getCamera_make() {
		return camera_make;
	}
	
	public void setCamera_make(String camera_make) {
		this.camera_make = camera_make;
	}
	
	public String getCamera_model() {
		return camera_model;
	}
	
	public void setCamera_model(String camera_model) {
		this.camera_model = camera_model;
	}
	
}
