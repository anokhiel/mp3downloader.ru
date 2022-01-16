package ru.mp3downloader.model;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class Yandex {
    private String token_type;
    private  String access_token;
    private  String expires_in;
    private  String refresh_token;
    private  String scope;
}
