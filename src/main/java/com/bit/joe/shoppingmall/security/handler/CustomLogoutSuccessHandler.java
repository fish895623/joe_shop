package com.bit.joe.shoppingmall.security.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class CustomLogoutSuccessHandler implements LogoutSuccessHandler {

    private static final Logger logger = LoggerFactory.getLogger(CustomLogoutSuccessHandler.class);

    @Override
    public void onLogoutSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication) {
        if (authentication != null) {
            String username = authentication.getName();
            logger.info("User {} has logged out successfully.", username);
            System.out.println(
                    "Logout success: "
                            + username); // This should print to the console for debugging
        } else {
            logger.info("Logout success but no authenticated user was found.");
            System.out.println("Logout success but no authenticated user was found.");
        }

        try {
            response.sendRedirect("/user/login?logout");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
