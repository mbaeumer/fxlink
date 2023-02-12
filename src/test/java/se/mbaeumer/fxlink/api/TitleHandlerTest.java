package se.mbaeumer.fxlink.api;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import se.mbaeumer.fxlink.models.Link;
import se.mbaeumer.fxlink.util.LinkTitleUtil;

import java.io.IOException;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class TitleHandlerTest {

    private TitleHandler titleHandler;

    @Mock
    private LinkTitleUtil linkTitleUtil;

    @Mock
    private YoutubeCrawler youtubeCrawler;

    @Before
    public void setUp(){
        titleHandler = new TitleHandler(linkTitleUtil, youtubeCrawler);
    }

    @Test
    public void generateTitleForNonYoutubeLink() {
        Link link = new Link("some-title", "www.stackoverflow.blog/some-hot-topic", "");
        when(linkTitleUtil.generateTitle(link)).thenReturn("some hot topic");
        String title = titleHandler.generateTitle(link);
        assertEquals("some hot topic", title);
        Mockito.verifyNoInteractions(youtubeCrawler);
    }

    @Test
    public void generateTitleForYoutubeLink() throws IOException {
        Link link = new Link("some-title", "https://www.youtube.com/watch?v=OPw5Qbak123", "");
        when(youtubeCrawler.getTitle(link.getURL())).thenReturn("some awesome youtube title");
        String title = titleHandler.generateTitle(link);
        assertEquals("some awesome youtube title", title);
        Mockito.verifyNoInteractions(linkTitleUtil);
    }

    @Test
    public void generateTitleForYoutubeChannelLink(){
        Link link = new Link("some-title", "https://www.youtube.com/@SomeChannel/some-more", "");
        when(linkTitleUtil.generateTitle(link)).thenReturn("SomeChannel some more");
        String title = titleHandler.generateTitle(link);
        assertEquals("SomeChannel some more", title);
        Mockito.verifyNoInteractions(youtubeCrawler);
    }


}