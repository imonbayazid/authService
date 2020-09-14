package com.authservice.project.service;

import java.util.ArrayList;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.authservice.project.model.DBuser;
import com.authservice.project.repository.UserRepository;

@Service
public class JwtUserDetailsService implements UserDetailsService{

	@Autowired
	private UserRepository userRepository; 
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
	    DBuser user = userRepository.findByUsername(username);
	    if(user == null) {
	    	throw new UsernameNotFoundException("User "+username+" is not found");
	    }
		return new User(user.getUserName(), user.getPassword(), new ArrayList<>()); 
		/*if ("javainuse".equals(username)) {
			return new User("javainuse", "$2a$10$slYQmyNdGzTn7ZLBXBChFOC9f6kFjAqPhccnP6DxlWXx2lPk1C3G6",
					new ArrayList<>());
		} else {
			throw new UsernameNotFoundException("User not found with username: " + username);
		} */
	}
	
}
