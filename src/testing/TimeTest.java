package testing;

import utility.Time;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

class TimeTest {

	@Test
	void testGetAllDatesSameDayOfTheWeek() {
		/*
		String[] dates = Time.getAllDatesSameDayOfTheWeek("12-12-2025", "19-12-2025", 1).split(" ");
		assertEquals(dates[0], "15-12-2025");
		dates = Time.getAllDatesSameDayOfTheWeek("01-12-2025", "01-02-2026", 1).split(" ");
		String[] c = new String[] {"01-12-2025", "08-12-2025", "15-12-2025", "22-12-2025", "29-12-2025",
				"05-01-2026", "12-01-2026", "19-01-2026", "26-01-2026"};
		for (int i = 0 ; i < c.length ; i++) {
			assertEquals(dates[i], c[i]);
		}
		dates = Time.getAllDatesSameDayOfTheWeek("01-01-2025", "07-01-2025", 3).split(" ");
	    assertEquals(dates[0], "01-01-2025");
	    
	    // Test con intervallo più lungo, controllando più date
	    dates = Time.getAllDatesSameDayOfTheWeek("01-02-2025", "28-02-2025", 5).split(" ");
	    String[] expected1 = {"07-02-2025", "14-02-2025", "21-02-2025", "28-02-2025"}; // Venerdì
	    assertArrayEquals(expected1, dates);

	    // Test con un anno bisestile
	    dates = Time.getAllDatesSameDayOfTheWeek("01-02-2024", "29-02-2024", 4).split(" ");
	    String[] expected2 = {"01-02-2024", "08-02-2024", "15-02-2024", "22-02-2024", "29-02-2024"}; // Giovedì
	    assertArrayEquals(expected2, dates);
	    
	    // Test su un intervallo che non include il giorno della settimana specificato
	    dates = Time.getAllDatesSameDayOfTheWeek("01-03-2025", "05-03-2025", 6).split(" ");
	    assertEquals(1, dates.length); // 1 sabato in questo arco, il primo marzo

	    dates = Time.getAllDatesSameDayOfTheWeek("01-01-2025", "31-12-2025", 7).split(" ");
	    assertTrue(dates.length > 50); // Dovrebbero esserci circa 52 domeniche
	    dates = Time.getAllDatesSameDayOfTheWeek("17-09-2025", "17-09-2025", 3).split(" ");
	    assertTrue(dates.length == 1 || dates.length == 0); // Se il 17/09/2025 è mercoledì

	    dates = Time.getAllDatesSameDayOfTheWeek("01-09-2025", "31-10-2025", 1).split(" ");
	    assertEquals(9, dates.length);
	    
	    dates = Time.getAvailabilityWindow("01-11-2025", "03-03-2026", Time.getDesideredMonthAndYear(16, "17-11-2025"));
	    dates = Time.getAllDatesSameDayOfTheWeek(dates[0], dates[1], 1).split(" ");
	    String[] expected3 = {"05-01-2026", "12-01-2026", "19-01-2026", "26-01-2026"}; 
	    assertArrayEquals(dates, expected3);
	    
	    dates = Time.getAvailabilityWindow("01-11-2025", "03-03-2026", Time.getDesideredMonthAndYear(16, "17-12-2025"));
	    System.out.println(dates[0] + " " + dates[1]);
	    dates = Time.getAllDatesSameDayOfTheWeek(dates[0], dates[1], 1).split(" ");
	    String[] expected4 = {"02-02-2026", "09-02-2026", "16-02-2026", "23-02-2026"}; 
	    assertArrayEquals(dates, expected4);
	    
	    dates = Time.getAvailabilityWindow("01-11-2025", "03-03-2026", Time.getDesideredMonthAndYear(16, "17-01-2026"));
	    System.out.println(dates[0] + " " + dates[1]);
	    dates = Time.getAllDatesSameDayOfTheWeek(dates[0], dates[1], 1).split(" ");
	    String[] expected5 = {"02-03-2026"}; 
	    assertArrayEquals(dates, expected5);
	    
	    
	    DA RISCRIVERE PERCHE- USO ARRAYLIST ORA
	    
		*/
	}
	
