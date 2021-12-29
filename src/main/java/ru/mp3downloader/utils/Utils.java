package ru.mp3downloader.utils;


import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.mp3downloader.model.LinkOrder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;


public class Utils {
    public static String  fileSafeName(String text) {// Убираем недопустимые символы в имени файла
        text = text.trim();
        text = text.replaceAll("\"", "");
        text = text.replaceAll("<", "");
        text = text.replaceAll(">", "");
        text = text.replaceAll(":", "");
        text = text.replaceAll("\\\\", "");
        text = text.replaceAll("/", "");
        text = text.replaceAll("\\|", "");
        text = text.replaceAll("\\?", "");
        text = text.replaceAll("\\*", "");
        return text;
    }
    public static void zipFile(File fileToZip, ZipOutputStream zipOut) throws IOException {
        FileInputStream fis = new FileInputStream(fileToZip);
        ZipEntry zipEntry = new ZipEntry(fileToZip.getName());
        zipOut.putNextEntry(zipEntry);
        byte[] bytes = new byte[1024];
        int length;
        while((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();


    }
    public static void delete(String fname) {
        File f=new File(fname);
        f.delete();

    }
    public static HashMap<String, String> getPage(String urls) throws IOException{// Получили список имя файлассылка
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
                text=Utils.fileSafeName(text);// Убираем недопустимые символы
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
    public static void downloadaFile(String link, String fname) {//Загрузка фала
        try {
            FileUtils.copyURLToFile(new URL(link),
                    new File(fname));

        } catch (IOException e) {
            //     e.printStackTrace();
        }
    }

    public static String getArchive(LinkOrder linkOrder){
        return "output/"+linkOrder.getId().toString()+".zip";
    }

}
