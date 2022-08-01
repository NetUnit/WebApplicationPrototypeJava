package com.example.demo.controllers;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    // @Controller class is responsible for url_patterns at our web application
    // every single function operates unique url address
    // when we refer to this url - the next method is launched by current controller
    @GetMapping("/")
    public String home(Model model) {
        // (1) first attr is a tag for identification in template
        // (2) second attr is header name of the page
        // https://i.imgur.com/vEKvcRE.png
        model.addAttribute("title", "Java Bootcamp Main Page");
        // (3) str variable home -> name of the template home.html
        return "home";
    }
}