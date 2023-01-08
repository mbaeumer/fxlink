package se.mbaeumer.fxlink.handlers;

import org.junit.Assert;
import org.junit.Test;
import se.mbaeumer.fxlink.models.Tag;

/**
 * Created by martinbaumer on 30/12/16.
 */
public class TagCreationDBHandlerTest{

    @Test
    public void testReturnNullIfTagIsNull(){
        Assert.assertNull(TagCreationDBHandler.constructSqlString(null));
    }

    @Test
    public void testConstructSqlString(){
        Tag tag = createTag();
        String expected = "INSERT INTO Tag VALUES(DEFAULT, 'someNewTag', 'this is a new tag', DEFAULT, DEFAULT)";
        String actual = TagCreationDBHandler.constructSqlString(tag);
        Assert.assertEquals(actual, expected);
    }

    private Tag createTag(){
        Tag tag = new Tag();
        tag.setName("someNewTag");
        tag.setDescription("this is a new tag");
        return tag;
    }
}
