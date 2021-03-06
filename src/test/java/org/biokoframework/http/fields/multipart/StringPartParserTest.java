/*
 * Copyright (c) $year.
 * 	Mikol Faro		<mikol.faro@gmail.com>
 * 	Simone Mangano	 	<simone.mangano@ieee.org>
 * 	Mattia Tortorelli	<mattia.tortorelli@gmail.com>
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
 */

package org.biokoframework.http.fields.multipart;

import org.biokoframework.http.mock.MockFormDataPart;
import org.biokoframework.utils.fields.Fields;
import org.junit.Test;

import static org.biokoframework.utils.matcher.Matchers.contains;
import static org.junit.Assert.assertThat;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014-08-12
 */
public class StringPartParserTest {

    @Test
    public void simpleTest() {
        MockFormDataPart part = new MockFormDataPart("key", "value");

        StringPartParser parser = new StringPartParser();

        Fields result = parser.parse(part, "UTF-8");

        assertThat(result, contains("key", "value"));
    }

    @Test
    public void integerNumberTest() {
        MockFormDataPart part = new MockFormDataPart("integer", "3");

        StringPartParser parser = new StringPartParser();

        Fields result = parser.parse(part, "UTF-8");

        assertThat(result, contains("integer", 3L));
    }

    @Test
    public void floatNumberTest() {
        MockFormDataPart part = new MockFormDataPart("integer", "3.0");

        StringPartParser parser = new StringPartParser();

        Fields result = parser.parse(part, "UTF-8");

        assertThat(result, contains("integer", 3.0));
    }

}
