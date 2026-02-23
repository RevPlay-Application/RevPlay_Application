package com.revature.revplay.customexceptions;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(UserNotFoundException.class)
    public ModelAndView handleUserNotFound(UserNotFoundException ex, Model model) {
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(ArtistNotFoundException.class)
    public ModelAndView handleArtistNotFound(ArtistNotFoundException ex, Model model) {
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(SongNotFoundException.class)
    public ModelAndView handleSongNotFound(SongNotFoundException ex, Model model) {
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(AlbumNotFoundException.class)
    public ModelAndView handleAlbumNotFound(AlbumNotFoundException ex, Model model) {
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(PlaylistNotFoundException.class)
    public ModelAndView handlePlaylistNotFound(PlaylistNotFoundException ex, Model model) {
        ModelAndView mav = new ModelAndView("error/404");
        mav.addObject("message", ex.getMessage());
        return mav;
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneralException(Exception ex) {
        ModelAndView mav = new ModelAndView("error/500");
        mav.addObject("message", "An unexpected error occurred: " + ex.getMessage());
        return mav;
    }
}
