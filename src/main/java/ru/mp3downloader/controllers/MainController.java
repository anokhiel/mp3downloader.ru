package ru.mp3downloader.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import ru.mp3downloader.model.LinkOrder;
import ru.mp3downloader.dto.MainLinkOrder;
import ru.mp3downloader.services.Downloader;
import ru.mp3downloader.services.EmailServiceImpl;
import ru.mp3downloader.services.LinkOrderService;

import java.time.LocalDateTime;

@Controller
public class MainController {

    @Autowired
    EmailServiceImpl EmailService;

    @Autowired
    private LinkOrderService linkOrderService;

    @Autowired
    private Downloader downloader;



    @GetMapping("/")
    public String mainPage() {
        return "index";
    }

    @PostMapping("/")
    @ResponseBody
    public String order(@ModelAttribute LinkOrder linkOrder){
        linkOrder.setOrdered(LocalDateTime.now());
        linkOrderService.addOrder(linkOrder);
       MainLinkOrder mainLinkOrder=new MainLinkOrder();
        mainLinkOrder.mainLinkOrder=linkOrder;
      new Thread(new Downloader(), "downloader").start();
               return linkOrder.toString();
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
