package com.example.config;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class InvalidSessionIdFilter implements Filter {
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;

        if (httpRequest.getRequestedSessionId() != null && !httpRequest.isRequestedSessionIdValid()) {
            // Сбрасываем невалидный sessionId
            httpRequest.getSession(true); // Создаём новую сессию
            // Логирование (опционально)
            System.out.println("Invalid session ID detected and reset.");
        }

        chain.doFilter(request, response);
    }
}