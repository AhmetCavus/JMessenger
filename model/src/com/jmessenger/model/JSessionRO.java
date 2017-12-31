package com.jmessenger.model;

import java.util.List;

import framework.time.DateTime;

public interface JSessionRO extends IStatus{
	long readId();
	JUserRO readUser1();
	JUserRO readUser2();
	DateTime readCreated();
	List<JChatRO> readChats();
	JUser readOpponent(long ownId);
	boolean isThisSession(JUserRO u1, JUserRO u2);
}
