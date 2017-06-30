package com.LTVAnalysis.Analysis;

import java.sql.Timestamp;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.LTVAnalysis.Common.Event;
import com.LTVAnalysis.Common.OrderEvent;
import com.LTVAnalysis.Common.Result;
import com.LTVAnalysis.Persistence.IPersistenceProvider;
import com.LTVAnalysis.Persistence.PersistenceProviderImpl;
import com.LTVAnalysis.Utils.Constants;

public class LTVComputator {
	
	private IPersistenceProvider persistencyClient = null;
	
	public LTVComputator() {
		persistencyClient = PersistenceProviderImpl.getInstance();
	}
	
	public Set<Result> getTOPK(int k) { 
		
		Set<String> customerIDs = persistencyClient.getUniqueKeys(Constants.CUSTOMER_EVENT);
		Set<Result> results = new TreeSet<Result>();
		
		double expenditure = 0;
		double visits = 0;
		double expenditurePerVisit = 0;
		double duration = 0;
		double customerValuePerWeek = 0;
		double lifeTimeValue = 0;
		
		Result result = null;
		
		for(String customerID : customerIDs) {
			expenditure = getExpenditurePerCustomer(persistencyClient.getEventsPerCustomer(customerID, Constants.ORDER_EVENT));
			visits = persistencyClient.getEventsPerCustomer(customerID, Constants.SITE_VISIT_EVENT).size();
			expenditurePerVisit = (double) expenditure/visits;
			duration = getDuration(customerID);
			
			if(duration == 0){ //Edge case
				duration = 1;
			}
			
			customerValuePerWeek = (expenditurePerVisit)*(visits/duration);
			lifeTimeValue = 52*customerValuePerWeek*10;
			result = new Result(customerID, lifeTimeValue);
			results.add(result);
		}
		
		return results;
	}
	
	private double getExpenditurePerCustomer(Map<String, Event> ordersPerCustomer) {
		double expenditure = 0.0;
		OrderEvent orderEvent = null;
		
		for(String orderID : ordersPerCustomer.keySet()) {
			orderEvent = (OrderEvent) ordersPerCustomer.get(orderID);
			expenditure += orderEvent.getTotalAmount();
		}
		
		return expenditure;
	}
	
	public long getDuration(String customerID) {

		Map<String, Event> siteVisits = persistencyClient.getEventsPerCustomer(customerID, Constants.SITE_VISIT_EVENT);

		Timestamp first = null;
		Timestamp last = null;
		
		Event visitEvent = null;
		
		for(String pageID : siteVisits.keySet()) {
			visitEvent = siteVisits.get(pageID);
			if(first == null) {
				first = visitEvent.getTimeStamp();
			}
			if(last == null) {
				last = visitEvent.getTimeStamp();
			}
			if(visitEvent.getTimeStamp().before(first)) {
				first = visitEvent.getTimeStamp();
			}
			if(visitEvent.getTimeStamp().after(last)) {
				last = visitEvent.getTimeStamp();
			}
	
		}
		
		Map<String, Event> ordersEvents = persistencyClient.getEventsPerCustomer(customerID, Constants.ORDER_EVENT);
		Event orderEvent = null;
		for(String orderID : ordersEvents.keySet()) {
			orderEvent = ordersEvents.get(orderID);
			if(orderEvent.getTimeStamp().after(last)){
				last = orderEvent.getTimeStamp(); //Probably it was updated later.
			}
		}

		long timeSlice = last.getTime() - first.getTime();
		long weeks = (timeSlice / (1000 * 60 * 60 * 24));
		
		return weeks;
	}

}
