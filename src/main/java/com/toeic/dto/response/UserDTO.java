package com.toeic.dto.response;

import lombok.Data;

@Data
public class UserDTO {

	private long id;
	private String fullname;
	private String email;
	private String role;
	private boolean activated;
	
}
