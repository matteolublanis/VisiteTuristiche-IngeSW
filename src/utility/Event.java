package utility;

import java.util.Calendar;

public class Event {
    Calendar start;
    Calendar end;
    String name;

    public Event(String name, Calendar start, Calendar end) {
        this.name = name;
        this.start = start;
        this.end = end;
    }

    public boolean conflictsWith(Event other) {
        return (start.before(other.end) && end.after(other.start)); //TODO leggere documentazione o test
    }

    @Override
    public String toString() {
        return name + " from " + start.getTime() + " to " + end.getTime();
    }
}
