package ru.mp3downloader;

import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.util.Assert;
import ru.mp3downloader.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import static org.junit.Assert.*;


@SpringBootTest(classes = {Utils.class})
public class UtillTest {
    TemporaryFolder folder;
    String badString = "a\"<>:/\\\\\\?\\*\\| ";
    String noLinks = "https://yandex.ru/";
    String hasLinks = "http://kkre-1.narod.ru/admoni.htm";
    String linkToRealFile = "http://kkre-45.narod.ru/flaks/blok1.mp3";
    String linkToFakeFile = "http://yandex.ru/2.mp3";

    @Test
    public void testFileSafeName() {
        assertEquals(Utils.fileSafeName(badString), "a");
    }

    @Test
    public void testGetPage() throws IOException {
        Map notEmty = Utils.getPage(hasLinks);
        Assert.notEmpty(notEmty);
        Map empty = Utils.getPage(noLinks);
        assertTrue(empty.isEmpty());
    }

    @Test
    public void testDownloadFile() {
        File tmp = new File("tmp.mp3");
        if (tmp.exists()) tmp.delete();
        Utils.downloadFile(linkToRealFile, "tmp.mp3");
        assertTrue(tmp.exists());
        if (tmp.exists()) tmp.delete();
    }

    @Test
            //(expected = IOException.class)
    public void testDownloadBadFile() {
        File tmp = new File("tmp.mp3");
        if (tmp.exists()) tmp.delete();
        Utils.downloadFile(linkToFakeFile, "tmp.mp3");
        assertTrue(!tmp.exists());
        if (tmp.exists()) tmp.delete();
    }
}
