package com.jmessenger.remote;

import java.rmi.Remote;
import java.rmi.RemoteException;
import java.util.List;

import com.jmessenger.model.JChatRO;
import com.jmessenger.model.JSessionRO;
import com.jmessenger.model.JUser;
import com.jmessenger.model.JUserRO;

/**
 * 
 * @author      Hamza
 * @version     0.1
 */
public interface RemoteChat extends Remote{
	/**
	 * Requests the buddy list from the server
	 * @return the buddy list as JSON-String
	 * @throws RemoteException
	 */
	public String buddyList(long id) throws RemoteException;

	/**
	 * 
	 * @param id
	 * @param buddy
	 * @throws RemoteException
	 */
	public void addBuddy(long id,String buddy) throws RemoteException;

	
	/**
	 * 
	 * @param id
	 * @throws RemoteException
	 */
	public void removeBuddy(long id, String buddy) throws RemoteException;
	
	/**
	 * Requests the online user
	 * @return the list of online user as JOSN-String
	 * @throws RemoteException
	 */
	public String userList() throws RemoteException;
	
	/**
	 * Creates new user with the given name. 
	 * @param host
	 * @param port
	 * @param name
	 * @return a result from the server: If a user already exists, 
	 * you get an error message in the JSON-String
	 * @throws RemoteException
	 */
	public String login(String host, int port, String name) throws RemoteException;
	
	/**
	 * 
	 * @param id
	 * @return
	 * @throws RemoteException
	 */
	public boolean logout(long id) throws RemoteException;
	
	/**
	 * 
	 * @param sessionId
	 * @param jUser
	 * @param message
	 * @return
	 * @throws RemoteException
	 */
	public String chat(long sessionId, String jUser, String message) throws RemoteException;
	
	/**
	 * 
	 * @param jUser
	 * @return
	 * @throws RemoteException
	 */
	public String createSession(String jUser, String jUser2) throws RemoteException;
	
}
