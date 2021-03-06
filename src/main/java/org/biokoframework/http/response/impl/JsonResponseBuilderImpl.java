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

package org.biokoframework.http.response.impl;

import com.google.common.net.MediaType;
import org.apache.commons.io.IOUtils;
import org.biokoframework.http.fields.RequestNotSupportedException;
import org.biokoframework.http.response.IResponseContentBuilder;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.utils.fields.Fields;
import org.json.simple.JSONValue;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.Writer;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 26, 2014
 *
 */
public class JsonResponseBuilderImpl implements IResponseContentBuilder {


    private static final String JSON_EXTENSION = "json";
    private static final String JS_EXTENSION = "js";


    @Override
    public boolean isCompatibleWith(MediaType mediaType) {
        return MediaType.JSON_UTF_8.is(mediaType);
    }

    @Override
    public boolean isCompatibleWith(String requestExtension) {
        return JSON_EXTENSION.equals(requestExtension) || JS_EXTENSION.equals(requestExtension);
    }

    @Override
    public void build(HttpServletResponse response, Fields output) throws IOException, RequestNotSupportedException {
        response.setContentType(MediaType.JSON_UTF_8.toString());

        Object responseContent = output.get(GenericFieldNames.RESPONSE);

        Writer writer = response.getWriter();
		IOUtils.write(JSONValue.toJSONString(responseContent), writer);
    }

}
