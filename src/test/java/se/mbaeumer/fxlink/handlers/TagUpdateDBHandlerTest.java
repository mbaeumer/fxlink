package se.mbaeumer.fxlink.handlers;

import junit.framework.TestCase;
import org.junit.Assert;
import se.mbaeumer.fxlink.models.Tag;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by martinbaumer on 02/01/17.
 */
public class TagUpdateDBHandlerTest extends TestCase {

    public void testReturnNullIfTagIsNull(){
        Assert.assertNull(TagUpdateDBHandler.constructSqlString(null));
    }

    public void testConstructSqlString(){
        Tag tag = createTag();
        String expected = "UPDATE Tag SET name='someTag', description='this is an existing tag', lastUpdated='"
                + tag.getLastUpdated() + "' WHERE id=1";
        String actual = TagUpdateDBHandler.constructSqlString(tag);
        assertTrue("The strings do not match", expected.equalsIgnoreCase(actual));
    }

    private Tag createTag(){
        Tag tag = new Tag();
        tag.setId(1);
        tag.setName("someTag");
        tag.setDescription("this is an existing tag");
        tag.setLastUpdated(createTimestamp());
        return tag;
    }

    private Timestamp createTimestamp(){
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return Timestamp.valueOf(df.format(new Date()));
    }
}
