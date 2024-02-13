package com.example.ethhashapp.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Application routing controller.
 *
 * @author Yann39
 * @since 1.0.0
 */
@Controller
public class RouteController {

    /**
     * To access the diplomas administration interface.
     *
     * @return The view name to load as {@link String}
     */
    @GetMapping("/diplomas")
    public String diplomas(Model model) {
        model.addAttribute("page", "diplomas");
        return "index";
    }

}