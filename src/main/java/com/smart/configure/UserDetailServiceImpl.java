package com.smart.configure;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.smart.dao.UserRepository;
import com.smart.entities.User;

public class UserDetailServiceImpl implements UserDetailsService {
    @Autowired
	private UserRepository userRepo;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		 // Fetch the user by username
        User user = userRepo.getUserByUsername(username);

        // Check if user is null and throw exception if not found
        if (user == null) {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }

        // Return a UserDetails object (e.g., a CustomUserDetails object)
        return new CustomUserDetails(user);
	}

}
