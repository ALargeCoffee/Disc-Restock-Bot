package com.discstock.demo.Controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
public class MainController {

    @GetMapping("/")
    public ModelAndView home() {
        ModelAndView homeView = new ModelAndView();
        homeView.setViewName("homeScreen.html");
        return homeView;
    }
    
}
