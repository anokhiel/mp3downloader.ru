package ru.mp3downloader.services;

import ru.mp3downloader.model.LinkOrder;

import java.util.Optional;

public interface LinkOrderService {
    public LinkOrder addOrder(LinkOrder order);
    public LinkOrder updateOrder(LinkOrder order);
    public void deleteOrder(Long id);
    public Optional<LinkOrder> findOrderById(Long id);
    public Optional<LinkOrder> findOrderByOrderNumber(Long orderNumber);

}
