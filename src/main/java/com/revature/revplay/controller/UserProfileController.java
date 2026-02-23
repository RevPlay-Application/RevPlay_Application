package com.revature.revplay.controller;

import com.revature.revplay.model.User;
import com.revature.revplay.service.ProfileManagementService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
public class UserProfileController {

    private final ProfileManagementService profileManagementService;

    public UserProfileController(ProfileManagementService profileManagementService) {
        this.profileManagementService = profileManagementService;
    }

    @GetMapping("/profile")
    public String viewProfile(@ModelAttribute("authenticatedUser") User user, Model model) {
        if (user == null)
            return "redirect:/login";
        return "profile";
    }

    @PostMapping("/profile/update")
    public String updateProfile(@ModelAttribute("authenticatedUser") User user,
            @RequestParam String displayName,
            @RequestParam String bio,
            @RequestParam(required = false) MultipartFile imageFile) {
        if (user == null)
            return "redirect:/login";
        profileManagementService.updateUserProfile(user.getUserId(), displayName, bio, imageFile);
        return "redirect:/profile?updated=true";
    }
}
