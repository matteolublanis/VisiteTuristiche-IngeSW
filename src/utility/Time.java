package utility;

import java.util.Calendar;

public class Time {
	
	public static Event createEvent (String name,int year, int month, int day, int hour, int minutes, int duration) {
		Calendar start = Calendar.getInstance();
		start.set(year, month, day, hour, minutes);
		Calendar end = Calendar.getInstance();
		int[] endTime = calculateEndTimeWithStartAndDuration(hour, minutes, duration);
		end.set(year, month, day, endTime[0], endTime[1]);
		return new Event(name, start, end);
	}
	
	//Questo metodo dà per scontato che la visita sia di qualche ora massimo e che non sfoci nel giorno dopo
	public static int[] calculateEndTimeWithStartAndDuration (int hour, int minute, int duration) {
		int endHour, endMinute;
		endHour = hour + (duration/60); 
		endMinute = minute + (60*(endHour-hour));
		return new int[] {endHour, endMinute};
	}
	
	public boolean overTimeLimit (int startHour, int startMinute, int limitHour, int limitMinute, int duration) {
		int[] h = calculateEndTimeWithStartAndDuration(startHour, startMinute, duration);
		return (h[0] > limitHour || (h[0] == limitHour && h[1] > limitMinute)); 
	}

	
}
