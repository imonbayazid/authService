package com.authservice.project.model;

import java.io.Serializable;

public class JwtResponse implements Serializable {

	public static final long serialVersionUID = -8091879091924046844L;
	private final String msg;

	public JwtResponse(String msg) {
		this.msg = msg;
	}

	public String getMsg() {
		return this.msg;
	}

}
