package se.mbaeumer.fxlink.util;

import junit.framework.TestCase;
import org.junit.Test;

public class URLValidatorTest extends TestCase {
	@Test
	public void testValidURL(){
		assertTrue(URLValidator.isValidURL("www.kicker.de"));
		assertTrue(URLValidator.isValidURL("http://www.java-forums.org/"));
		assertTrue(URLValidator.isValidURL("http://www.java-forums.org/newjava"));
		assertTrue(URLValidator.isValidURL("http://www.java-forums.org/newjavablalabla/"));
		assertTrue(URLValidator.isValidURL("http://www.java-forums.org/newjava/nonblockingcallbackmethod"));
		assertTrue(URLValidator.isValidURL("http://www.java-forums.org/new-java"));
		assertTrue(URLValidator.isValidURL("http://www.java-forums.org/new-java/71402-non-blocking-callback-method"));
		assertTrue(URLValidator.isValidURL("http://www.java-forums.org/new-java/71402-non-blocking-callback-method.html"));
		assertFalse(URLValidator.isValidURL("htpp://sport.com"));
		assertFalse(URLValidator.isValidURL("lott:pe"));
		assertTrue(URLValidator.isValidURL("lotto.co"));
		assertTrue(URLValidator.isValidURL("example.com"));
		assertFalse(URLValidator.isValidURL("www.ba"));
		assertFalse(URLValidator.isValidURL("www.ba."));
		assertFalse(URLValidator.isValidURL("www.ba.d"));
		assertFalse(URLValidator.isValidURL("www.bald"));

	}
}
