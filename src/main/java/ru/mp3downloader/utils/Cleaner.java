package ru.mp3downloader.utils;

import lombok.extern.slf4j.Slf4j;
import ru.mp3downloader.model.LinkOrder;

import java.io.File;

/**
 * Класс для удаления файлов, заказанных более часа назад
 */
@Slf4j
public class Cleaner implements Runnable {
    private LinkOrder linkOrder;

    public Cleaner(LinkOrder linkOrder) {
        this.linkOrder = linkOrder;
    }

    public void run() {
        try {
            Thread.sleep(3600000L);
        } catch (InterruptedException e) {
            log.info("File " + linkOrder.getFile() + " hasn't been removed because of interrupted exception");
            e.printStackTrace();
        }
        File toBeDeleted=new File(Utils.output + "/" + linkOrder.getFile() + ".zip");
        if(toBeDeleted.delete()){
            log.info("File " + linkOrder.getFile() + " has been removed");
        }else{
            log.info("File " + linkOrder.getFile() + " hasn't been removed");
        }

    }

}
