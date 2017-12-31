package framework.time;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

public class DateTime implements DateControl, Comparable<DateControl>{
	
	protected long mLDate;
	protected final Calendar mCal = Calendar.getInstance();
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		DateTime dt2014 = DateTime.from(2014, 1, 1);
		DateTime dt2013 = DateTime.from(2013, 31, 12);
		System.out.println(dt2014.compareTo(dt2013));
	}
	
	public DateTime(){
		mLDate = zero(); 
	}

	public static DateTime now(){
		return DateTime.fromMillis(System.currentTimeMillis());
	}

	@Override
	public int day(){
		mCal.setTimeInMillis(mLDate);
		return mCal.get(Calendar.DAY_OF_MONTH);
	}

	public int dayOfWeek(){
		mCal.setTimeInMillis(mLDate);
		return mCal.get(Calendar.DAY_OF_WEEK);
	}

	public int dayOfYear(){
		mCal.setTimeInMillis(mLDate);
		return mCal.get(Calendar.DAY_OF_YEAR);
	}

	@Override
	public int hour(){
		mCal.setTimeInMillis(mLDate);
		return mCal.get(Calendar.HOUR_OF_DAY);
	}

	@Override
	public int millisecond(){
		mCal.setTimeInMillis(mLDate);
		return mCal.get(Calendar.MILLISECOND);
	}

	@Override
	public int minute(){
		mCal.setTimeInMillis(mLDate);
		return mCal.get(Calendar.MINUTE);
	}

	public int month(){
		mCal.setTimeInMillis(mLDate);
		return mCal.get(Calendar.MONTH) + 1;
	}
	
	public int year(){
		mCal.setTimeInMillis(mLDate);
		return mCal.get(Calendar.YEAR);
	}
	
	@Override
	public int second(){
		mCal.setTimeInMillis(mLDate);
		return mCal.get(Calendar.SECOND);
	}
	
	public int timeOfDay(){
		return 0;
	}
	
	@Override
	public String nameOfDay(Locale locale){
		mCal.setTimeInMillis(mLDate);
		return mCal.getDisplayName(Calendar.DAY_OF_WEEK, Calendar.LONG, locale);
	}
	
	@Override
	public int cw(){
		mCal.setTimeInMillis(mLDate);
		return mCal.get(Calendar.WEEK_OF_YEAR);
	}
	
	public DateControl add(DateTime dt){
		if(dt == null) return this;
		addHours(dt.hour());
		addMinutes(dt.minute());
		addSeconds(dt.second());
		addMilliseconds(dt.millisecond());
		addDays(dt.day());
		addYears(dt.year());
		return this;
	}

	public DateControl add(TimeSpan ts){
		if(ts == null) return this;
		addHours(ts.hours());
		addMinutes(ts.minutes());
		addSeconds(ts.seconds());
//		addMilliseconds(ts.millisecond());
		addDays(ts.days());
		return this;
	}

	@Override
	public DateControl addDays(int value){
		mLDate+= value * 1000 * 60 * 60 * 24;
		return this;
	}
	
	@Override
	public DateControl addHours(int value){
		mLDate+= value * 1000 * 60 * 60;
		return this;
	}

	@Override
	public DateControl addMilliseconds(int value){
		mLDate+= value;
		return this;
	}
	
	@Override
	public DateControl addMinutes(int value){
		mLDate+= value * 1000 * 60;
		return this;
	}
	
	@Override
	public DateControl addMonths(int value){
		mCal.setTimeInMillis(mLDate);
		mCal.add(Calendar.MONTH, value);
		mLDate = mCal.getTimeInMillis();
		return this;
	}

	@Override
	public DateControl addMonthDay(int value){
		mCal.setTimeInMillis(mLDate);
		mCal.add(Calendar.DAY_OF_MONTH, value);
		mLDate = mCal.getTimeInMillis();
		return this;
	}

	@Override
	public DateControl addSeconds(int value){
		mLDate+= value * 1000;
		return this;
	}
	
	@Override
	public DateControl addTicks(int value){
		return this;
	}
	
	@Override
	public DateControl addYears(int value){
		mCal.setTimeInMillis(mLDate);
		mCal.add(Calendar.YEAR, value);
		mLDate = mCal.getTimeInMillis();
		return this;
	}
	
	public TimeSpan diff(DateTime dt){
		if(dt == null){
			return TimeSpan.fromMilliseconds(0);
		}
//		else if(dt.year() == year() && dt.month() == month() && dt.day() == day() && dt.hour() == hour() && dt.minute() == minute() && dt.second() == second()){
//			TimeSpan zeroTime = TimeSpan.fromMilliseconds(0);
//			return zeroTime;
//		}
		long lDiffTime = Math.abs(timeInMillis() - dt.timeInMillis());
		TimeSpan diff = TimeSpan.fromMilliseconds(lDiffTime);
		return diff;
	}

	public boolean isLeapYear(){
		int year = year();
		return year % 400 == 0 || (year % 4 == 0 && year % 100 != 0);
	}
	
	public void substract(DateTime dt){
		if(dt == null) return;
		addHours(-dt.hour());
		addMinutes(-dt.minute());
		addSeconds(-dt.second());
		addMilliseconds(-dt.millisecond());
		addDays(-dt.day());
		addYears(-dt.year());
	}

	public void substract(TimeSpan dt){
		if(dt == null) return;
		addHours(-dt.hours());
		addMinutes(-dt.minutes());
		addSeconds(-dt.seconds());
//		addMilliseconds(-dt.millisecond());
		addDays(-dt.days());
	}
	
	public static DateTime parse(String date){
		JSONObject jDate = null;
		try {
			jDate = new JSONObject(date);
		} catch (JSONException e) {
			e.printStackTrace();
			if(date == null) return DateTime.fromOADate(0);
			String [] times = date.split(" ")[0].split("[-/./]");
			if(times == null || times.length < 2) return DateTime.fromOADate(0);
			int year = Integer.parseInt(times[0]);
			int month = Integer.parseInt(times[1]);
			int day = Integer.parseInt(times[2]);
			return DateTime.from(year, month, day);
		}
		VODate voDate = createVoDate(jDate);
		DateTime dt = fromVoDate(voDate);
		return dt;
	}

	private static VODate createVoDate(JSONObject jDate) {
		VODate date = new VODate();
		date.hour = jDate.optInt("hour");
		date.minute = jDate.optInt("minute");
		date.second = jDate.optInt("second");
		date.millisecond = jDate.optInt("millisecond");
		date.day = jDate.optInt("day");
		date.month = jDate.optInt("month");
		date.year = jDate.optInt("year");
		date.cw = jDate.optInt("week");
		date.nameOfDay = jDate.optString("nameOfDay");
		return date;
	}

	public static DateTime parse(Date date){
		return new DateTime();
	}

	public static DateTime parse(Calendar c){
		DateTime dt = new DateTime();
		dt.setTimeInMillis(c.getTimeInMillis());
		return dt;
	}
	
	public static DateTime fromOADate(int value){
		DateTime dt = zeroOleDate();
		dt.addDays(value-1);
		return dt;
	}

	public static DateTime fromMillis(long millis){
		DateTime dt = new DateTime();
		dt.setTimeInMillis(millis);
		return dt;
	}
	
	public static DateTime fromVoDate(VODate voDate) {
		DateTime date = new DateTime();
		date.addHours(voDate.hour);
		date.addMinutes(voDate.minute);
		date.addSeconds(voDate.second);
		date.addMilliseconds(voDate.millisecond);
		date.addMonths(voDate.month-1);
		date.addMonthDay(voDate.day-1);
		date.addYears(voDate.year);
		return date;
	}

	public static DateTime from(int year, int month, int day) {
		DateTime date = new DateTime();
		Calendar c = Calendar.getInstance();
		c.set(year,month-1,day,0,0,0);
		date.mLDate = c.getTimeInMillis();
		return date;
	}
	
	protected void setTimeInMillis(long value) {
		mLDate = value;
	}
	
	protected long zero() {
		Calendar c = Calendar.getInstance();
		c.set(1970, 0, 0, 0, 0, 0);
		return c.getTimeInMillis();
	}
	
	@Override
	public long timeInMillis(){
		return mLDate;
	}
	
	protected static DateTime zeroOleDate(){
		Calendar c = Calendar.getInstance();
		c.set(1899, 12, 30, 0, 0, 0);
		DateTime dt = new DateTime();
		dt.setTimeInMillis(c.getTimeInMillis());
		return dt;
	}
	
	
	@Override
	public int compareTo(DateControl another) {
		if(another == null) return -1;
		if(another == this) return 0;
		long thisTime = timeInMillis();
		long otherTime = another.timeInMillis();
		if(thisTime == otherTime) return 0;
		return thisTime >= otherTime ? 1 : -1;
	}
	
	@Override
	public String toString(){
		StringBuilder sb = new StringBuilder();
		sb
		.append("{\"hour\":")
		.append(hour())
		.append(",\"minute\":")
		.append(minute())
		.append(",\"second\":")
		.append(second())
		.append(",\"millisecond\":")
		.append(String.format("%02d", millisecond()))
		.append(",\"day\":")
		.append(day())
		.append(",\"nameOfDay\":\"")
		.append(nameOfDay(Locale.getDefault()))
		.append("\",\"month\":")
		.append(month())
		.append(",\"week\":")
		.append(cw())
		.append(",\"year\":")
		.append(year())
		.append("}");
		return sb.toString();
	}
	
	public String simplify(){
		StringBuilder sb = new StringBuilder();
		sb
		.append(String.format("%02d", hour()))
		.append(":")
		.append(String.format("%02d", minute()))
		.append(":")
		.append(String.format("%02d", second()))
//		.append(":")
//		.append(String.format("%02d", millisecond()))
		.append(" ")
		.append(String.format("%02d", day()))
		.append(".")
		.append(String.format("%02d", month()))
		.append(".")
		.append(String.format("%04d", year()));
		return sb.toString();
	}
	
	public static class VODate{
		public int hour;
		public int minute;
		public int second;
		public int millisecond;
		public int day;
		public int month;
		public int year;
		public int cw;
		public String nameOfDay;
	}
}
