package com.revature.revplay.controller;

import com.revature.revplay.model.User;
import com.revature.revplay.service.UserIdentityService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class GlobalControllerAttributes {

    private final UserIdentityService userIdentityService;
    private final com.revature.revplay.repository.ArtistRepository artistRepository;
    private final com.revature.revplay.utils.Base64Util base64Util;

    public GlobalControllerAttributes(UserIdentityService userIdentityService,
                                      com.revature.revplay.repository.ArtistRepository artistRepository,
                                      com.revature.revplay.utils.Base64Util base64Util) {
        this.userIdentityService = userIdentityService;
        this.artistRepository = artistRepository;
        this.base64Util = base64Util;
    }

    @ModelAttribute("authenticatedUser")//This makes "authenticatedUser" available in all Thymeleaf views as ${authenticatedUser}
    public User getAuthenticatedUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();//Gets current logged-in user authentication object.
        if (auth != null && auth.isAuthenticated() && !auth.getPrincipal().equals("anonymousUser")) {
            return userIdentityService.findUserByUsername(auth.getName());
        }
        return null;
    }

    @ModelAttribute("profileImagePath")
    public String getProfileImagePath() {
        User user = getAuthenticatedUser();
        if (user != null && user.getProfileImage() != null) {
            return base64Util.encode(user.getProfileImage());
        }
        return "/images/music-placeholder.svg";
    }

    @ModelAttribute("artistBannerPath")
    public String getArtistBannerPath() {
        User user = getAuthenticatedUser();
        if (user != null && user.getRole() == com.revature.revplay.model.Role.ARTIST) {
            com.revature.revplay.model.Artist artist = artistRepository.findByUser(user).orElse(null);
            if (artist != null && artist.getBannerImage() != null) {
                return base64Util.encode(artist.getBannerImage());
            }
        }
        return null;
    }
}
