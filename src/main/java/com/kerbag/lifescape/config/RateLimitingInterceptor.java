package com.kerbag.lifescape.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;


@Component
public class RateLimitingInterceptor implements HandlerInterceptor {

    private long lastRequestTime = 0;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        long currentTime = System.currentTimeMillis();
        if (currentTime - lastRequestTime < 1000) { // Limiting requests to one per second for example
            response.setStatus(429); // Manually setting status code to 429
            return false;
        }
        lastRequestTime = currentTime;
        return true;
    }

}
