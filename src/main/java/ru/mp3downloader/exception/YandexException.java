package ru.mp3downloader.exception;

public class YandexException extends Exception {
    public String message;

    public YandexException(int code) {
        String message = "";
        switch (code) {
            case 400:
                this.message = "Некорректные данные";
                break;
            case 401:
                this.message = "Не авторизован.";
                break;
            case 403:
                this.message = "Ваши файлы занимают больше места, чем у вас есть. Удалите лишнее или увеличьте объём Диска.";
                break;
            case 429:
                this.message = "Слишком много запросов.";
                break;
            case 503:
                this.message = "Сервис временно недоступен.";
                break;
            case 507:
                this.message = "Недостаточно свободного места.";
                break;
            default:
                this.message = "Непредвиденная ошибка";
        }
        ;
    }
}
