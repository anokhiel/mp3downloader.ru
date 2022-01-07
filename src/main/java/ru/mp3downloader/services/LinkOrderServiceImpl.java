package ru.mp3downloader.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mp3downloader.model.LinkOrder;
import ru.mp3downloader.repository.LinkOrderRepository;
import java.util.Optional;

@Service
class LinkOrderServiceImpl implements  LinkOrderService {

    @Autowired
     private LinkOrderRepository linkOrderRepository;


    @Override
    public LinkOrder addOrder(LinkOrder order) {
            linkOrderRepository.save(order);
            return order;
         }

    @Override
    public LinkOrder updateOrder(LinkOrder order) {

        linkOrderRepository.save(order);
        return order;
    }


    @Override
    public void deleteOrder(Long id) {
        linkOrderRepository.deleteById(id);
         }

    @Override
    public Optional<LinkOrder> findOrderById(Long id) {
        return linkOrderRepository.findById(id);
    }

    @Override
    public Optional<LinkOrder> findOrderByOrderNumber(Long orderNumber){
        return  linkOrderRepository.findLinkOrderByOrderNumber(orderNumber);
    }

}
