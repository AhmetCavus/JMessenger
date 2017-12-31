package com.jmessenger.client;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.json.JSONException;

import com.jmessenger.client.JMessengerClient;
import com.jmessenger.listener.MessageReceiveListener;
import com.jmessenger.model.JChat;
import com.jmessenger.model.JChatRO;
import com.jmessenger.model.JMessengerPacket;
import com.jmessenger.model.JSession;
import com.jmessenger.model.JSessionRO;
import com.jmessenger.model.JUser;
import com.jmessenger.model.JUserRO;
import com.jmessenger.tools.Tools;

public class JMessengerConsole implements MessageReceiveListener{

	enum Command{
		UserList,
		BuddyList,
		Delete,
		Add,
		Chat,
		Exit
	}
	
	protected String mMenu;
	protected String mPrompt;
	protected Scanner mScanner;
	protected String mUserMenu;
	protected String mBuddyMenu;
	protected Future<?> mFuture;
	protected UICommand mUICommand;
	protected Boolean mExit = false;
	protected List<JUserRO> mUserList;
	protected List<JUserRO> mBuddyList;
	protected JMessengerClient mClient;
	protected List<JSession> mSessions;
	protected JSession mCurrentSession;
	protected ExecutorService mThreadPool;
	
	public static void main(String[] args) {
		JMessengerConsole messenger = new JMessengerConsole();
		messenger.start();
	}
	
	public void start(){
		clearScreen();
		mUICommand = new CommandMenu();
		launchMessengerService();
	}
	
	protected void launchMessengerService(){
	
		System.out.println("Connection to server...");
		try {
			InetAddress localHost = java.net.Inet4Address.getLocalHost();
			mClient = new JMessengerClient(localHost.getHostAddress(),7575);
			mClient.setMessageListener(this);
			mClient.connect();
			
			String input = "";
			boolean success;
			do{
				System.out.print("Enter your name: ");
				input = readInput();
				success = mClient.login(input);
				if(!success) System.out.println("User already exists");
			} while(!success);
			
		} catch (MalformedURLException e) {
			System.out.println(e.getMessage());
			onExit();
			return;
		} catch (IOException e) {
			System.out.println(e.getMessage());
			System.out.println("Maybe the server is not been started\n");
			onExit();
			return;
		} catch (NotBoundException e) {
			System.out.println(e.getMessage());
			System.out.println("Maybe the server is not been started\n");
			onExit();
			return;
		} 
		 mThreadPool = Executors.newSingleThreadExecutor();
		 mFuture = mThreadPool.submit(new Callable<Boolean>() {
			
			@Override
			public Boolean call() throws Exception {
				while(!Thread.currentThread().isInterrupted()
						&& !mExit){
					clearScreen();
					mUICommand.buildUI();
					promptToInput();
					String input = readInput();
					mUICommand.manageInput(input);
				}
				return true;
			}
		});
		try {
			mFuture.get();
			onExit();
		} catch (InterruptedException e) {
			e.printStackTrace();
			onExit();
		} catch (ExecutionException e){
			e.printStackTrace();
			onExit();
		}
	}
	
	public String readInput(){
		if(mScanner == null) mScanner = new Scanner(System.in);
		return mScanner.nextLine();
	}
	
	public String buildMenu(){
		if(mMenu == null || mMenu.isEmpty()){
			StringBuilder sb =
			buildHeader("Menu")
			.append("[1]  - User list\n")
			.append("[2]  - Buddy list\n\n")
			.append("[q!] - Exit\n")
			.append("====================================================================");
			mMenu = sb.toString();
		}
		return mMenu;
	}

	public String buildUserMenu(){
		mUserList = updateUserList();
		
		StringBuilder sb =
		buildHeader("Userlist")
		.append("# User online ")
		.append(mUserList.size())
		.append("\n--------------------------------------------------------------------\n");
		
		for (JUserRO user : mUserList) {
			if(user.readID() == mClient.user().readID()) continue;
			sb
			.append("[ID - ")
			.append(user.readID())
			.append("] - ")
			.append(user.readName())
			.append("\n--------------------------------------------------------------------\n\n");
		}
		
		sb
		.append("[1] - Chat\n")
		.append("[2] - Add to buddy list\n\n")
		.append("[3] - Go back\n")
		.append("====================================================================");
		mUserMenu = sb.toString();
		return mUserMenu;
	}

	protected StringBuilder buildHeader(String header){
		StringBuilder sb = new StringBuilder();
		JUserRO user = mClient.user();
		sb
		.append("\n============================ JMessenger ============================\n")
		.append("# ")
		.append(header)
		.append("\n# ")
		.append(user.readName())
		.append(" - ")
		.append(user.readHost())
		.append(":")
		.append(user.readPort())
		.append("\n# Online since: ")
		.append(user.readTimeLoggedIn().minutes())
		.append("m")
		.append("\n\n");
		return sb;
	}
	
