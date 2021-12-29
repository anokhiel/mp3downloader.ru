package ru.mp3downloader.services;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Service;
import ru.mp3downloader.model.LinkOrder;
import ru.mp3downloader.dto.MainLinkOrder;
import ru.mp3downloader.utils.Utils;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.zip.ZipOutputStream;

@Service
@AllArgsConstructor
@NoArgsConstructor
public class Downloader  implements Runnable {
    LinkOrder linkOrder;

    @PostConstruct
    private void init() {
        try {
            Files.createDirectories(Paths.get("output"));
            Files.createDirectories(Paths.get("downloads"));
        }catch (IOException e){
            e.printStackTrace();
        }
        }

    @Override
    public void run() {
        try {
            linkOrder=MainLinkOrder.mainLinkOrder;
            boolean done = executor(linkOrder);
            if (done) {
                informMainServer(linkOrder, "OK");
                System.out.print("OK");
            } else {
                informMainServer(linkOrder, "NOFILESFOUND");
                System.out.print("NOFILESFOUND");
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private boolean executor(LinkOrder linkOrder) throws IOException, InterruptedException {
        String link = linkOrder.getLink();// Получаем ввод пользователя
        if (!link.matches("^(https?|ftp|file)://[-a-zA-Z0-9+&@#/%?=~_|!:,.;]*[-a-zA-Z0-9+&@#/%=~_|]")) {
            return false;// Не ссылка
        }
        HashMap<String, String> linkList = Utils.getPage(link);// Получаем список "имя файла"->"ссылка"
        if (!linkList.isEmpty()) {// Если список не пустой
            FileOutputStream fos = new FileOutputStream(Utils.getArchive(linkOrder));
            ZipOutputStream zipOut = new ZipOutputStream(fos);
            for (HashMap.Entry<String, String> element : linkList.entrySet()) {
                //	System.out.println(element.getKey()+" "+element.getValue());
                String downloadedFile = linkOrder.getFolder() + "/" + element.getKey() + ".mp3";
                Utils.downloadaFile(element.getValue(), downloadedFile);// Загружаем файл
                File fileToZip = new File(downloadedFile);
                if (fileToZip.isFile()) {
                    Utils.zipFile(fileToZip, zipOut);
                    fileToZip.delete();
                }
            }
            zipOut.close();
            fos.close();
            return true;// Загрузили нормально
        }
        return false;
    }


    private void informMainServer(LinkOrder srs, String status) throws IOException{
//        String url = mainhost+"?id="+srs.getLid()+"&status="+status+"&secret="+secret;
//        URL obj = new URL(url);
//        HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
//
//        connection.setRequestMethod("GET");
//
//        BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
//        String inputLine;
//        StringBuffer response = new StringBuffer();
//
//        while ((inputLine = in.readLine()) != null) {
//            response.append(inputLine);
//        }
//        in.close();
//
//        System.out.println(response.toString());

    }
}
