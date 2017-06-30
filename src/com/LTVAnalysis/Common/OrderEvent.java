package com.LTVAnalysis.Common;

public class OrderEvent extends Event {
	
	private String customer_id;
	private String total_amount;
	
	public OrderEvent(String type, String verb, String key, String event_time, String customer_id, String total_amount) {
		super(type,verb,key,event_time);
		this.customer_id = customer_id;
		this.total_amount = total_amount;
	}
	
	public String getCustomer_id() {
		return customer_id;
	}
	
	public void setCustomer_id(String customer_id) {
		this.customer_id = customer_id;
	}
	
	public String getTotal_amount() {
		return total_amount;
	}
	
	public Double getTotalAmount() {
		Double amount = null;
		if(this.getTotal_amount() != null && !this.getTotal_amount().trim().isEmpty()) {
			String amountValue = this.getTotal_amount().trim();
			String array[] = amountValue.split(" ");
			amount = new Double(array[0]);		
		}
		return amount;
	}
	
	public void setTotal_amount(String total_amount) {
		this.total_amount = total_amount;
	}

}
