package com.jmessenger.model;

import java.io.Serializable;

public interface IStatus{
	String status();
	String errorMessage();
	boolean hasError();
	void setStatus(String status);
	void setError(boolean isError);
	void setErrorMessage(String errorMessage);
}
