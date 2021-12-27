package ru.mp3downloader.utils;

import org.springframework.stereotype.Component;

@Component
public class Utils {
    public String fileSafeName(String text) {// Убираем недопустимые символы в имени файла
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
}
