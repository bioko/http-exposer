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

package org.biokoframework.http.responsebuilder;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.KILL_ME.commons.GenericFieldValues;
import org.biokoframework.system.KILL_ME.commons.logger.Loggers;
import org.biokoframework.utils.fields.Fields;
import org.json.simple.JSONValue;

public class JSONResponseBuilder extends ResponseFromFieldsBuilder {

	private static final String JSON_CHARSET_UTF_8 = "application/json; charset=UTF-8";
	private Fields _output;
	
	@Override
	public boolean canHandle(String responseType) {
		return GenericFieldValues.JSON_CONTENT_TYPE.equals(responseType);
	}

	@Override
	public void build(HttpServletResponse response) throws Exception {
		Object responseJSON = _output.get(GenericFieldNames.RESPONSE);
		
		if (_output.contains(GenericFieldNames.TOKEN_HEADER)) {
			response.addHeader(GenericFieldNames.TOKEN_HEADER, _output.get(GenericFieldNames.TOKEN_HEADER).toString());
		}
		if (_output.contains(GenericFieldNames.TOKEN_EXPIRE_HEADER)) {
			response.addHeader(GenericFieldNames.TOKEN_EXPIRE_HEADER, _output.get(GenericFieldNames.TOKEN_EXPIRE_HEADER).toString());
		}
	
		response.setContentType(JSON_CHARSET_UTF_8 );
		PrintWriter writer = response.getWriter();
		JSONValue.writeJSONString(responseJSON, writer);
		Loggers.engagedInterface.info("OUT: " + JSONValue.toJSONString(responseJSON));
	}

	@Override
	public ResponseFromFieldsBuilder setInput(Fields input) {
		return this;
	}

	@Override
	public ResponseFromFieldsBuilder setOutput(Fields output) {
		_output = output;
		return this;
	}

}
