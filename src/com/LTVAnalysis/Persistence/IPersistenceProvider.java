package com.LTVAnalysis.Persistence;

import java.util.List;
import java.util.Map;
import java.util.Set;

import com.LTVAnalysis.Common.Event;

public interface IPersistenceProvider {
	
	public void ingestEvent(Event event);
	
	public Map<String, Event> getEventsPerCustomer(String customerID, String eventType);  //Result key == eventKey. Resultset per customer.
	
	public List<Event> getEvents(String eventType);   //Get all events of a particular type
	
	public Set<String> getUniqueKeys(String eventType);  //Get all unique keys per event type.

}
