package ru.mp3downloader.services;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.mp3downloader.exception.YandexException;
import ru.mp3downloader.model.LinkOrder;
import ru.mp3downloader.model.Status;
import ru.mp3downloader.utils.Utils;
import ru.mp3downloader.utils.YandexUpdoader;

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
    @Value("${yandex.api.link}")
    String cloudLink;

    LinkOrder linkOrder;

    String errorMessage ="";

    String token;

    String yandexDir="";

    @Autowired
    private EmailServiceImpl emailService;

    @PostConstruct
    private void init() {
        try {
            Files.createDirectories(Paths.get(Utils.output));
            Files.createDirectories(Paths.get(Utils.downloads));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void process(LinkOrder linkOrder, String token) {
        this.token = token;
        this.linkOrder = linkOrder;
        Runnable r = () -> {
            try {
                if (executor(linkOrder)) {
                    if (token.equals("noauth")) {
                        informUser(linkOrder, OK);
                        log.info("For " + linkOrder.toString() + " Status OK");
                    } else {
                        log.info("For " + linkOrder.toString() + " Status OK");
                        informUser(linkOrder,YANDEX);
                    }
                } else {
                    log.info("For " + linkOrder.toString() + " Status NOFILESFOUND");
                    informUser(linkOrder, NOFILESFOUND);

                }
            }catch(YandexException e){
               errorMessage=e.getMessage();
                informUser(linkOrder, YANDEXEXCEPTION);

            } catch (Exception e) {
                informUser(linkOrder, EXCEPTION);
                log.info("For " + linkOrder.toString() + " Status EXCEPTION");
                e.printStackTrace();
            }
        };
        new Thread(r, "process").start();
    }


    private boolean executor(LinkOrder linkOrder) throws IOException, YandexException {
        Map<String, String> linkList = Utils.getPage(linkOrder.getLink());// Получаем список "имя файла"->"ссылка"
        if (!linkList.isEmpty()) {// Если список не пустой
            if (token.equals("noauth")) {
                if (Utils.fileExists(linkOrder)) {
                    return true;
                }
                Utils.createArchive(linkOrder, linkList);
                return true;// Загрузили нормально
            } else {
                yandexDir=Utils.getGeneralDirName(linkOrder);
                YandexUpdoader yandexUpdoader = YandexUpdoader
                        .builder()
                        .token(token)
                        .root("mp3downloader")
                        .linkList(linkList).dir(yandexDir)
                        .cloudLink(cloudLink)
                        .build();

                return yandexUpdoader.uploadAllFiles();
            }
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
                            .replaceAll("%key%", Long.toString(linkOrder.getOrderNumber()))
                            .replaceAll("%%yandexdir%%", yandexDir)
                            .replaceAll("%errormessage%",errorMessage)
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
