/*
 ============================================================================
 Name        : Event.java
 Author      : Christoph White
 Version     :
 Copyright   : Please do not copy this without letting me know first. This is
 a learning piece of code that all can reference to use. Anyone taking comp6370 at Suburn
 should refrain from copying this code.

 Description : This holds the state of each event in the simulation. This includedes the time it should run, the
 event that occurs after it, the sending actor, the receiving actor, and all of the logic that may be needed in the simulation
 ============================================================================
 */

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class Event {
	// IF a timestamp in the session key is found more than x minutes outside of the current time, we will
	// indicate that a replay attack has occured. This variable sets the delay
	private final long delayBeforeInvalidTime = 10;
	
	// This is the name we will assign to the generated session keys
	private final String sessionKeyName = "Ks"; 
	
	// The actor sending the message
	private Actor sendingActor;
	
	// The actor recieving the message in this event
	private Actor receivingActor;
	
	// The integer for the time of this event
	// The number '1' indicates this is the starting event
	private int time;
	
	//An integer pointer to the next event in the sequence
	// The number '-1' indicates this is the final event
	private int nextEventTime;
	
	// The encrypted message
	private String encryptedMessage;
	
	//The message or initial message this event should send. If not provided, we use "last received message"
	private String message = "";
	
	// The message for what action this event is taking
	private String actionMess;
	
	// This determines whether logic needs be performed for generating the session key
	// or for adding the nonce
	private boolean genSessionKeyOrNonce = false;
	
	// This is the contructor to use for any event where a message is needed to be sent.
	public Event(Actor sendor, Actor receiver, String inMessage, String action, int currTime, int nextTime) {
		sendingActor = sendor;
		receivingActor = receiver;
		message = inMessage;
		actionMess = action;
		time = currTime;
		nextEventTime = nextTime;
	}
	
	// This is used when we need to generate wither the Nonce or session key. Uses the "last received message" for 
	// message maniuplation
	public Event(Actor sendor, Actor receiver,String action, int currTime, int nextTime, boolean inGenSessionOrNonce) {
		sendingActor = sendor;
		receivingActor = receiver;
		actionMess = action;
		time = currTime;
		nextEventTime = nextTime;
		genSessionKeyOrNonce = inGenSessionOrNonce;
	}
	
	// This constructor is used to generate some message based off of the last received message in the communication
	public Event(Actor sendor, Actor receiver,String action, int currTime, int nextTime) {
		sendingActor = sendor;
		receivingActor = receiver;
		actionMess = action;
		time = currTime;
		nextEventTime = nextTime;
	}
	
	public int getCurrentTime() {
		return time;
	}
	
	public int getNextEvent() {
		return nextEventTime;
	}
	
	// Generates the session key and adds it to the sendor. This methods represents the autheticating service 
	// generating a unique session key.
	private void genSessionKey() {
		// Add the generated session key to the receiving actor
		sendingActor.addsessionKey(sessionKeyName);
	}
	
	// This method is used when a session key has already been 'generated'
	// If the message received contains the session key, add it to the sending actors object
	private void addSessionKey(String lastMessage) {
		if (lastMessage.contains(sessionKeyName)) {
			sendingActor.addsessionKey(sessionKeyName);
		}
	}
	
	// A simple method that takes in the key and message to be encrypted
	private void encryptMessage(String key, String decryptedMessage) {

		if (sendingActor.getLastReceivedMess().isEmpty() && receivingActor.getLastReceivedMess().isEmpty()) {
			// There is no current communication going, therefore, this message is the introduction and does not need encryption.
			encryptedMessage = decryptedMessage;
		}
		else {
			encryptedMessage = "{" + decryptedMessage + "}" + key;
		}
	}
	
	// This generates a timestamp for the session key.
	// Timestamps allows for replay attacks to be stopped
	private String genTimeStamp() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		return sdf.format(ts);
	}
	
	// This will encrypt the session key with timestamp appended
	private String encryptSessionKey(String lastMessage) {
		String encSessionKey;
		String[] messageIds = lastMessage.split("\\|\\|");
		if (messageIds.length < 2) {
			return "Possible Replay Attack";
		}
		//The second person in the introduction is who we want to encrypt this session for.
		// The first person in the ID is going to receive the session key encrypted in a different spot
		String publicKey = sendingActor.getKey(messageIds[1]);
		
		
		encSessionKey = "{" + receivingActor.getName() + "||" + genTimeStamp() + "||" + sendingActor.getSessionKey() + "}" + publicKey;
		return encSessionKey;
	}
	
	// Generates the full session session
	private String genFullSessionKeyMessage(String lastMessage) {
		genSessionKey();
		return lastMessage + "||" + sendingActor.getSessionKey() + "||" + encryptSessionKey(lastMessage);
	}
	
	// Decryptes the received session key using the sendors last received message
	private String decryptSessKeys(String lastMessage) {
		String pubKey = sendingActor.getPubKey();
		if (lastMessage.isEmpty()) {
			return message;
		}
		if (lastMessage.contains("}" + pubKey)) {
			//Decrypt the string using the senders private key
			lastMessage = lastMessage.substring(1,lastMessage.length() - (pubKey.length() + 1));
			if (lastMessage.contains(sendingActor.getNonce())) {
				sendingActor.receivedMyNonce();
			}
			// Let the send get the session key
			addSessionKey(lastMessage);
			// Retrieve the encrypted session key to send to the other party
			if (lastMessage.charAt(0) != '{') {
				lastMessage = lastMessage.substring(lastMessage.indexOf("{")+1, 
						lastMessage.length() - (receivingActor.getPubKey().length() + 1));
			}

		}
		return lastMessage;
	}
	
	// Confirm that the message has the nonce. If nonce is found, we should not be starting the communication
	public String confirmNonce(String lastMessage) {
		// Check to see if this is the sending actors message being confirmed. If if it, return the message the sender 
		// is trying to send the desired message back.
		if (lastMessage.contains("{" + sendingActor.getNonce())) {
			
			// Check to see if the receiver has already validated her nonce, and if so we can set out 'other' to true
			if (receivingActor.getReceivedMyNonce()) {
				sendingActor.receivedOtherNonce();
			}
			// The sender has received and validated his nonce, we can set this to true 
			sendingActor.receivedMyNonce();
			// The receivor's nonce has now been validated, so we can confirm it
			// At the end of this, both the receiver and sender should have confirmed their nonces
			
			
			
			
			if (receivingActor.getReceivedOtherNonce() && sendingActor.getReceivedOtherNonce() && receivingActor.getReceivedMyNonce() && sendingActor.getReceivedMyNonce()) {
				return message;
			}
			return "Possible replay attack has occured";
		}
		// Check to see if the receiving actor has already sent his nonce for validation,
		// if they have, then you want to send a confirmation back to them
		else if (lastMessage.contains("{" + receivingActor.getNonce())) {
			sendingActor.receivedOtherNonce();
			return receivingActor.getNonce() + " + 1";
		}
		if (receivingActor.getReceivedOtherNonce() && sendingActor.getReceivedOtherNonce() && receivingActor.getReceivedMyNonce() && sendingActor.getReceivedMyNonce()) {
			return "";
		}
		// If this the nonce on both sides have not been confirmed at some point during this simulation, we will send back an err
		// This will keep messages being sent when we are not fully authenticated
		return "Possible replay attack has occured";
	}
	
	// While this method is a little un-needed for this protocol in simulation, this would validate that the timestamp is within range of the timeout
	// range specified by the protocol. If it is out of range, we return false and indicate to all involved parties that a replay attack is happenning
	// At this point no more valid messages are sent untill we try and create a new session
	public boolean validateTime(String lastMessage) {
		lastMessage = lastMessage.substring(1,lastMessage.length() - (sendingActor.getSessionKey().length() + 1));
		String[] messageArray = lastMessage.split("\\|\\|");
		if (messageArray.length < 2) {
			return false;
		}
		Timestamp ts = Timestamp.valueOf(messageArray[1]);
		Timestamp validTimeRangeTimestamp = new Timestamp(System.currentTimeMillis());
		validTimeRangeTimestamp.setTime(validTimeRangeTimestamp.getTime() + TimeUnit.MINUTES.toMillis(delayBeforeInvalidTime));
		if (ts.before(validTimeRangeTimestamp)) {
			return true;
		}
		return false;
	}
	
	// This will run each event
	public void runEvent(Map<String, Actor> currentActorState) {
		// Indicates whether the session should be generated or added by the receiver during this event.
		boolean genSessionKey = false;
		
		// Indicated whether the nonce needs to be added to the message
		boolean addNonce = false;
		String decryptedMessage = "";
		// At the beginning of each event, we want to retrieve the current state of each actor
		sendingActor = currentActorState.get(sendingActor.getName());
		receivingActor = currentActorState.get(receivingActor.getName());
		
		String lastMessage = sendingActor.getLastReceivedMess();
		
		decryptedMessage = decryptSessKeys(lastMessage);
		
		
		if (genSessionKeyOrNonce) {
			if (!sendingActor.getSessionKey().isEmpty() && !receivingActor.getSessionKey().isEmpty()) {
				addNonce = true;
			}
			else {
				genSessionKey = true;
			}
		}
		String encryptionKey;
		if (!sendingActor.getSessionKey().isEmpty() && !receivingActor.getSessionKey().isEmpty()) {
			encryptionKey = sendingActor.getSessionKey();
			if (!genSessionKeyOrNonce) {
				decryptedMessage = confirmNonce(lastMessage);
				if (decryptedMessage.isEmpty()) {
					decryptedMessage = message;
				}
			}
		}
		else {
			encryptionKey = receivingActor.getPubKey();
		}
		
		if (genSessionKey && !lastMessage.isEmpty()) {
			decryptedMessage = genFullSessionKeyMessage(lastMessage);
		}
		
		if (addNonce) {
			// Here the sendor would confirm that the timestamp was valid and that a replay attack was not ocuring in the last message
			if (validateTime(lastMessage)) {
				decryptedMessage = sendingActor.getNonce();
			}
			else {
				decryptedMessage = "Possible replay attack has occured";
			}

		}
		
		
		encryptMessage(encryptionKey, decryptedMessage);
		System.out.println("\nRunning the Event for time label: " + time);
		System.out.println(sendingActor.getName() + " is " + actionMess + " to " + receivingActor.getName() + ".");
		System.out.println("The encrypted message is: " + encryptedMessage);
		System.out.println("The decrypted message is: " + decryptedMessage);
		System.out.println("Moving to next event in simulation.");
		
		// The receiving actor stores the received encrypted message
		receivingActor.setLastReceivedMess(encryptedMessage);
		
		
		// At the end of each event, we want to update the state of the sending and receiving actor.
		currentActorState.put(sendingActor.getName(), sendingActor);
		currentActorState.put(receivingActor.getName(), receivingActor);
	}
	
	
	
	
}


