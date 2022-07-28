package com.discstock.demo.Controllers;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

@RestController
public class Controller {

    @GetMapping("/")
    public String home() {
        String homeScreen = "";
        try {
            BufferedReader input = new BufferedReader(new FileReader("C:\\Users\\Sean\\Java Programs\\Personal Projects\\demo\\src\\main\\java\\com\\discstock\\demo\\Controllers\\homeScreen.html"));
            String temp;
            while ((temp = input.readLine()) != null) {
                homeScreen += temp;
            }
            input.close();
        } catch (IOException e) {
        }
        return homeScreen;
    }
    
}
