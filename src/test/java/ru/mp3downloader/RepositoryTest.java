package ru.mp3downloader;


import org.hibernate.PropertyValueException;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.junit4.SpringRunner;
import ru.mp3downloader.application.Mp3DownloaderApp;
import ru.mp3downloader.model.LinkOrder;
import ru.mp3downloader.repository.LinkOrderRepository;

import javax.persistence.PersistenceException;
import javax.transaction.Transactional;
import java.time.LocalDateTime;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Mp3DownloaderApp.class)
public class RepositoryTest extends Assertions {

    @Autowired
    private LinkOrderRepository linkOrderRepository;

    private LinkOrder normalLinkOrder;
    private LinkOrder wrongLinkOrder;

    @Before
    public void setUpLinkOrder() {
        normalLinkOrder = LinkOrder.builder()
                .email("test@email.ru")
                .link("https://link.ru")
                .ordered(LocalDateTime.now())
                .downloaded(LocalDateTime.now().plusDays(1L))
                .file("1234")
                .orderNumber(123231L)
                .build();
        wrongLinkOrder = LinkOrder.builder()
                .email("")
                .link("https://link.ru")
                .build();
    }

    @Test
    @Transactional
    public void testSavingCorrectOrder() {
        linkOrderRepository.save(normalLinkOrder);
        Long id = normalLinkOrder.getId();
        Long orderNumber=normalLinkOrder.getOrderNumber();
        assertNotNull(linkOrderRepository.findById(id));
        assertNotNull(linkOrderRepository.findLinkOrderByOrderNumber(orderNumber));
    }
    @Test(expected = DataIntegrityViolationException.class)
    public void testSavingWrongOrder() {
            linkOrderRepository.save(wrongLinkOrder);

    }
}
