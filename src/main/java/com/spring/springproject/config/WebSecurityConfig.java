package com.spring.springproject.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Set;
//сделать проверку при импорте из json
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final UserDetailsService userDetailsService;

    @Autowired
    public WebSecurityConfig(UserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    protected void configure(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
                .csrf().disable()
                .authorizeRequests()
                .antMatchers ("/registration", "/login", "/registration/confirm").permitAll()
                .antMatchers( "/auth/**", "/tech/**", "/category/**", "customer/**").permitAll()
                .antMatchers("/admin/**").hasAuthority("ADMIN")
                .antMatchers("/customer/**").hasAuthority("USER")
                .anyRequest().authenticated()
                .and()
                .formLogin()
                .loginPage("/login")
                .failureUrl("/login?error=true")
                .successHandler((request, response, authentication) -> {
                    Set<String> roles = AuthorityUtils.authorityListToSet(authentication.getAuthorities());
                    if (roles.contains("ADMIN")) {
                        response.sendRedirect("/admin/home");
                    } else {
                        response.sendRedirect("/customer/home");
                    }
                })
                .permitAll()
                .and()
                .logout()
                .permitAll()
                .logoutSuccessUrl("/login");
    }

    @Autowired
    protected void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService).passwordEncoder(bCryptPasswordEncoder());
    }
}
