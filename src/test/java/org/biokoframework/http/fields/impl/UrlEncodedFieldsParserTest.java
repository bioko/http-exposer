package org.biokoframework.http.fields.impl;

import org.biokoframework.http.fields.RequestNotSupportedException;
import org.biokoframework.http.mock.MockRequest;
import org.biokoframework.utils.fields.Fields;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import static org.biokoframework.utils.matcher.Matchers.matchesPattern;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014-07-07
 */
public class UrlEncodedFieldsParserTest {

    @Rule
    public ExpectedException thrown = ExpectedException.none();

    @Test
    public void simpleParsing() throws Exception {
        UrlEncodedFieldsParser parser = new UrlEncodedFieldsParser();

        MockRequest request = new MockRequest("POST", "/something", "name=Rita&surname=Mamma&company=Pizzeria+Mamma+Rita");
        request.setContentType("application/x-www-form-urlencoded");

        Fields expected = new Fields(
                "name", "Rita",
                "surname", "Mamma",
                "company", "Pizzeria Mamma Rita"
        );

        assertThat(parser.parse(request), is(equalTo(expected)));
    }

    @Test
    public void failBecauseOfWrongContentType() throws Exception {
        thrown.expect(RequestNotSupportedException.class);
        thrown.expectMessage(matchesPattern(".*type example not supported.*"));

        JsonFieldsParser parser = new JsonFieldsParser();
        MockRequest request = new MockRequest("POST", "/something", "Some content");
        request.setContentType("example");

        parser.parse(request);
    }

}
