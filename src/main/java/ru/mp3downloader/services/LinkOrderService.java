package ru.mp3downloader.services;

import org.springframework.stereotype.Service;
import ru.mp3downloader.model.LinkOrder;

import java.util.Optional;

/**
 * Стандартный интерфейс для обработки сущности заказов
 */
public interface LinkOrderService {
    public LinkOrder addOrUpdate(LinkOrder order);
    public Optional<LinkOrder> findOrderByOrderNumber(Long orderNumber);
}
