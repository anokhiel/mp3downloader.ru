package ru.mp3downloader.services;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mp3downloader.model.LinkOrder;
import ru.mp3downloader.model.Status;
import ru.mp3downloader.utils.Utils;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;

import static ru.mp3downloader.model.Status.*;

@Slf4j
@Service
@NoArgsConstructor
public class Downloader {

    LinkOrder linkOrder;

    @Autowired
    private EmailServiceImpl emailService;

    @PostConstruct
    private void init() {
        try {
            Files.createDirectories(Paths.get("output"));
            Files.createDirectories(Paths.get("downloads"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void process(LinkOrder linkOrder) {
        this.linkOrder = linkOrder;
        Runnable r = () -> {
            try {
                if (executor(linkOrder)) {
                    informUser(linkOrder, OK);
                    log.info("For " + linkOrder.toString() + " Status OK");
                } else {
                    informUser(linkOrder, NOFILESFOUND);
                    log.info("For " + linkOrder.toString() + " Status NOFILESFOUND");
                }
            } catch (Exception e) {
                informUser(linkOrder, EXCEPTION);
                log.info("For " + linkOrder.toString() + " Status EXCEPTION");
                e.printStackTrace();
            }
        };
        new Thread(r, "process").start();
    }


    private boolean executor(LinkOrder linkOrder) throws IOException {
        if(Utils.fileExists(linkOrder)){return true;}
        Map<String, String> linkList = Utils.getPage(linkOrder.getLink());// Получаем список "имя файла"->"ссылка"
        if (!linkList.isEmpty()) {// Если список не пустой
            Utils.createArchive(linkOrder, linkList);
            return true;// Загрузили нормально
        }
        return false;
    }


    private void informUser(LinkOrder linkOrder, Status status) {
        try {
            emailService.sendMail(
                    linkOrder.getEmail(),
                    status.getSubject(),
                    status.getText()
                            .replaceAll("%link%", linkOrder.getLink())
                            .replaceAll("%key%", linkOrder.getFile())
            );
            log.info("Email sent to email " + linkOrder.getEmail() + ", subject: " +
                    status.getSubject() + " text: " +
                    status.getText()
                            .replaceAll("%link%", linkOrder.getLink())
                            .replaceAll("%key%", linkOrder.getFile())
            );
        } catch (Exception e) {

            e.printStackTrace();
        }
    }
}
