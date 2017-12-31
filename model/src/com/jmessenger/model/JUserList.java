package com.jmessenger.model;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import framework.time.DateTime;
import framework.time.TimeSpan;

/**
 * 
 * @author Hamza
 * @version 0.1
 */
public class JUserList extends JMessengerPacket implements JUserListRO{

	enum JUserListKey{
		User
	}
	
	public JUserList(String json) throws JSONException{
		super(json);
	}
	
	public JUserList(){
	}

	@Override
	public List<JUserRO> userList() {
		JSONArray jArr = readJSONArray(JUserListKey.User);
		List<JUserRO> list = new ArrayList<JUserRO>();
		for(int index = 0; index < jArr.length(); index++){
			JSONObject json = jArr.optJSONObject(index);
			JUserRO user;
			try {
				user = new JUser(json.toString());
				list.add(user);
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return list;
	}


	
}
