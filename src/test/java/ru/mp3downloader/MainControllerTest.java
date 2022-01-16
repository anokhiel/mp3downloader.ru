package ru.mp3downloader;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import ru.mp3downloader.application.Mp3DownloaderApp;
import ru.mp3downloader.controllers.MainController;
import ru.mp3downloader.model.LinkOrder;
import ru.mp3downloader.services.Downloader;
import ru.mp3downloader.services.LinkOrderService;
import ru.mp3downloader.utils.Utils;

import javax.servlet.http.HttpSession;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Optional;
import static org.mockito.Mockito.when;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Mp3DownloaderApp.class)
@ExtendWith(MockitoExtension.class)
public class MainControllerTest {
    private LinkOrder normalLinkOrder;
    private LinkOrder wrongLinkOrder;
    private HttpSession session;

    @MockBean
    @Autowired
    LinkOrderService linkOrderService;

    @MockBean
    @Autowired
    Downloader downloader;

    @InjectMocks
    @Autowired
    MainController mainController;

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
                .link("httpu")
                .build();
        session.setAttribute("yandex","noauth");
    }

    @Test
    public void testOrder(){
        when(linkOrderService.addOrUpdate(normalLinkOrder)).thenReturn(normalLinkOrder);
        when(linkOrderService.addOrUpdate(wrongLinkOrder)).thenThrow(DataIntegrityViolationException.class);
        Assert.assertEquals(mainController.order(normalLinkOrder, session), "Ваш запрос поступил в обработку. <br/>Результат будет отправлен на указанный адрес электронной почты.<br/><br/>");
        Assert.assertEquals(mainController.order(wrongLinkOrder,  session), "Вы ввели некорректную ссылку");
    }
    @Test
    public void testGetMyFiles() throws IOException {
        when(linkOrderService.findOrderByOrderNumber(123231L)).thenReturn(Optional.ofNullable(normalLinkOrder));
        File zip=new File(Utils.output+"/1234.zip");
        zip.createNewFile();
        Assert.assertEquals(mainController.getMyFiles(123231L).getHeaders().getContentType(), MediaType.APPLICATION_OCTET_STREAM);
        zip.delete();
    }
}
