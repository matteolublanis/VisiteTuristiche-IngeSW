package utility;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Time {
	
	public static final int DAY = 1, MONTH = 2, YEAR = 3;
	private static final String TIMEREGEX = "^(?:[01][0-9]|2[0-3]):[0-5][0-9]$";
    private static final String DATAREGEX = "\\b(0[1-9]|[12][0-9]|3[01])-(0[1-9]|1[0-2])-(\\d{4})\\b";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy");
	private static String fictionalDate = "17-04-2025";
    private static String actualDate = fictionalDate;//getTodaysDate();
    
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
    	if (!isValidDate(date)) return null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy");
        LocalDate givenDate = LocalDate.parse(date, formatter);
        int monthsToAdd = (givenDate.getDayOfMonth() < releaseDay) ? 1 : 2;
        LocalDate newDate = givenDate.plusMonths(monthsToAdd);

        return new int[]{newDate.getMonthValue(), newDate.getYear()};
    }
	
    public static String[] getAvailabilityWindow(String open, String close, int[] desiredMonthAndYear) {
        // Parse the open and close dates
        if (!isValidDate(open) || !isValidDate(close)) return null;
        LocalDate openDate = LocalDate.parse(open, FORMATTER);
        LocalDate closeDate = LocalDate.parse(close, FORMATTER);

        // Construct desired start and end dates for the given month and year
        LocalDate desiredStart = LocalDate.of(desiredMonthAndYear[1], desiredMonthAndYear[0], 1); // Month is 1-based
        LocalDate desiredEnd = desiredStart.withDayOfMonth(desiredStart.lengthOfMonth()); // Last day of the month

        // If the desired start date is after the close date, return null (invalid period)
        if (desiredStart.isAfter(closeDate)) {
            return null;
        }
        
        if (desiredStart.getYear() < openDate.getYear()) {
            return null;  // If the desired year is before the open year, it's too early
        }
        
        if (desiredStart.getMonthValue() < openDate.getMonthValue() && desiredStart.getYear() == openDate.getYear()) {
            return null;  // If it's in the previous month of the same year, it's too early
        }

        // If the desired start is before the open date, adjust the start to the open date
        if (desiredStart.isBefore(openDate)) {
            desiredStart = openDate;
        }

        // If the desired end is after the close date, adjust the end to the close date
        if (desiredEnd.isAfter(closeDate)) {
            desiredEnd = closeDate;
        }

        // Return the adjusted availability window
        return new String[]{desiredStart.format(FORMATTER), desiredEnd.format(FORMATTER)};
    }
	
	public static boolean todayIsDay (int day) {
		String[] s = actualDate.split("-");
		return (Integer.parseInt(s[0]) == day);
	}
	
    public static List<String> getAllDatesSameDayOfTheWeek(String open, String close, int desiredDay) {
    	if (!isValidDate(open) || !isValidDate(close)) return null;
        List<String> result = new ArrayList<>();
        
        // Verifica del formato delle date
        LocalDate startDate, endDate;
        try {
            startDate = LocalDate.parse(open, FORMATTER);
            endDate = LocalDate.parse(close, FORMATTER);
        } catch (Exception e) {
            return null; // Se le date non sono valide, restituisce null
        }

        if (desiredDay < 1 || desiredDay > 7) return null; // Controllo valore giorno

        // Scorre i giorni tra startDate ed endDate
        for (LocalDate date = startDate; !date.isAfter(endDate); date = date.plusDays(1)) {
            int dayOfWeek = date.getDayOfWeek().getValue(); // Lunedì = 1, Domenica = 7
            if (dayOfWeek == desiredDay) {
                result.add(date.format(FORMATTER));
            }
        }
        return result;
    }
	
	public static boolean isTimeBetween(String time, String start, String end) {
    	if (!isValidHour(start) || !isValidHour(end) || !isValidHour(time)) return false;
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
	
    public static boolean isThisDateInMonthPlus3(String date) {
        if (!isValidDate(date)) return false;
        LocalDate inputDate;
        try {
            inputDate = LocalDate.parse(date, FORMATTER);
        } catch (Exception e) {
            return false; // Se la data non è valida, restituisce false
        }

        LocalDate today = LocalDate.now();
        int monthsToAdd = (today.getDayOfMonth() > 15) ? 3 : 2;
        LocalDate targetMonth = today.plusMonths(monthsToAdd);

        return inputDate.getMonthValue() == targetMonth.getMonthValue() &&
               inputDate.getYear() == targetMonth.getYear();
    }
	
    public static int[] calculateEndTimeWithStartAndDuration(int hour, int minute, int duration) {
        LocalTime startTime = LocalTime.of(hour, minute);
        LocalTime endTime = startTime.plusMinutes(duration);

        return new int[]{endTime.getHour(), endTime.getMinute()};
    }
	
    public static boolean overTimeLimit(int startHour, int startMinute, int limitHour, int limitMinute, int duration) {
        LocalTime startTime = LocalTime.of(startHour, startMinute);
        LocalTime endTime = startTime.plusMinutes(duration);
        LocalTime limitTime = LocalTime.of(limitHour, limitMinute);

        return endTime.isAfter(limitTime);
    }
	
	public static boolean isValidDate (String date) {
		Pattern pattern = Pattern.compile(DATAREGEX); //dd-mm-yyyy
        Matcher matcher = pattern.matcher(date);
        if (!matcher.matches()) return false;
        else {
        	String[] s = date.split("-"); //s[0] day, s[1] month, s[2] year
        	if (Integer.parseInt(s[0]) > getMaxDayForMonth(Integer.parseInt(s[1]), Integer.parseInt(s[2])))  return false;
        }

        return true;
	}
	
    public static boolean comesBefore(String date1, String date2) {
    	if (!isValidDate(date1) || !isValidDate(date2)) return false;

        try {
            LocalDate d1 = LocalDate.parse(date1, FORMATTER);
            LocalDate d2 = LocalDate.parse(date2, FORMATTER);
            return d1.isBefore(d2);
        } catch (Exception e) {
            return false; // Se la data non è valida, restituisce false
        }
    }
	
	public static boolean isValidHour (String time) {
		Pattern pattern = Pattern.compile(TIMEREGEX); //hh-mm
        Matcher matcher = pattern.matcher(time);
        if (!matcher.matches()) return false;
        else return true;
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
	
	public static String getTodaysDate () { //local time
		String s = ZonedDateTime.now().format(FORMATTER);
		return s;
	}
	
	public static boolean isLeapYear (int year) {
		return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0);
	}
	
}
