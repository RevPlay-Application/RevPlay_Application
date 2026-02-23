package com.revature.revplay.customexceptions;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ModelAndView handleNotFound(ResourceNotFoundException ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("message", ex.getMessage());
        mav.addObject("status", 404);
        return mav;
    }

    @ExceptionHandler(InvalidFileException.class)
    public ModelAndView handleInvalidFile(InvalidFileException ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("message", ex.getMessage());
        mav.addObject("status", 400);
        return mav;
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ModelAndView handleUnauthorized(UnauthorizedException ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("message", ex.getMessage());
        mav.addObject("status", 401);
        return mav;
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public String handleUserConflict(UserAlreadyExistsException ex, Model model) {
        model.addAttribute("error", ex.getMessage());
        return "register";
    }

    @ExceptionHandler(Exception.class)
    public ModelAndView handleGeneral(Exception ex) {
        ModelAndView mav = new ModelAndView("error");
        mav.addObject("message", "An unexpected error occurred: " + ex.getMessage());
        mav.addObject("status", 500);
        return mav;
    }
}
