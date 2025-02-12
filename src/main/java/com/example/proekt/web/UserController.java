package com.example.proekt.web;

import com.example.proekt.model.Role;
import com.example.proekt.model.User;
import com.example.proekt.model.exceptions.InvalidUserCredentialsException;
import com.example.proekt.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/register")
    public String showRegistrationForm(Model model) {

        model.addAttribute("bodyContent","register");
        return "registration";
    }

    @PostMapping("/register")
    public String registerUser(@RequestParam String username,
                               @RequestParam String password,
                               @RequestParam String repeatPassword,
                               @RequestParam Role role,
                               Model model) {
        try {
            userService.register(username, password, repeatPassword, role);
            return "redirect:/login";
        } catch (Exception e) {
            model.addAttribute("errorMessage", e.getMessage());
            return "registration";
        }
    }
    @GetMapping("/login")
    public String getLoginPage(Model model) {
        model.addAttribute("bodyContent","login");
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        HttpServletRequest request,
                        Model model) {
        User user = null;
        try {
            user = this.userService.findByUsernameAndPasword(username,password);
            request.getSession().setAttribute("user", user); // Session management
            return "redirect:/home";
        } catch (InvalidUserCredentialsException exception) {
            model.addAttribute("hasError", true);
            model.addAttribute("error", exception.getMessage());
            return "login";
        }
    }



}
