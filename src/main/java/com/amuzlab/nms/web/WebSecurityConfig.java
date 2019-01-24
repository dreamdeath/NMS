package com.amuzlab.nms.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.LoginUrlAuthenticationEntryPoint;

import com.amuzlab.nms.web.login.LoginService;



@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    @Autowired
    private LoginService loginService;
    
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .antMatchers("/resources/**").permitAll()
                .antMatchers("/css/**").permitAll()
                .antMatchers("/js/**").permitAll()
                .antMatchers("/images/**").permitAll()
                .antMatchers("/ems/**").permitAll()
//                .antMatchers("/**").hasRole("USER")
                .antMatchers("/*").access("hasRole('USER') or hasRole('ADMIN')") 
                .antMatchers("/admin/**").access("hasRole('ADMIN')") 
                .anyRequest().authenticated()
                .and()
            .formLogin()
                .loginPage("/login.do")
                .failureUrl("/login.do?error")
                .loginProcessingUrl("/authLogin.do")
                .defaultSuccessUrl("/index.do",true) 
                .permitAll()
                .and()
            .logout()
                .logoutUrl("/logout.do")
                .logoutSuccessUrl("/login.do?logout")
                .invalidateHttpSession(true)
                .deleteCookies("JSESSIONID")
                .permitAll();
        
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED);        
        http.exceptionHandling().authenticationEntryPoint(new AjaxAwareAuthenticationEntryPoint("/login.do"));
//        	.accessDeniedPage("/login.do?error");
        http.sessionManagement().invalidSessionUrl("/login.do?invalidsession");
        http.csrf().disable();
//        http.addFilterBefore(new AuthenticationFilter(authenticationManager()), BasicAuthenticationFilter.class);
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(loginService);
    }   
}