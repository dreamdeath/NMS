package com.amuzlab.nms.web;

import org.apache.shiro.session.InvalidSessionException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.IOException;

public class AjaxAwareAuthenticationEntryPoint 
             extends LoginUrlAuthenticationEntryPoint {
	

    public AjaxAwareAuthenticationEntryPoint(String loginUrl) {
        super(loginUrl);
    }

    @Override
    public void commence(
        HttpServletRequest request, 
        HttpServletResponse response, 
        AuthenticationException authException) 
            throws IOException, ServletException {

    	if(isAjaxRequest(request)) {
            try {
            	response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            	//super.commence(request, response, authException);
            } catch (AccessDeniedException e) {
            	System.out.println("AccessDeniedException");
            	response.sendError(HttpServletResponse.SC_FORBIDDEN);
            } catch (AuthenticationException e) {
            	System.out.println("AuthenticationException");
            	response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            } catch (InvalidSessionException e) {
            	System.out.println("InvalidSessionException");
            	response.sendError(HttpServletResponse.SC_UNAUTHORIZED);
            }
	    } else {
	    	super.commence(request, response, authException);
	    }

    }
    
    private boolean isAjaxRequest(HttpServletRequest req) {
//		return req.getHeader(ajaxHeader) != null && req.getHeader(ajaxHeader).equals(Boolean.TRUE.toString());
		String header = req.getHeader("x-requested-with");
	    if(header != null && header.equals("XMLHttpRequest"))
	        return true;
	    else
	        return false;
		
		
	}
}