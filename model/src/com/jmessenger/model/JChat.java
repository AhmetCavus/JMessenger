package com.jmessenger.model;

import org.json.JSONException;

import framework.time.DateTime;

public class JChat extends JMessengerPacket implements JChatRO{

	enum JChatKey{
		Message,
		From,
		Send, SessionId
	}

	protected static JChat mNull;
	
	public JChat(String json) throws JSONException{
		super(json);
	}
	
	public JChat(){}
	
	@Override
	public String readMessage() {
		String message = readString(JChatKey.Message);
		return message;
	}
	
	public void putMessage(String message){
		putString(JChatKey.Message,message);
	}

	@Override
	public JUser readFrom() {
		String from = readString(JChatKey.From);
		JUser user;
		try {
			user = new JUser(from);
			return user;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new JUser();
	}
	
	public void putFrom(JUserRO from){
		putString(JChatKey.From,from.toString());
	}

	@Override
	public DateTime readSend() {
		DateTime date = readDateTime(JChatKey.Send);
		return date;
	}
	
	public void putSend(DateTime send){
		putDateTime(JChatKey.Send,send);
	}

	@Override
	public long readSessionId() {
		long id = readLong(JChatKey.SessionId);
		return id;
	}

	protected static JChatRO nullObject(String error){
		if(mNull == null){
			mNull.setError(true);
		}
		mNull.setErrorMessage(error);
		return mNull;
	}
	
	public static JChatRO create(String str){
		if(str == null) nullObject("NullpointerException");
		try {
			JChat chat = new JChat(str);
			return chat;
		} catch (JSONException e) {
			return nullObject(str);
		}
	}

}
