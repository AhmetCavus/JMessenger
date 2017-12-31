package com.jmessenger.remote;

import java.net.MalformedURLException;
import java.rmi.Naming;
import java.rmi.NotBoundException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;


public class RmiUtil {
	
	public static void launchService(String host, int port, Remote rmiObject) throws RemoteException, MalformedURLException{
		
		LocateRegistry.createRegistry(port);
		
//		RmiObjImpl rmi = new RmiObjImpl();

		try{
			//		Registry registry = LocateRegistry.getRegistry();
			Remote stub = UnicastRemoteObject.exportObject( rmiObject, 0 );
			
			StringBuilder adress =
					new StringBuilder("rmi://")
			.append(host)
			.append(":")
			.append(port)
			.append("/")
			.append(rmiObject.getClass().getName());
			
			Naming.rebind(adress.toString(), stub);
			System.out.println("RMI: host - " + host + ", port - " + port);
			System.out.println("RMI: Ready for requests...");
		} catch(ClassCastException err) { 
			System.out.println("A ClassCastException in RmiUtil launchService occured " + err);
		}
	}
	
	public static Remote bindClient(String host, int port, String stubName) throws RemoteException, NotBoundException{
		
		Registry registry = LocateRegistry.getRegistry(host, port);
		Remote stub = registry.lookup(stubName);
		
		return stub;
	}
	
}
