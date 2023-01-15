package se.mbaeumer.fxlink.handlers;

import org.junit.Assert;
import org.junit.Test;
import se.mbaeumer.fxlink.models.Tag;

/**
 * Created by martinbaumer on 12/01/17.
 */
public class TagDeletionDBHandlerTest {

    @Test
    public void testReturnNullIfTagIsNull(){
        Assert.assertNull("should be null", TagDeletionDBHandler.constructSqlString(null));
    }

    @Test
    public void testConstructSqlString(){
        String expected = "DELETE FROM Tag WHERE id=1";
        String actual = TagDeletionDBHandler.constructSqlString(createTag());
        Assert.assertTrue("The strings do not match", expected.equalsIgnoreCase(actual));
    }

    private Tag createTag(){
        Tag tag = new Tag();
        tag.setName("someNewTag");
        tag.setId(1);
        tag.setDescription("this is a new tag");
        return tag;
    }
}
