package com.interviewprep.web.controller;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Simple secret-based access gate.
 * Set the secret via environment variable APP_SECRET (default: "prep2026").
 * On Render, set it in Environment Variables so it's not in the code.
 */
@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Value("${app.secret:prep2026}")
    private String appSecret;

    @PostMapping("/login")
    public Map<String, Object> login(@RequestBody Map<String, String> body, HttpSession session) {
        String provided = body.getOrDefault("secret", "");
        if (appSecret.equals(provided)) {
            session.setAttribute("authenticated", true);
            return Map.of("success", true);
        }
        return Map.of("success", false, "message", "Wrong secret");
    }

    @GetMapping("/check")
    public Map<String, Object> check(HttpSession session) {
        Boolean auth = (Boolean) session.getAttribute("authenticated");
        return Map.of("authenticated", auth != null && auth);
    }

    @PostMapping("/logout")
    public Map<String, Object> logout(HttpSession session) {
        session.invalidate();
        return Map.of("success", true);
    }
}
