 A discrete Event Simulation written in Java that shows the Needham-Schroeder protocol with Denning Sacco modification
 * This was written for HW # 4 of Comp 6376. 
 * Please do not use this code to complete the assignment for this class. This should be used as a reference only
 * The simulation of this program is built based on the state of each individual Event object
 * The Even logic for this simulation is specific for modeling the Needham-Shroeder protocol with Denning Sacco modification
 * This is a very simple discrete event simulation program and there are most certainly better ways to write a DES.
 * This program simulates the forming of a session key with Cathy(The authenticator). Bob and Alice then initiates a full conversation using this session key
 * The main program is an example of the normal simulation of the protocol. Since this protocol should properly handle the validation of nonces and timestamps, you could write a different main that simulalted both bad nonces and bad/old timestamps. To do this you would have the program send in bad nonces. There is a flag in the Event class that can simulate a bad nonce r2. For timestamps and r1, you would have to order the events in a way that would that would cause these. For example, passing in RBAD instead of r1 when creating the first event. For a timestamp, there is a constructor that allows you to inject a replay attack which could be used for the timestamp. This replay attack constructor can be used for any of the attacks.
 * The main method has examples of how to simulate a replay attack using r1, r2, and a bad timestamp. Have fun
