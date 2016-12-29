package se.mbaeumer.fxlink.handlers;

import junit.framework.TestCase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class LinkSearchDBHandlerTest extends TestCase {
	private String searchTerm = null;
	private boolean isUrl = false;
	private boolean isTitle = false;
	private boolean isDescription = false;
	
	@Before
	public void setUp(){
		searchTerm = null;
		isUrl = false;
		isTitle = false;
		isDescription = false;
	}
	
	@Test
	public void testReturnNullIfSearchStringIsNull(){
		Assert.assertNull("The method should return null", LinkSearchDBHandler.constructSearchString(searchTerm, isUrl, isTitle, isDescription));
	}
	
	@Test
	public void testReturnNullIfSearchStringIsEmpty(){
		searchTerm = "";
		Assert.assertNull("The method should return null", LinkSearchDBHandler.constructSearchString(searchTerm, isUrl, isTitle, isDescription));
	}

	@Test
	public void testReturnNullIfNoCriteriaIsSelected(){
		searchTerm = "test";
		Assert.assertNull("The method should return null", LinkSearchDBHandler.constructSearchString(searchTerm, isUrl, isTitle, isDescription));
	}
	
	@Test
	public void testOnlyURL(){
		searchTerm = "test";
		isUrl = true;
		String expectedSqlString = LinkSearchDBHandler.BASE_QUERY + LinkSearchDBHandler.WHERE
				+ LinkSearchDBHandler.URL_CRITERIA_START + searchTerm + LinkSearchDBHandler.URL_CRITERIA_END;
		Assert.assertEquals(expectedSqlString, LinkSearchDBHandler.constructSearchString(searchTerm, isUrl, isTitle, isDescription));
	}
	
	@Test
	public void testOnlyTitle(){
		searchTerm = "test";
		isTitle = true;
		String expectedSqlString = LinkSearchDBHandler.BASE_QUERY + LinkSearchDBHandler.WHERE
				+ LinkSearchDBHandler.TITLE_CRITERIA_START + searchTerm + LinkSearchDBHandler.TITLE_CRITERIA_END;
		Assert.assertEquals(expectedSqlString, LinkSearchDBHandler.constructSearchString(searchTerm, isUrl, isTitle, isDescription));
	}
	
	@Test
	public void testOnlyDescription(){
		searchTerm = "test";
		isDescription = true;
		String expectedSqlString = LinkSearchDBHandler.BASE_QUERY + LinkSearchDBHandler.WHERE
				+ LinkSearchDBHandler.DESCRIPTION_CRITERIA_START + searchTerm + LinkSearchDBHandler.DESCRIPTION_CRITERIA_END;
		Assert.assertEquals(expectedSqlString, LinkSearchDBHandler.constructSearchString(searchTerm, isUrl, isTitle, isDescription));
	}
	
	public void testAllCriteria(){
		searchTerm = "test";
		isUrl = true;
		isTitle = true;
		isDescription = true;
		
		String expectedSqlString = LinkSearchDBHandler.BASE_QUERY + LinkSearchDBHandler.WHERE
				+ LinkSearchDBHandler.URL_CRITERIA_START + searchTerm + LinkSearchDBHandler.URL_CRITERIA_END
				+ LinkSearchDBHandler.OR
				+ LinkSearchDBHandler.TITLE_CRITERIA_START + searchTerm + LinkSearchDBHandler.TITLE_CRITERIA_END
				+ LinkSearchDBHandler.OR
				+ LinkSearchDBHandler.DESCRIPTION_CRITERIA_START + searchTerm + LinkSearchDBHandler.DESCRIPTION_CRITERIA_END;
		Assert.assertEquals(expectedSqlString, LinkSearchDBHandler.constructSearchString(searchTerm, isUrl, isTitle, isDescription));
	}
	
	public void testUrlAndTitle(){
		searchTerm = "test";
		isUrl = true;
		isTitle = true;
				
		String expectedSqlString = LinkSearchDBHandler.BASE_QUERY + LinkSearchDBHandler.WHERE
				+ LinkSearchDBHandler.URL_CRITERIA_START + searchTerm + LinkSearchDBHandler.URL_CRITERIA_END
				+ LinkSearchDBHandler.OR
				+ LinkSearchDBHandler.TITLE_CRITERIA_START + searchTerm + LinkSearchDBHandler.TITLE_CRITERIA_END;
		Assert.assertEquals(expectedSqlString, LinkSearchDBHandler.constructSearchString(searchTerm, isUrl, isTitle, isDescription));
	}
	
	public void testUrlAndDescription(){
		searchTerm = "test";
		isUrl = true;
		isDescription = true;
				
		String expectedSqlString = LinkSearchDBHandler.BASE_QUERY + LinkSearchDBHandler.WHERE
				+ LinkSearchDBHandler.URL_CRITERIA_START + searchTerm + LinkSearchDBHandler.URL_CRITERIA_END
				+ LinkSearchDBHandler.OR
				+ LinkSearchDBHandler.DESCRIPTION_CRITERIA_START + searchTerm + LinkSearchDBHandler.DESCRIPTION_CRITERIA_END;
		Assert.assertEquals(expectedSqlString, LinkSearchDBHandler.constructSearchString(searchTerm, isUrl, isTitle, isDescription));
	}
	
	public void testTitleAndDescription(){
		searchTerm = "test";
		isTitle = true;
		isDescription = true;
				
		String expectedSqlString = LinkSearchDBHandler.BASE_QUERY + LinkSearchDBHandler.WHERE
				+ LinkSearchDBHandler.TITLE_CRITERIA_START + searchTerm + LinkSearchDBHandler.TITLE_CRITERIA_END
				+ LinkSearchDBHandler.OR
				+ LinkSearchDBHandler.DESCRIPTION_CRITERIA_START + searchTerm + LinkSearchDBHandler.DESCRIPTION_CRITERIA_END;
		Assert.assertEquals(expectedSqlString, LinkSearchDBHandler.constructSearchString(searchTerm, isUrl, isTitle, isDescription));
	}
	


}
