package org.biokoframework.http.fields.impl;

import com.google.common.net.MediaType;
import org.apache.commons.io.IOUtils;
import org.biokoframework.http.fields.RequestNotSupportedException;
import org.biokoframework.utils.fields.Fields;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Reader;
import java.net.URLDecoder;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014-07-07
 */
public class UrlEncodedFieldsParser extends AbstractFieldsParser {

    public static final MediaType X_WWW_FORM_URLENCODED = MediaType.FORM_DATA;

    @Override
    protected Fields safelyParse(HttpServletRequest request) throws RequestNotSupportedException {
        Reader reader;
        try {
            if (request.getCharacterEncoding() == null) {
                request.setCharacterEncoding("utf-8");
            }
            reader = request.getReader();

            Fields parsedFields = new Fields();
            for (String aToken : IOUtils.toString(reader).split("&")) {
                int separator = aToken.indexOf("=");
                String aKey = URLDecoder.decode(aToken.substring(0, separator), request.getCharacterEncoding());
                String aValue = URLDecoder.decode(aToken.substring(separator + 1), request.getCharacterEncoding());
                parsedFields.put(aKey, aValue);
            }

            return parsedFields;
        } catch (IOException exception) {
            // TODO log exception
            throw new RequestNotSupportedException(exception);
        }
    }

    @Override
    protected void checkContentType(MediaType mediaType) throws RequestNotSupportedException {
        if (!isCompatible(mediaType)){
            throw badContentType(mediaType, X_WWW_FORM_URLENCODED);
        }
    }

    @Override
    public boolean isCompatible(MediaType mediaType) {
        return mediaType.withoutParameters().is(X_WWW_FORM_URLENCODED);
    }

}
