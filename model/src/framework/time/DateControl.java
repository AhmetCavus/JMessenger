package framework.time;

import java.util.Locale;

public interface DateControl {
	int day();

	int hour();

	int millisecond();

	int minute();

	int second();
	
	int cw();

	long timeInMillis();
	
	String nameOfDay(Locale locale);

	DateControl addDays(int value);
	
	DateControl addHours(int value);

	DateControl addMilliseconds(int value);
	
	DateControl addMinutes(int value);
	
	DateControl addSeconds(int value);

	DateControl addTicks(int value);

	DateControl addMonths(int value);

	DateControl addMonthDay(int value);

	DateControl addYears(int value);
}
