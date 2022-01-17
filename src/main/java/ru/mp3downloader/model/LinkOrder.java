package ru.mp3downloader.model;

import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;

/**
 * Сущность для сбора сведений о заказах
 */
@Entity
@Setter
@Getter
@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LinkOrder {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @Column(nullable = false)
    private String link; // Ссылка или текст html

    @Column(nullable = false)
    private String email;

    @Column
    private LocalDateTime ordered; // Дата заказа

    @Column
    private LocalDateTime downloaded; // Дата загрузки

    @Column(nullable = false)
    private String file; // Название файла

    @Column(nullable = false) // Идентификатор заказа (имя архива)
    private Long orderNumber;

}
