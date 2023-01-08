package se.mbaeumer.fxlink.util;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by martinbaumer on 13/05/17.
 */
public class SqlExeptionMapperTest{
    @Test
    public void testDuplicate(){
        Assert.assertEquals(SqlExceptionMapper.constructErrorMessage("unique constraint"), "The URL does already exist");
    }

    @Test
    public void testTooLong(){
        Assert.assertEquals(SqlExceptionMapper.constructErrorMessage("data exception"), "The URL is too long");
    }
}
