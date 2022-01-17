package ru.mp3downloader.controllers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import ru.mp3downloader.utils.Utils;

import javax.servlet.http.HttpSession;
import java.io.File;

import java.io.FileInputStream;
import java.io.IOException;
import java.time.LocalDateTime;

@Slf4j
@Controller
public class MainController {
    @Value("${yandex.client.id}")
    private String clientId;

    @Autowired
    EmailServiceImpl emailService;

    @Autowired
    private LinkOrderService linkOrderService;

    @Autowired
    private Downloader downloader;


    @GetMapping("/")
    public String mainPage(Model model, HttpSession session) {
        if (session.getAttribute("yandex") == null) {
            session.setAttribute("yandex", "noauth");
        }
        model.addAttribute("yandex", session.getAttribute("yandex").equals("noauth"));
        model.addAttribute("error", "");
        model.addAttribute("clientId", clientId);
        return "index";
    }

    @PostMapping("/")
    @ResponseBody
    public String order(@ModelAttribute LinkOrder linkOrder, HttpSession session) {
        String link = linkOrder.getLink();
        if (!link.matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) {
            return "Вы ввели некорректную ссылку";// Не ссылка
        }
        Long orderNumber = (long) (link + linkOrder.getEmail()).hashCode();
        linkOrder = linkOrderService.findOrderByOrderNumber(orderNumber).orElse(linkOrder);
        linkOrder.setOrdered(LocalDateTime.now());
        linkOrder.setFile(String.valueOf(link.hashCode()).substring(1));
        linkOrder.setOrderNumber(orderNumber);
        linkOrderService.addOrUpdate(linkOrder);
        downloader.process(linkOrder, (String) session.getAttribute("yandex"));
        return "Ваш запрос поступил в обработку. <br/>Результат будет отправлен на указанный адрес электронной почты.<br/><br/>";
    }

    @GetMapping("/getmyfiles/{orderNumber}")
    public ResponseEntity getMyFiles(@PathVariable Long orderNumber) {
        log.info("Download started for " + orderNumber + ".zip");
        HttpHeaders headers = new HttpHeaders();
        try {
            LinkOrder linkOrder = linkOrderService.findOrderByOrderNumber(orderNumber).orElseThrow(() -> new WrongLinkException());
            File archive = new File(Utils.output + "/" + linkOrder.getFile() + ".zip");
            if (!archive.exists()) {
                throw new ArchiveNotFound();
            }
            linkOrder.setDownloaded(LocalDateTime.now());
            linkOrderService.addOrUpdate(linkOrder);
            log.info("Download " + orderNumber + " started");
            headers.add("Cache-Control", "no-cache, no-store, must-revalidate");
            headers.add("Content-Type", "application/zip");
            headers.add("Pragma", "no-cache");
            headers.add("Content-Transfer-Encoding", "binary");
            headers.add("Expires", "0");
            headers.add("Content-Disposition", "attachment;filename=" + orderNumber + ".zip");
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

}
