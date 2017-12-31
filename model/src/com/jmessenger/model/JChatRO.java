package com.jmessenger.model;

import framework.time.DateTime;

public interface JChatRO extends IStatus{
	String readMessage();
	JUser readFrom();
	DateTime readSend();
	long readSessionId();
}
