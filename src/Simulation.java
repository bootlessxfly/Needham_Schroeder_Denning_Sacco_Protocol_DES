/*
 ============================================================================
 Name        : hamming_code_generator.c
 Author      : Christoph White
 Version     :
 Copyright   : Please do not copy this without letting me know first. This is
 a learning piece of code that all can reference to use. Anyone taking comp6370 at Suburn
 should refrain from copying this code.

 Description : This class runs the simulation. It looks for the starting event, and then looks for each next event
 untill the final event is found. 
 ============================================================================
 */

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Simulation {
	private Map<String, Event> eventMap;
	
	public Simulation(List<Event> eventList) {
		eventMap = new HashMap<String,Event>();
		for (int i = 0; i < eventList.size(); i++) {
			eventMap.put(Integer.toString(eventList.get(i).getCurrentTime()), eventList.get(i));
		}
	}
	
	public void run(Map<String, Actor> actorState) {
		// For this simulation any event with the label of '1' is the starting event
		int nextEvent = 1;
		Event event;
		// For this simulation, any event that has a next event of '-1' is the final event
		for (int i = 0; nextEvent != -1; i++) {
			event = eventMap.get(Integer.toString(nextEvent));
			if (event == null) {
				nextEvent = -1;
				continue;
			}
			System.out.print("\nRunning time sequence event: T" + i);
			event.runEvent(actorState);
			nextEvent = event.getNextEvent();
		}
		
		if (nextEvent == -1) {
			System.out.println("\nThe end of the simulation has been reached.");
		}
	}
	
	
	
	
}
