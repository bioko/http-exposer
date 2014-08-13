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

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;
import org.biokoframework.utils.fields.Fields;

import javax.servlet.http.Part;
import java.io.IOException;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014-08-12
 */
public class StringPartParser implements IPartParser {

    private static final Logger LOGGER = Logger.getLogger(StringPartParser.class);

    @Override
    public Fields parse(Part part, String charset) {
        Fields parsed = new Fields();

        try {
            String value = IOUtils.toString(part.getInputStream(), charset);
            parsed.put(part.getName(), convertToNumberIfPossibile(value));
        } catch (IOException exception) {
            LOGGER.error("Parsing part " + part.getName(), exception);
        }


        return parsed;
    }

    private Object convertToNumberIfPossibile(String value) {
        try {
            return Long.parseLong(value);
        } catch (NumberFormatException exception) {
            LOGGER.debug("'" + value + "' is not an integer");
        }

        try {
            return Double.parseDouble(value);
        } catch (NumberFormatException exception) {
            LOGGER.debug("'" + value + "' is not a float");
        }

        return value;
    }

}
