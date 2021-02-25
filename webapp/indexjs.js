var agentId="1";
var app = angular.module('chatAgent', []);
app.controller('chatAgentController', function($scope, $http) {
   // $scope.messages = 
});


function sendAnswerText(){
                
    answerTextElement = document.getElementById("answerArea");
    answerText = answerTextElement.value.trim(); 

    currentActiveUserIdList = document.getElementsByClassName('chatbox_active_userid');
    if(currentActiveUserIdList.length > 0){
        
        currentActiveUserId = currentActiveUserIdList[0];
        userIdString = currentActiveUserId.getAttribute('id');    
        userId = userIdString.toString().substring('user_'.length,userIdString.toString().length);
           
        if((answerText != null) && (answerText.length>0) && (status_Flag == true)){
            msgContainerElement = document.getElementsByClassName("chatbox_messages");    
            msgElement = document.createElement('div');
            msgElement.setAttribute("class","chatbox_messages_chatagent");
            msgTextElement = document.createElement('p');
            msgTextElement.innerHTML = answerText;
            msgTextElement.setAttribute("class","message");        
            showElement = document.createElement('div');
            showElement.setAttribute("class","arrow_chatagent");
            msgTextElement.appendChild(showElement);
            msgElement.appendChild(msgTextElement);
            msgContainerElement[0].appendChild(msgElement);
            
            //msgContainerElement[0].animate({ scrollTop: $(document).height() }, "fast");
            $(".chatbox_messages_user-message--ind-message").animate({ scrollTop: 300 }, "slow");
            //$( ".chatbox_messages_user-message--ind-message" ).animate({ "left": "-=50px" }, "slow" );
            
            ChatInterface.updateAnswer(agentId, userId, answerText);
            answerTextElement.value = "";
            answerTextElement.focus();
        }

        else{
            if(status_Flag == false){
                alert('Status Offline');
            }
            answerTextElement.value = "";
            answerTextElement.focus();
        }
        $(".chatbox_messages").animate({ scrollTop: $(".chatbox_messages> div").length*$(document).height() }, "fast");
    }
    else{
        alert("No active Users available!!!");
        answerTextElement.value = "";
        answerTextElement.focus();
    }
}

function addQuestionText(reqUserId, questionText){
    found = false;
    active = false;
    userId = reqUserId;
    
    currentActiveUserIdList = document.getElementsByClassName('chatbox_active_userid');
    if(currentActiveUserIdList.length > 0){
        currentActiveUserId = currentActiveUserIdList[0];
        userIdString = currentActiveUserId.getAttribute('id');    
        activeUserId = userIdString.toString().substring('user_'.length,userIdString.toString().length);
        if(activeUserId == reqUserId){
            found = true;
            active = true;
        }
    }
    
    userList = document.getElementsByClassName('chatbox_user_list');
    if((userList.length > 0) && (found == false)){
        userList = userList[0];
        
        for(i = 0; (i < userList.getElementsByTagName('div').length) && (found==false); i++){        
            if (userList.getElementsByTagName('div')[i].className.toString().search("chatbox_user_online") >= 0) {
                userTabId = userList.getElementsByTagName('div')[i].id;
                if(userTabId.toString().substring('user_'.length,userTabId.toString().length) == reqUserId){
                    found = true;
                    userId = userTabId;
                    userList.getElementsByTagName('div')[i].setAttribute("class","chatbox_user_online chatbox_user_new_msg");
                    if(notification_Flag == true && isActive==false){
                        sendNotification('Hey!!! You have a new Message.');
                    }
                }
            }
        }
        if(found == true){
            return;
        }
    }
    
    if(active == true){
        msgContainerElement = document.getElementsByClassName('chatbox_messages');
        msgElement = document.createElement('div');
        msgElement.setAttribute("class","chatbox_messages_chatbot");
        msgTextElement = document.createElement('p');
        msgTextElement.innerHTML = questionText;
        msgTextElement.setAttribute("class","message");
        
        
        showElement = document.createElement('div');
        showElement.setAttribute("class","arrow_chatbot");
        msgTextElement.appendChild(showElement);
        
        msgElement.appendChild(msgTextElement);
        msgContainerElement[0].appendChild(msgElement);
        if(notification_Flag == true && isActive==false){
            sendNotification('Hey!!! You have a new Message.');
        }
       $(".chatbox_messages").animate({ scrollTop: $(".chatbox_messages> div").length*$(document).height() }, "fast");
    }
    else if(found == false){      
        newUserRequest(reqUserId);   
    }
}

