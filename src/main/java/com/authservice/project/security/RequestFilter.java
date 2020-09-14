package com.authservice.project.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.authservice.project.service.JwtUserDetailsService;

import io.jsonwebtoken.ExpiredJwtException;

@Component
public class RequestFilter extends OncePerRequestFilter {

	@Autowired
	private JwtUserDetailsService jwtUserDetailsService;
	@Autowired
	private JwtTokenUtils jwtTokenUtils;

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		final String requestTokenHeader = request.getHeader("Authorization");
		String userName = null;
		String jwtToken = null;
		// get the token from the request header then get the userName from the token
		if (requestTokenHeader != null && requestTokenHeader.startsWith("Bearer ")) {
			jwtToken = requestTokenHeader.substring(7);
			try {
				userName = jwtTokenUtils.getUsernameFromToken(jwtToken);

			} catch (IllegalArgumentException e) {
				System.out.println("unable to get jwt token");
			} catch (ExpiredJwtException e) {
				System.out.println("jwt token expired");
			}

		} else {
			logger.warn("jwt token is not begin with Bearer string");
		}
		
		//check the token;
		//if the token is valid then make the user as authenticated and return to the request for getting the response
		if(userName!=null && SecurityContextHolder.getContext().getAuthentication() == null) {
			UserDetails userDetails =  this.jwtUserDetailsService.loadUserByUsername(userName);
			if(jwtTokenUtils.validateToken(jwtToken, userDetails)) {
				UsernamePasswordAuthenticationToken uNamePassAuthToken= new UsernamePasswordAuthenticationToken(
						userDetails,null,userDetails.getAuthorities());
				uNamePassAuthToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				// set the user as authenticated
				SecurityContextHolder.getContext().setAuthentication(uNamePassAuthToken);			
			}
		}
		filterChain.doFilter(request, response);

	}

}
