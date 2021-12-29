package ru.mp3downloader.model;


import lombok.*;
import org.springframework.beans.factory.annotation.Value;

import javax.persistence.*;
import java.nio.file.Path;
import java.time.DateTimeException;
import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class LinkOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column
    private String link; // Ссылка или текст html

    @Column
    private String email;

    @Column
    private LocalDateTime ordered;

    @Column
    private LocalDateTime downloaded;

    @Transient
    private String folder="downloads";

}
