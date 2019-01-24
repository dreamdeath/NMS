package com.amuzlab.nms.web.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.util.UrlPathHelper;

import com.amuzlab.nms.web.domain.UserVo;

public class AuthenticationFilter {

 public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
//        HttpServletRequest httpRequest = asHttp(request);
//        HttpServletResponse httpResponse = asHttp(response);
//
//        String username = httpRequest.getHeader("X-Auth-Username");
//        String password = httpRequest.getHeader("X-Auth-Password");
//        String token = httpRequest.getHeader("X-Auth-Token");
//
//        String resourcePath = new UrlPathHelper().getPathWithinApplication(httpRequest);
//
//        try {
//
//            if (postToAuthenticate(httpRequest, resourcePath)) {            
//                processUsernamePasswordAuthentication(httpResponse, username, password);
//                return;
//            }
//
//            if(token != null){
//                processTokenAuthentication(token);
//            }
//            chain.doFilter(request, response);
//        } catch (InternalAuthenticationServiceException internalAuthenticationServiceException) {
//            SecurityContextHolder.clearContext();
//            httpResponse.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//        } catch (AuthenticationException authenticationException) {
//            SecurityContextHolder.clearContext();
//            httpResponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, authenticationException.getMessage());
//        } finally {
//        }
//    }
//
//     private HttpServletRequest asHttp(ServletRequest request) {
//            return (HttpServletRequest) request;
//        }
//
//        private HttpServletResponse asHttp(ServletResponse response) {
//            return (HttpServletResponse) response;
//        }
//
//        private boolean postToAuthenticate(HttpServletRequest httpRequest, String resourcePath) {
//            return Constant.AUTHENTICATE_URL.equalsIgnoreCase(resourcePath) && httpRequest.getMethod().equals("POST");
//        }
//
//        private void processUsernamePasswordAuthentication(HttpServletResponse httpResponse,String username, String password) throws IOException {
//            Authentication resultOfAuthentication = tryToAuthenticateWithUsernameAndPassword(username, password);
//            SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
//            httpResponse.setStatus(HttpServletResponse.SC_OK);
//            httpResponse.addHeader("Content-Type", "application/json");
//            httpResponse.addHeader("X-Auth-Token", resultOfAuthentication.getDetails().toString());
//        }
//
//        private Authentication tryToAuthenticateWithUsernameAndPassword(String username,String password) {
//            UsernamePasswordAuthenticationToken requestAuthentication = new UsernamePasswordAuthenticationToken(username, password);
//            return tryToAuthenticate(requestAuthentication);
//        }
//
//        private void processTokenAuthentication(String token) {
//            Authentication resultOfAuthentication = tryToAuthenticateWithToken(token);
//            SecurityContextHolder.getContext().setAuthentication(resultOfAuthentication);
//        }
//
//        private Authentication tryToAuthenticateWithToken(String token) {
//            PreAuthenticatedAuthenticationToken requestAuthentication = new PreAuthenticatedAuthenticationToken(token, null);
//            return tryToAuthenticate(requestAuthentication);
//        }
//
//        private Authentication tryToAuthenticate(Authentication requestAuthentication) {
//            Authentication responseAuthentication = authenticationManager.authenticate(requestAuthentication);
//            if (responseAuthentication == null || !responseAuthentication.isAuthenticated()) {
//                throw new InternalAuthenticationServiceException("Unable to authenticate Domain User for provided credentials");
//            }
//            return responseAuthentication;
//        }

 }
}
