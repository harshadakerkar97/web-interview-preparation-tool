package com.interviewprep.web.config;

import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.io.IOException;

/**
 * Blocks access to /api/* (except /api/auth/*) unless the session is authenticated.
 * Static files (index.html, CSS, JS) are always accessible so the login page loads.
 */
@Component
@Order(1)
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;

        String path = req.getRequestURI();

        // Allow: static files, auth endpoints, root
        if (!path.startsWith("/api/") || path.startsWith("/api/auth/")) {
            chain.doFilter(request, response);
            return;
        }

        // Check session
        HttpSession session = req.getSession(false);
        Boolean authenticated = session != null ? (Boolean) session.getAttribute("authenticated") : null;

        if (authenticated != null && authenticated) {
            chain.doFilter(request, response);
        } else {
            resp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            resp.setContentType("application/json");
            resp.getWriter().write("{\"error\":\"unauthorized\",\"message\":\"Please enter the secret to access\"}");
        }
    }
}
