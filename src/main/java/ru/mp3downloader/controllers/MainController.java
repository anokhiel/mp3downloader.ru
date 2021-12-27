package ru.mp3downloader.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import ru.mp3downloader.services.EmailServiceImpl;

@Controller
public class MainController {

    @Autowired
    EmailServiceImpl EmailService;

    @GetMapping("/")
    public String mainPage() {
        return "index";
    }

    @GetMapping("/sendTest")
    public String sentTest() {
        try {

            EmailService.sendMail("alexandpopov@yandex.ru", "test", "test");
            return "index";
        } catch (Exception e) {
            return "error";
        }

    }


}
