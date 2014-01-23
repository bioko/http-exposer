package it.bioko.http.requestbuilder;

import it.bioko.http.FieldsFromRequestBuilder;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.charset.Charset;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public abstract class FieldsFromNonMultipartRequestRequestBuilder extends
		FieldsFromRequestBuilder {

	@Override
	public FieldsFromNonMultipartRequestRequestBuilder setRequest(HttpServletRequest request) {
		super.setRequest(request);
		setBody(readBody(request));
		setParameters(request.getParameterMap());
		return this;
	}

	protected abstract FieldsFromNonMultipartRequestRequestBuilder setParameters(Map<String, String[]> parameters);

	protected abstract FieldsFromNonMultipartRequestRequestBuilder setMethod(String anHttpMethod);

	protected abstract FieldsFromNonMultipartRequestRequestBuilder setPathInfo(String aPathInfo);

	protected abstract FieldsFromNonMultipartRequestRequestBuilder setBody(String body);

	protected String readBody(HttpServletRequest httpRequest) {
		String result = "";
		String charsetName = getRequestCharset(httpRequest);
		try {
			InputStream inputStream = httpRequest.getInputStream();
			StringWriter writer = new StringWriter(1024);
			
			BufferedReader inputReader = new BufferedReader(new InputStreamReader(inputStream, Charset.forName(charsetName)));
			int c;
			while ((c = inputReader.read()) != -1) {
				writer.write(c);
			}
			
			result = writer.toString();
		} catch (IOException ex) {
			System.out.println(ex.getStackTrace());
		}
		
		return result;
	}

}
