package ru.mp3downloader.utils;


import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import ru.mp3downloader.exception.YandexException;

import java.util.HashMap;
import java.util.Map;

/**
 * Утилита для загрузки файлов на Яндекс диск
 */

@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class YandexUpdoader {
    private String token;
    private String root;
    private String dir;
    private Map<String, String> linkList;
    private String cloudLink;

    public boolean uploadAllFiles() throws YandexException {//Загрузка всех файлов на диск
        int root = createDir();
        int folderCreated = createDir();
        if (folderCreated == 201 | folderCreated == 409) {
            for (HashMap.Entry<String, String> element : linkList.entrySet()) {
                int resp = updoadAFile(element.getKey(), element.getValue());
                if (!(resp == 202 || resp == 409)) {
                    log.info("Произошла ошибка при загрузке файла" + element.getValue() + ".mp3 с адреса " + element.getKey());
                    throw new YandexException(resp);
                }
            }
            log.info("Все файлы загружены нормально");
            return true;

        } else {
            log.info("Произошла ошибка создания папки " + dir + ". Код ошибки " + folderCreated);
            throw new YandexException(folderCreated);
        }
    }

    private int createDir() {//Создание директории для фалов
        if (query(cloudLink + "?path=" + root, "GET") != 200) {// Создание корневой директории
            query(cloudLink + "?path=" + root, "PUT");
        }
        return query(cloudLink + "?path=" + root + "/" + dir, "PUT");//Создание директории для заказа
    }

    private int updoadAFile(String fileName, String link) {//Загрузка одного файла
        int resp = query(cloudLink + "/upload?path=" + root + "/" + dir + "/" + fileName + ".mp3&url=" + link, "POST");
        return resp;
    }

    private int query(String link, String method) {//Общая форма REST запроса к API Яндекс диска
        log.info("Соединяемся по ссылке " + link);
        Client client = Client.create();
        WebResource webResource = client.resource(link);
        ClientResponse response = webResource
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "OAuth " + token)
                .method(method, ClientResponse.class);
        log.info(response.toString());
        return response.getStatus();
    }
}
