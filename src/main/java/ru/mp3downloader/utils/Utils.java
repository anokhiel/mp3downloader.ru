package ru.mp3downloader.utils;


import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FileUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.mp3downloader.model.LinkOrder;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Slf4j
public class Utils {
    public static String downloads ="downloads";
    public static String  output="output";
    public static String fileSafeName(String text) {// Убираем недопустимые символы в имени файла
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
        while ((length = fis.read(bytes)) >= 0) {
            zipOut.write(bytes, 0, length);
        }
        fis.close();
    }

    public static boolean fileExists(LinkOrder linkOrder) {
        File file=new File(Utils.getArchive(linkOrder));
      if(file.exists()) {
          file.setLastModified(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());// Продлить время до стирания
          return  true;
      }
      return false;
    }

    public static Map<String, String> getPage(String urls) throws IOException {// Получили список имя файла-ссылка
        HashMap<String, String> output = new HashMap<>();
        Document htmlDoc = Jsoup.connect(urls).userAgent("Mozilla").get();
        Elements elements = htmlDoc.getElementsByTag("a");// Получаем все ссылки
        for (Element element : elements) {
            String link = element.absUrl("href");
            if (link.indexOf(".mp3") != -1) {// Оставляем только mp3
                String text = element.text();// Получаем название будущего файла по тексту в ссылке
                if (text.isEmpty() || text.equals(link)) {//Если названия нет или совпадает со ссылкой, оставляем название файла как в ссылке
                    text = link.substring(link.lastIndexOf("/"));
                }
                text = Utils.fileSafeName(text);// Убираем недопустимые символы
                if (output.containsKey(text)) {// Если имя файла уже есть
                    int i = 1;
                    while (output.containsKey(text)) {// Добавляем порядковый номер в конце файла
                        if (i == 1) {
                            text = text + " " + Integer.toString(i); // Для первого случая приписываем в конце 1
                        } else {
                            int j = i - 1;
                            text = text.replace(Integer.toString(j), Integer.toString(i)); // Если файлов с этим именем больше одного, в последнем файле заменяем на следующий номер
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

    public static void downloadFile(String link, String fileName) {//Загрузка файла
        try {
            File result;
            FileUtils.copyURLToFile(new URL(link),
                    result = new File(fileName));
            if (result.length() == 0) {
                result.delete();
            }
        } catch (IOException e) {
            log.info("Ошибка загрузки файла " + fileName);
            e.printStackTrace();
        }
    }

    public static String getArchive(LinkOrder linkOrder) {
        return output+"/" + linkOrder.getFile() + ".zip";
    }

    public static void createArchive(LinkOrder linkOrder, Map<String, String> linkList) throws IOException {
        FileOutputStream fos = new FileOutputStream(Utils.getArchive(linkOrder));
        ZipOutputStream zipOut = new ZipOutputStream(fos, java.nio.charset.StandardCharsets.UTF_8);
        for (HashMap.Entry<String, String> element : linkList.entrySet()) {
            log.info(element.getKey() + " " + element.getValue());
            String downloadedFile = downloads + "/" + element.getKey() + ".mp3";
            Utils.downloadFile(element.getValue(), downloadedFile);// Загружаем файл
            File fileToZip = new File(downloadedFile);
            if (fileToZip.isFile()) {
                Utils.zipFile(fileToZip, zipOut);
                fileToZip.delete();
            }
        }
        zipOut.close();
        fos.close();
    }

}
