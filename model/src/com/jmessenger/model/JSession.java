package com.jmessenger.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import framework.time.DateTime;

public class JSession extends JMessengerPacket implements JSessionRO{

	enum JSessionKey{
		SessionId,
		Created,
		User1,
		User2,
		Chats
	}

	protected static long mSessionCount = 0;
	private static JSession mNull;
	
	public JSession(String json) throws JSONException {
		super(json);
	}

	public JSession(){
		mSessionCount+= 1;
		putId(mSessionCount);
	}

	@Override
	public long readId() {
		long val = readLong(JSessionKey.SessionId);
		return val;
	}
	
	public void putId(long id){
		putLong(JSessionKey.SessionId, id);
	}

	@Override
	public JUser readOpponent(long ownId) {
		String val = readString(JSessionKey.User1);
		String val2 = readString(JSessionKey.User2);
		JUser opponent = null;
		try {
			JUser jUser = new JUser(val);
			JUser jUser2 = new JUser(val2);
			opponent = jUser.readID() == ownId ? jUser2 : jUser;
			return opponent;
			
		} catch (JSONException e) {
			e.printStackTrace();
			opponent = new JUser();
			opponent.setError(true);
			opponent.setErrorMessage(e.getMessage());
			return opponent;
		}
	}
	
	@Override
	public boolean isThisSession(JUserRO u1, JUserRO u2) {
		if(u1 == null) return false;
		if(u2 == null) return false;
		long id1 = u1.readID();
		long id2 = u2.readID();
		JUserRO user1 = readUser1();
		JUserRO user2 = readUser2();
		return (id1 == user1.readID() && id2 == user2.readID()) || (id2 == user1.readID() && id1 == user2.readID());
	}
	
	public void putUser(JUserRO user1, JUserRO user2){
		putString(JSessionKey.User1, user1.toString());
		putString(JSessionKey.User2, user2.toString());
	}

	public  JUserRO readUser1(){
		String str = readString(JSessionKey.User1);
		return JUser.create(str);
	}

	public  JUserRO readUser2(){
		String str = readString(JSessionKey.User2);
		return JUser.create(str);
	}
	
	@Override
	public DateTime readCreated() {
		return readDateTime(JSessionKey.Created);
	}

	@Override
	public List<JChatRO> readChats() {
		JSONArray jArr = readJSONArray(JSessionKey.Chats);
		List<JChatRO> chats = new ArrayList<JChatRO>();
		if(jArr == null) return chats;
		for(int index = 0; index < jArr.length(); index++){
			String str = jArr.optString(index);
			try {
				JChat chat = new JChat(str);
				chats.add(chat);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return chats;
	}
	
	public void putChats(List<JChatRO> list) {
		JSONArray arr = new JSONArray(list);
		putJSONArray(JSessionKey.Chats, arr);
	}
	
	protected static JSession nullObject(String error){
		if(mNull == null){
			mNull.setError(true);
		}
		mNull.setErrorMessage(error);
		return mNull;
	}
	
	public static JSession create(String str){
		if(str == null) nullObject("NullpointerException");
		try {
			JSession obj = new JSession(str);
			return obj;
		} catch (JSONException e) {
			return nullObject(str);
		}
	}


}
