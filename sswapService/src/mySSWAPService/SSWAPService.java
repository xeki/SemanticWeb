package mySSWAPService;

import java.io.FileReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import info.sswap.api.model.RIG;
import info.sswap.api.model.SSWAPIndividual;
import info.sswap.api.model.SSWAPObject;
import info.sswap.api.model.SSWAPPredicate;
import info.sswap.api.model.SSWAPProperty;
import info.sswap.api.model.SSWAPSubject;
import info.sswap.api.servlet.MapsTo;

public class SSWAPService extends MapsTo {
	// initialize variables here
	RIG rigGraph;
	public SSWAPPredicate VCpredicate;
	public SSWAPPredicate VDpredicate;
	public SSWAPPredicate ARpredicate;
	public SSWAPPredicate ATpredicate;
	public SSWAPPredicate ANpredicate;
	public SSWAPPredicate AIpredicate;
	public SSWAPPredicate AFpredicate;
	public SSWAPPredicate HRpredicate;
	public SSWAPPredicate HIpredicate;
	public SSWAPPredicate HNpredicate;
	public SSWAPPredicate HLngpredicate;
	public SSWAPPredicate HLowpredicate;
	public SSWAPPredicate HLatpredicate;
	public SSWAPPredicate HPpredicate;
	public SSWAPPredicate FODTpredicate;
	public SSWAPPredicate FOATpredicate;
	public SSWAPPredicate FOOpredicate;
	public SSWAPPredicate FODpredicate;
	public SSWAPPredicate FIDTpredicate;
	public SSWAPPredicate FIATpredicate;
	public SSWAPPredicate FIOpredicate;
	public SSWAPPredicate FIDpredicate;
	public SSWAPPredicate BudgetPredicate;
	ArrayList<HashMap<String, String>> hotelList = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> vaccinationList = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> attractionList = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> inboundList = new ArrayList<HashMap<String, String>>();
	ArrayList<HashMap<String, String>> outboundList = new ArrayList<HashMap<String, String>>();
	ArrayList<SSWAPIndividual> outBoundFlightIndList = new ArrayList<SSWAPIndividual>();

	@Override
	protected void initializeRequest(RIG rig) {
		rigGraph = rig;

		// if we need to check service parameters we could start here
	}

