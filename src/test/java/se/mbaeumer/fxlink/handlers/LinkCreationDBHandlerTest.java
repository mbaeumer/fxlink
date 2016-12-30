package se.mbaeumer.fxlink.handlers;

import junit.framework.TestCase;
import org.junit.Test;
import se.mbaeumer.fxlink.models.Link;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by martinbaumer on 30/12/16.
 */
public class LinkCreationDBHandlerTest extends TestCase {

    @Test
    public void testReturnNullIfLinkIsNull(){
        assertNull(LinkCreationDBHandler.constructSqlString(null));
    }

    @Test
    public void testConstructSqlString(){
        Link link = createLink();
        String expected = "INSERT INTO Link VALUES(DEFAULT, 'Der Kicker', 'www.kicker.de', 'German sports magazin', DEFAULT, DEFAULT,";
        expected += " '" + link.getLastUpdated() + "')";
        String actual = LinkCreationDBHandler.constructSqlString(link);
        assertTrue("actual: " + actual, actual.equalsIgnoreCase(expected));
    }

    private Link createLink(){
        Link link = new Link("Der Kicker", "www.kicker.de", "German sports magazin");
        link.setLastUpdated(createTimestamp());
        return link;
    }

    private Timestamp createTimestamp(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return Timestamp.valueOf(df.format(new Date()));
    }
}
