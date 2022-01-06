package ru.mp3downloader.application;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@ComponentScan(basePackages = {"ru.mp3downloader.*"})
@EntityScan("ru.mp3downloader.*")
@EnableJpaRepositories("ru.mp3downloader.repository")

public class Mp3DownloaderApp {
    public static void main(String[] args) {
        SpringApplication.run(Mp3DownloaderApp.class, args);
    }
}
