package ru.mp3downloader.model;

public enum Status {
    OK("Здравствуйте.\n Вы можете скачать архив с файлами mp3 с сайта %link% по адресу  https://mp3downloader.ru/getmyfiles/%key% в течение часа после получения данного сообщения."),
    NOFILESFOUND("Здравствуйте.\n На сайте %link% файлов mp3 не обнаружено."),
    EXCEPTION("Здравствуйте.\n При загрузке файлов по ссылке %link% произошла ошибка. Попробуйте повторить попытку позже.");
    String subject="Заказ на скачивание файлов на сайте mp3downloader.ru";
    String text;
    Status( String text){
        this.text=text;
    }

    public String getSubject() {
        return subject;
    }

    public String getText() {
        return text;
    }
}
