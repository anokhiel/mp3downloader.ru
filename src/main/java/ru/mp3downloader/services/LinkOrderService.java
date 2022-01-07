package ru.mp3downloader.services;

import ru.mp3downloader.model.LinkOrder;

import java.util.Optional;

public interface LinkOrderService {
    public LinkOrder addOrUpdate(LinkOrder order);
    public Optional<LinkOrder> findOrderByOrderNumber(Long orderNumber);
}
