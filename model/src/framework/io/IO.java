package framework.io;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import org.json.JSONException;
import org.json.JSONObject;

public final class IO {

	private static String _basePath = "";
	private static JSONObject _jsonError;
	
	public static <T extends Serializable> boolean serializeObject(final T o, final String filename) {

		try {
//			createDirectory(directory);
			String path = _basePath + File.separator + filename;
			FileOutputStream fout = new FileOutputStream(path);
			ObjectOutputStream out = new ObjectOutputStream(fout);
			out.writeObject(o);
			fout.close();
			out.close();
			System.out.println("serialization successfull -> " + o.toString());
			return true;
		} // try
		catch (IOException err) {
			System.out.println("serialization failed -> " + err.getMessage());
		}
		catch(NullPointerException err) { 
			System.out.println("serialization failed -> " + err.getMessage());
		}
		return false;
	} // serializeObject

	public static Object deserializeObject(final String filename) {

		try {
//			createDirectory(directory);
			String path = _basePath + File.separator + filename;
			FileInputStream fin = new FileInputStream(path);
			ObjectInputStream in = new ObjectInputStream(fin);
			Object o = in.readObject();
			fin.close();
			in.close();
			System.out.println("deserialization successfull -> " + o.toString());
			return o;
			
		} // try
		catch (IOException err) { 
			System.out.println("deserialization failed IOException -> " + err.getMessage());
		} 
		catch (ClassNotFoundException err) { 
			System.out.println("deserialization failed ClassNotFoundException -> " + err.getMessage());
		}
		catch(NullPointerException err) { 
			System.out.println("deserialization failed NullPointerException -> " + err.getMessage());
		}
		return null;
	} // deserializeObject

	public static JSONObject deserializeJSON(final String filename){
		try {
//			createDirectory(directory);
			String path = _basePath + File.separator + filename;
			FileInputStream fin = new FileInputStream(path);
			ObjectInputStream in = new ObjectInputStream(fin);
			String jsonStr = (String) in.readObject();
			try {
				JSONObject json = new JSONObject(jsonStr);
				System.out.println("deserialization successfull -> " + json.toString());
				return json;
			} catch (JSONException e) {
				e.printStackTrace();
			} finally{
				fin.close();
				in.close();
			}
		} // try
		catch (IOException err) { 
			System.out.println("deserialization failed IOException -> " + err.getMessage());
		} 
		catch (ClassNotFoundException err) { 
			System.out.println("deserialization failed ClassNotFoundException -> " + err.getMessage());
		}
		catch(NullPointerException err) { 
			System.out.println("deserialization failed NullPointerException -> " + err.getMessage());
		}
		if(_jsonError == null){
			_jsonError = new JSONObject();
			try {
				_jsonError.put("error", true);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		return _jsonError;
	}
	
	public static boolean delete(String filename){
		String path = _basePath + File.separator + filename;
		File f = new File(path);
		return f.delete();
	}
	
	public static void strWrite(String filename, String data){
		if(filename == null || filename.isEmpty() || data == null) return;
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(filename);
			out.write(data.getBytes());
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				out.close();
			} catch (IOException e) {
				e.printStackTrace();
				out = null;
			}
		}
	}
	
	public static String strRead(String filename){
		if(filename == null || filename.isEmpty()) return "";
		FileInputStream in = null;
		try {
			in = new FileInputStream(filename);
			byte [] buffer = new byte[in.available()];
			in.read(buffer);
			String res = new String(buffer);
			return res;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				in.close();
			} catch (IOException e) {
				e.printStackTrace();
				in = null;
			}
		}
		return "";
	}
	
	public static String readInput(InputStream in){
		if(in == null) return "";
		BufferedInputStream bin = null;
		try {
			byte [] buffer = new byte[in.available()];
			bin = new BufferedInputStream(in);
			bin.read(buffer);
			return new String(buffer);
		} catch (IOException e) {
			e.printStackTrace();
			return "";
		} finally{
//			try {
//				bin.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
		}
		
	}
	
	public static void updateBasePath(final String basepath){
		synchronized (_basePath) {
			if(basepath != null)
				_basePath = basepath;
		}
	} // updateBasePath
	
} // class