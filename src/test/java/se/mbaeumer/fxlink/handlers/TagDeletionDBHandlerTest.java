package se.mbaeumer.fxlink.handlers;

import junit.framework.TestCase;
import org.junit.Assert;
import se.mbaeumer.fxlink.models.Tag;

/**
 * Created by martinbaumer on 12/01/17.
 */
public class TagDeletionDBHandlerTest extends TestCase {

    public void testReturnNullIfTagIsNull(){
        Assert.assertNull("should be null", TagDeletionDBHandler.constructSqlString(null));
    }

    public void testConstructSqlString(){
        String expected = "DELETE FROM Tag WHERE id=1";
        String actual = TagDeletionDBHandler.constructSqlString(createTag());
        assertTrue("The strings do not match", expected.equalsIgnoreCase(actual));
    }

    private Tag createTag(){
        Tag tag = new Tag();
        tag.setName("someNewTag");
        tag.setId(1);
        tag.setDescription("this is a new tag");
        return tag;
    }
}
