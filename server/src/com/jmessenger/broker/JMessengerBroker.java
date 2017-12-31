package com.jmessenger.broker;

import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.jmessenger.model.BuddyManager;
import com.jmessenger.model.JChatRO;
import com.jmessenger.model.JSession;
import com.jmessenger.model.JSessionRO;
import com.jmessenger.model.JUser;
import com.jmessenger.model.JUserRO;
import com.jmessenger.model.SessionManager;
import com.jmessenger.remote.RemoteChat;
import com.jmessenger.remote.RmiUtil;
import com.jmessenger.server.JServer;
import com.jmessenger.tools.Tools;

public class JMessengerBroker implements RemoteChat{

	public final int PORT;
	public final String HOST;
	protected Scanner mScanner;
	protected Boolean mExit = false;
	private ExecutorService mThreadPool;
	protected final JServer mSServer;
	protected final BuddyManager mBuddy = new BuddyManager();
	protected final List<JUserRO> mLoggedUser = new ArrayList<JUserRO>();
	protected final SessionManager mSession = new SessionManager();
	
	public static void main(String[] args) {
		try {
			InetAddress localHost = java.net.Inet4Address.getLocalHost();
			JMessengerBroker broker = new JMessengerBroker(localHost.getHostAddress(),7575);
			broker.launchInputService();
			broker.launch();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (RemoteException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
	}
	
	
	public JMessengerBroker(final String host, final int port){
		PORT = port;
		HOST = host;
		mSServer = new JServer(PORT+1);
	}
	
	public void launch() throws RemoteException, MalformedURLException{
		clearScreen();
		RmiUtil.launchService(HOST, PORT, this);
		mSServer.launch();
	}

	public void launchInputService(){
		mScanner = new Scanner(System.in);
		mThreadPool = Executors.newCachedThreadPool();
		mThreadPool.submit(new Runnable() {
			
			@Override
			public void run() {
				while(!mExit && !Thread.currentThread().isInterrupted()){
					String input = readInput();
					if(!input.equals("q!")) continue;
					exit();
				}
			}
		});
	}
	
	protected String readInput(){
		if(mScanner == null) mScanner = new Scanner(System.in);
		return mScanner.nextLine();
	}

	@Override
	public String buddyList(long id) throws RemoteException {
		log("RMI: buddyList", id + "");
		return mBuddy.getBuddy(id).toString();
	}

	@Override
	public void addBuddy(long id,String buddy) throws RemoteException {
		log("RMI: buddyList", id + "");
		JUserRO jBuddy = JUser.create(buddy); 
		mBuddy.addBuddy(id,jBuddy);
	}
	
	@Override
	public void removeBuddy(long id, String buddy) throws RemoteException {
		log("RMI: buddyList", id + "");
		JUserRO jBuddy = JUser.create(buddy);
		mBuddy.removeBuddy(id, jBuddy);
	}

	@Override
	public String userList() throws RemoteException {
		log("RMI: userList", "");
		JSONArray jArr = new JSONArray(mLoggedUser);
		return jArr.toString();
	}

	@Override
	public String login(String host, int port, String name) throws RemoteException {
		log("RMI: login", "host " + host + ", port " + port + ", name " + name);
		for(JUserRO user : mLoggedUser){
			if(user.readName().equals(name)){
				JUserRO packet = new JUser();
				packet.setError(true);
				packet.setStatus("0001");
				packet.setErrorMessage("Username already exists");
				return packet.toString();
			}
		}
		JUser user = new JUser(host,port,name);
		mLoggedUser.add(user);
		return user.toString();
	}

	@Override
	public boolean logout(long id) throws RemoteException {
		log("RMI: logout", "" + id);
		JUserRO user = findUser(id);
		if(user.hasError()) return false;
		deleteUser(id);
		Socket socket = mSServer.findSocket(user.readHost(), user.readPort());
		if(socket == null) return true;
		Tools.safeClose(socket);
		return socket.isClosed();
	}

	@Override
	public String chat(long sessionId, String from, String message)
			throws RemoteException {
		log("RMI: chat", "session id " + sessionId + ", user " + from + ", message " + message);
		JUserRO user = null;
		user = JUser.create(from);
		JChatRO chat =  mSession.addChat(sessionId, user, message);
		JSession session = mSession.getSession(sessionId);
		JUserRO opponent = session.readOpponent(user.readID());
		if(!mSServer.sendChat(opponent.readHost(), opponent.readPort(), chat.toString())){
			chat.setError(true);
			chat.setErrorMessage(opponent.readName() + " has left the session");
		}
		return chat.toString();
	}

	@Override
	public String createSession(String jUser, String jUser2)
			throws RemoteException {
		log("RMI: createSession","user1 " + jUser + ", jUser2 " + jUser2);
		JUserRO user1 = JUser.create(jUser);
		JUserRO user2 = JUser.create(jUser2);
		JSessionRO session = mSession.createSession(user1, user2);
		return session.toString();
	}
	
	protected JUserRO findUser(long id){
		for (JUserRO user : mLoggedUser) {
			if(user.readID() == id) return user;
		}
		JUser user = new JUser();
		user.setError(true);
		user.setErrorMessage("Not found");
		return user;
	}
	
	protected void deleteUser(long id){
		for (int index = 0; index < mLoggedUser.size(); index++) {
			JUserRO user = mLoggedUser.get(index);
			if(user.readID() != id) continue;
			mLoggedUser.remove(index);
			return;
		}
	}
	
	protected void clearScreen(){
		try {
			Process exitCode;
			if (System.getProperty("os.name").startsWith("Window")) {
				exitCode = Runtime.getRuntime().exec("cls");
			} else {
				exitCode = Runtime.getRuntime().exec("clear");
			}
		} catch (IOException e) {
			for (int i = 0; i < 1000; i++) {
				System.out.println();
			}
		}
	}

	protected void exit(){
		log("Server", "Prepare closing the server");
		for (JUserRO user : mLoggedUser) {
			mSServer.sendChat(user.readHost(), user.readPort(), "EXIT");
		}
		Tools.safeSleep(501);
		synchronized (mExit) {
			log("Server", "Shutting up prompting service");
			mExit = true;
			Tools.safeClose(mScanner);
		}
		log("Server","Closing the server services");
		mSServer.close();
		mLoggedUser.clear();
		if(mThreadPool != null){
			mThreadPool.shutdown();
			if(!mThreadPool.isTerminated()){
				mThreadPool.shutdownNow();
			}
		}
		log("Server", "Shutting down");
		Tools.safeSleep(501);
		System.exit(0);
	}
	
	protected void log(String key, String value){
		System.out.println(key + ": " + value);
	}
	
	protected String error(String msg){
		return "{\"error\":true,\"errorMessage\":\"" + msg + "}";
	}
	
}