	@Test
	void validDate() {
		assertTrue(Time.isValidDate("01-05-2025"));
		assertTrue(Time.isValidDate("15-08-2023"));
		assertTrue(Time.isValidDate("29-02-2024"));
		assertTrue(Time.isValidDate("01-01-2000"));
		assertTrue(Time.isValidDate("31-12-1999"));
		assertTrue(Time.isValidDate("10-10-2010"));
		assertTrue(Time.isValidDate("28-02-2021"));
		assertTrue(Time.isValidDate("30-04-2022"));
		assertTrue(Time.isValidDate("31-07-2019"));
		assertTrue(Time.isValidDate("25-12-2025"));
		assertTrue(Time.isValidDate("09-09-1995"));
		assertFalse(Time.isValidDate("32-01-2023"));
		assertFalse(Time.isValidDate("30-02-2023"));
		assertFalse(Time.isValidDate("31-04-2022"));
		assertFalse(Time.isValidDate("29-02-2023"));
		assertFalse(Time.isValidDate("00-06-2021"));
		assertFalse(Time.isValidDate("15-13-2022"));
		assertFalse(Time.isValidDate("31-06-2018"));
		assertFalse(Time.isValidDate("12-00-2020"));
		assertTrue(Time.isValidDate("25-12-1899"));
		assertFalse(Time.isValidDate("29-02-2100"));
	}
	
	@Test
	void comesBeforeCheck() {
		assertFalse(Time.comesBefore("08-09-2025", "08-08-2025"));
		assertTrue(Time.comesBefore("08-08-2025", "08-09-2025"));
		assertTrue(Time.comesBefore("08-08-1998", "08-09-2025"));
		assertFalse(Time.comesBefore("31-09-2025", "08-09-2025"));
	    // Date valide
	    assertTrue(Time.comesBefore("01-01-2020", "02-01-2020"));
	    assertFalse(Time.comesBefore("31-12-2025", "01-01-2025"));
	    assertTrue(Time.comesBefore("15-06-2010", "20-06-2010"));
	    assertFalse(Time.comesBefore("10-10-2010", "10-10-2010"));
	    assertTrue(Time.comesBefore("01-01-1990", "01-01-2000"));
	    assertFalse(Time.comesBefore("01-01-2050", "01-01-2049"));
	    assertTrue(Time.comesBefore("28-02-2024", "29-02-2024")); // Anno bisestile
	    assertFalse(Time.comesBefore("29-02-2024", "28-02-2024")); // Inversione
	    assertTrue(Time.comesBefore("30-04-2023", "01-05-2023"));
	    assertFalse(Time.comesBefore("31-07-2022", "01-07-2022"));
	    
	    // Date con mesi fuori range
	    assertFalse(Time.comesBefore("10-13-2025", "08-09-2025"));
	    assertFalse(Time.comesBefore("15-00-2025", "15-01-2025"));
	    
	    // Date con giorni fuori range
	    assertFalse(Time.comesBefore("32-01-2025", "01-02-2025"));
	    assertFalse(Time.comesBefore("00-06-2025", "01-06-2025"));
	    assertFalse(Time.comesBefore("31-04-2025", "01-05-2025"));
	    assertFalse(Time.comesBefore("29-02-2023", "01-03-2023")); // Non bisestile

	    // Confronto tra anni
	    assertTrue(Time.comesBefore("01-01-1999", "01-01-2000"));
	    assertFalse(Time.comesBefore("01-01-2100", "01-01-2099"));
	    
	    // Confronti tra stessi mesi/giorni
	    assertTrue(Time.comesBefore("07-07-2007", "08-07-2007"));
	    assertFalse(Time.comesBefore("08-07-2007", "07-07-2007"));
	}
	
