package com.LTVAnalysis.Common;

public class Result implements Comparable<Result>{
	
	private String customerID;
	
	private Double lifeTimeValue;
	
	public Result(String customerID, Double lifeTimeValue) {
		this.customerID = customerID;
		this.lifeTimeValue = lifeTimeValue;
	}
	
	public String getCustomerID() {
		return customerID;
	}

	public void setCustomerID(String customerID) {
		this.customerID = customerID;
	}

	public Double getLifeTimeValue() {
		return lifeTimeValue;
	}

	public void setLifeTimeValue(double lifeTimeValue) {
		this.lifeTimeValue = lifeTimeValue;
	}

	@Override
	public int compareTo(Result o) {
		//return this.getLifeTimeValue().compareTo(o.getLifeTimeValue());
		return o.getLifeTimeValue().compareTo(this.getLifeTimeValue());
	}
	
}
