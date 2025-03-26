package testing;

import utility.Time;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

class TimeTest {

	@Test
	void getAllDatesSameDayOfTheWeekCheckIfDayCorrect() {
		ArrayList<String> dates = Time.getAllDatesSameDayOfTheWeek("12-12-2025", "19-12-2025", 1);
		for (String s : dates) {
			assertEquals(s, "15-12-2025");
		}
	}
	
	@Test
	void getAllDatesSameDayOfTheWeekCheckIfDatesCorrect() {
		ArrayList<String> dates = Time.getAllDatesSameDayOfTheWeek("01-12-2025", "01-02-2026", 1);
		String[] c = new String[] {"01-12-2025", "08-12-2025", "15-12-2025", "22-12-2025", "29-12-2025",
				"05-01-2026", "12-01-2026", "19-01-2026", "26-01-2026"};
		for (int i = 0 ; i < c.length ; i++) {
			assertEquals(dates.get(i), c[i]);
		}
	}
	
	@Test
	void validDate() {
		assertTrue(Time.isValidDate("01-05-2025"));
		//TODO aggiungere altri casi di test
	}
	
	@Test
	void comesBeforeCheck() {
		assertFalse(Time.comesBefore("08-09-2025", "08-08-2025"));
		assertTrue(Time.comesBefore("08-08-2025", "08-09-2025"));
		assertTrue(Time.comesBefore("08-08-1998", "08-09-2025"));
		assertFalse(Time.comesBefore("31-09-2025", "08-09-2025"));
		//TODO aggiungere altri casi di test
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
