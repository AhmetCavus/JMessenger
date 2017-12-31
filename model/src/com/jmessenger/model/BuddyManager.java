package com.jmessenger.model;

import org.json.JSONException;
import org.json.JSONObject;

public class BuddyManager {

	JSONObject mBuddyMap = new JSONObject();
	
	public void addBuddy(long id, JUserRO buddy){
		JSONObject buddyList = mBuddyMap.optJSONObject(id + "");
		if(buddyList == null) buddyList = new JSONObject();
		try {
			buddyList.put(buddy.readID() + "", buddy.toString());
			mBuddyMap.put(id + "", buddyList);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public void removeBuddy(long id, JUserRO buddy){
		JSONObject buddyList = mBuddyMap.optJSONObject(id + "");
		if(buddyList == null) return;
		buddyList.remove(buddy.readID() + "");
	}
	
	public JSONObject getBuddy(long id){
		JSONObject buddyList = mBuddyMap.optJSONObject(id + "");
		if(buddyList == null){
			buddyList = new JSONObject();
			try {
				mBuddyMap.put(id + "", buddyList);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return buddyList;
	}
	
}
