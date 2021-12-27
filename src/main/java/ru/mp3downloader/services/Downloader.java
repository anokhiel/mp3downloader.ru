package ru.mp3downloader.services;

import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mp3downloader.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;

@Service
public class Downloader {
    @Autowired
    private Utils utils;

    public  HashMap<String, String> getPage(String urls) throws IOException{// Получили список имя файлассылка
        HashMap<String, String> output = new HashMap<>();
        Document htmldoc = Jsoup.connect(urls).userAgent("Mozilla").get();
        Elements elements=htmldoc.getElementsByTag("a");// Получаем все ссылки
        for(Element element: elements) {
            String link=element.absUrl("href");
            if(link.indexOf(".mp3")!=-1) {// Оставляем только mp3
                String text=element.text();// Получаем название будущего файла по тексту в ссылке
                if(text.isEmpty()||text.equals(link)) {//Если названия нет или совпадает со ссылкой, оставляем название файла как в ссылке
                    text=link.substring(link.lastIndexOf("/"));
                }
                text=utils.fileSafeName(text);// Убираем недопустимые символы
                if(output.containsKey(text)) {// Если имя файла уже есть
                    int i=1;
                    while(output.containsKey(text)) {// Добавляем порядковый номер в конце файла
                        if(i==1) {
                            text=text+" "+Integer.toString(i); // Для первого случая приписываем в конце 1
                        }else {
                            int j=i-1;
                            text=text.replace(Integer.toString(j),Integer.toString(i) ); // Если файлов с этим именем больше одного, в последнем файле заменяем на следующий номер
                        }
                        i++;
                    }
                }

                // Получаем абсолютную ссылку
                output.put(text, link); // Добавляем в конец

            }
        }
        return output;
    }



    private void downloadaFile(String link, String fname) {//Загрузка фала
        try {
            FileUtils.copyURLToFile(new URL(link),
                    new File(fname));

        } catch (IOException e) {
            //     e.printStackTrace();
        }
    }
}
