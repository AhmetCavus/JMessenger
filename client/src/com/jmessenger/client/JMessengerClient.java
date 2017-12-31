package com.jmessenger.client;

import java.io.IOException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jmessenger.listener.MessageReceiveListener;
import com.jmessenger.model.JChat;
import com.jmessenger.model.JChatRO;
import com.jmessenger.model.JSession;
import com.jmessenger.model.JUser;
import com.jmessenger.model.JUserRO;
import com.jmessenger.remote.RemoteChat;
import com.jmessenger.remote.RmiUtil;
import com.jmessenger.client.JClient;

public class JMessengerClient implements MessageReceiveListener{

	public final int SPORT;
	public final String SHOST;

	protected JUserRO mUser;
	protected RemoteChat mRemote;

	protected final JClient mClient;
	private MessageReceiveListener mListener;
	
	public JMessengerClient(final String host, final int port){
		SPORT = port;
		SHOST = host;
		mClient = new JClient(host, port+1);
		mClient.setMessageListener(this);
	}
	
	public void connect() throws IOException, NotBoundException{
		mRemote = (RemoteChat) RmiUtil.bindClient(SHOST, SPORT, com.jmessenger.broker.JMessengerBroker.class.getName());
		mClient.connect();
		mClient.listen();
	}

	public List<JUserRO> buddyList() throws RemoteException {
//		log("buddyList", jUser);
		String res = mRemote.buddyList(mUser.readID());
		JSONObject json = null;
		try {
			List<JUserRO> list = new ArrayList<JUserRO>();
			json = new JSONObject(res);
			Iterator<?> keys = json.keys();
			while(keys.hasNext()){
				String key = keys.next().toString();
				String str = json.optString(key);
				JUserRO buddy = JUser.create(str);
				list.add(buddy);
			}
			return list;
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return new ArrayList<JUserRO>();
	}
	
	public void removeBuddy(JUserRO buddy) throws RemoteException{
		mRemote.removeBuddy(mUser.readID(),buddy.toString());
	}

	public void addBuddy(JUserRO buddy) throws RemoteException{
		mRemote.addBuddy(mUser.readID(),buddy.toString());
	}

	public List<JUserRO> userList() throws RemoteException {
//		log("userList", "");
		String res = mRemote.userList();
		JSONArray jArr = null;
		try {
			jArr = new JSONArray(res);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		List<JUserRO> userList = new ArrayList<JUserRO>();
		for (int index = 0; index < jArr.length(); index++) {
			String item = jArr.optString(index);
			JUserRO user = null;
			try {
				user = new JUser(item);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			userList.add(user);
		}
		return userList;
	}

	public boolean login(String name) throws RemoteException {
		if(mRemote == null) return false;
		String res = mRemote.login(mClient.host(), mClient.port(), name);
		mUser = JUser.create(res);
		return !mUser.hasError();
	}

	public boolean logout(long id) throws RemoteException {
//		log("logout", "" + id);
		return mRemote.logout(id);
	}

	public JChatRO chat(long sessionId, String message)
			throws RemoteException {
		String res = mRemote.chat(sessionId, mUser.toString(), message);
		JChatRO chat = JChat.create(res);
		return chat;
	}

	public JSession createSession(JUserRO opponent)
			throws RemoteException {
//		log("createSession","user1 " + mUser + ", jUser2 " + opponent);
		String res = mRemote.createSession(mUser.toString(), opponent.toString());
		JSession session = JSession.create(res);
		return session;
	}
	
	public void log(String key, String value){
//		System.out.println("Remote call -> " + key + ", val -> " + value);
	}
	
	public String host(){
		return mClient.host();
	}
	
	public int port(){
		return mClient.port();
	}
	
	public JUserRO user(){
		return mUser;
	}

	public void setMessageListener(MessageReceiveListener listener){
		mListener = listener;
		mClient.setMessageListener(listener);
	}
	
	@Override
	public void onMessageReceive(String data) {
		if(mListener == null) return;
		mListener.onMessageReceive(data);
	}

	public void close() {
		if(mRemote != null){
			try {
				mRemote.logout(mUser.readID());
			} catch (RemoteException e) {}
		}
		if(mClient == null) return;
		mClient.close();
		mRemote = null;
		mUser = null;
	}
	
	protected String error(){
		return "{\"error\":true}";
	}
}
