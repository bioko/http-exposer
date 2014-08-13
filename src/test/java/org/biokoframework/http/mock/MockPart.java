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

package org.biokoframework.http.mock;

import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014-08-12
 */
public class MockPart implements Part {

    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private final String fContentDisposition;
    private final String fName;
    private final String fContentType;

    public MockPart(String contentDisposition, String name) {
        this(contentDisposition, name, null);
    }

    public MockPart(String contentDisposition, String name, String contentType) {
        fContentDisposition = contentDisposition;
        fName = name;
        fContentType = contentType;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getContentType() {
        return fContentType;
    }

    @Override
    public String getName() {
        if (fName == null) {
            throw new UnsupportedOperationException();
        } else {
            return fName;
        }
    }

    @Override
    public String getSubmittedFileName() {
        throw new UnsupportedOperationException();
    }

    @Override
    public long getSize() {
        throw new UnsupportedOperationException();
    }

    @Override
    public void write(String s) throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public void delete() throws IOException {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getHeader(String headerName) {
        if (headerName.equals(CONTENT_DISPOSITION)) {
            return fContentDisposition;
        } else {
            throw new UnsupportedOperationException();
        }
    }

    @Override
    public Collection<String> getHeaders(String s) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<String> getHeaderNames() {
        throw new UnsupportedOperationException();
    }
}
