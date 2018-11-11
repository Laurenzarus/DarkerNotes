/* 
 * for client side websockets implementation
 * needed for autosave
 */
// wait until page is loaded

//setup before functions
var typingTimer;                //timer identifier
var doneTypingInterval = 5000;  //time in ms (5 seconds)
var socket;

//on keyup, start the countdown
$('#text-area').keyup(function(){
    clearTimeout(typingTimer);
    if (document.getElementById("text-area").innerHTML.length > 0) {
        typingTimer = setTimeout(doneTyping, doneTypingInterval);
    }
});

//user is "finished typing," do something
function doneTyping () {
    //Get Current FileID
	var currFileID = sessionStorage.getItem("currentFileId");
	//Get rawData
	var rawFileData = document.getElementById("text-area").innerHTML;
	
	console.log(sessionStorage.getItem("signedin"))
	
	if (sessionStorage.getItem("signedin") == "true") {
		console.log("sending a message to server now")
		
		socket.send(JSON.stringify({
			action: "Save",
			email: sessionStorage.getItem("email"),
			fileID: currFileID,
			rawData: rawFileData
		}));
	}
	
	//How Do we want to handle the case where a user is making a new file???
	//How do we want to handle the case where a user has not specified a filename
}

//Setting up the WebSocket connection for client side
$(document).ready(function () {
	socket = new WebSocket("ws://localhost:8080/final-project/ws");
	sessionStorage.setItem("signedin", false);
	sessionStorage.setItem("currentFileID", -1);
	socket.onopen = function(event) {
		console.log("Connected in socket.js")
	}
	socket.onmessage = function(event) {
		console.log("Message in socket.js" + event.data)
		
		//A message sent from server to client
		
		//If action == updateFileID {
		//Server sent back a fileID for a new file
		//sessionStorage.setItem("currentFileID", event.data);
		//}
		
		//If action == notification {
		// Display a new notification to the user
		//}
	}
	socket.onclose = function(event) {
		console.log("Disconnected in socket.js")
	}
	
	
});