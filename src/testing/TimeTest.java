package testing;

import utility.Time;
import static org.junit.jupiter.api.Assertions.*;
import java.util.ArrayList;

import org.junit.jupiter.api.Test;

class TimeTest {

	@Test
	void getAllDatesSameDayOfTheWeekCheckIfDayCorrect() {
		String dates = Time.getAllDatesSameDayOfTheWeek("12-12-2025", "19-12-2025", 1).split(",")[0];
		assertEquals(dates, "15-12-2025");
	}
	
	@Test
	void getAllDatesSameDayOfTheWeekCheckIfDatesCorrect() {
		String[] dates = Time.getAllDatesSameDayOfTheWeek("01-12-2025", "01-02-2026", 1).split(",");
		String[] c = new String[] {"01-12-2025", "08-12-2025", "15-12-2025", "22-12-2025", "29-12-2025",
				"05-01-2026", "12-01-2026", "19-01-2026", "26-01-2026"};
		for (int i = 0 ; i < c.length ; i++) {
			assertEquals(dates[i], c[i]);
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
}
