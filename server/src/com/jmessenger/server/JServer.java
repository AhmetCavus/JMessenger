package com.jmessenger.server;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import javax.net.ServerSocketFactory;

import com.jmessenger.listener.MessageReceiveListener;
import com.jmessenger.tools.Tools;

import framework.io.IO;
import framework.time.PeriodicTimer;
import framework.time.PeriodicTimer.PeriodListener;

public class JServer implements PeriodListener{
	
	protected final int PORT;
	protected Socket mNullSocket;      
	protected PeriodicTimer mTimer;
	protected ServerSocket mSSocket = null;
	protected MessageReceiveListener mListener = null;
	protected List<Socket> mSocketPool = new ArrayList<Socket>();
	
	public JServer(final int port){
		PORT = port;
		mTimer = new PeriodicTimer(this);
		int delay = 10001;
		int period = 20001;
		// To check periodically if sockets closed, start a timer 
		mTimer.start(delay, period);
	}
	
	public static void main(String[] args) {
		JServer server = new JServer(6565);
		server.launch();
	}
	
	public boolean launch(){
		try{
			// Build connection
			String hostAddress = Inet4Address.getLocalHost().getHostAddress();
			mSSocket = ServerSocketFactory.getDefault().createServerSocket(PORT);
			
			System.out.println("Socket Server: host - " + hostAddress + ", port - " + PORT);
			while(!Thread.currentThread().isInterrupted()){
				Socket socket = null;
				System.out.println("Socket Server: Ready for requests...");
				socket = mSSocket.accept();
				addSocket(socket);
				System.out.println("Socket Server: Adding new Socket. Count " + mSocketPool.size());
				listen(socket);
			}
		} catch(IOException err){
			try {
				mSSocket = ServerSocketFactory.getDefault().createServerSocket(PORT);
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			
		}
		return true;
	}
	
	public void listen(final Socket socket) throws IOException{
		if(socket == null) return;
		
		// Access to the streams
		final InputStream in = new BufferedInputStream(socket.getInputStream());
		final OutputStream out = new BufferedOutputStream(socket.getOutputStream());
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				String logKey = "Socket Server " + socket.getPort();
				try{
					// Listen to the server
					while(!Thread.currentThread().isInterrupted() || !socket.isClosed()){
						System.out.println(logKey + " : Waiting for response..." );
						while(in.available() <= 0){ 
							Tools.safeSleep(51);
							if(!socket.isClosed()) continue;
							System.out.println(logKey + " : Socket is closed");
							return;
						}
						String request = IO.readInput(socket.getInputStream());
						System.out.println(logKey + " : Request of Client -  " + request);
						sendChat(out, request);
					}
				} catch(IOException err){
					System.out.println(logKey + " : Error - " + err);
					// ignore
				} finally{
					Tools.safeClose(in);
					Tools.safeClose(out);
					Tools.safeClose(socket);
				}
			}
		}).start();
	}
	
	public boolean sendChat(String host, int port, String data){
		Socket socket = findSocket(host, port);
		if(socket == null) return false;
		OutputStream out = null;
		if(socket.isClosed()) return false;
		try {
			out = socket.getOutputStream();
			out.write(data.getBytes());
			out.flush();
			return !socket.isClosed();
		} catch (IOException e) {
			return false;
		}
		finally{
		}
	}
	
	public void sendChat(Socket socket, String data){
		if(socket == null) return;
		OutputStream out = null;
		try {
			out = socket.getOutputStream();
			out.write(data.getBytes());
			out.flush();
		} catch (IOException e) {}
		finally{
//			Tools.safeClose(bout);
		}
	}

	public void sendChat(OutputStream out, String data){
		if(out == null) return;
		try {
			out.write(data.getBytes());
			out.flush();
		} catch (IOException e) {}
		finally{
//			Tools.safeClose(bout);
		}
	}

	public Socket findSocket(String host, int port){
		for(Socket socket : mSocketPool){
			InetAddress inetAddress = socket.getInetAddress();
			String sIp = inetAddress.getHostAddress();
			int sPort = socket.getPort();
			if(sIp.equals(host) && sPort == port) return socket;
		}
		return null;
	}

	protected void addSocket(Socket socket) {
		if(socket == null) return;
		String hostAddress = socket.getInetAddress().getHostAddress();
		int port = socket.getPort();
		Socket toFind = findSocket(hostAddress, port);
		if(toFind != null) return;
		mSocketPool.add(socket);
	}
	
	protected Socket nullSocket() {
		if(mNullSocket == null) mNullSocket = new Socket();
		return mNullSocket;
	}

	public void setMessageListener(MessageReceiveListener listener){
		mListener = listener;
	}

	@Override
	public void onPeriod(PeriodicTimer timer, int periodCount, long time) {
		System.out.println("Socket Server: Checking for closed Sockets");
		for(int index = 0; index < mSocketPool.size(); index++){
			Socket socket = mSocketPool.get(index);
			if(!socket.isClosed()) continue;
			mSocketPool.remove(index);
			socket = null;
			System.out.println("Socket Server: Removing socket. Count " + mSocketPool.size());
		}
	}

	public void close(){
		Tools.safeClose(mNullSocket);
		Tools.safeClose(mSSocket);
		mTimer.stop();
		for (Socket socket : mSocketPool) Tools.safeClose(socket);
		mSocketPool.removeAll(mSocketPool);
		mSocketPool = null;
	}
}
