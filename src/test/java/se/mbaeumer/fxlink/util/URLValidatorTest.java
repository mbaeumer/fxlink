package se.mbaeumer.fxlink.util;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class URLValidatorTest{
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
		assertTrue(URLValidator.isValidURL("lotto.co"));
		assertTrue(URLValidator.isValidURL("example.com"));
	}

	@Test
	public void testInvalidUrl(){
		assertFalse("www.ba should fail", URLValidator.isValidURL("www.ba"));
		assertFalse("www.ba. should fail", URLValidator.isValidURL("www.ba."));
		assertFalse("www.ba.d should fail", URLValidator.isValidURL("www.ba.d"));
		assertFalse("www.bald should fail", URLValidator.isValidURL("www.bald"));
		assertFalse("htpp://sport.com. should fail", URLValidator.isValidURL("htpp://sport.com"));
		assertFalse("lotto:pe should fail", URLValidator.isValidURL("lott:pe"));
	}

	@Test
	public void testAllowUrlWithFourCharsinDomainSuffix(){
		assertTrue(URLValidator.isValidURL("https://allegro.tech/2018"));
	}
}
