package utility;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Time {
	
    private static final String DATAREGEX = "\\b(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[0-2])-(\\d{4})\\b";
	
	public static Event createEvent (String name,int year, int month, int day, int hour, int minutes, int duration) {
		Calendar start = Calendar.getInstance();
		start.set(year, month, day, hour, minutes);
		Calendar end = Calendar.getInstance();
		int[] endTime = calculateEndTimeWithStartAndDuration(hour, minutes, duration);
		end.set(year, month, day, endTime[0], endTime[1]);
		return new Event(name, start, end);
	}
	
	public static int[] calculateEndTimeWithStartAndDuration (int hour, int minute, int duration) {
		int endHour, endMinute;
		endHour = hour + (duration/60); 
		endMinute = minute + (60*(endHour-hour));
		return new int[] {endHour, endMinute};
	}
	
	public static boolean overTimeLimit (int startHour, int startMinute, int limitHour, int limitMinute, int duration) {
		int[] h = calculateEndTimeWithStartAndDuration(startHour, startMinute, duration);
		return (h[0] > limitHour || (h[0] == limitHour && h[1] > limitMinute)); 
	}
	
	public static boolean isValidDate (String date) {
		Pattern pattern = Pattern.compile(DATAREGEX); //dd-mm-yyyy
        Matcher matcher = pattern.matcher(date);
        if (!matcher.matches()) return false;
        else {
        	String[] s = date.split("-");
        	switch (s[1]) {
        	case "02":
        		if (isLeapYear(Integer.parseInt(s[2]))) {
        			if (Integer.parseInt(s[0]) > 29) return false;
        		}
        		else if (Integer.parseInt(s[0]) > 28) return false;
        		break;
        	case "04": case "06": case "09": case "11":
        		if (Integer.parseInt(s[0]) > 30) return false; 
        		break;
        	}
        
        }
        return true;
	}
	
	public static int getActualYear () {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
		String s = ZonedDateTime.now().format(formatter);
		return Integer.parseInt(s);
	}
	
	public static int getDayOfTheMonth () {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd");
		String s = ZonedDateTime.now().format(formatter);
		return Integer.parseInt(s);
	}
	
	public static int getActualMonth () {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM");
		String s = ZonedDateTime.now().format(formatter);
		return Integer.parseInt(s);
	}
	
	public static String getTodaysDate () {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
		String s = ZonedDateTime.now().format(formatter);
		return s;
	}
	
	public static boolean isLeapYear (int year) {
		return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
	}
	
}