function addNewUser(eventObj){
    userId = eventObj.target.parentNode.id;
/*     wrapperDivId = eventObj.target.parentNode.id;
    reqElementId = 'div_' + wrapperDivId; */
    reqElementId = 'div_' + userId;
    document.getElementById(reqElementId).remove();
    
    userDivWrapper = document.getElementsByClassName('chatbox_user_list')[0];
    userDiv = document.createElement('div');
    userDiv.setAttribute("class","chatbox_user_online chatbox_user_new_msg");
    //userDiv.setAttribute("id","user_" + userDivWrapper.children.length);
    userDiv.setAttribute("id","user_" + userId);
    
   
        
    
    //userDivText = document.createElement('p');
    //userDivText.innerHTML = 'User ' + userId;
    //userDiv.appendChild(userDivText);
    userDiv.innerHTML = 'User ' + userId;
    userDiv.onclick = selectUserId;
    userDivWrapper.appendChild(userDiv);
    
     
    $('#user_'+userId).append('<i class="material-icons user_req_icon" onclick="deleteUser(event);" style="float:right;font-size:20px;color:White;">clear</i>');
    
    //send info to java
}

function loadUsers(userIdList){
    for(i=0;i<userIdList.length;i++){
        userId = userIdList[i];
        
        userDivWrapper = document.getElementsByClassName('chatbox_user_list')[0];
        userDiv = document.createElement('div');
        userDiv.setAttribute("class","chatbox_user_online chatbox_user_new_msg");
        //userDiv.setAttribute("id","user_" + userDivWrapper.children.length);
        userDiv.setAttribute("id","user_" + userId);
        
        
        //userDivText = document.createElement('p');
        //userDivText.innerHTML = 'User ' + userId;
        //userDiv.appendChild(userDivText);
        
        userDiv.innerHTML = 'User ' + userId;
        
        userDiv.onclick = selectUserId;
        userDivWrapper.appendChild(userDiv);
        $('#user_'+userId).append('<i class="material-icons user_req_icon" onclick="deleteUser(event);" style="float:right;font-size:20px;color:White;">clear</i>');
    }
    
}

function deleteUser(eventObj){
    eventObj.stopPropagation()
    userDivId = eventObj.target.parentNode.id;
    userId = userDivId.toString().substring('user_'.length,userDivId.toString().length);
       
    if (confirm("Are you sure you want to close the chat session with User: " + userId + "?")) {        
        currentActiveUserIdList = document.getElementsByClassName('chatbox_active_userid');
        //console.log(currentActiveUserIdList.length);
        if(currentActiveUserIdList.length > 0){
            currentActiveUserId = currentActiveUserIdList[0];
            userIdString = currentActiveUserId.getAttribute('id');    
            //activeUserId = userIdString.toString().substring('user_'.length,userIdString.toString().length);
            
            if(userIdString == userDivId){
                var elemObj = document.getElementsByClassName('chatbox_messages');
                if(elemObj.length > 0){
                    while (elemObj[0].hasChildNodes()) {
                        elemObj[0].removeChild(elemObj[0].lastChild);
                    }
                }
            }
        }
    ChatInterface.deleteUser(agentId,userId);
    document.getElementById(userDivId).remove();    
    } else {
        txt = "You pressed Cancel!";
    }    
}

function selectUserId(eventObj){
/*     console.log(event.srcElement.id); 
    console.log(this.id);  */
    selectedUserId = document.getElementById(this.id);
    currentActiveUserIdList = document.getElementsByClassName('chatbox_active_userid');
    if(currentActiveUserIdList.length > 0){
        currentActiveUserId = currentActiveUserIdList[0];
        if(currentActiveUserId.id != selectedUserId.id){            
            currentActiveUserId.setAttribute("class","chatbox_user_online");
        }
        else{
            return;
        }
    }
    selectedUserId.setAttribute("class","chatbox_user_online chatbox_active_userid");
    
    userId = this.id.toString().substring('user_'.length,this.id.toString().length);
    reloadAllMsgs(agentId, userId);
}

