package ru.mp3downloader;

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
import ru.mp3downloader.services.LinkOrderService;

import java.time.LocalDateTime;
import java.util.Optional;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Mp3DownloaderApp.class)
public class LinkOrderServiceTest extends Assertions {
    @Autowired
    LinkOrderService linkOrderService;
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
    public void saveOrUpdateNormalLinkOrderTest() {
        LinkOrder saved = linkOrderService.addOrUpdate(normalLinkOrder);
        Optional<LinkOrder> found =linkOrderService.findOrderByOrderNumber(saved.getOrderNumber());
        Optional<LinkOrder> notfound =linkOrderService.findOrderByOrderNumber(-1L);
        assertNotNull(found.get());
        assertFalse(notfound.isPresent());
    }
    @Test(expected = DataIntegrityViolationException.class)
    public void saveOrUpdateWrongLinkOrderTest() {
        linkOrderService.addOrUpdate(wrongLinkOrder);
    }
}
