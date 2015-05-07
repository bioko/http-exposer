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
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpStatus;
import org.apache.log4j.Logger;
import org.biokoframework.http.fields.RequestNotSupportedException;
import org.biokoframework.http.response.IHttpResponseBuilder;
import org.biokoframework.http.response.IResponseContentBuilder;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.utils.domain.ErrorEntity;
import org.biokoframework.utils.fields.FieldNames;
import org.biokoframework.utils.fields.Fields;

import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 26, 2014
 *
 */
public class HttpResponseBuilderImpl implements IHttpResponseBuilder {

	private static final String ACCEPT = "Accept";
	private final Pattern fExtensionPattern = Pattern.compile("\\.([a-zA-Z0-9]+)$");
    private final Set<IResponseContentBuilder> fContentBuilders;
    private final Map<String, String> fHeadersMapping;

    @Inject
    public HttpResponseBuilderImpl(@Named("fieldsHttpHeaderToMap") Map<String, String> headersMapping,
                                   Set<IResponseContentBuilder> contentBuilders) {

        fHeadersMapping = headersMapping;
        fContentBuilders = contentBuilders;
    }

	@Override
	public final void build(HttpServletRequest request, HttpServletResponse response, Fields input, Fields output) throws IOException, RequestNotSupportedException {

        addHeaders(output, response);

        IResponseContentBuilder contentBuilder = findContentBuilder(request, output);

        if (contentBuilder != null) {
            response.setStatus(HttpStatus.SC_OK);
            contentBuilder.build(response, output);
        } else {
            cannotSupportRequest();
        }
    }

    private IResponseContentBuilder findContentBuilder(HttpServletRequest request, Fields output) {
        IResponseContentBuilder contentBuilder = null;

        try {

            if (output != null) {
                contentBuilder = findBuilderForForcedContentType(output);
            }
            if (contentBuilder == null) {
                contentBuilder = findBuilderForExtension(request);
            }
            if (contentBuilder == null) {
                contentBuilder = findBuilderByAcceptHeader(request);
            }
            if (contentBuilder == null) {
                contentBuilder = findBuilderByRequestContentType(request);
            }

            List<MediaType> acceptedTypes = getAccept(request);
            if (acceptedTypes.isEmpty() && (request.getMethod().equals("POST") || request.getMethod().equals("PUT"))) {
                if (!StringUtils.isEmpty(request.getContentType())) {
                    acceptedTypes.add(MediaType.parse(request.getContentType().trim()));
                }
            }

        } catch (Exception e) {
            Logger.getLogger(HttpResponseBuilderImpl.class).error("Error when searching content builder", e);
            throw  e;
        }

        return contentBuilder;
    }

    private void cannotSupportRequest() throws RequestNotSupportedException {
        ErrorEntity entity = new ErrorEntity();
        entity.setAll(new Fields(
                ErrorEntity.ERROR_CODE, FieldNames.UNSUPPORTED_FORMAT_CODE,
                ErrorEntity.ERROR_MESSAGE, "Response type not supported"));
        throw new RequestNotSupportedException(entity);
    }

    private void addHeaders(Fields output, HttpServletResponse response) {
        for (Map.Entry<String, String> entry : fHeadersMapping.entrySet()) {
            if (output.containsKey(entry.getKey())) {
                response.setHeader(entry.getValue(), output.get(entry.getKey()).toString());
            }
        }
    }

    private IResponseContentBuilder findBuilderForForcedContentType(Fields output) {
        MediaType mediaType = output.get(GenericFieldNames.RESPONSE_CONTENT_TYPE);
        if (mediaType != null) {
            return findBuilderForMediaType(mediaType);
        }
        return null;
    }

    private IResponseContentBuilder findBuilderForExtension(HttpServletRequest request) {
        String extension = getRequestExtension(request.getPathInfo());
        if (!StringUtils.isEmpty(extension)) {
            for (IResponseContentBuilder aBuilder : fContentBuilders) {
                if (aBuilder.isCompatibleWith(extension)) {
                    return aBuilder;
                }
            }
        }
        return null;
    }

    private String getRequestExtension(String pathInfo) {
        Matcher matcher = fExtensionPattern.matcher(pathInfo);
        if (matcher.find()) {
            return matcher.group(1);
        }
        return "";
    }


    private IResponseContentBuilder findBuilderByAcceptHeader(HttpServletRequest request) {
        List<MediaType> mediaTypes = getAccept(request);
        IResponseContentBuilder aBuilder;
        for (MediaType aMediaType : mediaTypes) {
            if ((aBuilder = findBuilderForMediaType(aMediaType)) != null) {
                return aBuilder;
            }
        }
        return null;
    }

    private IResponseContentBuilder findBuilderForMediaType(MediaType aMediaType) {
        for (IResponseContentBuilder aBuilder : fContentBuilders) {
            if (aBuilder.isCompatibleWith(aMediaType)) {
                return aBuilder;
            }
        }
        return null;
    }

    protected List<MediaType> getAccept(HttpServletRequest request) {
        SortedMap<Double, MediaType> acceptedMediaTypes = new TreeMap<>();
        String accept = request.getHeader(ACCEPT);
        if (!StringUtils.isEmpty(accept)) {
            for (String aStringAccept : accept.split(",")) {
                MediaType mediaType = MediaType.parse(aStringAccept.trim());

                List<String> qValues = mediaType.parameters().get("q");
                if (qValues.isEmpty()) {
                    acceptedMediaTypes.put(1.0, mediaType.withoutParameters());
                } else {
                    acceptedMediaTypes.put(Double.parseDouble(qValues.get(0)), mediaType.withoutParameters());
                }
            }
        }
        return new ArrayList<>(acceptedMediaTypes.values());
    }


    private IResponseContentBuilder findBuilderByRequestContentType(HttpServletRequest request) {
        if (!StringUtils.isEmpty(request.getContentType())) {
            return findBuilderForMediaType(MediaType.parse(request.getContentType().trim()));
        }
        return null;
    }

}