function newUserRequest(userId){
    reqContainerElement = document.getElementsByClassName('chatbox_new_user_requests')[0];
    foundReq = false;
    for(i=0;i<reqContainerElement.getElementsByTagName('div').length;i++){
        if(reqContainerElement.getElementsByTagName('div')[i].id == userId){
            foundReq = true;
        }
    }
    
    if(foundReq == false){
        userReqCount = reqContainerElement.children.length;
        reqElement = document.createElement('div');
        reqElement.setAttribute("class","chatbox_new_user_req");
        //reqElement.setAttribute("id","div_"+userReqCount);
        reqElement.setAttribute("id","div_"+userId);
        reqTextElement = document.createElement('p');
        reqTextElement.setAttribute("class","chatbox_new_user_para");
        reqTextElement.innerHTML = 'New User Request:';
        
        wrapperDiv = document.createElement('div');
        //wrapperDiv.setAttribute("id", userReqCount);
        wrapperDiv.setAttribute("id", userId);
        wrapperDiv.style.display = 'inline';
        
        reqElement.appendChild(reqTextElement);    
        reqElement.appendChild(wrapperDiv); 

        
        reqContainerElement.appendChild(reqElement);
        //$('#'+userReqCount).append('<i class="material-icons user_req_icon" onclick="addNewUser(event);" style="margin-top: 10px;font-size:20px;color:White;">done</i>');
        //$('#'+userReqCount).append('<i class="material-icons user_req_icon" onclick="rejectReq(event);" style="margin-top: 10px;font-size:20px;color:White;">clear</i>');
        
        $('#'+userId).append('<i class="material-icons user_req_icon" onclick="addNewUser(event);" style="margin-top: 10px;font-size:20px;color:White;">done</i>');
        $('#'+userId).append('<i class="material-icons user_req_icon" onclick="rejectReq(event);" style="margin-top: 10px;font-size:20px;color:White;">clear</i>');
        
        if(notification_Flag == true && isActive==false){
                sendNotification('Hey!!! You have a new Chat request.');
            }
    }
}

function rejectReq(eventObj){
    userId = eventObj.target.parentNode.id;
    wrapperDivId = userId;
    reqElementId = 'div_' + wrapperDivId;
    document.getElementById(reqElementId).remove();    
    ChatInterface.rejectReq(agentId,userId);
//send info to java    
}

function sendNotification(notificationText){
    var img = '/to-do-notifications/img/icon-128.png';
    var notification = new Notification('Chatagent', { body: notificationText, tag: 'chatbotNotification'});
    notification.addEventListener('error', function(errorMsg) {
        console.log('something went wrong');
        console.log(errorMsg);
    });

    notification.addEventListener('click', notification_clicked);
    setTimeout(notification.close.bind(notification), 5000);
}

function notification_clicked(evt) {
      parent.focus();
      window.focus(); //older browsers
      this.close();
}

var isActive;

window.onfocus = function () { 
  isActive = true; 
}; 

window.onblur = function () { 
  isActive = false; 
}; 

var notification_Flag;
var status_Flag;

function toggleNotification() {
    if(notification_Flag == true){
        document.getElementsByClassName('notify_on_btn')[0].setAttribute('class','notify_off_btn');
        document.getElementsByClassName('nofity_on_icon')[0].setAttribute('class','material-icons nofity_off_icon');
        document.getElementsByClassName('notify_on_text2')[0].setAttribute('class','notify_off_text2');
        document.getElementsByClassName('notify_on_text1')[0].setAttribute('class','notify_off_text1');
        notification_Flag = false;
    }
    else{
        document.getElementsByClassName('notify_off_btn')[0].setAttribute('class','notify_on_btn');
        document.getElementsByClassName('nofity_off_icon')[0].setAttribute('class','material-icons nofity_on_icon');
        document.getElementsByClassName('notify_off_text2')[0].setAttribute('class','notify_on_text2');
        document.getElementsByClassName('notify_off_text1')[0].setAttribute('class','notify_on_text1');
        notification_Flag = true;
    }
}

function notificationPermission() {
    if (!("Notification" in window)) {
        alert("This browser does not support system notifications");
    }
    else if (Notification.permission === "granted") {        
        //document.getElementById('notification_status_icon').style.color = "Black";
        //document.getElementById('notification_status').innerHTML = "Enabled";
        notification_Flag = true;
    }
    else if (Notification.permission !== 'denied') {
        Notification.requestPermission(function (permission) {
            if (permission === "granted") {
                //document.getElementById('notification_status_icon').style.color = "Black";
                //document.getElementById('notification_status').innerHTML = "Enabled";
                notification_Flag = true;
            }
            else{
                //document.getElementById('notification_status_icon').style.color = "Grey";
                //document.getElementById('notification_status').innerHTML = "Disabled";
                notification_Flag = false;
            }
        });
    }
    else{
        //document.getElementById('notification_status_icon').style.color = "Grey";
        //document.getElementById('notification_status').innerHTML = "Disabled";
        notification_Flag = false;
    }
}

