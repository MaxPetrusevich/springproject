package com.example.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class CustomAuthenticationFilter extends UsernamePasswordAuthenticationFilter {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Authentication attemptAuthentication(HttpServletRequest request, HttpServletResponse response)
            throws AuthenticationException {

        if (request.getContentType() != null && request.getContentType().contains("application/json")) {
            try {
                // Читаем JSON из тела запроса
                Map<String, String> requestBody = objectMapper.readValue(request.getInputStream(), Map.class);
                String username = requestBody.get("username");
                String password = requestBody.get("password");

                UsernamePasswordAuthenticationToken authRequest =
                        new UsernamePasswordAuthenticationToken(username, password);

                // Устанавливаем детали (если нужно)
                setDetails(request, authRequest);

                return this.getAuthenticationManager().authenticate(authRequest);

            } catch (IOException e) {
                throw new RuntimeException("Failed to parse authentication request body", e);
            }
        }

        // Если тип запроса не JSON, вызываем стандартный процесс
        return super.attemptAuthentication(request, response);
    }
}