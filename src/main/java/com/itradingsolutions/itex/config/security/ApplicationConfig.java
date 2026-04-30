package com.itradingsolutions.itex.config.security;

import com.itradingsolutions.itex.api.admin.user.services.IUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {

    private final IUserService userService;

   @Bean
   public UserDetailsService userDetailsService() {
       return userService::findDetailByUsername;
   }
}