function clearChatHistory(){
    currentActiveUserIdList = document.getElementsByClassName('chatbox_active_userid');
    if(currentActiveUserIdList.length > 0){
        currentActiveUserId = currentActiveUserIdList[0];
        userIdString = currentActiveUserId.getAttribute('id');    
        userId = userIdString.toString().substring('user_'.length,userIdString.toString().length);
        
        
    
        ChatInterface.clearAllMsgs(agentId,userId);
        var elemObj = document.getElementsByClassName('chatbox_messages');
        if(elemObj.length > 0){
            while (elemObj[0].hasChildNodes()) {
                elemObj[0].removeChild(elemObj[0].lastChild);
            }
        }
    }
}

function terminateSession(){
    if (confirm("Are you sure you want to terminate the session?")) {        
        ChatInterface.terminateSession(agentId);
        window.open(location.href, "_self").close();
    }    
}

function reloadAllMsgs(agentId, userId){
    var elemObj = document.getElementsByClassName('chatbox_messages');
        if(elemObj.length > 0){
            while (elemObj[0].hasChildNodes()) {
                elemObj[0].removeChild(elemObj[0].lastChild);
            }
        }
    
    var msgContainerElement = document.getElementsByClassName('chatbox_messages');
    //msgContainerElement[0].animate({ scrollTop: $(document).height() }, "fast");
	 ChatInterface.getAllMsgs(agentId, userId, {
        callback:function(chatHistory) { 
        msgArray = chatHistory[1];
        msgArrayType = chatHistory[0];
        if(msgArrayType.length <= 0){
            alert("No messages to load!!!");
        }
        else{
            for(i=0;i<msgArrayType.length;i++){
                if(msgArrayType[i] == "question"){
                    msgContainerElement = document.getElementsByClassName('chatbox_messages');
                    msgElement = document.createElement('div');
                    msgElement.setAttribute("class","chatbox_messages_chatbot");
                    msgTextElement = document.createElement('p');
                    msgTextElement.innerHTML = msgArray[i];
                    msgTextElement.setAttribute("class","message");                
                    showElement = document.createElement('div');
                    showElement.setAttribute("class","arrow_chatbot");
                    msgTextElement.appendChild(showElement);                
                    msgElement.appendChild(msgTextElement);
                    msgContainerElement[0].appendChild(msgElement);
                }
                else{
                    msgContainerElement = document.getElementsByClassName("chatbox_messages");    
                    msgElement = document.createElement('div');
                    msgElement.setAttribute("class","chatbox_messages_chatagent");
                    msgTextElement = document.createElement('p');
                    msgTextElement.innerHTML = msgArray[i];
                    msgTextElement.setAttribute("class","message");        
                    showElement = document.createElement('div');
                    showElement.setAttribute("class","arrow_chatagent");
                    msgTextElement.appendChild(showElement);
                    msgElement.appendChild(msgTextElement);
                    msgContainerElement[0].appendChild(msgElement);
                    answerTextElement = document.getElementById("answerArea");
                    answerTextElement.value = "";
                    answerTextElement.focus();
                }
                $(".chatbox_messages").animate({ scrollTop: $(".chatbox_messages> div").length*$(document).height() }, "fast");
            }
        }
  }  
});
  
}