	public String buildBuddyMenu(){
		
		mBuddyList = updateBuddyList();
		
		StringBuilder sb =
		buildHeader("Buddylist")
		.append("# Count of buddies ")
		.append(mBuddyList.size())
		.append("\n--------------------------------------------------------------------\n");
		
		for (JUserRO user : mBuddyList) {
			if(user.readID() == mClient.user().readID()) continue;
			sb
			.append("[ID - ")
			.append(user.readID())
			.append("] - ")
			.append(user.readName())
			.append("\n--------------------------------------------------------------------\n\n");
		}
		
		sb
		.append("[1] - Chat\n")
		.append("[2] - Remove from buddy list\n\n")
		.append("[3] - Go back\n")
		.append("====================================================================");
		mUserMenu = sb.toString();
		return mUserMenu;
	}
	
	public void promptToInput(){
		if(mPrompt == null || mPrompt.isEmpty()){
			mPrompt = "Your input: ";
		}
		System.out.print(mPrompt);
	}
	
	public String buildChatMenu(JSessionRO sessions){
		if(sessions == null) return "";
		
		StringBuilder sb = new StringBuilder();
		
		if(sessions != null){
			for (JChatRO chat : sessions.readChats()) {
				sb
				.append("\n====================================================================\n")
				.append("Send: ")
				.append(chat.readSend().simplify())
				.append("\nName: ")
				.append(chat.readFrom().readName())
				.append("\nMessage: ")
				.append(chat.readMessage())
				.append("\n====================================================================\n");
			}
		}
		
		sb
		.append("\n\n")
		.append(buildHeader("Chat"))
		.append("[3!] - Go back\n\n")
		.append("[q!] - Exit\n")
		.append("====================================================================");
		
		return sb.toString();
	}

	@Override
	public void onMessageReceive(String data) {
		if(data.equals("EXIT")){
//			System.setIn(new ByteArrayInputStream("EXIT".getBytes()));
			System.out.println("\n\n >> Messenger: Server is closed. Type any key to exit the application <<");
			exit();
			return;
		}
		boolean fromBuddy = false;
		mUICommand = new CommandChat(fromBuddy);
		mUICommand.onMessageReceive(data);
		promptToInput();
//		System.out.println("Receiving message: " + data);
	}

	protected JUserRO findUser(String input){
		if(input == null){
			return userError();
		}
		long id = -1;
		try{
			id = Long.parseLong(input);
		} catch(NumberFormatException err){}
		for (JUserRO user : mUserList) {
			if(user.readID() != id && !user.readName().equals(input)) continue;
			return user;
		}
		return userError();
	}

	protected JUserRO findBuddy(String input){
		if(input == null){
			return userError();
		}
		long id = -1;
		try{
			id = Long.parseLong(input);
		} catch(NumberFormatException err){}
		for (JUserRO buddy : mBuddyList) {
			if(buddy.readID() != id && !buddy.readName().equals(input)) continue;
			return buddy;
		}
		return buddyError();
	}
	
	protected List<JUserRO> updateUserList() {
		try {
			return mClient.userList();
		} catch (RemoteException e) {
			error(e, "User list is not accessible");
			List<JUserRO> list = new ArrayList<JUserRO>();
			return list;
		} 
	}

	protected List<JUserRO> updateBuddyList() {
		try {
			return mClient.buddyList();
		} catch (RemoteException e) {
			error(e, "Buddy list is not accessible");
			List<JUserRO> list = new ArrayList<JUserRO>();
			return list;
		} 
	}

	protected JUserRO userError() {
		JUser user = new JUser();
		user.setError(true);
		user.setErrorMessage("User is not online");
		return user;
	}
	
	protected JUserRO buddyError() {
		JUser user = new JUser();
		user.setError(true);
		user.setErrorMessage("No buddy found in your list");
		return user;
	}
	
	protected void error(Exception err, String extra){
		System.out.println("Error: " + err.getMessage());
		if(extra == null) return;
		System.out.println(extra);
	}

	public void exit(){
		synchronized (mExit) {
			mExit = true;
		}
		
	}

