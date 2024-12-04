package com.smart.configure;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
@EnableWebSecurity
@Configuration
public class MyConfig  {
	@Bean
	public UserDetailsService getUserDetailService() {
		return new UserDetailServiceImpl();
	}
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public DaoAuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider provider= new DaoAuthenticationProvider();
		provider.setUserDetailsService(this.getUserDetailService());
		provider.setPasswordEncoder(this.passwordEncoder());
		return provider;
	}
	@Bean
    public AuthenticationManager authenticationManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder auth = http.getSharedObject(AuthenticationManagerBuilder.class);
        auth.authenticationProvider(authenticationProvider());
        return auth.build();
    }
	
	
	@Bean
	public SecurityFilterChain sfc(HttpSecurity http) throws Exception{
		
		http
        .csrf(csrf -> csrf.disable())  // Disable CSRF for simplicity, enable in production
        .authorizeHttpRequests(authorize -> authorize
            .requestMatchers("/admin/**").hasRole("ADMIN")   // Restrict access to ADMIN
            .requestMatchers("/user/**").hasRole("USER")     // Restrict access to USER
            .requestMatchers("/**").permitAll()              // Allow access to all other public endpoints
        )
        .formLogin(form -> form
            .loginPage("/signin")                            // Custom login page
            .loginProcessingUrl("/signin")                   // URL where form POSTs for login
            .defaultSuccessUrl("/user/index", true)      // Redirect to dashboard upon success
            .failureUrl("/signin?error=true")                // Redirect to login with error on failure
            .permitAll()                                     // Allow everyone to see login page
        )
        .logout(logout -> logout
            .logoutUrl("/logout")                            // Define logout URL
            .logoutSuccessUrl("/signin?logout=true")         // Redirect to login page after logout
            .permitAll()                                     // Allow everyone to log out
        )
        .authorizeHttpRequests(authorize -> authorize
            .anyRequest().authenticated()                    // Authenticate all other requests
        );

    return http.build();

 
	}

}
