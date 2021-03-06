import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.Set;

import com.LTVAnalysis.Analysis.LTVComputator;
import com.LTVAnalysis.Common.CustomerEvent;
import com.LTVAnalysis.Common.Event;
import com.LTVAnalysis.Common.ImageUploadEvent;
import com.LTVAnalysis.Common.OrderEvent;
import com.LTVAnalysis.Common.Result;
import com.LTVAnalysis.Common.SiteVisitEvent;
import com.LTVAnalysis.Persistence.IPersistenceProvider;
import com.LTVAnalysis.Persistence.PersistenceProviderImpl;
import com.LTVAnalysis.Utils.Constants;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

public class Driver {
	
	public void initiateWorkFlow(String path, int topK) {
		JsonArray data = this.getJSONData(path);
		this.initiateIngestion(data);
		this.computeLTV(topK);
	}
	
	private JsonArray getJSONData(String path) {
		if(path == null || path.trim().isEmpty()) {
			throw new IllegalArgumentException("Kindly check path variable!");
		}
		
		String data = this.readData(path);
		JsonParser parser = new JsonParser();
		
		JsonArray result = (JsonArray) parser.parse(data);
		return result;
	}

	private void computeLTV(int topK) {
		LTVComputator computeLTV = new LTVComputator();
		Set<Result> results = computeLTV.getTOPK(3);
		publishResults(results, topK);
	}
	
	private void publishResults(Set<Result> results, int topK) {

		BufferedWriter writer = null;
		String path = "./output/results.txt";
		try{
			writer = new BufferedWriter(new FileWriter(path,true));

			Iterator<Result> resultsIterator = results.iterator();
			
			Result result = null;
			for(int i=0; i<topK && resultsIterator.hasNext(); i++) {
				result = resultsIterator.next();
				
				writer.write(Constants.CUSTOMER_ID_TAG);
				writer.write(Constants.TAB);
				writer.write(Constants.TAB);
				writer.write(result.getCustomerID());
				writer.write(Constants.TAB);
				writer.write(Constants.TAB);
				writer.write(Constants.LIFE_TIME_VALUE_TAG);
				writer.write(String.valueOf(result.getLifeTimeValue()));
				writer.newLine();
			}
		} catch(Exception e) {
			throw new RuntimeException(e.getMessage());
		} finally {
			try {
				if(writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}
	
	private boolean initiateIngestion(JsonArray data) {
		
		if(data != null) {
			Gson jsonBuilder = new Gson();
			JsonObject obj = null;
			String eventType = null;
			Event event = null;
			IPersistenceProvider ingestionClient = PersistenceProviderImpl.getInstance();
			for(int i=0; i<data.size(); i++) {
				obj = (JsonObject)data.get(i);
				eventType = obj.get(Constants.EVENT_TYPE).getAsString();

				if(eventType == null) {
					System.out.println("Missing Event Type. Dropping the event from further processing.");
					continue;
				}

				if(eventType.trim().equals(Constants.CUSTOMER_EVENT)) {
					event = jsonBuilder.fromJson(obj, CustomerEvent.class);
				} else if(eventType.trim().equals(Constants.SITE_VISIT_EVENT)) {
					event = jsonBuilder.fromJson(obj, SiteVisitEvent.class);
				} else if(eventType.trim().equals(Constants.IMAGE_EVENT)) {
					event = jsonBuilder.fromJson(obj, ImageUploadEvent.class);
				} else if(eventType.trim().equals(Constants.ORDER_EVENT)) {
					event = jsonBuilder.fromJson(obj, OrderEvent.class);
				} else {
					System.out.println("Unknown Event Type. Dropping the event from further processing.");
					continue;
				}
				ingestionClient.ingestEvent(event);
			}
			return true;
		} else {
			return false;
		}
	}
	
	private String readData(String path) {

		BufferedReader br = null;
		FileReader reader = null;
		
		try{

			String currentLine = null;
			StringBuilder sb = new StringBuilder();
			
			reader = new FileReader(path);
			br = new BufferedReader(reader);
			
			while ((currentLine = br.readLine()) != null) {
				sb.append(currentLine);
			}
			
			return sb.toString();
		} catch (IOException e) {
			throw new RuntimeException(e.getMessage());
		} catch(Exception e) {
			throw new RuntimeException(e.getMessage());
		} finally {
			try {
				if(br != null) {
					br.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				if(reader != null) {
					reader.close();
				}
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}
	
	public static void main(String[] args) {
		
		String path = "./input/input2.txt";
		int topK = 1;
		
		if(args.length >= 2){
			path = args[0];
			topK = Integer.parseInt(args[1].trim());
		}
		
		Driver driver = null;
		try{
			driver = new Driver();
			driver.initiateWorkFlow(path, topK);
		} catch(Exception e) {
			System.out.println("LTV Computation failed with the following cause :: "+e.getMessage());
		}
		
	}
	
}
