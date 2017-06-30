package com.LTVAnalysis.Persistence;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.LTVAnalysis.Common.CustomerEvent;
import com.LTVAnalysis.Common.Event;
import com.LTVAnalysis.Common.ImageUploadEvent;
import com.LTVAnalysis.Common.OrderEvent;
import com.LTVAnalysis.Common.SiteVisitEvent;
import com.LTVAnalysis.Utils.Constants;

public class PersistenceProviderImpl implements IPersistenceProvider {
	
	private static PersistenceProviderImpl INSTANCE = null;
	
	/* The following hashtables mimic an in-memory data-store*/
	
	private Map<String, Map<String, Event>> CUSTOMERS_STORE; //1:1 MAPPING
	
	//Outer Maps key == customerID. Inner maps key == event key
	private Map<String, Map<String, Event>> SITE_VISITS_STORE;  //Assuming PAGE_ID is unique across all events of SiteVisitType.
	private Map<String, Map<String, Event>> IMAGES_STORE;
	private Map<String, Map<String, Event>> ORDERS_STORE;

	
	private PersistenceProviderImpl() {
		CUSTOMERS_STORE = new HashMap<String, Map<String,Event>>();
		SITE_VISITS_STORE = new HashMap<String, Map<String,Event>>();
		IMAGES_STORE = new HashMap<String, Map<String,Event>>();
		ORDERS_STORE = new HashMap<String, Map<String,Event>>();
	}
	
	public static PersistenceProviderImpl getInstance() {
		if(PersistenceProviderImpl.INSTANCE == null) {
			synchronized(PersistenceProviderImpl.class) {
				if(PersistenceProviderImpl.INSTANCE == null) {
					PersistenceProviderImpl.INSTANCE = new PersistenceProviderImpl();
				} 
			}
		}
		return PersistenceProviderImpl.INSTANCE;
	}

	
	@Override
	public void ingestEvent(Event event) {
		if(event == null) {
			System.out.println("Null event to insert");
			return;
		} else if(event.getType() == null || event.getType().trim().isEmpty()) {
			System.out.println("Null or Empty Event Type Attribute");
			return;
		} else if(event.getKey() == null || event.getKey().trim().isEmpty()) {
			System.out.println("Null or Empty Event Key Attribute");
			return;
		}
		
		if(event.getType().equals(Constants.CUSTOMER_EVENT)) {
			this.ingestCustomerEvent(event);
		} else if(event.getType().equals(Constants.SITE_VISIT_EVENT)) {
			this.ingestSiteVisitEvent(event);
		} else if(event.getType().equals(Constants.IMAGE_EVENT)) {
			this.ingestImageUploadEvent(event);
		} else if(event.getType().equals(Constants.ORDER_EVENT)) {
			this.ingestOrderEvent(event);
		} 
	}
	
	
	private void upsertEventToStore(String customerID, Event eventToInsert, Map<String, Map<String,Event>> dataStore) {
		Map<String,Event> eventsPertainingToCustomer = dataStore.get(customerID);
		if(eventsPertainingToCustomer == null) {
			eventsPertainingToCustomer = new HashMap<String,Event>();
		}
		
		Event previousEvent = null;
		
		if(eventsPertainingToCustomer.containsKey(eventToInsert.getKey())) {
			previousEvent = eventsPertainingToCustomer.get(eventToInsert.getKey());
			
			if(eventToInsert.getTimeStamp().after(previousEvent.getTimeStamp()) ||
					eventToInsert.getTimeStamp().equals(previousEvent.getTimeStamp())) {
				eventsPertainingToCustomer.put(eventToInsert.getKey(), eventToInsert);
			} else {
				//Do not update. Out of order Event.
			}
			
		} else {
			eventsPertainingToCustomer.put(eventToInsert.getKey(), eventToInsert);	
		}
		
		dataStore.put(customerID, eventsPertainingToCustomer);
	}
	
	
	private void addEventToStore(String customerID, Event eventToInsert, Map<String, Map<String,Event>> dataStore) {
		Map<String,Event> eventsPertainingToCustomer = dataStore.get(customerID);
		if(eventsPertainingToCustomer == null) {
			eventsPertainingToCustomer = new HashMap<String,Event>();
		}
		
		eventsPertainingToCustomer.put(eventToInsert.getKey(), eventToInsert);
		dataStore.put(customerID, eventsPertainingToCustomer);
	}
	
	private void ingestCustomerEvent(Event event) {
		CustomerEvent customerEvent = (CustomerEvent) event;
		if(customerEvent.getKey() != null && !customerEvent.getKey().trim().isEmpty()) {
			upsertEventToStore(customerEvent.getKey(), customerEvent, this.CUSTOMERS_STORE);
		} else {
			System.out.println("Null customer ID");
		}
	}
	
