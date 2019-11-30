/*
 ============================================================================
 Name        : hamming_code_generator.c
 Author      : Christoph White
 Version     :
 Copyright   : Please do not copy this without letting me know first. This is
 a learning piece of code that all can reference to use. Anyone taking comp6370 at Suburn
 should refrain from copying this code.

 Description : The main method of the simulation. It will create all three actors. It then loads in all public
keys into the authenticating server. It creates an event for each sequence of the simulation and loads it in
a random order into the simulation program. The simulator then detects the proper order of the events and runs them 
 ============================================================================
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Main {
	public static void main(String[] args) {
		System.out.println("Starting Discrete Event Simulation");
		Actor alice = new Actor("Alice", "Ka", "r1");
		System.out.println(alice.getName() + "'s nonce is " + alice.getNonce());
		//creating communicating party num 2
		Actor bob = new Actor("Bob", "Kb", "r2");
		System.out.println(bob.getName() + "'s nonce is " + bob.getNonce());
		
		// create the authenticating service names cathy
		Actor cathy = new Actor("Cathy");
		cathy.addPubKey(alice);
		cathy.addPubKey(bob);
		
		// This object keeps up with the state of each actor or server
		Map<String,Actor> actorState = new HashMap<String,Actor>();
		actorState.put(alice.getName(), alice);
		actorState.put(bob.getName(), bob);
		actorState.put(cathy.getName(), cathy);
		
		ArrayList<Event> eventList = new ArrayList<Event>() {
			/**
			 * This is an array list holding all of then events of the simulation
			 * 
			 * I added the events into the array list in a completly random order
			 * This was done to show that the sequence of the events where only tied to the next tag in each
			 * event object. You could arrange these events in any order into the array, and the simmulation would still schedule it based on the 
			 * time and next time tags in the Event object
			 * 
			 * The only tags that where coded to a set value where the first(1) and last (-1). This numbers are specifiers 
			 * for the start and end of the simulation. All other tags where given random values
			 */
			private static final long serialVersionUID = -7369862372458146699L;

			{	
				// The flirting continues
				add(new Event(alice, bob, "How about we go grab dinner at Mcdonalds at 5 tonight? I see that it is your favorite.",
						"confirmed and talking to ", 34, 90000123));
				
				// Create an event with session key generation set to true to start session key generation from cathy
				add(new Event(cathy, alice, "sending encrypted session key back to", 33, 12, true, false));  
				
				
				/*
				 * This is an example how you would simulate a bad R2.
				 * add(new Event(bob, alice, "validating timestamp and then sending his/her nonce encryped with session key to ", 83, 3, true, true));
				 */
				
				/*
				 * This is an example how you would simulate a bad timestamp.
				 * add(new Event(bob, alice, "validating timestamp and then sending his/her nonce encryped with session key to ", 83, 3, true, "{Alice||2019-11-29 17:46:55||Ks}Kb"));
				 */
				
				
				
				// Bob gets the session key, and then sends his confirmation nonce back to Alice so she can confirm herself
				add(new Event(bob, alice, "validating timestamp and then sending his/her nonce encryped with session key to ", 83, 3, true, false));
				
				// The flirting has completed, so we close the session. This ends our simulation
				add(new Event(bob, alice, "That sounds splendid. Ill see you tonight. Ill go ahead and end our communication",
						"confirmed and talking to ", 90000123, -1));
				
				// Alice Sends back Bob's 'Nonce + 1' and bob uses this to confirm that he is talking to alice
				add(new Event(alice, bob, "sending his/her confirmation ", 3, 1033));
				
				// Bob flirts back
				add(new Event(bob, alice, "Wow that is the most flattering thing I have ever heard. I think you are stunning also",
						"confirmed and talking to ", 1000000, 34));
				
				// Alice decrypts the message, stores the key, and then sends the key to bob so that he can have the session
				add(new Event(alice, bob, "sending " + bob.getName() + "'s encrypted session key to ", 12, 83));
				
				// Alice flirts with bob
				add(new Event(alice, bob,bob.getName() + ", thanks for confirming it was you, I just wanted to tell you that you where the most beautiful person on this side of the Mississippi",
						"confirmed and talking to ", 9, 1000000));
				
				// Bob sends a message to alice indicating he is ready to talk
				add(new Event(bob, alice,"Hey " + alice.getName() + " , glad we where able to verify each other. What did you want to talk about?", 
						" confirmed and talking to ", 1033, 9));
				
				/*
				 * This is an example of how to simulate a bad r1
				 * add(new Event(alice, cathy, alice.getName() + "||" + bob.getName() + "||" + "RBAD", "sending introduction", 1, 33)); 		
				 */
				
				// Send an introduction to cathy, indicating that she needs a session key for Bob and Alice to communicate. This is start message
				add(new Event(alice, cathy, alice.getName() + "||" + bob.getName() + "||" + alice.getNonce(), "sending introduction", 1, 33)); 				
			}
		};
		Simulation sim = new Simulation(eventList);
		sim.run(actorState);
    }
}
