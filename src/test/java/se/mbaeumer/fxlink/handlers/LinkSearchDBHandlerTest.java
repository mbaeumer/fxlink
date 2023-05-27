package se.mbaeumer.fxlink.handlers;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import se.mbaeumer.fxlink.models.Category;
import se.mbaeumer.fxlink.util.ValueConstants;

import java.util.Date;

public class LinkSearchDBHandlerTest{
	private String searchTerm = null;
	private boolean isUrl = false;
	private boolean isTitle = false;
	private boolean isDescription = false;
	private LinkSearchDBHandler linkSearchDBHandler;

	@Before
	public void setUp(){
		searchTerm = null;
		isUrl = false;
		isTitle = false;
		isDescription = false;
		linkSearchDBHandler = new LinkSearchDBHandler();
	}

	@Test
	public void testReturnNullIfSearchStringIsNull(){
		Assert.assertNull("The method should return null", linkSearchDBHandler.constructSearchString(searchTerm, isUrl, isTitle, isDescription, createPseudoCategory(ValueConstants.VALUE_ALL)));
	}

	@Test
	public void testReturnNullIfSearchStringIsEmpty(){
		searchTerm = "";
		Assert.assertNull("The method should return null", linkSearchDBHandler.constructSearchString(searchTerm, isUrl, isTitle, isDescription, createPseudoCategory(ValueConstants.VALUE_ALL)));
	}

	@Test
	public void testReturnNullIfNoCriteriaIsSelected(){
		searchTerm = "test";
		Assert.assertNull("The method should return null", linkSearchDBHandler.constructSearchString(searchTerm, isUrl, isTitle, isDescription, createPseudoCategory(ValueConstants.VALUE_ALL)));
	}

	@Test
	public void testOnlyURL(){
		searchTerm = "test";
		isUrl = true;
		String expectedSqlString =
				LinkSearchDBHandler.BASE_QUERY + "where (LCASE(url) LIKE '%test%' OR LCASE(url) LIKE 'test%' )";
		String actual = linkSearchDBHandler.constructSearchString(searchTerm, isUrl, isTitle, isDescription, createPseudoCategory(ValueConstants.VALUE_ALL));
		System.out.println(expectedSqlString);
		System.out.println(actual);
		Assert.assertEquals(expectedSqlString, linkSearchDBHandler.constructSearchString(searchTerm, isUrl, isTitle, isDescription, createPseudoCategory(ValueConstants.VALUE_ALL)));
	}

	@Test
	public void testOnlyTitle(){
		searchTerm = "test";
		isTitle = true;
		String expectedSqlString =
				LinkSearchDBHandler.BASE_QUERY + "where (LCASE(title) LIKE '%test%' OR LCASE(title) LIKE 'test%' )";
		String actual = linkSearchDBHandler.constructSearchString(searchTerm, isUrl, isTitle, isDescription, createPseudoCategory(ValueConstants.VALUE_ALL));
		System.out.println(expectedSqlString);
		System.out.println(actual);
		Assert.assertEquals(expectedSqlString, linkSearchDBHandler.constructSearchString(searchTerm, isUrl, isTitle, isDescription, createPseudoCategory(ValueConstants.VALUE_ALL)));
	}

	@Test
	public void testOnlyDescription(){
		searchTerm = "test";
		isDescription = true;
		String expectedSqlString =
				LinkSearchDBHandler.BASE_QUERY + "where (LCASE(description) LIKE '%test%' OR LCASE(description) LIKE 'test%' )";
		String actual = linkSearchDBHandler.constructSearchString(searchTerm, isUrl, isTitle, isDescription, createPseudoCategory(ValueConstants.VALUE_ALL));
		System.out.println(expectedSqlString);
		System.out.println(actual);

		Assert.assertEquals(expectedSqlString, linkSearchDBHandler.constructSearchString(searchTerm, isUrl, isTitle, isDescription, createPseudoCategory(ValueConstants.VALUE_ALL)));
	}

