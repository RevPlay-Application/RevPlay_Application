package com.revature.revplay.controller;

import com.revature.revplay.service.UserIdentityService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
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
            @RequestParam String displayName,
            Model model) {
        if (userIdentityService.existsByEmail(email)) {
            model.addAttribute("error", "Email already exists");
            return "register";
        }
        if (userIdentityService.existsByUsername(username)) {
            model.addAttribute("error", "Username already exists");
            return "register";
        }
        userIdentityService.registerSubscriber(email, username, password, displayName);
        return "redirect:/login?registered=true";
    }

    @PostMapping("/register/artist")
    public String registerArtist(@RequestParam String email,
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String artistName,
            @RequestParam String genre,
            Model model) {
        if (userIdentityService.existsByEmail(email)) {
            model.addAttribute("error", "Email already exists");
            return "register";
        }
        if (userIdentityService.existsByUsername(username)) {
            model.addAttribute("error", "Username already exists");
            return "register";
        }
        userIdentityService.registerArtist(email, username, password, artistName, genre);
        return "redirect:/login?registered=true";
    }
}
