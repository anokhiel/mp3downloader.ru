package ru.mp3downloader.utils;

import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.mp3downloader.model.LinkOrder;

import java.io.File;
import java.time.Instant;

/**
 * Класс для удаления файлов, заказанных более часа назад
 */
@Slf4j

public class Cleaner implements Runnable {

    private final File directoryToBeCleaned;

    public Cleaner(){
        this.directoryToBeCleaned = new File(Utils.output);
    }
    public void run() {
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
