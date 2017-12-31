package com.jmessenger.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import framework.time.DateTime;

public class JMessengerPacket extends JSONObject implements IStatus{

	enum JStatus{
		Status,
		Error,
		ErrorMessage
	}
	
	public JMessengerPacket(String json) throws JSONException{
		super(json);
	}
	
	public JMessengerPacket(){}
	
	@Override
	public String status() {
		String status = readString(JStatus.Status);
		return status;
	}

	@Override
	public String errorMessage() {
		String errorMessage = readString(JStatus.ErrorMessage);
		return errorMessage;
	}

	@Override
	public boolean hasError() {
		boolean isError = readBoolean(JStatus.Error);
		return isError;
	}

	@Override
	public void setStatus(String status) {
		putString(JStatus.Status, status);
		
	}

	@Override
	public void setError(boolean isError) {
		putBoolean(JStatus.Error, isError);
	}

	@Override
	public void setErrorMessage(String errorMessage) {
		putString(JStatus.ErrorMessage,errorMessage);
	}

	
	protected void putString(Enum<?> key,String val){
		try {
			put(key.name(),val);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	protected String readString(Enum<?> key){
		return optString(key.name());
	}
	
	protected void putDateTime(Enum<?> key,DateTime val){
		putLong(key,val.timeInMillis());
	}
	
	protected DateTime readDateTime(Enum<?> key){
		long millis = readLong(key);
		DateTime date = DateTime.fromMillis(millis);
		return date;
	}
	
	protected void putLong(Enum<?> key,long val){
		try {
			put(key.name(),val);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	protected long readLong(Enum<?> key){
		return optLong(key.name());
	}
	
	protected void putInt(Enum<?> key,int val){
		try {
			put(key.name(),val);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	protected int readInt(Enum<?> key){
		return optInt(key.name());
	}
	
	protected void putBoolean(Enum<?> key,boolean val){
		try {
			put(key.name(),val);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}
	
	protected boolean readBoolean(Enum<?> key){
		return optBoolean(key.name());
	}
	
	protected void putJSON(Enum<?> key, JSONObject json){
		try {
			put(key.name(),json);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected JSONObject readJSON(Enum<?> key){
		JSONObject json = optJSONObject(key.name());
		return json;
	}
	
	protected void putJSONArray(Enum<?> key, JSONArray jarr){
		try {
			put(key.name(),jarr);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	protected JSONArray readJSONArray(Enum<?> key){
		JSONArray jarr = optJSONArray(key.name());
		return jarr;
	}
	
}
