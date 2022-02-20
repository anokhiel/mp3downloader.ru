package ru.mp3downloader.utils;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.stereotype.Component;
import ru.mp3downloader.model.LinkOrder;

import javax.annotation.PostConstruct;
import java.io.File;
import java.time.Instant;

/**
 * Класс для удаления файлов, заказанных более часа назад
 */
@Slf4j
@Component
public class Cleaner implements ApplicationListener<ContextRefreshedEvent> {

    private  File directoryToBeCleaned;

    public void watcher() {
        try {
            Thread.sleep(3600000L);
        } catch (InterruptedException e) {
            log.info("Cleaner threw interrupted exception");
            e.printStackTrace();
        }
       cleanDirectory();

    }
    public void cleanDirectory() {
                File[] allContents = directoryToBeCleaned.listFiles();
        if (allContents != null) {
            for (File file : allContents) {
                if((file.lastModified() - Instant.now().toEpochMilli()) < 3600000){
                    file.delete();
                }
            }
        }
    }

    @Override
    public void onApplicationEvent(ContextRefreshedEvent event) {
        this.directoryToBeCleaned = new File(Utils.output);
        Runnable cleaner=()->watcher();
        new Thread(cleaner,"Cleaner").start();
        log.info("Old files cleaner started");
    }
}
