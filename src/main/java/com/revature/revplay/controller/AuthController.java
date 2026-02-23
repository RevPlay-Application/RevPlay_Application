package com.revature.revplay.controller;

import com.revature.revplay.model.Artist;
import com.revature.revplay.model.Role;
import com.revature.revplay.model.User;
import com.revature.revplay.service.ArtistService;
import com.revature.revplay.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AuthController {

    private final UserService userService;
    private final ArtistService artistService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {
        model.addAttribute("user", new User());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(@Valid @ModelAttribute("user") User user,
            BindingResult result, Model model) {
        if (result.hasErrors()) {
            return "register";
        }

        if (userService.existsByUsername(user.getUsername())) {
            model.addAttribute("error", "Username is already taken");
            return "register";
        }

        if (userService.existsByEmail(user.getEmail())) {
            model.addAttribute("error", "Email is already registered");
            return "register";
        }

        // Default role is USER if not specified
        if (user.getRole() == null) {
            user.setRole(Role.USER);
        }

        User savedUser = userService.registerUser(user);

        if (savedUser.getRole() == Role.ARTIST) {
            Artist artist = new Artist();
            artist.setUser(savedUser);
            // Use display name or username as artist name
            String artistName = (savedUser.getDisplayName() != null && !savedUser.getDisplayName().isEmpty())
                    ? savedUser.getDisplayName()
                    : savedUser.getUsername();
            artist.setArtistName(artistName);
            artistService.registerArtist(artist);
        }

        return "redirect:/login?success=true";
    }
}
