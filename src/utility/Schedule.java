package utility;

import java.util.*;

public class Schedule {
    private List<Event> events; 
    
    public Schedule() {
        events = new ArrayList<>();
    }

    public boolean addEvent(Event newEvent) {
        for (Event event : events) {
            if (event.conflictsWith(newEvent)) {
                System.out.println("Cannot add event: " + newEvent.name + ", it conflicts with " + event.name);
                return false;
            }
        }
        events.add(newEvent);
        System.out.println("Event added: " + newEvent);
        return true;
    }
    
}