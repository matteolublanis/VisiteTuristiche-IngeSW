package utility;

import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Time {
	
	private static final int DAY = 1, MONTH = 2, YEAR = 3;
	private static final String TIMEREGEX = "^(?:[01][0-9]|2[0-3]):[0-5][0-9]$";
    private static final String DATAREGEX = "\\b(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[0-2])-(\\d{4})\\b";
	private static String fictionalDate = "09-05-2018";
    private static String actualDate = getTodaysDate();
    
	public static Event createEvent (String name,int year, int month, int day, int hour, int minutes, int duration) {
		Calendar start = Calendar.getInstance();
		start.set(year, month, day, hour, minutes);
		Calendar end = Calendar.getInstance();
		int[] endTime = calculateEndTimeWithStartAndDuration(hour, minutes, duration);
		end.set(year, month - 1, day, endTime[0], endTime[1]);
		return new Event(name, start, end);
	}
	
	public static void setActualDate (String date) {
		actualDate = date;
	}
	
	public static String getActualDate () {
		return actualDate;
	}
	
	public static void setFictionalDate (String date) {
		fictionalDate = date;
	}
	
	public static String getFictionalDate () {
		return fictionalDate;
	}
	
	public static int[] getDesideredMonthAndYear(int releaseDay, String date) {
		String[] d = date.split("-");
		if (Integer.parseInt(d[0]) < releaseDay) {
			if (Integer.parseInt(d[1]) + 1 > 12) return new int[] {(Integer.parseInt(d[1]) + 1 - 12), (Integer.parseInt(d[2]) + 1)};
			return new int[] {(Integer.parseInt(d[1]) + 1), (Integer.parseInt(d[2]))};
		}
		else {
			if (Integer.parseInt(d[1]) + 2 > 12) return new int[] {(Integer.parseInt(d[1]) + 2 - 12), (Integer.parseInt(d[2]) + 1)};
			return new int[] {(Integer.parseInt(d[1]) + 2), (Integer.parseInt(d[2]))};
		}
	}
	
	public static String[] getAvailabilityWindow (String open, String close, int[] desideredMonthAndYear) {
		String desideredStartingDate = String.format("%02d-%02d-%04d", 1, desideredMonthAndYear[0], desideredMonthAndYear[1]);
		if (comesBefore(desideredStartingDate, open) || !comesBefore(desideredStartingDate, close)) {
			return null;
		}
		else {
			//se la starting date non è errata allora di sicuro inizia dal giorno 1
			String finish = String.format("%02d-%02d-%04d", getMaxDayForMonth(desideredMonthAndYear[0], desideredMonthAndYear[1]), desideredMonthAndYear[0], desideredMonthAndYear[1]);
			if (comesBefore(close, finish)) {
				finish = String.format("%02d-%02d-%04d", Integer.parseInt(close.split("-")[0]), desideredMonthAndYear[0], desideredMonthAndYear[1]);
				return new String[] {desideredStartingDate, finish};
			}
			else {
				return new String[] {desideredStartingDate, finish};
			}
		}
	}
	
	public static int getMaxDayForMonth(int month, int year) {
    	switch (month) {
    	case 2:
    		if (isLeapYear(year)) {
    			return 29;
    		}
    		else return 28;
    	case 4: case 6: case 9: case 11:
    		return 30;
    	default:
    		return 31;
    	}
	}
	
	public static boolean todayIsDay (int day) {
		String[] s = actualDate.split("-");
		return (Integer.parseInt(s[0]) == day);
	}
	
	public static String getAllDatesSameDayOfTheWeek (String open, String close, int desideredDay) { 
		String s = "";
		String[] start = open.split("-");
		String[] stop = close.split("-");
		if (desideredDay < 1 || desideredDay > 7) return s; //TODO ECCEZIONE
		String stopMonth = stop[1], startMonth = start[1];
		Calendar c = Calendar.getInstance();
		for (int i = Integer.parseInt(start[2]) ; i <= Integer.parseInt(stop[2]) ; i++) { //ciclo anni
			if (i != Integer.parseInt(stop[2])) { //se anno non finale cicla fino dicembre
				stopMonth = "12";
			}
			else stopMonth = stop[1]; //se anno finale cicla fino stop mese
			for (int j = 1 ; j <= Integer.parseInt(stopMonth) ; j++) { //ciclo mesi
				if (i == Integer.parseInt(start[2]) && Integer.parseInt(startMonth) > 1) { //se anno iniziale e mese start dopo gennaio setta
					j = Integer.parseInt(startMonth);
					startMonth = "-1"; 
				}
				for (int k = 1 ; k <= 31 ; k++) { //ciclo giorni
					if (Integer.parseInt(start[0]) > 1 && j == Integer.parseInt(start[1]) && i == Integer.parseInt(start[2])) { //se giorno iniziale maggiore di 1 setta
						k = Integer.parseInt(start[0]);
						start[0] = "-1";
					}
					String nowDate = new String(String.format("%02d-%02d-%04d", k, j, i));
					if (Time.isValidDate(nowDate)) {
						String[] a = nowDate.split("-");
						c.set(Integer.parseInt(a[2]),Integer.parseInt(a[1]) - 1, Integer.parseInt(a[0]));
						int day = c.get(Calendar.DAY_OF_WEEK);
						if (day == 1) day = 7;
						else day -= 1;
						if (day == desideredDay) s += (nowDate) + " ";
					}
					if (nowDate.equals(close)) break;
				}
			}
		}
		return s;
	}
	
	public static boolean isTimeBetween(String time, String start, String end) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
		LocalTime targetTime = LocalTime.parse(time, formatter);
		LocalTime startTime = LocalTime.parse(start, formatter);
		LocalTime endTime = LocalTime.parse(end, formatter);
		if (startTime.isBefore(endTime)) {
			return !targetTime.isBefore(startTime) && !targetTime.isAfter(endTime);
		} else {
			return !targetTime.isBefore(startTime) || !targetTime.isAfter(endTime);
		}
	}
	
	public static boolean isThisDateInMonthiplus3 (String date) {
		String[] d = date.split("-");
		String[] s = actualDate.split("-");
		if (Integer.parseInt(d[0]) > 15) { //16 mese i al 31 del mese i
			if (Integer.parseInt(d[1]) < 10) {
				return (Integer.parseInt(d[1]) == Integer.parseInt(s[1]) + 3);
			}
			else {
				if (Integer.parseInt(d[2]) != Integer.parseInt(s[2]) + 1) return false;
				else return (Integer.parseInt(d[1]) == Integer.parseInt(s[1]) + 3 - 12); 
			}
		}
		else { //dall'1 al 15 del mese i+1
			if (Integer.parseInt(d[0]) < 16) {
				if (Integer.parseInt(d[1]) < 11) {
					return (Integer.parseInt(d[1]) == Integer.parseInt(s[1]) + 2);
				}
				else {
					if (Integer.parseInt(d[2]) != Integer.parseInt(s[2]) + 1) return false;
					else return (Integer.parseInt(d[1]) == Integer.parseInt(s[1]) + 2 - 12); 
				}
			}
			else return false;
		}
	}
	
	public static int[] calculateEndTimeWithStartAndDuration(int hour, int minute, int duration) {
	    int endHour = hour + (duration / 60); 
	    int endMinute = minute + (duration % 60); 

	    if (endMinute >= 60) { 
	        endMinute -= 60;
	        endHour += 1;
	    }

	    return new int[]{endHour, endMinute};
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
        	default:
        		break;
        	}
        
        }

        return true;
	}
	
	public static boolean comesBefore (String date1, String date2) {
		if (!Time.isValidDate(date1) || !Time.isValidDate(date2)) return false;
		String[] date1_vals = date1.split("-"); String[] date2_vals = date2.split("-");
		for (int i = 2; i >= 0 ; i--) {
			if (Integer.parseInt(date1_vals[i]) < Integer.parseInt(date2_vals[i])) return true;
			else if (Integer.parseInt(date1_vals[i]) > Integer.parseInt(date2_vals[i])) return false;
		}
		return false;
	}
	
	public static boolean isValidHour (String time) {
		Pattern pattern = Pattern.compile(TIMEREGEX); //hh-mm
        Matcher matcher = pattern.matcher(time);
        if (!matcher.matches()) return false;
        else return true;
	}
	
	public static int getActualDateValue (int s) {
		String[] d = actualDate.split("-");
		switch (s) {
		case DAY:
			return Integer.parseInt(d[0]);
		case MONTH:
			return Integer.parseInt(d[1]);
		case YEAR:
			return Integer.parseInt(d[2]);
		default:
			return -1;
			
		}
	}
	
	public static int getActualDayOfTheMonth () {
		String s[] = actualDate.split("-");
		return Integer.parseInt(s[0]);
	}
	
	public static int getTodayYear () {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy");
		String s = ZonedDateTime.now().format(formatter);
		return Integer.parseInt(s);
	}
	
	public static int getDayOfTheMonth () {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd");
		String s = ZonedDateTime.now().format(formatter);
		return Integer.parseInt(s);
	}
	
	public static int getTodayMonth () {
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
