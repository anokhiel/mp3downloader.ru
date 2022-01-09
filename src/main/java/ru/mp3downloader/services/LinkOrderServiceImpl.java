package ru.mp3downloader.services;

import lombok.EqualsAndHashCode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.mp3downloader.model.LinkOrder;
import ru.mp3downloader.repository.LinkOrderRepository;
import java.util.Optional;

@Service
@EqualsAndHashCode
class LinkOrderServiceImpl implements  LinkOrderService {

    @Autowired
     private LinkOrderRepository linkOrderRepository;


    @Override
    public LinkOrder addOrUpdate(LinkOrder order) {
            linkOrderRepository.save(order);
            return order;
         }

    @Override
    public Optional<LinkOrder> findOrderByOrderNumber(Long orderNumber){
        return  linkOrderRepository.findLinkOrderByOrderNumber(orderNumber);
    }

}
