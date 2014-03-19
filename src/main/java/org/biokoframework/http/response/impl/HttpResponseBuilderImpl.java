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

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.biokoframework.http.fields.RequestNotSupportedException;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.utils.domain.ErrorEntity;
import org.biokoframework.utils.fields.FieldNames;
import org.biokoframework.utils.fields.Fields;
import org.json.simple.JSONValue;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 26, 2014
 *
 */
public class HttpResponseBuilderImpl extends AbstractHttpResponseBuilder {

	private static final String JSON_EXTENSION = "json";
	private static final String APPLICATION_JSON = "application/json";
    private final Map<String, String> fHeadersMapping;

    @Inject
    public HttpResponseBuilderImpl(@Named("fieldsHttpHeaderToMap") Map<String, String> headersMapping) {
        fHeadersMapping = headersMapping;
    }

	@Override
	protected void safelyBuild(HttpServletRequest request, HttpServletResponse response, Fields input, Fields output)
			throws IOException, RequestNotSupportedException {
		
		response.setContentType(APPLICATION_JSON + withCharsetFrom(request));
		
		Object responseContent = output.get(GenericFieldNames.RESPONSE);

        for (Map.Entry<String, String> entry : fHeadersMapping.entrySet()) {
            if (output.containsKey(entry.getKey())) {
                response.setHeader(entry.getValue(), output.get(entry.getKey()).toString());
            }
        }
		
		Writer writer = response.getWriter();
		IOUtils.write(JSONValue.toJSONString(responseContent), writer);
	}
	
	@Override
	protected void checkAcceptType(List<String> acceptedTypes, String extension) throws RequestNotSupportedException {

		if (!StringUtils.isEmpty(extension) && !JSON_EXTENSION.equals(extension)) {
			ErrorEntity entity = new ErrorEntity();
			entity.setAll(new Fields(
					ErrorEntity.ERROR_CODE, FieldNames.UNSUPPORTED_FORMAT_CODE,
					ErrorEntity.ERROR_MESSAGE, "Request with extension " + extension + " are not supported"));
			throw new RequestNotSupportedException(entity);
		} else if (acceptedTypes != null && !acceptedTypes.isEmpty()) {
			for (String anAcceptedType : acceptedTypes) {
				if (anAcceptedType.startsWith(APPLICATION_JSON) || anAcceptedType.startsWith(JOLLY_TYPE)) {
					return;
				}
			}
			ErrorEntity entity = new ErrorEntity();
			entity.setAll(new Fields(
					ErrorEntity.ERROR_CODE, FieldNames.UNSUPPORTED_FORMAT_CODE,
					ErrorEntity.ERROR_MESSAGE, "Response with type " + extension + " are not supported"));
			throw new RequestNotSupportedException(entity);
		}
	}
	
}
