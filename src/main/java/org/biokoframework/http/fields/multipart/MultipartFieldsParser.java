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

import org.apache.commons.lang3.StringUtils;
import org.biokoframework.http.fields.RequestNotSupportedException;
import org.biokoframework.http.fields.impl.AbstractFieldsParser;
import org.biokoframework.utils.fields.Fields;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Part;
import java.io.IOException;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014-08-12
 */
public class MultipartFieldsParser extends AbstractFieldsParser {

    private static final String MULTIPART_TYPE = "multipart/form-data";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";

    private final IPartParser fFilePartParser;
    private final IPartParser fStringPartParser;

    @Inject
    public MultipartFieldsParser(FileDataPartParser filePartParser, StringPartParser stringPartParser) {
        fFilePartParser = filePartParser;
        fStringPartParser = stringPartParser;
    }

    @Override
    protected Fields safelyParse(HttpServletRequest request) throws RequestNotSupportedException {
        Fields fields = new Fields();

        try {
            for(Part aPart : request.getParts()) {
                if (isAFilePart(aPart)) {
                    fields.putAll(fFilePartParser.parse(aPart, null));
                } else {
                    fields.putAll(fStringPartParser.parse(aPart, null));
                }
            }
        } catch (IOException | ServletException e) {
            e.printStackTrace();
        }

        return fields;
    }

    private boolean isAFilePart(Part part) {
        return !StringUtils.isEmpty(part.getContentType());
    }


    @Override
    protected void checkContentType(String contentType) throws RequestNotSupportedException {
        if (!isCompatible(contentType)){
            throw badContentType(contentType, MULTIPART_TYPE);
        }
    }

    @Override
    public boolean isCompatible(String contentType) {
        return StringUtils.startsWith(contentType, MULTIPART_TYPE);
    }

}
