package com.devjaewoo.springoauth2test.global.security.config;

import com.devjaewoo.springoauth2test.domain.client.Role;
import com.devjaewoo.springoauth2test.global.security.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

@Configuration
@RequiredArgsConstructor
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    private final CustomOAuth2UserService customOAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {

        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/login/**").permitAll()
                .antMatchers("/api/**").hasAnyRole(Role.GUEST.name()).anyRequest().authenticated()
                .and().oauth2Login().userInfoEndpoint().userService(customOAuth2UserService);
    }
}