	protected void onExit(){
		System.out.println("Shutting down the application...");
		mBuddyMenu = null;
		mUICommand = null;
		mUserMenu = null;
		mMenu = null;
		Tools.safeSleep(101);
		Tools.safeClose(mScanner);
		if(mThreadPool != null){
			mThreadPool.shutdown();
			mFuture.cancel(true);
			if(!mThreadPool.isTerminated()) mThreadPool.shutdownNow();
			mThreadPool = null;
			mFuture = null;
		}
		if(mClient != null) mClient.close();
		System.out.println("Application closed");
		System.exit(0);
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
	
	protected boolean hasError(JMessengerPacket packet){
		if(packet == null) return true;
		if(packet.hasError()){
			System.out.println(packet.errorMessage());
			return true;
		}
		return false;
	}
	
	public interface UICommand extends MessageReceiveListener{
		void manageInput(String input);
		void buildUI();
	}
	
	public abstract class ACommand implements UICommand{
		
		@Override
		public void onMessageReceive(String data) {
			clearScreen();
			JChat chat = null;
			try {
				chat = new JChat(data);
			} catch (JSONException e) {
				System.out.println(e.getMessage());
				return;
			} 
			if(hasError(chat)) return;
			try {
				mCurrentSession = mClient.createSession(chat.readFrom());
				if(mCurrentSession.hasError()) return;
				boolean fromBuddy = false;
				mUICommand = new CommandChat(fromBuddy);
			} catch (RemoteException e) {
				error(e, "Message not receivable");
			}
		}
		
		@Override
		public void manageInput(String input) {
			if(input == null) return;
			if(input.equals("q!")){
				exit();
			}
		}
	}

	public class CommandChat extends ACommand{

		protected boolean mFromBuddy;

		public CommandChat(boolean fromBuddy) {
			mFromBuddy = fromBuddy;
		}

		@Override
		public void onMessageReceive(String data) {
			try {
				JChatRO chat = JChat.create(data);
				if(chat.hasError()){
					System.out.println(chat.errorMessage());
					return;
				}
				JUserRO user = chat.readFrom();
				mCurrentSession = mClient.createSession(user);
				System.out.println(buildChatMenu(mCurrentSession));
			} catch (RemoteException e) {
				error(e, "Receiving message failed. Failed to call RMI-Method");
			}
		}

		@Override
		public void manageInput(String input) {
			super.manageInput(input);
			if(input.equals("q!")){
				return;
			} else if(input.equals("3!")){
				mUICommand = mFromBuddy ? new CommandBuddy() : new CommandUser();
			} else{
				try {
					JChatRO chat = mClient.chat(mCurrentSession.readId(), input);
					if(chat.hasError()){
						System.out.println();
						System.out.println(chat.errorMessage());
						System.out.println("Type any key to go back");
						readInput();
						mUICommand = mFromBuddy ? new CommandBuddy() : new CommandUser();
						return;
					}
					List<JChatRO> chats = mCurrentSession.readChats();
					chats.add(chat);
					mCurrentSession.putChats(chats);
				} catch (RemoteException e) {
					error(e, "");
				}
			}
		}

		@Override
		public void buildUI() {
			System.out.println(buildChatMenu(mCurrentSession));
		}
		
	}
	
	public class CommandBuddy extends ACommand{

		@Override
		public void manageInput(String input) {
			super.manageInput(input);
			if(input.equals("1")){
				System.out.print("Select buddy with name or id: ");
				String str = readInput();	
				JUserRO buddy = findBuddy(str);
				if(buddy.hasError()){
					System.out.println(buddy.errorMessage());
					readInput();
					return;
				}
				try {
					mCurrentSession = mClient.createSession(buddy);
				} catch (RemoteException e) {
					error(e, "Cannot create Session");
					return;
				}
				boolean fromBuddy = true;
				mUICommand = new CommandChat(fromBuddy);
			} else if(input.equals("2")){
				System.out.print("Select buddy with name or id: ");
				String str = readInput();	
				JUserRO user = findUser(str);
				if(user.hasError()){
					System.out.println(user.errorMessage());
					return;
				}
				try {
					mClient.removeBuddy(user);
				} catch (RemoteException e) {
					error(e, "Cannot remove buddy");
					return;
				}
			} else if(input.equals("3")){
				mUICommand = new CommandMenu();
			}
			
		}

		@Override
		public void buildUI() {
			System.out.println(buildBuddyMenu());
		}

	}

	public class CommandUser extends ACommand{

		@Override
		public void manageInput(String input) {
			super.manageInput(input);
			if(input.equals("1")){
				System.out.print("Select user with name or id: ");
				String str = readInput();	
				JUserRO user = findUser(str);
				if(user.hasError()){
					System.out.println(user.errorMessage());
					return;
				}
				try {
					mCurrentSession = mClient.createSession(user);
				} catch (RemoteException e) {
					error(e, "Cannot create Session");
					return;
				}
				boolean fromBuddy = false;
				mUICommand = new CommandChat(fromBuddy);
			} else if(input.equals("2")){
				System.out.print("Select user with name or id: ");
				String str = readInput();	
				JUserRO user = findUser(str);
				if(user.hasError()){
					System.out.println(user.errorMessage());
					readInput();
					return;
				}
				try {
					mClient.addBuddy(user);
				} catch (RemoteException e) {
					error(e, "Cannot add buddy");
					return;
				}
			} else if(input.equals("3")){
				mUICommand = new CommandMenu();
			}
		}

		@Override
		public void buildUI() {
			System.out.println(buildUserMenu());
		}
		
	}

	public class CommandMenu extends ACommand{

		@Override
		public void manageInput(String input) {
			super.manageInput(input);
			if(input.equals("1")){
				mUICommand = new CommandUser();
			} else if(input.equals("2")){
				mUICommand = new CommandBuddy();
			}
		}

		@Override
		public void buildUI() {
			System.out.println(buildMenu());
		}
		
		
	}
	

}
