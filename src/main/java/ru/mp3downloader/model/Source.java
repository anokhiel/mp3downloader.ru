package ru.mp3downloader.model;


import lombok.*;

import javax.persistence.*;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Source {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column
    @NonNull
    private String link; // Ссылка или текст html

    @Column
    @NonNull
    private String email;

}
