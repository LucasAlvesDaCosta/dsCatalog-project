package com.lucasdev.dscatalog.services.exceptions;

public class EntityNotFoundException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public EntityNotFoundException(String msg) {
		super(msg);
	}
	
	public EntityNotFoundException(String msg, Throwable cause) {
		super(msg, cause);
	}

}
