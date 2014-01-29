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

package org.biokoframework.http.requestbuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.biokoframework.http.multipart.RequestPart;
import org.biokoframework.system.KILL_ME.commons.GenericFieldValues;
import org.biokoframework.system.KILL_ME.commons.logger.Loggers;
import org.biokoframework.system.entity.binary.BinaryEntity;
import org.biokoframework.utils.fields.Fields;

public class BinaryPartBuilder implements FieldsFromPartBuilder {

	private static final ArrayList<String> ACCEPTED_CONTENT_TYPES = new ArrayList<String>(Arrays.asList(
			GenericFieldValues.OCTET_CONTENT_TYPE, GenericFieldValues.JPEG_CONTENT_TYPE, 
			GenericFieldValues.PNG_CONTENT_TYPE, GenericFieldValues.GIF_CONTENT_TYPE));
	
	@Override
	public Fields build(RequestPart aPart, String charset) {		
		Loggers.engagedInterface.info(new StringBuilder().
				append("REQUEST PART: " + aPart.getName() + "\n").
				append("content type: " + aPart.getContentType() + "\n").
				append("content size: " + aPart.getSize() + "\n").
				toString());
				
		Fields fields = Fields.empty(); 

		try {
			Fields blobFields = Fields.empty();
			//blobFields.put(Blob.STREAM, aPart.getInputStream());			
			blobFields.put(BinaryEntity.MEDIA_TYPE, aPart.getContentType());
			blobFields.put(BinaryEntity.SIZE_BYTES, Long.toString(aPart.getSize()));
			BinaryEntity blob = new BinaryEntity(blobFields);
			blob.setStream(aPart.getInputStream());
			fields.put(aPart.getName(), blob);
		} catch (IOException exception) {
			Loggers.engagedInterface.error("Accessing stream of request content part ("+aPart.getName()+","+aPart.getContentType()+")", exception);
			return Fields.empty();
		}
		
		return fields;
	}
	
	@Override
	public boolean canHandle(RequestPart part) {
		for (String anAcceptedContentType : ACCEPTED_CONTENT_TYPES) {
			if (part.getContentType().startsWith(anAcceptedContentType)) {
				return true;
			}
		}
		return false;
	}

}
