package chatagent.classes;

import java.io.IOException;
import java.util.*;
import java.util.Map.Entry;

import javax.servlet.ServletException;

import org.directwebremoting.WebContext;
import org.directwebremoting.WebContextFactory;

public class Chatagent {

	private static Chatagent myObj;

	private Chatagent() {
		recentQuestion = null;
		questionArray = new ArrayList<String>();
		recentAnswer = null;
		answerArray = new ArrayList<String>();
		msgArray = new ArrayList<String>();
		msgArrayType = new ArrayList<String>();
		chatMsgDetails = new HashMap<String, HashMap<String, ArrayList<String>>>();
		chatMsgTypeDetails = new HashMap<String, HashMap<String, ArrayList<String>>>();
		chatagentUserMap = new HashMap<String, String>();
		chatagentList = new ArrayList<String>();
		chatagentStats = new HashMap<String,Integer>();
		userMsgDirection = new HashMap<String, String>();
	}

	public static Chatagent getInstance() {
		if (myObj == null) {
			myObj = new Chatagent();
		}
		return myObj;
	}
	
	void clearData(String agentId, String userId){
		recentQuestion = null;
		questionArray = new ArrayList<String>();
		recentAnswer = null;
		answerArray = new ArrayList<String>();
		msgArray = new ArrayList<String>();
		msgArrayType = new ArrayList<String>();
		
		
		HashMap<String, ArrayList<String>> agentChatDetails = chatMsgDetails.get(agentId);
		HashMap<String, ArrayList<String>> agentChatTypeDetails = chatMsgTypeDetails.get(agentId);
		if( agentChatDetails != null){			
			ArrayList<String> userChatList = new ArrayList<String>();
			ArrayList<String> userChatTypeList = new ArrayList<String>();
			if( agentChatDetails.get(userId) != null){				
				agentChatDetails.put(userId, userChatList);
				agentChatTypeDetails.put(userId, userChatTypeList);
				chatMsgDetails.put(agentId, agentChatDetails);
				chatMsgTypeDetails.put(agentId, agentChatTypeDetails);
				userMsgDirection.put(userId, "n");
			}
			else{
				System.out.println("User Details not found!!!!");
			}		
		}
		else{
			System.out.println("Agent Id doesn't exist!!!");
		}
	}
	
	public String recentQuestion;
	public List<String> questionArray;
	public String recentAnswer;
	public List<String> answerArray;
	public List<String> msgArray;
	public List<String> msgArrayType;
	private HashMap<String, HashMap<String, ArrayList<String>>> chatMsgDetails;
	private HashMap<String, HashMap<String, ArrayList<String>>> chatMsgTypeDetails;
	private HashMap<String, String> chatagentUserMap;
	private List<String> chatagentList;
	private HashMap<String,Integer> chatagentStats; 
	private HashMap<String, String> userMsgDirection;
	
	public void initChatagent(String agentId){
		HashMap<String, ArrayList<String>> tempHashMapObj = new HashMap<String, ArrayList<String>> ();
		HashMap<String, ArrayList<String>> tempHashMapObj2 = new HashMap<String, ArrayList<String>> ();
		HashMap<String, ArrayList<String>> checkAgentId = chatMsgDetails.get(agentId);
		if( checkAgentId != null){
			System.out.println("Agent Id already exists!!!");			
		}
		else{
			chatagentList.add(agentId);
			chatMsgDetails.put(agentId, tempHashMapObj);
			chatMsgTypeDetails.put(agentId, tempHashMapObj2);
			chatagentStats.put(agentId,0);
		}
	}
	
	public void removeChatagent(String agentId){			
		if( chatMsgDetails.get(agentId) != null){
			ArrayList<String> userIdList = getUserIdList(agentId);
			for(String userId : userIdList){
				deleteUser(agentId, userId); 
			}
			chatagentList.remove(agentId);
			chatMsgDetails.remove(agentId);
			chatMsgTypeDetails.remove(agentId);
			chatagentStats.remove(agentId);
		}
		else{
			System.out.println("Agent Id doesn't exist!!!");
		}		
	}
	
