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

/**
 * Сервис загрузки файлов
 * Да. Я в курсе, что это не по SOLID, но переделывать нет времени.
 * Так случилось, потому что я брал этот класс из раннего десктопного проекта с тем же функционалом, а потом еще добавил Яндекс.
 */
@Slf4j
@Service
@NoArgsConstructor
public class Downloader {
    @Value("${yandex.api.link}")
    String cloudLink;

    LinkOrder linkOrder;

   String token;

    String yandexDir = "";

    @Autowired
    private EmailServiceImpl emailService;

    @PostConstruct
    private void init() {// Создание рабочих папок.
        try {

            Files.createDirectories(Paths.get(Utils.output));
            Files.createDirectories(Paths.get(Utils.downloads));
        } catch (IOException e) {
            log.error("Exception has occured", e);
        }
    }

    public void process(LinkOrder linkOrder, String token) {// Обработка заказов

            Runnable r = () -> {
            String errorMessage = "";
            try {
                if (executor(linkOrder)) {
                    if (token.equals("noauth")) {// Если нужно создать архив
                        informUser(linkOrder, OK);
                        log.info("For " + linkOrder.toString() + " Status OK");

                    } else {// Если нужно загрузить на Яндекс диск
                        log.info("For " + linkOrder.toString() + " Status OK");
                        informUser(linkOrder, YANDEX);
                    }
                } else {// Если файлов не найдено
                    log.info("For " + linkOrder.toString() + " Status NOFILESFOUND");
                    informUser(linkOrder, NOFILESFOUND);

                }
            } catch (YandexException e) {// Если произошла ошибка загрузки на Яндекс диск
                errorMessage = e.getMessage();
                informUser(linkOrder, YANDEXEXCEPTION);
            } catch (Exception e) {// Если произошли прочие ошибки
                informUser(linkOrder, EXCEPTION);
                log.info("For " + linkOrder.toString() + " Status EXCEPTION");
                log.error("Exception has occured", e);
            }
        };
        new Thread(r, "process").start();
    }


    private boolean executor(LinkOrder linkOrder) throws YandexException, IOException {

        Map<String, String> linkList = Utils.getPage(linkOrder.getLink());// Получаем список "имя файла"->"ссылка"
        if (!linkList.isEmpty()) {// Если список не пустой
            if (token.equals("noauth")) {// Загрузка в архив
                if (Utils.fileExists(linkOrder)) {
                    return true;
                }
                Utils.createArchive(linkOrder, linkList);
                return true;// Загрузили нормально
            } else {
                yandexDir = Utils.getGeneralDirName(linkOrder);// Загрузка на Яндекс диск
                YandexUpdoader yandexUpdoader = YandexUpdoader
                        .builder()
                        .token(token)
                        .root("mp3downloader")
                        .linkList(linkList)
                        .dir(yandexDir)
                        .cloudLink(cloudLink)
                        .build();

                return yandexUpdoader.uploadAllFiles();
            }
        }

        return false;
    }


    private void informUser(LinkOrder linkOrder, Status status) {// Отправка письма пользователю с результатами загрузки
        String errorMessage = "";
        try {
            emailService.sendMail(
                    linkOrder.getEmail(),
                    status.getSubject(),
                    status.getText()
                            .replaceAll("%link%", linkOrder.getLink())
                            .replaceAll("%key%", Long.toString(linkOrder.getOrderNumber()))
                            .replaceAll("%%yandexdir%%", yandexDir)
                            .replaceAll("%errormessage%", errorMessage)
            );
            log.info("Email sent to email " + linkOrder.getEmail() + ", subject: " +
                    status.getSubject() + " text: " +
                    status.getText()
                            .replaceAll("%link%", linkOrder.getLink())
                            .replaceAll("%key%", linkOrder.getFile())
            );
        } catch (Exception e) {
            log.info(("Error while sending email to user"));
            log.error("Exception has occured", e);
        }
    }
}
