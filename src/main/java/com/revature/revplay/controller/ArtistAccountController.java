package com.revature.revplay.controller;

import com.revature.revplay.model.Artist;
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
public class ArtistAccountController {

    private final ProfileManagementService profileManagementService;

    public ArtistAccountController(ProfileManagementService profileManagementService) {
        this.profileManagementService = profileManagementService;
    }

    @GetMapping("/artist/dashboard")
    public String showArtistDashboard(@ModelAttribute("authenticatedUser") User user, Model model) {
        if (user == null)
            return "redirect:/login";
        Artist artist = profileManagementService.getArtistByUserId(user.getUserId());
        model.addAttribute("artist", artist);
        return "artist/dashboard";
    }

    @PostMapping("/artist/profile/update")
    public String updateArtistProfile(@ModelAttribute("authenticatedUser") User user,
            @RequestParam String artistName,
            @RequestParam String bio,
            @RequestParam String genre,
            @RequestParam(required = false) MultipartFile imageFile) {
        if (user == null)
            return "redirect:/login";
        profileManagementService.updateArtistProfile(user.getUserId(), artistName, bio, genre, imageFile);
        return "redirect:/artist/dashboard?updated=true";
    }
}
