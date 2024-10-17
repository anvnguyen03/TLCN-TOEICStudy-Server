package com.toeic.service;

import com.toeic.entity.User;

public interface AccountService {
	User fetchAccount(String token);
}