	public boolean initUser(String agentId, String userId){		
		ArrayList<String> tempArrayList = new ArrayList<String> ();
		ArrayList<String> tempArrayList2 = new ArrayList<String> ();
		HashMap<String, ArrayList<String>> checkAgentId = chatMsgDetails.get(agentId);
		if( checkAgentId != null){
			HashMap<String, ArrayList<String>> agentChatDetails = chatMsgDetails.get(agentId);
			HashMap<String, ArrayList<String>> agentChatTypeDetails = chatMsgTypeDetails.get(agentId);
			//tempHashMapObj.put(userId, tempArrayList);
			agentChatDetails.put(userId, tempArrayList);
			chatMsgDetails.put(agentId, agentChatDetails);
			//tempHashMapObj2.put(userId, tempArrayList2);
			agentChatTypeDetails.put(userId, tempArrayList2);
			chatMsgTypeDetails.put(agentId, agentChatTypeDetails);
			chatagentUserMap.put(userId, agentId);
			
			chatagentStats.put(agentId,chatagentStats.get(agentId) + 1);
			
			return true;
		}
		else{
			return false;
		}
	}
	
	public String getChatagentId(){
		//String ChatagentId = "";
		//ChatagentId = new ArrayList<String>(chatMsgDetails.keySet()).get(0);
		System.out.println(chatagentStats);
		
		Entry<String, Integer> min = null;
		for (Entry<String, Integer> entry : chatagentStats.entrySet()) {
		    if (min == null || min.getValue() > entry.getValue()) {
		        min = entry;
		    }
		}		
		return min.getKey();
	}
	
	public String checkUserId(String userId){
		String agentId = chatagentUserMap.get(userId);
		return agentId;
	}
	
	public void deleteUser(String agentId, String userId){
		//let the chatbot know of this
		HashMap<String, ArrayList<String>> agentChatDetails = chatMsgDetails.get(agentId);
		HashMap<String, ArrayList<String>> agentChatTypeDetails = chatMsgTypeDetails.get(agentId);
		if( agentChatDetails != null){						
			if( agentChatDetails.get(userId) != null){
				agentChatDetails.remove(userId);
				agentChatTypeDetails.remove(userId);
				chatagentUserMap.remove(userId);
				chatMsgDetails.put(agentId, agentChatDetails);
				chatMsgTypeDetails.put(agentId, agentChatTypeDetails);
							
				chatagentStats.put(agentId,chatagentStats.get(agentId)-1);
			}
			else{
				System.out.println("User Details not found!!!!");
			}			
		}
		else{
			System.out.println("Agent Id doesn't exist!!!");
		}
	}
	
	public String rejectReq(String agentId, String userId){
		String questionText = "";
		HashMap<String, ArrayList<String>> agentChatDetails = chatMsgDetails.get(agentId);
		HashMap<String, ArrayList<String>> agentChatTypeDetails = chatMsgTypeDetails.get(agentId);
		if( agentChatDetails != null){						
			if( agentChatDetails.get(userId) != null){
				questionText = agentChatDetails.get(userId).get(0);
				agentChatDetails.remove(userId);
				agentChatTypeDetails.remove(userId);
				chatagentUserMap.remove(userId);
				chatMsgDetails.put(agentId, agentChatDetails);
				chatMsgTypeDetails.put(agentId, agentChatTypeDetails);							
				chatagentStats.put(agentId,chatagentStats.get(agentId)-1);
			}
			else{
				System.out.println("User Details not found!!!!");
			}			
		}
		else{
			System.out.println("Agent Id doesn't exist!!!");
		}
		return questionText;
	}
	
    public ArrayList<String> getUserIdList(String agentId){
    	HashMap<String, ArrayList<String>> agentChatDetails = chatMsgDetails.get(agentId);
		if( agentChatDetails != null){
			ArrayList<String> userIdList = new ArrayList<String>( agentChatDetails.keySet());
	    	return userIdList;
		}
		else{
			
			System.out.println("Agent Id doesn't exist!!!");
			return new ArrayList<String>();
		}    	
    }
    	
	public boolean checkAgentAvailability(){
		if(chatMsgDetails.size() > 0){
			return true;
		}
		return false;
	}
	
