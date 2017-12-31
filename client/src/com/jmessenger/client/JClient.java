package com.jmessenger.client;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

import javax.swing.plaf.basic.BasicScrollPaneUI.HSBChangeListener;

import com.jmessenger.listener.MessageReceiveListener;
import com.jmessenger.tools.Tools;

import framework.io.IO;

public class JClient{
	
	protected final int SPORT;
	protected final String SHOST;
	protected boolean mInterrupt;
	protected Socket mSocket = null;
	protected InputStream mIn = null;
	protected OutputStream mOut = null;
	protected MessageReceiveListener mListener = null;
	
	public JClient(final String host, final int port){
		SHOST = host;
		SPORT = port;
	}
	
	public static void main(String[] args) {
		String hostAddress;
		try {
			hostAddress = java.net.Inet4Address.getLocalHost().getHostAddress();
			JClient client = new JClient(hostAddress, 6565);
			if(!client.connect()) return;
			client.listen();
			client.send("Test");
//			client.sendChat("Test 2");
//			client.sendChat("Test 3");
//			client.stopListening();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean connect(){
		try{
			// Build connection
			mSocket = new Socket(SHOST, SPORT);
		} catch(IOException err){
			try {
				mSocket = new Socket(SHOST, SPORT);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			
		}
//		System.out.println("Socket Client: host -  " + mSocket.getInetAddress().getHostAddress() + ", port - " + mSocket.getLocalPort());
		return true;
	}
	
	public void send(String chat){
		try{
			mOut.write(chat.getBytes());
			mOut.flush();
		} catch(IOException err){
			System.out.println("Socket Client: Error - " + err);
			// ignore
		}
	}
	
	public void listen() throws IOException{
		// Access to the streams
		mIn = new BufferedInputStream(mSocket.getInputStream());
		mOut = new BufferedOutputStream(mSocket.getOutputStream());
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				try{
					// Listen to the server
					while(!mInterrupt || !Thread.currentThread().isInterrupted()){
//						System.out.println("Socket Client: Waiting for response..." );
						while(mIn != null && mIn.available() <= 0) Tools.safeSleep(11);
						if(mInterrupt) return;
						String data = IO.readInput(mIn);
//						System.out.println("Socket Client: Message from server " + data);
						if(mListener != null){
							mListener.onMessageReceive(data);
//							System.out.println("Incoming message...");
						}
						Tools.safeSleep(101);
					}
				} catch(IOException err){
					System.out.println("Socket Client: Error - " + err);
					// ignore
				} finally{
					Tools.safeClose(mIn);
					Tools.safeClose(mOut);
					Tools.safeClose(mSocket);
				}
			}
		}).start();
	}
	
	public void stopListening(){
		if(mSocket == null) return;
		synchronized (mSocket) {
			mInterrupt = true;
		}
	}
	
	public int port(){
		return mSocket.getLocalPort();
	}
	
	public String host(){
		return mSocket.getInetAddress().getHostAddress();
	}
	
	public void close(){
		stopListening();
		mListener = null;
		mIn = null;
		mOut = null;
		mSocket = null;
	}

	public void setMessageListener(MessageReceiveListener listener){
		mListener = listener;
	}
	
}
