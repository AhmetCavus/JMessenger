package framework.time;

public interface TimeControl {
	public int days();

	public int hours();

	public long milliseconds();

	public int minutes();

	public int seconds();
	
	public double totalDays();

	public double totalHours();

	public double totalMinutes();

	public double totalSeconds();
	
	public double totalMillisecond();
	
	public TimeControl addDays(int value);
	
	public TimeControl addHours(int value);

	public TimeControl addMilliseconds(long value);
	
	public TimeControl addMinutes(int value);
	
	public TimeControl addSeconds(int value);
}
