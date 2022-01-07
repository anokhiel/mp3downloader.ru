package ru.mp3downloader.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.mp3downloader.model.LinkOrder;

import java.util.Optional;

@Repository
public interface LinkOrderRepository extends JpaRepository<LinkOrder, Long> {
    public Optional<LinkOrder> findLinkOrderByOrderNumber(Long orderNumber);
}
