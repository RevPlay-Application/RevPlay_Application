package com.revature.revplay.controller;

import com.revature.revplay.service.UserIdentityService;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserAuthenticationController {

    private final UserIdentityService userIdentityService;

    public UserAuthenticationController(UserIdentityService userIdentityService) {
        this.userIdentityService = userIdentityService;
    }

    @GetMapping("/login")
    public String showLoginForm() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm() {
        return "register";
    }

    @PostMapping("/register/subscriber")
    public String registerSubscriber(@RequestParam String email,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String displayName) {
        userIdentityService.registerSubscriber(email, username, password, displayName);
        return "redirect:/login?registered=true";
    }

    @PostMapping("/register/artist")
    public String registerArtist(@RequestParam String email,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String artistName,
            @RequestParam String genre) {
        userIdentityService.registerArtist(email, username, password, artistName, genre);
        return "redirect:/login?registered=true";
    }
}