	@Test
	void testGetAvailabilityWindow() {
	    // Case 1: Availability window falls entirely within open-close range
	    String[] result = Time.getAvailabilityWindow("01-06-2025", "30-09-2025", new int[]{7, 2025});
	    assertArrayEquals(new String[]{"01-07-2025", "31-07-2025"}, result);

	    // Case 2: Desired month is before the open date -> should return null
	    result = Time.getAvailabilityWindow("01-06-2025", "30-09-2025", new int[]{5, 2025});
	    assertNull(result);

	    // Case 3: Desired month is after the close date -> should return null
	    result = Time.getAvailabilityWindow("01-06-2025", "30-09-2025", new int[]{10, 2025});
	    assertNull(result);

	    // Case 4: Desired month starts before open but ends within range
	    result = Time.getAvailabilityWindow("15-06-2025", "30-09-2025", new int[]{6, 2025});
	    assertArrayEquals(null, result);

	    // Case 5: Desired month is the same as close date -> end date should be the close date
	    result = Time.getAvailabilityWindow("01-06-2025", "20-07-2025", new int[]{7, 2025});
	    assertArrayEquals(new String[]{"01-07-2025", "20-07-2025"}, result);

	    // Case 6: Open and close dates span an entire year
	    result = Time.getAvailabilityWindow("01-01-2025", "31-12-2025", new int[]{3, 2025});
	    assertArrayEquals(new String[]{"01-03-2025", "31-03-2025"}, result);

	    // Case 7: Open date is exactly at the start of the desired month
	    result = Time.getAvailabilityWindow("01-05-2025", "31-12-2025", new int[]{5, 2025});
	    assertArrayEquals(new String[]{"01-05-2025", "31-05-2025"}, result);

	    // Case 8: Close date is exactly at the end of the desired month
	    result = Time.getAvailabilityWindow("01-01-2025", "30-09-2025", new int[]{9, 2025});
	    assertArrayEquals(new String[]{"01-09-2025", "30-09-2025"}, result);

	    // Case 9: Close date is in the middle of the desired month
	    result = Time.getAvailabilityWindow("01-01-2025", "15-09-2025", new int[]{9, 2025});
	    assertArrayEquals(new String[]{"01-09-2025", "15-09-2025"}, result);

	    // Case 10: Open and close are exactly the same -> only that day is available
	    result = Time.getAvailabilityWindow("15-07-2025", "15-07-2025", new int[]{7, 2025});
	    assertArrayEquals(null, result);

	    // Case 11: Open date and close date are in different years
	    result = Time.getAvailabilityWindow("01-06-2024", "30-06-2026", new int[]{12, 2025});
	    assertArrayEquals(new String[]{"01-12-2025", "31-12-2025"}, result);

	    // Case 12: Close date is in February of a leap year
	    result = Time.getAvailabilityWindow("01-01-2024", "29-02-2024", new int[]{2, 2024});
	    assertArrayEquals(new String[]{"01-02-2024", "29-02-2024"}, result);

	    // Case 13: Close date is in February of a non-leap year
	    result = Time.getAvailabilityWindow("01-01-2023", "28-02-2023", new int[]{2, 2023});
	    assertArrayEquals(new String[]{"01-02-2023", "28-02-2023"}, result);

	    // Case 14: Desired month is in a different year than open and close
	    result = Time.getAvailabilityWindow("01-06-2024", "30-06-2026", new int[]{3, 2023});
	    assertNull(result);

	    // Case 15: Open and close cover an entire decade
	    result = Time.getAvailabilityWindow("01-01-2020", "31-12-2029", new int[]{5, 2025});
	    assertArrayEquals(new String[]{"01-05-2025", "31-05-2025"}, result);

	    // Case 16: Open and close dates are on different days but within the same month
	    result = Time.getAvailabilityWindow("10-06-2025", "20-06-2025", new int[]{6, 2025});
	    assertArrayEquals(null, result);

	    // Case 17: Desired month is at the exact beginning of the range
	    result = Time.getAvailabilityWindow("01-03-2025", "31-12-2025", new int[]{3, 2025});
	    assertArrayEquals(new String[]{"01-03-2025", "31-03-2025"}, result);

	    // Case 18: Desired month is at the exact end of the range
	    result = Time.getAvailabilityWindow("01-03-2025", "31-10-2025", new int[]{10, 2025});
	    assertArrayEquals(new String[]{"01-10-2025", "31-10-2025"}, result);

	    // Case 19: Desired month starts within range but ends outside it
	    result = Time.getAvailabilityWindow("01-06-2025", "15-09-2025", new int[]{9, 2025});
	    assertArrayEquals(new String[]{"01-09-2025", "15-09-2025"}, result);

	    // Case 20: Open date is exactly at the start of the desired year
	    result = Time.getAvailabilityWindow("01-01-2025", "31-12-2025", new int[]{1, 2025});
	    assertArrayEquals(new String[]{"01-01-2025", "31-01-2025"}, result);
	}
	
	@Test
	void testTimeWithinRange() {
		assertTrue(Time.isTimeBetween("12:30", "10:00", "14:00"));
		assertTrue(Time.isTimeBetween("23:30", "22:00", "23:59"));
		assertTrue(Time.isTimeBetween("00:30", "23:00", "02:00"));
	}

	@Test
	void testTimeOutsideRange() {
		assertFalse(Time.isTimeBetween("09:30", "10:00", "14:00"));
		assertFalse(Time.isTimeBetween("14:30", "10:00", "14:00"));
		assertFalse(Time.isTimeBetween("03:00", "23:00", "02:00"));
	}

	@Test
	void testTimeAtBoundary() {
		assertTrue(Time.isTimeBetween("10:00", "10:00", "14:00"));
		assertTrue(Time.isTimeBetween("14:00", "10:00", "14:00"));
		assertTrue(Time.isTimeBetween("23:00", "23:00", "02:00"));
		assertTrue(Time.isTimeBetween("02:00", "23:00", "02:00"));
	}

	@Test
	void testMidnightCrossing() {
		assertTrue(Time.isTimeBetween("01:00", "23:00", "02:00"));
		assertFalse(Time.isTimeBetween("03:00", "23:00", "02:00"));
		assertTrue(Time.isTimeBetween("23:30", "23:00", "02:00"));
	}

}
