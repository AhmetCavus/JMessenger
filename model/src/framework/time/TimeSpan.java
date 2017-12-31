package framework.time;

import java.util.Calendar;
import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;


public class TimeSpan implements TimeControl{
	
	protected long mLTime;
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		TimeSpan ts = TimeSpan.now();
		System.out.println(ts.totalSeconds());
	}
	
	public TimeSpan(){
		mLTime = zero();
	}

	public static TimeSpan now(){
		Calendar c = Calendar.getInstance();
		return TimeSpan.parse(c);
	}

	public long timeInMillis(){
		return mLTime;
	}

	@Override
	public int days(){
		int day = (int) (mLTime / 1000 / 60 / 60 / 24);
		return day;
	}

	@Override
	public int hours(){
		int hour = (int) (mLTime / 1000 / 60 / 60) % 24;
		return hour;
	}


	@Override
	public int minutes(){
		int minute = (int) (mLTime / 1000 / 60) % 60;
		return minute;
	}

	@Override
	public int seconds(){
		int minute = (int) (mLTime / 1000) % 60;
		return minute;
	}

	@Override
	public long milliseconds(){
		return mLTime % 1000;
	}
	
	public int timeOfDay(){
		return 0;
	}
	
	public void add(TimeSpan dt){
		if(dt == null) return;
		addHours(dt.hours());
		addMinutes(dt.minutes());
		addSeconds(dt.seconds());
		addMilliseconds(dt.milliseconds());
		addDays(dt.days());
	}

	@Override
	public TimeControl addDays(int value){
		mLTime+= value * 24 * 60 * 60 * 1000;
		return this;
	}
	
	@Override
	public TimeControl addHours(int value){
		mLTime+= value * 60 * 60 * 1000;
		return this;
	}

	@Override
	public TimeControl addMilliseconds(long value){
		mLTime+= value;
		return this;
	}
	
	@Override
	public TimeControl addMinutes(int value){
		mLTime+= value * 60 * 1000;
		return this;
	}
	
	@Override
	public TimeControl addSeconds(int value){
		mLTime+= value * 1000;
		return this;
	}
	
	public TimeControl addTicks(int value){
		return this;
	}
	
	public void substract(TimeSpan dt){
		if(dt == null) return;
		addHours(-dt.hours());
		addMinutes(-dt.minutes());
		addSeconds(-dt.seconds());
		addMilliseconds(-dt.milliseconds());
		addDays(-dt.days());
	}
	
	public static TimeSpan parse(String time){
		JSONObject jTime = null;
		try {
			jTime = new JSONObject(time);
		} catch (JSONException e) {
			e.printStackTrace();
			return TimeSpan.fromMilliseconds(0);
		}
		TimeSpan ts = new TimeSpan();
		int day = jTime.optInt("day");
		int hour = jTime.optInt("hour");
		int minute = jTime.optInt("minute");
		int second = jTime.optInt("second");
		int millisecond = jTime.optInt("millisecond");
		ts.addDays(day);
		ts.addDays(hour);
		ts.addDays(minute);
		ts.addDays(second);
		ts.addDays(millisecond);
		return ts;
	}

	public static TimeSpan parse(Date date){
		return new TimeSpan();
	}

	public static TimeSpan parse(Calendar c){
		TimeSpan dt = new TimeSpan();
//		int day = c.get(Calendar.DAY_OF_MONTH);
		int hour = c.get(Calendar.HOUR_OF_DAY);
		int minute = c.get(Calendar.MINUTE);
		int second = c.get(Calendar.SECOND);
		int milliSecond = c.get(Calendar.MILLISECOND);
		
//		dt.addDays(day);
		dt.addHours(hour);
		dt.addMinutes(minute);
		dt.addSeconds(second);
		dt.addMilliseconds(milliSecond);
		return dt;
	}
	
	private void setTimeInMillis(long millis){
		mLTime = millis;
	}

	public static TimeSpan fromMilliseconds(long millis){
		TimeSpan ts = new TimeSpan();
		ts.setTimeInMillis(millis);
		return ts;
	}

	
	private void setTimeInSeconds(int seconds){
		mLTime = seconds * 1000;
	}
	
	public static TimeSpan fromSeconds(int seconds){
		TimeSpan ts = new TimeSpan();
		ts.setTimeInSeconds(seconds);
		return ts;
	}
	
	private void setTimeInMinutes(int minutes){
		mLTime = minutes * 60 * 1000;
	}
	
	public static TimeSpan fromMinutes(int minutes){
		TimeSpan ts = new TimeSpan();
		ts.setTimeInMinutes(minutes);
		return ts;
	}
	
	private void setTimeInHours(int hours){
		mLTime = hours * 60 * 60 * 1000;
	}
	
	public static TimeSpan fromHours(int hours){
		TimeSpan ts = new TimeSpan();
		ts.setTimeInHours(hours);
		return ts;
	}
	
	private void setTimeInDays(int days){
		mLTime = days * 24 * 60 * 60 * 1000;
	}
	
	public static TimeSpan fromDays(int days){
		TimeSpan ts = new TimeSpan();
		ts.setTimeInDays(days);
		return ts;
	}

//	public static void fromTicks(long value){
//		TimeSpan ts = new TimeSpan();
//		ts.addMilliseconds(value);
//		return ts;
//	}
	
	protected static long zero() {
		Calendar c = Calendar.getInstance();
		c.setTimeInMillis(0);
		return c.getTimeInMillis();
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb
		.append("{\"hour\":")
		.append(hours())
		.append(",\"minute\":")
		.append(minutes())
		.append(",\"second\":")
		.append(seconds())
		.append(",\"millisecond\":")
		.append(String.format("%02d", milliseconds()))
		.append(",\"day\":")
		.append(days())
		.append("}");
		return sb.toString();
	}
	
	public String simplify(){
		StringBuilder sb = new StringBuilder();
		sb
		.append(String.format("%02d", hours()))
		.append(":")
		.append(String.format("%02d", minutes()))
		.append(":")
		.append(String.format("%02d", seconds()))
		.append(":")
		.append(String.format("%02d", milliseconds()))
		.append(" ")
		.append(days());
		return sb.toString();
	}

	@Override
	public double totalDays() {
		double days = mLTime / 1000f / 60f / 60f / 24f;
		return days;
	}
	
	@Override
	public double totalHours() {
		double hours = mLTime / 1000f / 60f / 60f;
		return hours;
	}

	@Override
	public double totalMinutes() {
		double minutes = mLTime / 1000f / 60f;
		return minutes;
	}
	
	@Override
	public double totalSeconds() {
		double seconds = mLTime / 1000f;
		return seconds;
	}


	@Override
	public double totalMillisecond() {
		return mLTime;
	}

}