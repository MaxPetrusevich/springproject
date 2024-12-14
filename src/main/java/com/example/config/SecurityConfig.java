package com.example.config;

import com.example.security.CustomAuthenticationSuccessHandler;
import com.example.service.CustomUserDetailsService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig  {

    private final CustomUserDetailsService userDetailsService;
    private final CustomAuthenticationSuccessHandler authenticationSuccessHandler;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception{
        return authConfig.getAuthenticationManager();
    }
   /* @Bean
    public AuthenticationFilter authenticationFilter(AuthenticationManager authenticationManager, AuthenticationConverter authenticationConverter) {
        AuthenticationFilter filter = new AuthenticationFilter(authenticationManager,authenticationConverter);
        filter.setSuccessHandler(authenticationSuccessHandler);
        filter.setFailureHandler((request, response, exception) -> {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.setContentType("application/json");
            response.getWriter().write("{\"message\":\"Invalid credentials\"}");
        });
        return filter;
    }*/

    @Bean
    public DaoAuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider();
        authProvider.setUserDetailsService(userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());
        return authProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, AuthenticationManager authenticationManager) throws Exception {
        CustomAuthenticationFilter customAuthenticationFilter = new CustomAuthenticationFilter();
        customAuthenticationFilter.setAuthenticationManager(authenticationManager);
        customAuthenticationFilter.setFilterProcessesUrl("/api/auth/login");
        customAuthenticationFilter.setAuthenticationSuccessHandler(authenticationSuccessHandler);// URL для обработки логина

        http
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "/home", "/error/**").permitAll()
                .antMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll()
                .antMatchers("/login", "/register", "/api/auth/**").permitAll()
                .antMatchers("/files/avatars/**", "/api/users/profile/avatar").authenticated()
                .antMatchers("/organiser/**", "/api/organisations/**", "/api/**").hasRole("ORGANIZER")
                .antMatchers("/customer/**", "/api/**").hasRole("USER")
                .antMatchers("/**").hasRole("ADMIN")
                .and()
                .sessionManagement()
                .invalidSessionUrl("/login?invalid-session") // Перенаправление при невалидной сессии
                .sessionFixation().newSession() // Создание новой сессии при логине
                .and()
                .addFilterAt(customAuthenticationFilter, UsernamePasswordAuthenticationFilter.class) // Заменяем стандартный фильтр
                .formLogin().disable() // Отключаем стандартную обработку формы логина
                .logout()
                .logoutUrl("/logout")
                .logoutSuccessUrl("/login?logout");

        return http.build();
    }


    /* @Bean
    public AuthenticationConverter authenticationConverter() {
        return new CustomAuthenticationConverter();
    }
    @Bean
    public HttpSessionListener httpSessionListener() {
        return new HttpSessionListener() {
            @Override
            public void sessionCreated(HttpSessionEvent se) {
                System.out.println("Session created: " + se.getSession().getId());
            }

            @Override
            public void sessionDestroyed(HttpSessionEvent se) {
                System.out.println("Session destroyed: " + se.getSession().getId());
            }
        };
    }*/
    @Bean
    public FilterRegistrationBean<InvalidSessionIdFilter> invalidSessionIdFilter() {
        FilterRegistrationBean<InvalidSessionIdFilter> filter = new FilterRegistrationBean<>();
        filter.setFilter(new InvalidSessionIdFilter());
        filter.addUrlPatterns("/*");
        return filter;
    }
}