	public void addQuestionText(String agentId, String userId, String questionText) {				
		HashMap<String, ArrayList<String>> agentChatDetails = chatMsgDetails.get(agentId);
		HashMap<String, ArrayList<String>> agentChatTypeDetails = chatMsgTypeDetails.get(agentId);
		if( agentChatDetails != null){			
			ArrayList<String> userChatList = agentChatDetails.get(userId);
			ArrayList<String> userChatTypeList = agentChatTypeDetails.get(userId);
			if( userChatList != null){
				userMsgDirection.put(userId, "q");
				userChatList.add(questionText);
				userChatTypeList.add("question");
				agentChatDetails.put(userId, userChatList);
				agentChatTypeDetails.put(userId, userChatTypeList);
				chatMsgDetails.put(agentId, agentChatDetails);
				chatMsgTypeDetails.put(agentId, agentChatTypeDetails);
			}
			else{
				System.out.println("User Details not found!!!!");
				if (initUser(agentId, userId)){
					userMsgDirection.put(userId, "q");
					agentChatDetails = chatMsgDetails.get(agentId);
					agentChatTypeDetails = chatMsgTypeDetails.get(agentId);
					userChatList = agentChatDetails.get(userId);
					userChatTypeList = agentChatTypeDetails.get(userId);
					userChatList.add(questionText);
					userChatTypeList.add("question");							
					agentChatDetails.put(userId, userChatList);
					agentChatTypeDetails.put(userId, userChatTypeList);
					chatMsgDetails.put(agentId, agentChatDetails);
					chatMsgTypeDetails.put(agentId, agentChatTypeDetails);
				}
			}			
		}
		else{
			
			System.out.println("Agent Id doesn't exists!!!");
		}

		//System.out.println(chatMsgDetails);
		//System.out.println(chatMsgTypeDetails);
		
		recentQuestion = questionText;
		questionArray.add(questionText);
		msgArray.add(questionText);
		msgArrayType.add("question");
	}

	public void addAnswerText(String agentId, String userId, String answerText) {
		HashMap<String, ArrayList<String>> agentChatDetails = chatMsgDetails.get(agentId);
		HashMap<String, ArrayList<String>> agentChatTypeDetails = chatMsgTypeDetails.get(agentId);
		if( agentChatDetails != null){			
			ArrayList<String> userChatList = agentChatDetails.get(userId);
			ArrayList<String> userChatTypeList = agentChatTypeDetails.get(userId);
			if( userChatList != null){
				userMsgDirection.put(userId, "a");
				userChatList.add(answerText);
				userChatTypeList.add("answer");
				agentChatDetails.put(userId, userChatList);
				agentChatTypeDetails.put(userId, userChatTypeList);
				chatMsgDetails.put(agentId, agentChatDetails);
				chatMsgTypeDetails.put(agentId, agentChatTypeDetails);
			}
			else{
				System.out.println("User Details not found!!!!");
			}			
		}
		else{
			
			System.out.println("Agent Id doesn't exists!!!");
		}
		
		recentAnswer = answerText;
		answerArray.add(answerText);
		msgArray.add(answerText);
		msgArrayType.add("answer");
	}
	
	public List<List<String>> getAllMsgs(String agentId, String userId) {
		List<List<String>> chatList = new ArrayList<List<String>>();
		
		HashMap<String, ArrayList<String>> agentChatDetails = chatMsgDetails.get(agentId);
		HashMap<String, ArrayList<String>> agentChatTypeDetails = chatMsgTypeDetails.get(agentId);
		if( agentChatDetails != null){			
			ArrayList<String> userChatList = agentChatDetails.get(userId);
			ArrayList<String> userChatTypeList = agentChatTypeDetails.get(userId);
			if( userChatList != null){
				chatList.add(userChatTypeList);
				chatList.add(userChatList);
			}
			else{
				System.out.println("User Details not found!!!!");
				chatList.add(new ArrayList<String>());
				chatList.add(new ArrayList<String>());
			}			
		}
		else{
			
			System.out.println("Agent Id doesn't exists!!!");
		}
		
		
		
		
		//chatList.add(msgArrayType);
		//chatList.add(msgArray);
		return chatList;
	}



public String getUserMsgDirection(String userId){
	String msgDirection = "n";
	if(userMsgDirection.get(userId) != null){
		msgDirection = userMsgDirection.get(userId);
	}
	return msgDirection;
}

public String getAnswer(String agentId, String userId){
	List<List<String>> chatList = new ArrayList<List<String>>();
	
	HashMap<String, ArrayList<String>> agentChatDetails = chatMsgDetails.get(agentId);
	if( agentChatDetails != null){			
		ArrayList<String> userChatList = agentChatDetails.get(userId);
		if( userChatList != null){
			return userChatList.get(userChatList.size()-1);
		}
		else{
			return "User Details not found!!!!";			
		}			
	}
	else{
		
		return "Agent Id doesn't exists!!!";
	}
	
}



}