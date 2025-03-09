package utility;

import java.util.*;

public class Schedule {
    private List<Event> events; 
    private static final String DATAREGEX = "\\b(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[0-2])-(\\d{4})\\b";
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