	@Override
	protected void mapsTo(SSWAPSubject translatedSubject) throws Exception {

		SSWAPSubject subject = translatedSubject;
		SSWAPObject object = translatedSubject.getObject();

		HashMap<String, String> paraValue = new HashMap<String, String>();
		Iterator<SSWAPProperty> iterator = subject.getProperties().iterator();

		while (iterator.hasNext()) {
			SSWAPProperty property = iterator.next();
			SSWAPPredicate predicate = rigGraph.getPredicate(property.getURI());
			String[] a = predicate.getURI().toString().split("#", 2);
			System.out.println("value" + subject.getProperty(predicate).getValue().asString());
			paraValue.put(a[1], subject.getProperty(predicate).getValue().asString());

		}
		// URIBuilder builder;

		String backendUrl = "http://limitless-lowlands-64274.herokuapp.com/sdp?";
		JSONParser parser = new JSONParser();
		JSONArray array;
		JSONArray vaccines;
		JSONArray attractions;

		try {
			System.out.println("Inside json parsing");
			String rootPath = MySSWAPServlet.path();
			Object obj = null;
			try {
				obj = parser.parse(new FileReader(rootPath + "json.txt"));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			JSONObject jsonObject = (JSONObject) obj;
			System.out.println(jsonObject);
			array = (JSONArray) jsonObject.get("results");
			int i;
			for (i = 0; i < array.size(); ++i) {

				JSONObject result = (JSONObject) array.get(i);
				// vaccines
				vaccines = (JSONArray) result.get("Vaccinations");
				for (int v = 0; v < vaccines.size(); ++v) {
					JSONObject vaccine = (JSONObject) vaccines.get(v);
					String vac = (String) vaccine.get("category");
					String desc = (String) vaccine.get("description");
					HashMap<String, String> tempVac = new HashMap<String, String>();
					tempVac.put("category", vac);
					tempVac.put("description", desc);
					vaccinationList.add(tempVac);

				}
				// attractions
				attractions = (JSONArray) result.get("Attractions");
				for (int a = 0; a < attractions.size(); ++a) {
					JSONObject attraction = (JSONObject) attractions.get(a);
					String name = (String) attraction.get("name");
					String add = (String) attraction.get("formatted_address");
					String index = Long.toString((Long) attraction.get("index"));
					String rate;
					if (attraction.get("rating").getClass().getName().contains("Long")) {
						rate = Long.toString((Long) attraction.get("rating"));
					} else {
						rate = Double.toString((Double) attraction.get("rating"));
					}

					String types = (String) attraction.get("types").toString();
					HashMap<String, String> attractMap = new HashMap<String, String>();
					attractMap.put("name", name);
					attractMap.put("formatted_address", add);
					attractMap.put("rating", rate);
					attractMap.put("types", types);
					attractMap.put("index", index);
					attractionList.add(attractMap);
				}
				// hotels
				JSONObject hotel = (JSONObject) result.get("hotel");
				String h_name = (String) hotel.get("name");
				String h_lat = (String) hotel.get("latitude");
				String h_lng = (String) hotel.get("longitude");
				String h_rank = Long.toString((Long) hotel.get("ranking"));
				String h_room_price = Long.toString((Long) hotel.get("room_price"));
				String h_id = Long.toString((Long) hotel.get("id"));
				String h_low_rate = (String) hotel.get("latitude");

				HashMap<String, String> hotelMap = new HashMap<String, String>();
				hotelMap.put("name", h_name);
				hotelMap.put("latitude", h_lat);
				hotelMap.put("longitude", h_lng);
				hotelMap.put("ranking", h_rank);
				hotelMap.put("room_price", h_room_price);
				hotelMap.put("id", h_id);
				hotelMap.put("low_rate", h_low_rate);
				hotelList.add(hotelMap);
				// flights
				HashMap<String, String> flightMap = new HashMap<String, String>();
				JSONObject flight = (JSONObject) result.get("flight");
				JSONArray flightInfoArray = (JSONArray) flight.get("flight_info");
				JSONObject outBound = (JSONObject) flightInfoArray.get(0);

				JSONArray outBoundTransits = (JSONArray) outBound.get("info");
				// flightMap.put("count",
				// Integer.toString(outBoundTransits.size()));
				for (int ft = 0; ft < outBoundTransits.size(); ft++) {
					JSONObject transits = (JSONObject) outBoundTransits.get(ft);
					HashMap<String, String> outBoundTransit = new HashMap<String, String>();
					outBoundTransit.put("arrival_time", transits.get("arrivalTime").toString());
					outBoundTransit.put("departure_time", transits.get("departureTime").toString());
					outBoundTransit.put("destination", transits.get("destination").toString());
					outBoundTransit.put("origin", transits.get("origin").toString());

					outboundList.add(outBoundTransit);
				}

				JSONObject inBound = (JSONObject) flightInfoArray.get(1);

				JSONArray inBoundTransits = (JSONArray) inBound.get("info");
				// flightMap
				// .put("count", Integer.toString(inBoundTransits.size()));
				for (int ft = 0; ft < inBoundTransits.size(); ft++) {
					JSONObject transits = (JSONObject) inBoundTransits.get(ft);
					HashMap<String, String> inBoundTransitMap = new HashMap<String, String>();
					inBoundTransitMap.put("arrival_time", transits.get("arrivalTime").toString());
					inBoundTransitMap.put("departure_time", transits.get("departureTime").toString());
					inBoundTransitMap.put("destination", transits.get("destination").toString());
					inBoundTransitMap.put("origin", transits.get("origin").toString());

					inboundList.add(inBoundTransitMap);
				}
				if (i == 0) {
					recursive(object, "start");
					//addVaccination(object, vaccinationList, 1);
					//addAttraction(object, attractionList, 1);
					//testSetOutBoundFlight(object);
					hotelList.clear();
					vaccinationList.clear();
					attractionList.clear();
					inboundList.clear();
					outboundList.clear();
				} /*else {
					// add more objects
					System.out.println("__________________________________________________");
					SSWAPObject sswapObject = null;
					sswapObject = assignObject(subject);
					// Iterator<SSWAPProperty> iteratorAdd =
					// object.getProperties().iterator();
					// while (iteratorAdd.hasNext()) {
					// SSWAPProperty property = iteratorAdd.next();
					// SSWAPPredicate predicate =
					// rigGraph.getPredicate(property.getURI());
					// sswapObject.addProperty(predicate, "");
					// }
					
					SSWAPIndividual inbdFlight = rigGraph.createIndividual();
					inbdFlight.addType(rigGraph.getType(new URI("http://myOnt/ont.owl#Inbound")));
					addInBoundFlight(inbdFlight, inboundList);
					
					System.out.println("Test inboundList count: " + inboundList.size());
					System.out.println("Test outBoundList count: " + outboundList.size());
					
					SSWAPIndividual obdFlight = rigGraph.createIndividual();
					obdFlight.addType(rigGraph.getType(new URI("http://myOnt/ont.owl#Outbound")));
					addOutBoundFlight(obdFlight, outboundList);
					
					SSWAPIndividual indFlight = rigGraph.createIndividual();
					indFlight.addType(rigGraph.getType(new URI("http://myOnt/ont.owl#Flight")));
					addOutBoundFlight(indFlight, outboundList);
					addInBoundFlight(indFlight, inboundList);
					
					SSWAPPredicate outbPredicate = rigGraph.getPredicate(new URI("http://myOnt/ont.owl#outbound"));
					indFlight.addProperty(outbPredicate, obdFlight);
					
					SSWAPPredicate inbPredicate = rigGraph.getPredicate(new URI("http://myOnt/ont.owl#inbound"));
					indFlight.addProperty(inbPredicate, inbdFlight);
					

					SSWAPIndividual ind = rigGraph.createIndividual();
					ind.addType(rigGraph.getType(new URI("http://myOnt/ont.owl#Package")));
					SSWAPPredicate predicate = rigGraph.getPredicate(new URI("http://myOnt/ont.owl#package"));
					SSWAPPredicate predicateF = rigGraph.getPredicate(new URI("http://myOnt/ont.owl#flight"));
					ind.addProperty(predicateF, indFlight);
					addHotel(ind, hotelList);
					addAttraction(ind, attractionList, 0);
					addVaccination(ind, vaccinationList, 0);
					sswapObject.addProperty(predicate, ind);
					//sswapObject.addProperty(BudgetPredicate, "second-object-budget");
					subject.addObject(sswapObject);
					hotelList.clear();
					vaccinationList.clear();
					attractionList.clear();
					inboundList.clear();
					outboundList.clear();
				}*/
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void addVaccination(SSWAPIndividual object, ArrayList<HashMap<String, String>> vaccinationList, int from)
			throws URISyntaxException {
		for (int v = from; v < vaccinationList.size(); v++) {
			SSWAPIndividual ind = rigGraph.createIndividual();
			ind.addType(rigGraph.getType(new URI("http://localhost:8080/travel-ontology#Vaccination")));
			ind.addProperty(VCpredicate, (String) vaccinationList.get(v).get("category"));
			ind.addProperty(VDpredicate, (String) vaccinationList.get(v).get("description"));
			SSWAPPredicate predicate = rigGraph
					.getPredicate(new URI("http://localhost:8080/travel-ontology#vaccination"));
			object.addProperty(predicate, ind);
		}
	}

	private void addAttraction(SSWAPIndividual object, ArrayList<HashMap<String, String>> attractionList, int from)
			throws URISyntaxException {
		for (int a = 1; a < attractionList.size(); ++a) {
			SSWAPIndividual ind = rigGraph.createIndividual();
			ind.addType(rigGraph.getType(new URI("http://localhost:8080/travel-ontology#Attraction")));
			ind.addProperty(ARpredicate, (String) attractionList.get(a).get("rating"));
			ind.addProperty(ATpredicate, (String) attractionList.get(a).get("types"));
			ind.addProperty(ANpredicate, (String) attractionList.get(a).get("name"));
			ind.addProperty(AIpredicate, (String) attractionList.get(a).get("index"));
			ind.addProperty(AFpredicate, (String) attractionList.get(a).get("formatted_address"));
			SSWAPPredicate predicate = rigGraph
					.getPredicate(new URI("http://localhost:8080/travel-ontology#attraction"));
			object.addProperty(predicate, ind);
		}
	}

	private void addHotel(SSWAPIndividual object, ArrayList<HashMap<String, String>> hotelList)
			throws URISyntaxException {

		SSWAPIndividual ind = rigGraph.createIndividual();
		ind.addType(rigGraph.getType(new URI("http://localhost:8080/travel-ontology#Hotel")));
		ind.addProperty(HRpredicate, (String) hotelList.get(0).get("ranking"));
		ind.addProperty(HIpredicate, (String) hotelList.get(0).get("id"));
		ind.addProperty(HNpredicate, (String) hotelList.get(0).get("name"));
		ind.addProperty(HLngpredicate, (String) hotelList.get(0).get("longitude"));
		ind.addProperty(HLowpredicate, (String) hotelList.get(0).get("low_rate"));
		ind.addProperty(HLatpredicate, (String) hotelList.get(0).get("latitude"));
		ind.addProperty(HPpredicate, (String) hotelList.get(0).get("room_price"));
		SSWAPPredicate predicate = rigGraph.getPredicate(new URI("http://localhost:8080/travel-ontology#hotel"));
		object.addProperty(predicate, ind);

	}

	private void testSetOutBoundFlight(SSWAPIndividual object) throws URISyntaxException {
		SSWAPPredicate outBpredicate = rigGraph.getPredicate(new URI("http://localhost:8080/travel-ontology#outbound"));
		SSWAPIndividual ind = rigGraph.createIndividual();
		ind.addType(rigGraph.getType(new URI("http://localhost:8080/travel-ontology#Outbound")));
		ind.addProperty(FODTpredicate, "test-departure_time");
		ind.addProperty(FOATpredicate, "test-arrival-time");
		ind.addProperty(FOOpredicate, "test-origin");
		ind.addProperty(FODpredicate, "test-destination");
		object.setProperty(outBpredicate, ind);

	}

	private void addOutBoundFlight(SSWAPIndividual object, ArrayList<HashMap<String, String>> outboundList)
			throws URISyntaxException {

		SSWAPIndividual ind = rigGraph.createIndividual();
		ind.addType(rigGraph.getType(new URI("http://localhost:8080/travel-ontology#Outbound")));
		ind.addProperty(FODTpredicate, (String) outboundList.get(0).get("departure_time"));
		ind.addProperty(FOATpredicate, (String) outboundList.get(0).get("arrival_time"));
		ind.addProperty(FOOpredicate, (String) outboundList.get(0).get("origin"));
		ind.addProperty(FODpredicate, (String) outboundList.get(0).get("destination"));
		SSWAPPredicate predicate = rigGraph.getPredicate(new URI("http://localhost:8080/travel-ontology#outbound"));
		object.addProperty(predicate, ind);
	}

	private void addInBoundFlight(SSWAPIndividual object, ArrayList<HashMap<String, String>> inboundList)
			throws URISyntaxException {

		SSWAPIndividual ind = rigGraph.createIndividual();
		ind.addType(rigGraph.getType(new URI("http://localhost:8080/travel-ontology#Inbound")));
		ind.addProperty(FIDTpredicate, (String) inboundList.get(0).get("departure_time"));
		ind.addProperty(FIATpredicate, (String) inboundList.get(0).get("arrival_time"));
		ind.addProperty(FIOpredicate, (String) inboundList.get(0).get("origin"));
		ind.addProperty(FIDpredicate, (String) inboundList.get(0).get("destination"));
		SSWAPPredicate predicate = rigGraph.getPredicate(new URI("http://localhost:8080/travel-ontology#inbound"));
		object.addProperty(predicate, ind);

	}

	private void recursive(SSWAPIndividual sswapIndividual, String indName) {
		Iterator<SSWAPProperty> iteratorProperties = sswapIndividual.getProperties().iterator();

		while (iteratorProperties.hasNext()) {

			SSWAPProperty property = iteratorProperties.next();
			SSWAPPredicate predicate = rigGraph.getPredicate(property.getURI());
			System.out.println("<- ********** Looping outside: ->^**********");

/*			
			  if (predicate.isDatatypePredicate()) {
			 System.out.println("Litral Values: ->^**********");
			 
			 SSWAPPredicate tempPredicate =
			 rigGraph.getPredicate(property.getURI()); 
			 String predUri = tempPredicate.getURI().toString();
			 System.out.println("Url:**->: " + predUri);
			 String delimiter = "";
			 if (predUri.contains("#")) {
				 delimiter = "#";
				 }
			 else {
			 			 delimiter = "/";
			 	}
			 String[] temp = predUri.split(delimiter); 
			 if (temp[temp.length - 1].equals("budget")) {
			 sswapIndividual.setProperty(tempPredicate, "No-budget");
			 BudgetPredicate = tempPredicate;
			 System.out.println("Budget set");
			 
			 } if(indName.equals("vaccination")){

					//for(int v=0;v<vaccinationList.size();v++){
						 String lookupName = getStrName(property.getURI());
						 predicate = rigGraph.getPredicate(property.getURI());
			                if(lookupName.equals("category")){
			                String value=(String) vaccinationList.get(0).get("category");
			                sswapIndividual.addProperty(predicate, value);
			                VCpredicate=predicate;
			                }
			                else{
			                	String value=(String) vaccinationList.get(0).get("description");
			                	sswapIndividual.addProperty(predicate, value); 
			                	 VDpredicate=predicate;}
					//}	
	            }
					if(indName.equals("attraction")){
				//		for (int a = 0; a < attractionList.size(); ++a) {
							String lookupName = getStrName(property.getURI());
							predicate = rigGraph.getPredicate(property.getURI());
							if(lookupName.equals("rating")){
								 String value=(String) attractionList.get(0).get("rating");
								 sswapIndividual.addProperty(predicate, value);
								 ARpredicate=predicate;
							}
	                        if(lookupName.equals("types")){
	                        	String value=(String) attractionList.get(0).get("types");
					            sswapIndividual.addProperty(predicate, value);
					            ATpredicate=predicate;
							}
	                        if(lookupName.equals("name")){
	                        	String value=(String) attractionList.get(0).get("name");
					            sswapIndividual.addProperty(predicate, value);
					            ANpredicate=predicate;
							}
	                        if(lookupName.equals("index")){
	                        	String value=(String) attractionList.get(0).get("index");
					            sswapIndividual.addProperty(predicate, value);
					            AIpredicate=predicate;
							}
	                        if(lookupName.equals("formatted_address")){
	                        	String value=(String) attractionList.get(0).get("formatted_address");
					            sswapIndividual.addProperty(predicate, value);
					            AFpredicate=predicate;
	                        }
						//}
					}
					if(indName.equals("hotel")){
						String lookupName = getStrName(property.getURI());
						predicate = rigGraph.getPredicate(property.getURI());
						if(lookupName.equals("ranking")){
							 String value=(String) hotelList.get(0).get("ranking");
				             sswapIndividual.addProperty(predicate, value);
				             HRpredicate=predicate;
						}
						if(lookupName.equals("id")){
							 String value=(String) hotelList.get(0).get("id");
				             sswapIndividual.addProperty(predicate, value);
				             HIpredicate=predicate;
						}
						if(lookupName.equals("name")){
							 String value=(String) hotelList.get(0).get("name");
				             sswapIndividual.addProperty(predicate, value);
				             HNpredicate=predicate;
						}
						if(lookupName.equals("longitude")){
							 String value=(String) hotelList.get(0).get("longitude");
				             sswapIndividual.addProperty(predicate, value);
				             HLngpredicate=predicate;
						}
						if(lookupName.equals("low_rate")){
							 String value=(String) hotelList.get(0).get("low_rate");
							 System.out.println("hotel_low_rate: "+value);
				             sswapIndividual.addProperty(predicate, value);
				             HLowpredicate=predicate;
						}
						if(lookupName.equals("latitude")){
							 String value=(String) hotelList.get(0).get("latitude");
				             sswapIndividual.addProperty(predicate, value);
				             HLatpredicate=predicate;
						}
						if(lookupName.equals("room_price")){
							 String value=(String) hotelList.get(0).get("room_price");
				             sswapIndividual.addProperty(predicate, value);
				             HPpredicate=predicate;
						}
					}
					if(indName.equals("outbound")){
						String lookupName = getStrName(property.getURI());
						predicate = rigGraph.getPredicate(property.getURI());
						if(lookupName.equals("departure_time")){
							 String value=(String) outboundList.get(0).get("departure_time");
	                         sswapIndividual.addProperty(predicate, value);
	                         FODTpredicate=predicate;
						}
						if(lookupName.equals("arrival_time")){
							 String value=(String) outboundList.get(0).get("arrival_time");
	                         sswapIndividual.addProperty(predicate, value);
	                         FOATpredicate=predicate;
						}
						if(lookupName.equals("origin")){
							 String value=(String) outboundList.get(0).get("origin");
							 sswapIndividual.addProperty(predicate, value);
							 FOOpredicate=predicate;
						}
						if(lookupName.equals("destination")){
							 String value=(String) outboundList.get(0).get("destination");
				             sswapIndividual.addProperty(predicate, value);
				             FODpredicate=predicate;
						}
					}	
					if(indName.equals("inbound")){
						String lookupName = getStrName(property.getURI());
						predicate = rigGraph.getPredicate(property.getURI());
						if(lookupName.equals("departure_time")){
							 String value=(String) inboundList.get(0).get("departure_time");
					         sswapIndividual.addProperty(predicate, value);
					         FIDTpredicate=predicate;
						}
						if(lookupName.equals("arrival_time")){
							 String value=(String) inboundList.get(0).get("arrival_time");
					         sswapIndividual.addProperty(predicate, value);
					         FIATpredicate=predicate;
						}
						if(lookupName.equals("origin")){
							 String value=(String) inboundList.get(0).get("origin");
					         sswapIndividual.addProperty(predicate, value);
					         FIOpredicate=predicate;
						}
						if(lookupName.equals("destination")){
							 String value=(String) inboundList.get(0).get("destination");
					         sswapIndividual.addProperty(predicate, value);
					         FIDpredicate=predicate;
						}	
					}
					} else */
			  if (predicate.isObjectPredicate()) {
				  
				System.out.println("^^^^^^^^^Property value is Individual:^^^^^^^^");
				SSWAPIndividual ind = property.getValue().asIndividual();
				indName = getStrName(property.getURI()).toLowerCase();
				System.out.println("------->: " + indName);
				
				if (indName.equals("vaccination")) {
					
					System.out.println("------->: " + indName);
					sswapIndividual.setProperty(predicate, setVaccination(ind));
					
					
				} else if (indName.equals("attraction")) {
					
					System.out.println("------->: " + indName);
					for (int a = 0; a < attractionList.size(); a++) {
						if(a==0){
							sswapIndividual.setProperty(predicate, setAtraction(ind,a));
						}else{
							sswapIndividual.addProperty(predicate, setAtraction(ind,a));
						}
					}
					
					
				}else if(indName.equals("hotel")){
					
					System.out.println("------->: " + indName);
					sswapIndividual.setProperty(predicate, setHotel(ind));
					
					
				}else if(indName.equals("flight")){
					
					System.out.println("------->: " + indName);
					for(int i=0;i<inboundList.size();i++){
					SSWAPIndividual flightInd = null;
					Iterator<SSWAPProperty> iteratorPropertiesFlight = ind.getProperties().iterator();
					
					while (iteratorPropertiesFlight.hasNext()) {
						
						SSWAPProperty propertyFlight = iteratorPropertiesFlight.next();
						SSWAPPredicate predicateFlight = rigGraph.getPredicate(propertyFlight.getURI());
						 flightInd = propertyFlight.getValue().asIndividual();
						indName = getStrName(propertyFlight.getURI()).toLowerCase();
										
							 if(indName.equals("outbound")){
			
									ind.setProperty(predicateFlight, setOutBound(flightInd,i));
								}
							else if(indName.equals("inbound")){
								
									ind.setProperty(predicateFlight, setInBound(flightInd, i));	
								}
							 
					}
						sswapIndividual.setProperty(predicate, ind);
					}
					
				}
				System.out.println("indName: " + indName);

				System.out.println("^^^^^^^^^Property value is Individual:^^^^^^^^");
				if(indName.equals("package")){
					recursive(ind, indName);
				}
			}
		}
	}

	private SSWAPIndividual setAtraction(SSWAPIndividual sswapIndividual,int a) {
//		SSWAPIndividual tempInd = sswapIndividual
					Iterator<SSWAPProperty> iteratorProperties = sswapIndividual.getProperties().iterator();
			while (iteratorProperties.hasNext()) {
				SSWAPProperty property = iteratorProperties.next();
				SSWAPPredicate predicate = rigGraph.getPredicate(property.getURI());

				String lookupName = getStrName(property.getURI());
				// SSWAPPredicate predicate =
				// rigGraph.getPredicate(property.getURI());
				if (lookupName.equals("rating")) {
					String value = (String) attractionList.get(a).get("rating");
					sswapIndividual.setProperty(predicate, value);
					ARpredicate = predicate;
				}
				if (lookupName.equals("types")) {
					String value = (String) attractionList.get(a).get("types");
					sswapIndividual.setProperty(predicate, value);
					ATpredicate = predicate;
				}
				if (lookupName.equals("name")) {
					String value = (String) attractionList.get(a).get("name");
					sswapIndividual.setProperty(predicate, value);
					ANpredicate = predicate;
				}
				if (lookupName.equals("index")) {
					String value = (String) attractionList.get(a).get("index");
					sswapIndividual.setProperty(predicate, value);
					AIpredicate = predicate;
				}
				if (lookupName.equals("formatted_address")) {
					String value = (String) attractionList.get(a).get("formatted_address");
					sswapIndividual.setProperty(predicate, value);
					AFpredicate = predicate;
				}
			}
		
		return sswapIndividual;
	}
	private SSWAPIndividual setHotel(SSWAPIndividual sswapIndividual) {
		Iterator<SSWAPProperty> iteratorProperties = sswapIndividual.getProperties().iterator();
		while (iteratorProperties.hasNext()) {
		SSWAPProperty property = iteratorProperties.next();
		SSWAPPredicate predicate = rigGraph.getPredicate(property.getURI());
		String lookupName = getStrName(property.getURI());
		predicate = rigGraph.getPredicate(property.getURI());
		if(lookupName.equals("ranking")){
			 String value=(String) hotelList.get(0).get("ranking");
             sswapIndividual.setProperty(predicate, value);
             HRpredicate=predicate;
		}
		if(lookupName.equals("id")){
			 String value=(String) hotelList.get(0).get("id");
             sswapIndividual.setProperty(predicate, value);
             HIpredicate=predicate;
		}
		if(lookupName.equals("name")){
			 String value=(String) hotelList.get(0).get("name");
             sswapIndividual.setProperty(predicate, value);
             HNpredicate=predicate;
		}
		if(lookupName.equals("longitude")){
			 String value=(String) hotelList.get(0).get("longitude");
             sswapIndividual.setProperty(predicate, value);
             HLngpredicate=predicate;
		}
		if(lookupName.equals("low_rate")){
			 String value=(String) hotelList.get(0).get("low_rate");
			 System.out.println("hotel_low_rate: "+value);
             sswapIndividual.setProperty(predicate, value);
             HLowpredicate=predicate;
		}
		if(lookupName.equals("latitude")){
			 String value=(String) hotelList.get(0).get("latitude");
             sswapIndividual.setProperty(predicate, value);
             HLatpredicate=predicate;
		}
		if(lookupName.equals("room_price")){
			 String value=(String) hotelList.get(0).get("room_price");
             sswapIndividual.setProperty(predicate, value);
             HPpredicate=predicate;
		}
		}
		return sswapIndividual;
	}
	private SSWAPIndividual setOutBound(SSWAPIndividual sswapIndividual,int ob){
		
			Iterator<SSWAPProperty> iteratorProperties = sswapIndividual.getProperties().iterator();
			while (iteratorProperties.hasNext()) {
				SSWAPProperty property = iteratorProperties.next();
				SSWAPPredicate predicate = rigGraph.getPredicate(property.getURI());
			String lookupName = getStrName(property.getURI());
			predicate = rigGraph.getPredicate(property.getURI());
			if(lookupName.equals("departure_time")){
				 String value=(String) outboundList.get(ob).get("departure_time");
                 sswapIndividual.setProperty(predicate, value);
                 FODTpredicate=predicate;
			}
			if(lookupName.equals("arrival_time")){
				 String value=(String) outboundList.get(ob).get("arrival_time");
                 sswapIndividual.setProperty(predicate, value);
                 FOATpredicate=predicate;
			}
			if(lookupName.equals("origin")){
				 //String value=(String) outboundList.get(ob).get("origin");
				String value ="K E B E T E *->";
				 sswapIndividual.setProperty(predicate, value);
				 FOOpredicate=predicate;
			}
			if(lookupName.equals("destination")){
				 String value=(String) outboundList.get(ob).get("destination");
	             sswapIndividual.setProperty(predicate, value);
	             FODpredicate=predicate;
			}
		}	
			return sswapIndividual;
	}
	private SSWAPIndividual setInBound(SSWAPIndividual sswapIndividual,int ib){
		
		Iterator<SSWAPProperty> iteratorProperties = sswapIndividual.getProperties().iterator();
			while (iteratorProperties.hasNext()) {
				SSWAPProperty property = iteratorProperties.next();
				SSWAPPredicate predicate = rigGraph.getPredicate(property.getURI());
			String lookupName = getStrName(property.getURI());
			predicate = rigGraph.getPredicate(property.getURI());
			if(lookupName.equals("departure_time")){
				 String value=(String) inboundList.get(ib).get("departure_time");
		         sswapIndividual.setProperty(predicate, value);
		         FIDTpredicate=predicate;
			}
			if(lookupName.equals("arrival_time")){
				 String value=(String) inboundList.get(ib).get("arrival_time");
		         sswapIndividual.setProperty(predicate, value);
		         FIATpredicate=predicate;
			}
			if(lookupName.equals("origin")){
				 String value=(String) inboundList.get(ib).get("origin");
		         sswapIndividual.setProperty(predicate, value);
		         FIOpredicate=predicate;
			}
			if(lookupName.equals("destination")){
				 String value=(String) inboundList.get(ib).get("destination");
		         sswapIndividual.setProperty(predicate, value);
		         FIDpredicate=predicate;
			}	
		}
		 
		return sswapIndividual;
	}
	private SSWAPIndividual setVaccination(SSWAPIndividual sswapIndividual) {
		for (int a = 0; a < vaccinationList.size(); a++) {
			Iterator<SSWAPProperty> iteratorProperties = sswapIndividual.getProperties().iterator();
			while (iteratorProperties.hasNext()) {
				SSWAPProperty property = iteratorProperties.next();
				SSWAPPredicate predicate = rigGraph.getPredicate(property.getURI());

				String lookupName = getStrName(property.getURI());
				// for(int v=0;v<vaccinationList.size();v++){
				// SSWAPPredicate predicate =
				// rigGraph.getPredicate(property.getURI());
				if (lookupName.equals("category")) {
					String value = (String) vaccinationList.get(a).get("category");
					sswapIndividual.setProperty(predicate, value);
					VCpredicate = predicate;
				} else {
					String value = (String) vaccinationList.get(a).get("description");
					sswapIndividual.setProperty(predicate, value);
					VDpredicate = predicate;
				}
			}
		}
		return sswapIndividual;

	}

	private String getStrValue(SSWAPIndividual sswapIndividual, SSWAPPredicate sswapPredicate) {
		String value = null;
		SSWAPProperty sswapProperty = sswapIndividual.getProperty(sswapPredicate);
		if (sswapProperty != null) {
			value = sswapProperty.getValue().asString();
			if (value.isEmpty()) {
				value = null;
			}
		}
		return value;
	}

	private String getStrName(URI uri) {
		String[] a = uri.toString().split("#", 2);

		System.out.println("predicate: " + a[1]);
		return a[1];
	}

}