	private void ingestSiteVisitEvent(Event event) {
		SiteVisitEvent siteVisitEvent = (SiteVisitEvent) event;
		if(siteVisitEvent.getCustomer_id() != null && !siteVisitEvent.getCustomer_id().trim().isEmpty()) {
			addEventToStore(siteVisitEvent.getCustomer_id(), siteVisitEvent, this.SITE_VISITS_STORE);
		} else {
			System.out.println("Null customer ID");
		}
	}
	
	private void ingestImageUploadEvent(Event event) {
		ImageUploadEvent imageUploadEvent = (ImageUploadEvent) event;
		if(imageUploadEvent.getCustomer_id() != null && !imageUploadEvent.getCustomer_id().trim().isEmpty()) {
			addEventToStore(imageUploadEvent.getCustomer_id(), imageUploadEvent, this.IMAGES_STORE);
		} else {
			System.out.println("Null customer ID");
		}
	}
	
	
	private void ingestOrderEvent(Event event) {
		OrderEvent orderEvent = (OrderEvent) event;
		if(orderEvent.getCustomer_id() != null && !orderEvent.getCustomer_id().trim().isEmpty()) {
			upsertEventToStore(orderEvent.getCustomer_id(), orderEvent, this.ORDERS_STORE);
		} else {
			System.out.println("Null customer ID");
		}
	}
	

	@Override
	public Map<String, Event> getEventsPerCustomer(String customerID, String eventType) {
		Map<String, Event> result = null;
			if(customerID != null && !customerID.trim().isEmpty() &&
					!eventType.trim().isEmpty() && eventType != null) {
				if(eventType.equals(Constants.CUSTOMER_EVENT)) {
					result = this.CUSTOMERS_STORE.get(customerID);
				} else if(eventType.equals(Constants.SITE_VISIT_EVENT)) {
					result = this.SITE_VISITS_STORE.get(customerID);
				} else if(eventType.equals(Constants.IMAGE_EVENT)) {
					result = this.IMAGES_STORE.get(customerID);
				} else if(eventType.equals(Constants.ORDER_EVENT)) {
					result = this.ORDERS_STORE.get(customerID);
				}
			}
		return result;
	}

	@Override
	public List<Event> getEvents(String eventType) {
		List<Event> result = null;
		if(eventType != null && !eventType.trim().isEmpty()) {
			if(eventType.equals(Constants.CUSTOMER_EVENT)) {
				result = this.getAllEvents(this.CUSTOMERS_STORE);
			} else if(eventType.equals(Constants.SITE_VISIT_EVENT)) {
				result = this.getAllEvents(this.SITE_VISITS_STORE);
			} else if(eventType.equals(Constants.IMAGE_EVENT)) {
				result = this.getAllEvents(this.IMAGES_STORE);
			} else if(eventType.equals(Constants.ORDER_EVENT)) {
				result = this.getAllEvents(this.ORDERS_STORE);
			}
		}
		return result;
	}
	

	private List<Event> getAllEvents(Map<String, Map<String, Event>> eventsPerCustomer) {
		List<Event> result = null;
		if(eventsPerCustomer != null && !eventsPerCustomer.isEmpty()) {
			for(String customerID : eventsPerCustomer.keySet()) {
				for(String eventKey : eventsPerCustomer.get(customerID).keySet()){
					if(result == null){
						result = new ArrayList<Event>();
					}
					result.add(eventsPerCustomer.get(customerID).get(eventKey));
				}
			}
		}
		return result;
	}

	@Override
	public Set<String> getUniqueKeys(String eventType) {
		Set<String> result = null;
		if(eventType != null && !eventType.trim().isEmpty()) {
			if(eventType.equals(Constants.CUSTOMER_EVENT)) {
				result = this.CUSTOMERS_STORE.keySet();
			} else if(eventType.equals(Constants.SITE_VISIT_EVENT)) {
				result = this.getKeys(this.SITE_VISITS_STORE);
			} else if(eventType.equals(Constants.IMAGE_EVENT)) {
				result = this.getKeys(this.IMAGES_STORE);
			} else if(eventType.equals(Constants.ORDER_EVENT)) {
				result = this.getKeys(this.ORDERS_STORE);
			}
		}
		return result;
	
	}
	
	private Set<String> getKeys(Map<String, Map<String, Event>> dataStore) {
		Set<String> result = null;
		if(dataStore != null) {
			for(String key : dataStore.keySet()) {
				if(result == null) {
					result = new HashSet<String>();
				}
				result.addAll(dataStore.get(key).keySet());
			}
		}
		return result;
	}
	
}
