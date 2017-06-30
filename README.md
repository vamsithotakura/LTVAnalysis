# LTVAnalysis

- NOTES :
	1. A high throughput, distributed in-memory key-value store would satisfy persistence requirements.
	2. Modeled data structures to mimic a key-value store.
	3. CustomerID is used as the primary hash for grouping events.
	
- Assumptions : 
	1. Assuming time-stamps pertaining to different event types belong to the same time-zone. 
	2. Keys are unique across event types.
	3. Time Stamp format remains consistent over time and across event types.
	4. TimeStamp is given a higher priority over 'VERB' type while ingesting  out of order data. This enables us to handle key collisions.
	5. Prior to data ingestion, tables (or data structures) are already set-in place. 
	6. Order amount format remains consistent and value is always represented in US dollars. 
	
- Project can be run by executing the Driver class. The work-flow initiating 'Driver' program takes-in two command line arguments :
	1. Path to the input data file.
	2. 'TopK' parameter.
	