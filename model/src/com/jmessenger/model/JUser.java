package com.jmessenger.model;

import org.json.JSONException;

import framework.time.DateTime;
import framework.time.TimeSpan;

/**
 * 
 * @author Hamza
 * @version 0.1
 */
public class JUser extends JMessengerPacket implements JUserRO{
	
	
	enum JUserKey{
		CREATED,
		LOGGED_IN,
		NAME,
		ID, 
		HOST, PORT
	}

	protected static long mCount = 0;
	private static JUserRO mNull;
	protected String mThis;
	
	public JUser(String host, int port, String name){
		mCount+= 1;
		DateTime now = DateTime.now();
		putCreeated(now);
		putHost(host);
		putPort(port);
		putName(name);
		putID(mCount);
		mThis = toString();
	}
	
	public JUser(String json) throws JSONException{
		super(json);
		mThis = toString();
	}

	public JUser(){
		mCount+= 1;
		DateTime now = DateTime.now();
		putCreeated(now);
		putID(mCount);
	}

	@Override
	public DateTime readCreated() {
		DateTime dt = readDateTime(JUserKey.CREATED);
		return dt;
	}
	
	public void putCreeated(DateTime created){
		putDateTime(JUserKey.CREATED,created);
	}

	@Override
	public TimeSpan readTimeLoggedIn() {
		DateTime dt = readCreated();
		DateTime now = DateTime.now();
		TimeSpan diff = now.diff(dt);
		return diff;
	}

	@Override
	public String readName() {
		String val = readString(JUserKey.NAME);
		return val;
	}
	
	public void putName(String name){
		putString(JUserKey.NAME, name);
	}

	@Override
	public long readID() {
		long val = readLong(JUserKey.ID);
		return val;
	}
	
	public void putID(long id){
		putLong(JUserKey.ID, id);
	}
	
	@Override
	public String readHost(){
		String host = readString(JUserKey.HOST);
		return host;
	}
	
	public void putHost(String host){
		putString(JUserKey.HOST, host);
	}

	@Override
	public int readPort(){
		int port = readInt(JUserKey.PORT);
		return port;
	}
	
	public void putPort(int port){
		putInt(JUserKey.PORT, port);
	}
	
	protected static JUserRO nullObject(String error){
		if(mNull == null){
			mNull.setError(true);
		}
		mNull.setErrorMessage(error);
		return mNull;
	}
	
	public static JUserRO create(String str){
		if(str == null) nullObject("NullpointerException");
		try {
			JUserRO obj = new JUser(str);
			return obj;
		} catch (JSONException e) {
			return nullObject(str);
		}
	}
	
}
