package com.authservice.project.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.authservice.project.model.DBuser;
import com.authservice.project.model.JwtResponse;
import com.authservice.project.model.LoginRequest;
import com.authservice.project.model.SignupRequest;
import com.authservice.project.repository.UserRepository;
import com.authservice.project.security.JwtTokenUtils;
import com.authservice.project.service.JwtUserDetailsService;

@RestController
@CrossOrigin
public class AuthController {

	@Autowired
	private AuthenticationManager authManager;

	@Autowired
	private JwtTokenUtils jwtTokenUtils;

	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	PasswordEncoder passEncoder;

	@PostMapping("/sigin")
	public ResponseEntity<?> signIn(@Valid @RequestBody LoginRequest loginReq) throws Exception {
		try {
			Authentication auth = authManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginReq.getUserName(), loginReq.getPassword()));
			SecurityContextHolder.getContext().setAuthentication(auth);

		} catch (DisabledException e) {
			throw new Exception("USER_DISABLED", e);
		} catch (BadCredentialsException e) {
			throw new Exception("INVALID_CREDENTIALS", e);
		}
		final UserDetails userDetails = jwtUserDetailsService.loadUserByUsername(loginReq.getUserName());

		String jwtToken = jwtTokenUtils.generateToken(userDetails);

		return ResponseEntity.ok(new JwtResponse(jwtToken));

	}

	@PostMapping("/signup")
	public ResponseEntity<?> signUp(@Valid @RequestBody SignupRequest signupReq) throws Exception {
		if (userRepository.existsByUserName(signupReq.getUserName())) {
			return ResponseEntity.badRequest().body(new JwtResponse("Error: UserName is already taken"));
		}
		if (userRepository.existsByEmail(signupReq.getEmail())) {
			return ResponseEntity.badRequest().body(new JwtResponse("Error: Email is already in use"));
		}

		// create a new user
		DBuser user = new DBuser(signupReq.getUserName(), signupReq.getPassword(),
				passEncoder.encode(signupReq.getPassword()));
		userRepository.save(user);

		return ResponseEntity.ok(new JwtResponse("User is successfully created!!"));

	}

}
