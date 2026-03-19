package com.sourashis.quizapp.modules.auth.controller;

import com.sourashis.quizapp.modules.auth.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthenticationController {

    @Autowired
    private AuthenticationService authenticationService;

    /**
     * POST /auth/login
     * User login
     */
    @PostMapping("/login")
    public String login(String username, String password) {
        // Call the authentication service to validate credentials and generate token
        return authenticationService.login(username, password);
    }

     /**
     * POST /auth/register
     * User registration
     */
}
