/*
 ============================================================================
 Name        : Actor.java
 Author      : Christoph White
 Version     :
 Copyright   : Please do not copy this without letting me know first. This is
 a learning piece of code that all can reference to use. Anyone taking comp6370 at Suburn
 should refrain from copying this code.

 Description : This object holds each state of the actor in this simulation. An actor can be one of the two:
 a communicating user
 an authenticating server
 ============================================================================
 */

import java.util.HashMap;
import java.util.Map;

public class Actor {
	//Name of the actor in the simulation
	private String name; // The name of the actor or server
	
	//The public key name assigned to this actor
	// For the trusted third party, this key will be called AS for authenticating server 
	private String pubKey;
	
	// This is the randomly generated nonce that is specific to the actor. AS will not have this
	private String nonce;
	
	// A map of all of public keys this actor holds
	// Key is actor name, value is public key name
	// This is used mostly for AS, however other actors will have their key names in here as well
	// An empty keyStore indicates that this actor is not the authenticating server.
	private Map<String,String> keyStore = new HashMap<String,String>();
	
	//This is the session key used for the current communication
	// Generated at the beginning of an exchange
	private String sessKey = "";
	
	// The last received message in the protocol by this user.
	private String lastReceivedMessage = "";
	
	// This is used to validate communication between user 1 and user 2. Once the nonce 'r2' has been validated betwen
	// both sides, we can have communications.
	// This is not for use with authenticating servers
	// Without this we can not confirm possible replay attacks
	private boolean receivedOtherNonce = false;
	
	// This is used to see if this actors has validated his nonce for this session
	// This is not for use with authenticating servers
	// WIthout this we can not confirm possible replay attacks
	private boolean receivedMyNonce = false;
	
	private boolean timestampValid = true;
	
	// For creating a non authenticating actor
	public Actor(String inName, String inPubKey, String inNonce) {
		name = inName;
		pubKey = inPubKey;
		nonce = inNonce;
	}
	
	// Used to create the authenticating actor
	public Actor(String inName) {
		name = inName;
		pubKey = "AS"; // Represents this is the authenticating server
	}
	
	
	public String getName() {
		return name;
	}
	
	public String getPubKey() {
		return pubKey;
	}
	
	public void addsessionKey(String inSessionKey) {
		sessKey = inSessionKey;
	}
	
	public String getSessionKey() {
		return sessKey;
	}
	
	public String getNonce() {
		return nonce;
	}
	
	// Add any public keys. Can only be used for the authenticator
	public boolean addPubKey(Actor actor) {
		if (pubKey.equals("AS")) {
			System.out.println("Before the simulation, " + actor.getName() + " adds their public key to " + name + "'s keyStore");
			keyStore.put(actor.getName(), actor.getPubKey());
			return true;
		}
		return false;
	}
	
	// Get key. Can only be used for authenticating server
	public String getKey(String key) {
		if (keyStore.isEmpty()) {
			return "";
		}
		else if (!keyStore.containsKey(key)) {
			return "";
		}
		else {
			return keyStore.get(key);
		}
	}
	
	public void setLastReceivedMess(String message) {
		lastReceivedMessage = message;
	}
	
	public String getLastReceivedMess() {
		return lastReceivedMessage;
	}
	
	public void receivedOtherNonce() {
		receivedOtherNonce = true;
	}
	
	public boolean getReceivedOtherNonce() {
		return receivedOtherNonce;
	}
	
	public void receivedMyNonce() {
		receivedMyNonce = true;
	}
	
	public boolean getReceivedMyNonce() {
		return receivedMyNonce;
	}
	
	public void setTimeValid() {
		timestampValid = false;
	}
	
	public boolean getTimeValid() {
		return timestampValid;
	}
	
}
