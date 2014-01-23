/*
 * Copyright (c) 2014																 
 *	Mikol Faro			<mikol.faro@gmail.com>
 *	Simone Mangano		<simone.mangano@ieee.org>
 *	Mattia Tortorelli	<mattia.tortorelli@gmail.com>
 *
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 * 
 */

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