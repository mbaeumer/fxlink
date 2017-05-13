package se.mbaeumer.fxlink.util;

import junit.framework.TestCase;
import org.junit.Test;

/**
 * Created by martinbaumer on 13/05/17.
 */
public class SqlExeptionMapperTest extends TestCase {
    @Test
    public void testDuplicate(){
        assertTrue("The error message is not as expected!", SqlExceptionMapper.constructErrorMessage("unique constraint").equalsIgnoreCase("The URL does already exist"));
    }

    @Test
    public void testTooLong(){
        assertTrue("The error message is not as expected!", SqlExceptionMapper.constructErrorMessage("data exception").equalsIgnoreCase("The URL is too long"));
    }
}
