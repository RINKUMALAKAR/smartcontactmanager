package com.smart.service;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.smart.dao.UserRepository;
import com.smart.entities.User;

@Service
public class UserService {
	
	@Autowired
   private UserRepository userRepo;
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;
	
	public boolean authenticate(String email , String password) {
		User user1 =new User();
		User user =userRepo.getUserByUsername(email);
		return user !=null && passwordEncoder.matches(password,user1.getPassword());
	}
	
}
