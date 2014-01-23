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

import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.KILL_ME.commons.GenericFieldValues;
import org.biokoframework.system.KILL_ME.commons.logger.Loggers;
import org.biokoframework.system.entity.binary.BinaryEntity;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;
import org.biokoframework.utils.fields.Fields;

public class BinaryResponseBuilder extends ResponseFromFieldsBuilder {

	private static final ArrayList<String> ACCEPTED_CONTENT_TYPES = new ArrayList<String>(Arrays.asList(
			GenericFieldValues.OCTET_CONTENT_TYPE, 
			GenericFieldValues.JPEG_CONTENT_TYPE, GenericFieldValues.PNG_CONTENT_TYPE, GenericFieldValues.GIF_CONTENT_TYPE));
	
	private Fields _output;
	
	@Override
	public boolean canHandle(String responseType) {
		for (String anAcceptedContentType : ACCEPTED_CONTENT_TYPES) {
			if (anAcceptedContentType.equals(responseType)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void build(HttpServletResponse servletResponse) throws Exception {
		servletResponse.setContentType(_output.stringNamed(GenericFieldNames.RESPONSE_CONTENT_TYPE));
		
		Object response = _output.valueFor(GenericFieldNames.RESPONSE);
		InputStream inputStream = null;
		if (response instanceof InputStream) {
			// Backward compatibility
			inputStream = (InputStream) response;
		} else {
			BinaryEntity blob  = ((ArrayList<BinaryEntity>) response).get(0);
			
			servletResponse.setHeader("Content-Length", blob.get(BinaryEntity.SIZE_BYTES));
			inputStream = blob.getStream();
		}

		if (inputStream != null) {
			ServletOutputStream outputStream = servletResponse.getOutputStream();
			IOUtils.copy(inputStream, outputStream);
			inputStream.close();
			outputStream.close();
		}
		
		Loggers.engagedInterface.info("OUT: binary file - " + 
				_output.stringNamed(GenericFieldNames.RESPONSE_CONTENT_TYPE));
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
