package com.jmessenger.model;

import java.util.Iterator;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import framework.time.DateTime;

/**
 * 
 * @author Hamza
 * 
 */
public class SessionManager {

	// This must be a Database
	protected JSONObject mSessionMap = new JSONObject();

	public JChat addChat(long sessionId, JUserRO from, String message) {
		JSession session = getSession(sessionId);
		List<JChatRO> chats = session.readChats();
		JChat chat = new JChat();
		chat.putFrom(from);
		DateTime dt = DateTime.now();
		chat.putSend(dt);
		chat.putMessage(message);
		chats.add(chat);
		session.putChats(chats);
		try {
			mSessionMap.put(session.readId() + "", session.toString());
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return chat;
	}

	public JSessionRO createSession(JUserRO user, JUserRO user2) {
		Iterator<?> keys = mSessionMap.keys();
		while (keys.hasNext()) {
			String key = keys.next().toString();
			String str = mSessionMap.optString(key);
			JSessionRO session = JSession.create(str);
			if(!session.isThisSession(user, user2)) continue;
			return session;
		}
		JSession session = new JSession();
		session.putUser(user, user2);
		try {
			mSessionMap.put(session.readId() + "", session.toString());
			return session;
		} catch (JSONException e) {
			return error();
		}
	}

	public JSession getSession(long sessionId) {
		String value = mSessionMap.optString(sessionId + "");
		JSession session;
		try {
			session = new JSession(value);
			return session;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new JSession();
	}
	
	protected JSessionRO error(){
		JSessionRO session = new JSession();
		session.setError(true);
		session.setErrorMessage("Session is not creatable");
		return session;
	}
	
}
