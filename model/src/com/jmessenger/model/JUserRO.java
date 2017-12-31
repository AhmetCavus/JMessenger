package com.jmessenger.model;

import framework.time.DateTime;
import framework.time.TimeSpan;

public interface JUserRO extends IStatus{

	TimeSpan readTimeLoggedIn();
	DateTime readCreated();
	String readName();
	long readID();
	String readHost();
	int readPort();
	
}
