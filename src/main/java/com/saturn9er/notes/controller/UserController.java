package com.saturn9er.notes.controller;

import com.saturn9er.notes.model.User;
import com.saturn9er.notes.service.BaseSecurityService;
import com.saturn9er.notes.service.BaseUserService;
import com.saturn9er.notes.validator.UserValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import javax.validation.Valid;

@Controller
public class UserController {

    private BaseUserService userService;
    private BaseSecurityService securityService;
    private UserValidator userValidator;

    @Autowired
    public UserController(BaseUserService userService, BaseSecurityService securityService, UserValidator userValidator) {
        this.userService = userService;
        this.securityService = securityService;
        this.userValidator = userValidator;
    }

    @GetMapping("/registration")
    public String registration(Model model) {
        if (isAuthenticated()) return "redirect:/note";

        model.addAttribute("userForm", new User());

        return "registration";
    }

    @PostMapping("/registration")
    public String registration(@Valid @ModelAttribute("userForm") User userForm, BindingResult bindingResult) {
        if (isAuthenticated()) return "redirect:/note";

        userValidator.validate(userForm, bindingResult);

        if (bindingResult.hasErrors()) {
            return "registration";
        }

        userService.save(userForm);

        securityService.autoLogin(userForm.getUsername(), userForm.getPasswordConfirm());

        return "redirect:/note";
    }

    @GetMapping("/login")
    public String login(Model model, String error, String logout) {
        if (isAuthenticated()) return "redirect:/note";

        if (error != null)
            model.addAttribute("error", "Your username and password is invalid.");

        if (logout != null)
            model.addAttribute("message", "You have been logged out successfully.");

        return "login";
    }

    @GetMapping("/")
    public String welcome(Model model) {
        return "redirect:/note";
    }

    private boolean isAuthenticated() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        return !(auth instanceof AnonymousAuthenticationToken);
    }
}