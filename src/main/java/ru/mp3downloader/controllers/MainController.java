package ru.mp3downloader.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import ru.mp3downloader.exception.ArchiveNotFound;
import ru.mp3downloader.exception.WrongLinkException;
import ru.mp3downloader.model.LinkOrder;
import ru.mp3downloader.services.Downloader;
import ru.mp3downloader.services.EmailServiceImpl;
import ru.mp3downloader.services.LinkOrderService;

import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Controller
public class MainController {

    @Autowired
    EmailServiceImpl emailService;

    @Autowired
    private LinkOrderService linkOrderService;

    @Autowired
    private Downloader downloader;


    @GetMapping("/")
    public String mainPage(Model model) {
        model.addAttribute("error", "");
        return "index";
    }

    @PostMapping("/")
    @ResponseBody
    public String order(@ModelAttribute LinkOrder linkOrder) {
        if (!linkOrder.getLink().matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) {
            return "Вы ввели некорректную ссылку";// Не ссылка
        }
        linkOrder.setOrdered(LocalDateTime.now());
        linkOrderService.addOrder(linkOrder);
        downloader.process(linkOrder);
        return "Ваш запрос поступил в обработку. <br/>Результат будет отправлен на указанный адрес электронной почты.<br/><br/>";
    }

    @GetMapping("/getmyfiles/{key}")
    public ResponseEntity getMyFiles(@PathVariable String key) {
        String[] data = key.split("_");
        log.info("Download started for " + data[0]+".zip");
        HttpHeaders headers = new HttpHeaders();
        try {
            LinkOrder linkOrder = linkOrderService.findOrderById(Long.parseLong(data[0])).orElseThrow(() -> new WrongLinkException() );
            if (data[1] == null | !data[1].equals(linkOrder.getOrdered().hashCode())) {
                              throw new WrongLinkException();
            }
            File archive = new File("output/" + data[0] + ".zip");
            if (!archive.exists()) {
                throw new ArchiveNotFound();
            }
            linkOrder.setDownloaded(LocalDateTime.now());
            linkOrderService.updateOrder(linkOrder);
            log.info("Download "+key+" started");
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Content-Type", "application/zip");
            headers.add("Pragma", "no-cache");
            headers.add("Content-Transfer-Encoding", "binary");
            headers.add("Expires", "0");
            headers.add("Content-Disposition", "attachment;filename=" + data[0] + ".zip");
            InputStreamResource resource = new InputStreamResource(new FileInputStream(archive));

            return ResponseEntity.ok()
                    .headers(headers)
                    .contentLength(archive.length())
                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .body(resource);
        } catch (ArchiveNotFound |
                WrongLinkException |
                NullPointerException |
                IOException e) {
            log.info("Error " + e.getMessage());
            headers.add("Location", "/errorpage/" + e.getMessage());
            return ResponseEntity.status(HttpStatus.MOVED_PERMANENTLY).headers(headers).body("");
        }

    }

    @GetMapping("/errorpage/{mes}")
    public String errorPage(@PathVariable String mes, Model model) {
        model.addAttribute("error", mes);
        return "index";
    }

    @GetMapping("/sendTest")
    public String sentTest() {
        try {

            //  emailService.sendMail("alexandpopov@yandex.ru", "test", "test");
            return "index";
        } catch (Exception e) {
            return "error";
        }


    }


}
