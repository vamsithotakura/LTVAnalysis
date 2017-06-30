package com.LTVAnalysis.Common;

import java.util.List;

import com.google.gson.JsonObject;

public class SiteVisitEvent extends Event  {
	
	private String customer_id;
	private List<JsonObject>tags;
	
	public SiteVisitEvent(String type, String verb, String key, String event_time, String customer_id) {
		super(type,verb,key,event_time);
		this.customer_id = customer_id;
	}

	public String getCustomer_id() {
		return customer_id;
	}

	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}

	public List<JsonObject> getTags() {
		return tags;
	}

	public void setTags(List<JsonObject> tags) {
		this.tags = tags;
	}
	
}