	@Test
	public void testAllCriteria(){
		searchTerm = "test";
		isUrl = true;
		isTitle = true;
		isDescription = true;

		String expectedSqlString =
				LinkSearchDBHandler.BASE_QUERY + "where (LCASE(url) LIKE '%test%' OR LCASE(url) LIKE 'test%' "
						+ "OR LCASE(title) LIKE '%test%' OR LCASE(title) LIKE 'test%' "
				+ "OR LCASE(description) LIKE '%test%' OR LCASE(description) LIKE 'test%' )";

		/*
		String expectedSqlString = LinkSearchDBHandler.BASE_QUERY + LinkSearchDBHandler.WHERE + "("
				+ LinkSearchDBHandler.URL_CRITERIA_START + searchTerm + LinkSearchDBHandler.URL_CRITERIA_END
				+ LinkSearchDBHandler.OR
				+ LinkSearchDBHandler.TITLE_CRITERIA_START + searchTerm + LinkSearchDBHandler.TITLE_CRITERIA_END
				+ LinkSearchDBHandler.OR
				+ LinkSearchDBHandler.DESCRIPTION_CRITERIA_START + searchTerm + LinkSearchDBHandler.DESCRIPTION_CRITERIA_END + ")";

		 */
		Assert.assertEquals(expectedSqlString, linkSearchDBHandler.constructSearchString(searchTerm, isUrl, isTitle, isDescription, createPseudoCategory(ValueConstants.VALUE_ALL)));
	}

	@Test
	public void testUrlAndTitle(){
		searchTerm = "test";
		isUrl = true;
		isTitle = true;

		String expectedSqlString =
				LinkSearchDBHandler.BASE_QUERY + "where (LCASE(url) LIKE '%test%' OR LCASE(url) LIKE 'test%' "
				+ "OR LCASE(title) LIKE '%test%' OR LCASE(title) LIKE 'test%' )";
		// LCASE(title) LIKE '%test%' OR LCASE(title) LIKE 'test%'
		String actual = linkSearchDBHandler.constructSearchString(searchTerm, isUrl, isTitle, isDescription, createPseudoCategory(ValueConstants.VALUE_ALL));
		System.out.println(expectedSqlString);
		System.out.println(actual);

		Assert.assertEquals(expectedSqlString, linkSearchDBHandler.constructSearchString(searchTerm, isUrl, isTitle, isDescription, createPseudoCategory(ValueConstants.VALUE_ALL)));
	}

	@Test
	public void testUrlAndDescription(){
		searchTerm = "test";
		isUrl = true;
		isDescription = true;

		String expectedSqlString =
				LinkSearchDBHandler.BASE_QUERY + "where (LCASE(url) LIKE '%test%' OR LCASE(url) LIKE 'test%' "
						+ "OR LCASE(description) LIKE '%test%' OR LCASE(description) LIKE 'test%' )";

		Assert.assertEquals(expectedSqlString, linkSearchDBHandler.constructSearchString(searchTerm, isUrl, isTitle, isDescription, createPseudoCategory(ValueConstants.VALUE_ALL)));
	}

	@Test
	public void testTitleAndDescription(){
		searchTerm = "test";
		isTitle = true;
		isDescription = true;

		String expectedSqlString =
				LinkSearchDBHandler.BASE_QUERY + "where (LCASE(title) LIKE '%test%' OR LCASE(title) LIKE 'test%' "
						+ "OR LCASE(description) LIKE '%test%' OR LCASE(description) LIKE 'test%' )";
		Assert.assertEquals(expectedSqlString, linkSearchDBHandler.constructSearchString(searchTerm, isUrl, isTitle, isDescription, createPseudoCategory(ValueConstants.VALUE_ALL)));
	}

