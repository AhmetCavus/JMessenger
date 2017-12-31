package com.jmessenger.tools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

//import com.androidquery.AQuery;

public final class Tools {

	public enum Fonts{
		JOURNAL
	}
	
	public interface ToDo {
		public void toDo();
	}

	public static void CopyStream(InputStream is, OutputStream os) {
		final int buffer_size = 1024;
		try {
			byte[] bytes = new byte[buffer_size];
			for (;;) {
				int count = is.read(bytes, 0, buffer_size);
				if (count == -1)
					break;
				os.write(bytes, 0, count);
			}
		} catch (Exception ex) {
		}
	}

	public static String extractNumbers(final String input) {
		if (input == null) {
			return "";
		}
		return input.replaceAll("[^0-9]+[0-9]*[^0-9]*", "");
	}

	public static String extractLetters(final String input) {
		if (input == null) {
			return "";
		}
		return input.replaceAll("[^a-zA-Z]", "");
	}

	public static Boolean isConnection() // Pr�fen ob Verbindung da ist
	{
		try {
			InetAddress address = InetAddress.getByName("www.google.de");
			if (address.isReachable(5000))
				return true;
			else
				return false;
		} catch (IOException e) {
			return false;
		} catch (IllegalArgumentException e) {
			return false;
		}
	}

	public static void safeSleep(final TimeUnit timeUnit, final long duration) {
		safeSleep(timeUnit.toMillis(duration));
	} // safeSleep

	public static void safeSleep(final long durationInMilliSecs) {
		try {
			Thread.sleep(durationInMilliSecs);
		} catch (final InterruptedException err) {
			Thread.currentThread().interrupt();
		}
	} // safeSleep
	
	public static void safeClose(final InputStream in){
		try{
			if(in == null) return;
			in.close();
		}catch(IOException err){
			// ignore
		}
	}

	public static void safeClose(final OutputStream out){
		try{
			if(out == null) return;
			out.close();
		}catch(IOException err){
			// ignore
		}
	}

	public static void safeClose(final Socket socket){
		try{
			if(socket == null) return;
			socket.close();
		}catch(IOException err){
			// ignore
		}
	}

	public static void safeClose(final ServerSocket socket){
		try{
			if(socket == null) return;
			socket.close();
		}catch(IOException err){
			// ignore
		}
	}
	
	public static void safeClose(Scanner scanner) {
		if(scanner == null) return;
		scanner.close();
	}

	public static void proofIfNull(Object o) {
		if (o == null) {
			o = new Object();
		}
	}

	public static ScheduledFuture<?> schedule(Runnable process, int timeStart,
			int timePeriod) {
		ScheduledExecutorService se = Executors.newScheduledThreadPool(1);

		// Hier wird der Inhalt definiert, dass wiederholt ausgeführt werden
		// soll
		// Die Methode gibt eine Art Steuerung zurück, über der die Ausführung
		// abgebrochen werden kann
		final ScheduledFuture<?> control = se.scheduleAtFixedRate(process,
				timeStart, timePeriod, TimeUnit.SECONDS);
		return control;
	}

	public static String getStringValue(String value) {
		return value == null ? "" : value;
	}

	public static boolean getBooleanValue(String value) {
		return value == null ? false : Boolean.valueOf(value);
	}

	public static double getDoubleValue(String value) {
		return value == null ? 0.00 : Double.valueOf(value);
	}

	public static int getIntegerValue(String value) {
		return value == null ? 0 : Integer.valueOf(value);
	}
	
}
