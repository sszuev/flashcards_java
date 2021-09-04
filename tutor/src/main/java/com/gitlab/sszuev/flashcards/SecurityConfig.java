package com.gitlab.sszuev.flashcards;

import org.springframework.context.annotation.Bean;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Created by @ssz on 01.08.2021.
 */
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    public void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/registration").permitAll()
                .antMatchers("/webjars/**").permitAll()
                .antMatchers("/**").authenticated()

                .and().logout().logoutUrl("/logout").logoutSuccessUrl("/login").permitAll()
                .and().formLogin().loginPage("/login").defaultSuccessUrl("/").permitAll()
        ;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