	@Test
	public void testTitleAndDescriptionAndCategoryIdIsNull(){
		searchTerm = "test";
		isTitle = true;
		isDescription = true;

		String expectedSqlString =
				LinkSearchDBHandler.BASE_QUERY + "where (LCASE(title) LIKE '%test%' OR LCASE(title) LIKE 'test%' "
						+ "OR LCASE(description) LIKE '%test%' OR LCASE(description) LIKE 'test%' )"
				+ "AND categoryId IS NULL";

		String actual = linkSearchDBHandler.constructSearchString(searchTerm, isUrl, isTitle, isDescription, createPseudoCategory(ValueConstants.VALUE_N_A));
		System.out.println(expectedSqlString);
		System.out.println(actual);

		/*
		String expectedSqlString = LinkSearchDBHandler.BASE_QUERY + LinkSearchDBHandler.WHERE + "("
				+ LinkSearchDBHandler.TITLE_CRITERIA_START + searchTerm + LinkSearchDBHandler.TITLE_CRITERIA_END
				+ LinkSearchDBHandler.OR
				+ LinkSearchDBHandler.DESCRIPTION_CRITERIA_START + searchTerm + LinkSearchDBHandler.DESCRIPTION_CRITERIA_END + ")"
				+ LinkSearchDBHandler.AND + LinkSearchDBHandler.CATEGORY_ID + LinkSearchDBHandler.IS_NULL;
		*/
		Assert.assertEquals(expectedSqlString, linkSearchDBHandler.constructSearchString(searchTerm, isUrl, isTitle, isDescription, createPseudoCategory(ValueConstants.VALUE_N_A)));
	}

	@Test
	public void testTitleAndDescriptionAndCategoryIdIsSet(){
		searchTerm = "test";
		isTitle = true;
		isDescription = true;

		String expectedSqlString =
				LinkSearchDBHandler.BASE_QUERY + "where (LCASE(title) LIKE '%test%' OR LCASE(title) LIKE 'test%' "
						+ "OR LCASE(description) LIKE '%test%' OR LCASE(description) LIKE 'test%' )"
						+ "AND categoryId = ?";

		String actual = linkSearchDBHandler.constructSearchString(searchTerm, isUrl, isTitle, isDescription, createCategoryWithNameJava());
		System.out.println(expectedSqlString);
		System.out.println(actual);

		/*
		String expectedSqlString = LinkSearchDBHandler.BASE_QUERY + LinkSearchDBHandler.WHERE + "("
				+ LinkSearchDBHandler.TITLE_CRITERIA_START + searchTerm + LinkSearchDBHandler.TITLE_CRITERIA_END
				+ LinkSearchDBHandler.OR
				+ LinkSearchDBHandler.DESCRIPTION_CRITERIA_START + searchTerm + LinkSearchDBHandler.DESCRIPTION_CRITERIA_END + ")"
				+ LinkSearchDBHandler.AND + LinkSearchDBHandler.CATEGORY_ID + LinkSearchDBHandler.EQUALS + "?";
		*/
		Assert.assertEquals(expectedSqlString, linkSearchDBHandler.constructSearchString(searchTerm, isUrl, isTitle, isDescription, createCategoryWithNameJava()));
	}

	@Test
	public void buildWhereClause() {
		String expected = "LCASE(url) LIKE '%test%' OR LCASE(url) LIKE 'test%' ";
		Assert.assertEquals(expected, linkSearchDBHandler.buildWhereClause("url", "test"));
	}

	private Category createPseudoCategory(String categoryName){
		Category category = new Category();
		category.setId(-1);
		category.setName(categoryName);
		category.setDescription(categoryName);
		category.setCreated(new Date());
		category.setLastUpdated(new Date());
		return category;
	}

	private Category createCategoryWithNameJava(){
		Category category = new Category();
		category.setId(1);
		category.setName("Java");
		category.setDescription("Java");
		category.setCreated(new Date());
		category.setLastUpdated(new Date());
		return category;
	}



}
