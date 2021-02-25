package chatagent.classes;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;

import org.directwebremoting.Browser;
import org.directwebremoting.ScriptSession;
import org.directwebremoting.ScriptSessionFilter;
import org.directwebremoting.ScriptSessions;
import org.directwebremoting.ServerContextFactory;
import org.directwebremoting.WebContextFactory;

import chatagent.controller.InterfaceController;

public class ChatagentInterface
{
	Chatagent chatagentObj;
    public ChatagentInterface(){
    	chatagentObj = Chatagent.getInstance();
    }   

    public String getUserMsgDirection(String userId){
    	return chatagentObj.getUserMsgDirection(userId);
    }
    public String getAnswer(String userId){
    	String agentId = chatagentObj.checkUserId(userId);
    	return chatagentObj.getAnswer(agentId, userId);
    }
    
    
    
    
    public void updateInterface(final String userId, final String questionText)
    {    	
    	if((questionText != null) && (userId != null)){  		
    		String agentId = chatagentObj.checkUserId(userId);
    		if(agentId == null){
    			agentId = chatagentObj.getChatagentId();
    		}    		
    		chatagentObj.addQuestionText(agentId, userId, questionText);
    	
    	
        // Get the current page.
        String page = ServerContextFactory.get().getContextPath() + "/index.html";
        // Create a new AttributeScriptSessionFilter which will look for an attribute on the ScriptSession
        ScriptSessionFilter attributeFilter = new AttributeScriptSessionFilter(SCRIPT_SESSION_ATTR,agentId);
        // Update the page, filters ScriptSessions using attributeFilter.  If the SCRIPT_SESSION_ATTR
        // has not been set on the ScriptSession the page in question will not receive updates.
        Browser.withPageFiltered(page, attributeFilter, new Runnable()
        {
            public void run()
            {   
                // Call DWR's util which adds rows into a table.  peopleTable is the id of the tbody and 
                // data contains the row/column data. 
                //Util.addRows("testIds", data);
                //Util.setValue("testIds", "value of div");
                //ScriptSessions.addScript(("document.title = 'My new title, from DWR reverse AJAX!';"));
                //ScriptSessions.addScript(("document.getElementById('chatagent_box').innerText = 'My new title, from DWR reverse AJAX!';"));                
            	ScriptSessions.addFunctionCall("addQuestionText", userId, questionText);
            }
        });
    	}
    } 
    
    public void terminateSession(String agentId){
    	chatagentObj.removeChatagent(agentId);
    }
    
    public void updateAnswer(String agentId, String userId, String answerText){
    	chatagentObj.addAnswerText(agentId, userId, answerText);
    	InterfaceController.sendAnswerText(userId, answerText);    	
    }
    
    public boolean checkAgentAvailability(){
    	return chatagentObj.checkAgentAvailability();
    }
    
    public void rejectReq(String agentId, String userId){
    	String questionText = chatagentObj.rejectReq(agentId, userId);
    	updateInterface(userId, questionText);
    }
    
    public void deleteUser(String agentId, String userId){
    	//let chatbot know of this
    	chatagentObj.deleteUser(agentId, userId);
    }
    
    public void clearAllMsgs(String agentId, String userId){
    	chatagentObj.clearData(agentId, userId);
    }
    
    public String initChatagent(String agentId){

    	WebContextFactory.get().getSession().setAttribute("agentId", agentId);

    	chatagentObj.initChatagent(agentId); 
    	return agentId;
    }
    
    public ArrayList<String> getUserIdList(String agentId){
    	return chatagentObj.getUserIdList(agentId);
    }
    
    public List<List<String>> getAllMsgs(String agentId, String userId){
    	return chatagentObj.getAllMsgs(agentId, userId);
    }
	
	/**
	 * Called from the client to add an attribute on the ScriptSession.  This
	 * attribute will be used so that only pages (ScriptSessions) that have 
	 * set this attribute will be updated.
	 */
    public void addAttr(String agentId){
    	WebContextFactory.get().getScriptSession().setAttribute("agentId", agentId);
    }
    
    public void addAttributeToScriptSession() {
/*    	 System.out.println("Add Attribute");
    	 Enumeration en = WebContextFactory.get().getSession().getAttributeNames();    	 
         System.out.println(en);
         while(en.hasMoreElements()){
         	System.out.println(en.nextElement().toString());
         }      
         System.out.println();
         System.out.println(WebContextFactory.get().getContextPath());
         System.out.println(WebContextFactory.get().getCurrentPage());
         System.out.println(WebContextFactory.get().getServletContext());
         String[] st = WebContextFactory.get().getSession().getValueNames();    	 
         System.out.println(st);
        for(String ste : st){
         	System.out.println(ste);
         }      
    	System.out.println("Over");
    	System.out.println(agentIdValue);*/
    	
    	ScriptSession scriptSession = WebContextFactory.get().getScriptSession();
    	String agentIdValue = WebContextFactory.get().getSession().getAttribute("agentId").toString();
        scriptSession.setAttribute(SCRIPT_SESSION_ATTR, agentIdValue);
    }
    
    /**
	 * Called from the client to remove an attribute from the ScriptSession.  
	 * When called from a client that client will no longer receive updates (unless addAttributeToScriptSession)
	 * is called again.
	 */
    public void removeAttributeToScriptSession() {
        ScriptSession scriptSession = WebContextFactory.get().getScriptSession();
        scriptSession.removeAttribute(SCRIPT_SESSION_ATTR);
    }
    
    /**
     * This is the ScriptSessionFilter that will be used to filter out all ScriptSessions
     * unless they contain the SCRIPT_SESSION_ATTR attribute. 
     */
    protected class AttributeScriptSessionFilter implements ScriptSessionFilter
    {
        public AttributeScriptSessionFilter(String attributeName, String attributeValue)
        {
            this.attributeName = attributeName;
            this.attributeValue = attributeValue;
        }
        @Override
        public boolean match(ScriptSession session)
        {      	
            Object check = session.getAttribute(attributeName);
            
            
/*            System.out.println(session.getPage());
            System.out.println(session.getAttribute(attributeName));
            Iterator itr = session.getAttributeNames();
            System.out.println(itr);
            while(itr.hasNext()){
            	System.out.println(itr.next().toString());
            }            
            System.out.println(this.attributeValue);*/
            
            
            
            return (check != null && check.equals(this.attributeValue));
        }

        private final String attributeName;
        private final String attributeValue;
    }

    private final static String SCRIPT_SESSION_ATTR = "agentId";
}