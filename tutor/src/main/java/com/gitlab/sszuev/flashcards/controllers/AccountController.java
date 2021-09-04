package com.gitlab.sszuev.flashcards.controllers;

import com.gitlab.sszuev.flashcards.dto.AccountRequest;
import com.gitlab.sszuev.flashcards.services.AccountService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import java.util.Map;
import java.util.Objects;

/**
 * Created by @ssz on 04.09.2021.
 */
@Controller
public class AccountController {
    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

    private final AccountService service;

    public AccountController(AccountService service) {
        this.service = service;
    }

    @GetMapping("/registration")
    public ModelAndView showRegistrationForm() {
        return new ModelAndView("registration", Map.of("user", new AccountRequest()));
    }

    @PostMapping("/registration")
    public ModelAndView registerNewUser(HttpServletRequest request, @ModelAttribute("user") AccountRequest user) {
        String msg = validateUser(user);
        if (msg != null) {
            return new ModelAndView("registration", Map.of("error", msg));
        }
        if (!service.save(user)) {
            return new ModelAndView("registration", Map.of("error", "A user with the same name already exists."));
        }
        try {
            request.login(user.getLogin(), user.getPassword());
        } catch (ServletException e) {
            LOGGER.error("Can't login", e);
        }
        return new ModelAndView("redirect:/");
    }

    private String validateUser(AccountRequest user) {
        String login = Objects.requireNonNull(user.getLogin());
        if (login.length() < 4) {
            return "Login too short.";
        }
        String pwd = Objects.requireNonNull(user.getPassword());
        if (pwd.length() < 4) {
            return "Password too short.";
        }
        if (!pwd.equals(user.getMatchingPassword())) {
            return "Password mismatch.";
        }
        return null;
    }
}
