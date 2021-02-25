package chatagent.controller;

import java.io.IOException;

import org.json.simple.JSONObject;
import org.json.*;
import org.json.simple.parser.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.JsonParser;

import chatagent.utility.Utility;
import chatagent.classes.ChatagentInterface;

@RestController
public class InterfaceController {

	ChatagentInterface chatagentInterfaceObj = new ChatagentInterface();
	
	public InterfaceController() throws IOException {
		System.out.println("################################");
		new Utility().utilityDesc();
	}

	@RequestMapping(value = "/query", method = RequestMethod.POST)
	public String getTicketDataById(@RequestBody String jsonData) throws ParseException {
		if(chatagentInterfaceObj.checkAgentAvailability()){		
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(jsonData);
		JSONObject jo = (JSONObject) obj;
		 String userId = (String) jo.get("userId");
		 String questionText = (String) jo.get("questionText");
		chatagentInterfaceObj.updateInterface(userId, questionText);
		return "OK";
		}
		else{
			return "No Agents available!!!";
		}
	}

	@RequestMapping(value = "/queryfull", method = RequestMethod.POST)
	public String getResponse(@RequestBody String jsonData) throws ParseException {
		if(chatagentInterfaceObj.checkAgentAvailability()){		
		JSONParser parser = new JSONParser();
		Object obj = parser.parse(jsonData);
		JSONObject jo = (JSONObject) obj;
		 String userId = (String) jo.get("userId");
		 String questionText = (String) jo.get("questionText");
		chatagentInterfaceObj.updateInterface(userId, questionText);
		
		while(chatagentInterfaceObj.getUserMsgDirection(userId) != "a");
		
		return chatagentInterfaceObj.getAnswer(userId);				
		}
		else{
			return "No Agents available!!!";
		}
	}
	
	public static void sendAnswerText(String userId, String answerText){
		RestTemplate restTemplate = new RestTemplate();
		String chatbotUrl = "http://localhost:8585/models";
		String result = null;
		try {
			System.out.println("sent response");
			//result = restTemplate.postForObject(chatbotUrl, answerText, String.class);
		}
		catch (Exception e){
			e.printStackTrace();			
		}
	}
	
	@RequestMapping("*")
	public String fallbackMethod(){
		return "fallback method";
	}
	
	/*
	@RequestMapping(value = "/getMultiTicketPrediction", method = { RequestMethod.POST })
	public List<Object> getMultiTicketPredictionById(HttpServletRequest request, @RequestBody String idList) {
		String[] idArray=idList.split("~~~~");
		IncidentList incClassList = new IncidentList ();
		List<Incident> incList = new ArrayList<Incident>();
		for(int i=0;i<idArray.length;i++){
			if(idArray[i].isEmpty()){
				continue;
			}				
		incList.add(new Incident(interfaceService.getTestIncidentTicket(Long.parseLong(idArray[i]))));
		}
		incClassList.setIncidents(incList);
		RestTemplate restTemplate = new RestTemplate();
		String url = Utility.prop.getProperty("multiPredictionTestUrl");
		// String url = "http://localhost:8080/Classifier/classify";
		// String url = "http://10.177.120.69:8080/Classifier/classify";
		List<RestResponse> result = new ArrayList<RestResponse>();
		//List<RestResponse> result = new ArrayList<RestResponse>();
	
		try {
			result =  restTemplate.postForObject(url, incClassList, List.class);		
		} catch (Exception e) {
			e.printStackTrace();
		}
		List<Object> res = new ArrayList<Object>();
		res.add(result);
		return res;
	}
*/
}
