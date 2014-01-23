package it.bioko.http.rest;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import it.bioko.http.rest.FieldsFromRestRequestBuilder;
import it.bioko.utils.domain.DomainEntity;
import it.bioko.utils.fields.FieldNames;
import it.bioko.utils.fields.Fields;

import java.util.HashMap;

import org.junit.Test;

public class FieldsFromRestURLBuilderTest {

	@Test
	public void retrieveFiledName() throws IllegalArgumentException, IllegalAccessException {
		String actual = new FieldsFromRestRequestBuilder().retrieveFieldName("contact".toUpperCase(), "id".toUpperCase());
		assertThat(actual, equalTo("contactId"));
	}
	
	@Test
	public void contactGet() {
		Fields expected = Fields.single(FieldNames.COMMAND_NAME, "GET_contact");
		expected.put(DomainEntity.ID, "1");
		
		FieldsFromRestRequestBuilder fieldsBuilder = new FieldsFromRestRequestBuilder();
		fieldsBuilder.setPathInfo("/contact/1");
		fieldsBuilder.setMethod("GET");
		fieldsBuilder.setBody("");
		fieldsBuilder.setParameters(new HashMap<String, String[]>());
		Fields actual = fieldsBuilder.build();
		
		assertThat(actual, equalTo(expected));
	}
	
	@Test
	public void contactPost() {
		Fields expected = Fields.single(FieldNames.COMMAND_NAME, "POST_contact");
		
		FieldsFromRestRequestBuilder fieldsBuilder = new FieldsFromRestRequestBuilder();
		fieldsBuilder.setPathInfo("/contact/");
		fieldsBuilder.setMethod("POST");
		fieldsBuilder.setBody("");
		Fields actual = fieldsBuilder.build();
		
		assertThat(actual.keys(), equalTo(expected.keys()));
		for (String aKey : expected.keys()) {
			assertThat(actual.valueFor(aKey), equalTo(expected.valueFor(aKey)));
		}
	}
}