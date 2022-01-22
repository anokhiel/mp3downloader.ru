package ru.mp3downloader.utils;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
public class Cleaner {

    private  File directoryToBeCleaned;

    @PostConstruct
    private void init(){
        this.directoryToBeCleaned = new File(Utils.output);
        Runnable cleaner=()->watcher();
        new Thread(cleaner,"Cleaner").start();
        log.info("Old files cleaner started");
    }


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

}
