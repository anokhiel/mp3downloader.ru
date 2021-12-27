package ru.mp3downloader.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.mp3downloader.model.Source;

public interface SourceRepository extends JpaRepository<Source, Long> {

}