window.onload=function()
{
    
    agentId = prompt("Please enter your Agent ID:", "");
    if (agentId == null || agentId == "") {
        window.stop();
        document.execCommand('Stop');
        alert("Cannot initiate the session!!!");
    } else {
            
        
        
        notificationPermission();
        
     
        ChatInterface.initChatagent(agentId, {
        callback:function(returnValue) {
            //console.log(returnValue);  
            } });
        
        
        
        //ChatInterface.initChatagent(agentId);
       // userIdList = "";
        

        
        ChatInterface.getUserIdList(agentId, {
            callback:function(userIdList) {
                if(userIdList.length > 0){
                    loadUsers(userIdList);
                }
                    
            }
        });
           

           
        currentActiveUserIdList = document.getElementsByClassName('chatbox_active_userid');
        if(currentActiveUserIdList.length > 0){
            //console.log("Loading messages after reload");
            currentActiveUserId = currentActiveUserIdList[0];
            userIdString = currentActiveUserId.getAttribute('id');    
            userId = userIdString.toString().substring('user_'.length,userIdString.toString().length);
            reloadAllMsgs(agentId, userId);
        }


        
        var answerArea = document.getElementById("answerArea");
        answerArea.addEventListener("keyup", function(event) {
            event.preventDefault();
            if (event.keyCode === 13) {
                sendAnswerText();
            }
        });

        
        dwr.engine.setActiveReverseAjax(true); // Initiate reverse ajax polling
        dwr.engine.setErrorHandler(errorHandler); // Called when a call and all retry attempts fail
        dwr.engine.setPollStatusHandler(updatePollStatus); // Optional function to call when the reverse ajax status changes (e.g. online to offline)
        updatePollStatus(true); // Optional - We are online right now!  Until DWR determines we are not!
        //dwr.engine.setNotifyServerOnPageUnload(true); // Optional - When the page is unloaded, remove this ScriptSession.	
        //Tabs.init('ChatInterface', 'tabContents'); // Initialize the tabs for this display    
        //ChatInterface.updateInterface(); // Make a call to the server to begin updating the table!   
        
        
        
        addAttributeToScriptSession(); // Make a remote call to the server to add an attribute onto the ScriptSession which will be used in determining what pages receive updates!   
    }
}
	  
function errorHandler(message, ex) {
    dwr.util.setValue("error", "Cannot connect to server. Initializing retry logic.", {escapeHtml:false});
    setTimeout(function() { dwr.util.setValue("error", ""); }, 5000)
}


function updatePollStatus(pollStatus) {
    dwr.util.setValue("pollStatus", pollStatus ? "Online" : "Offline", {escapeHtml:false});
    pollStatusValue = document.getElementById("pollStatus").innerHTML;
    if(pollStatusValue == "Offline"){
        alert("Cannot connect to Server!!!");
        notificationRotationElement = document.getElementsByClassName('notification_yin_yang_rotating');
        if(notificationRotationElement.length > 0){
            removeAttributeToScriptSession();        
            notificationElement = document.getElementsByClassName('notification_yin_yang')[0];       
            notificationElement.classList.remove("notification_yin_yang_rotating");
            document.getElementsByClassName('status_on_icon')[0].setAttribute('class','material-icons status_off_icon');
        }
    }
}

// Make a remote call to add an attribute on the ScriptSession.
// Only clients that have this attribute set will receive updates.	  
function addAttributeToScriptSession() {
    status_Flag = true;
    //ChatInterface.addAttributeToScriptSession();
    ChatInterface.addAttr(agentId);
}

// Make a remote call to remove an attribute from the ScriptSession.
// Clients that call this will no longer receive updates (unless addAttributeToScriptSession is called again).	  	  
function removeAttributeToScriptSession() {
    status_Flag = false;
    ChatInterface.removeAttributeToScriptSession();
}

function reverseNotification(){
    pollStatus = document.getElementById('pollStatus').innerHTML ;
    currentActiveUserIdList = document.getElementsByClassName('chatbox_user_online');
    currentActiveUserReqIdList = document.getElementsByClassName('chatbox_new_user_req');

    
    if((currentActiveUserIdList.length > 0) || (currentActiveUserReqIdList.length > 0)){
        alert("Not possible to go offline with active chat sessions / requests!!!");
    }
    else{
        notificationRotationElement = document.getElementsByClassName('notification_yin_yang_rotating');
        if(notificationRotationElement.length > 0){
            removeAttributeToScriptSession();        
            notificationElement = document.getElementsByClassName('notification_yin_yang')[0];       
            notificationElement.classList.remove("notification_yin_yang_rotating");
            document.getElementsByClassName('status_on_icon')[0].setAttribute('class','material-icons status_off_icon');
        }
        else{
            addAttributeToScriptSession();
            notificationElement = document.getElementsByClassName('notification_yin_yang')[0];
            notificationElement.setAttribute("class",'notification_yin_yang_rotating notification_yin_yang');
            document.getElementsByClassName('status_off_icon')[0].setAttribute('class','material-icons status_on_icon');
        }
    }
}