package ru.mp3downloader.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan({"ru.mp3downloader"})

public class Mp3downloaderapp {
    public static void main(String[] args) {
        SpringApplication.run(Mp3downloaderapp.class, args);
    }
}
