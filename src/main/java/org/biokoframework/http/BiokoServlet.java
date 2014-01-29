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

package org.biokoframework.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.input.AutoCloseInputStream;
import org.apache.log4j.PropertyConfigurator;
import org.biokoframework.http.requestbuilder.FieldsFromRequestBuilderFactory;
import org.biokoframework.http.responsebuilder.JSONResponseBuilder;
import org.biokoframework.http.responsebuilder.ResponseFromFieldsBuilder;
import org.biokoframework.http.responsebuilder.ResponseFromFieldsBuilderFactory;
import org.biokoframework.http.rest.FieldsFromRestRequestBuilder;
import org.biokoframework.http.rest.RestResponseFromCommandExceptionBuilder;
import org.biokoframework.http.rest.RestResponseFromSystemExceptionBuilder;
import org.biokoframework.system.ConfigurationEnum;
import org.biokoframework.system.KILL_ME.SystemNames;
import org.biokoframework.system.KILL_ME.XSystem;
import org.biokoframework.system.KILL_ME.XSystemIdentityCard;
import org.biokoframework.system.KILL_ME.commons.GenericFieldNames;
import org.biokoframework.system.KILL_ME.commons.logger.Loggers;
import org.biokoframework.system.KILL_ME.exception.SystemException;
import org.biokoframework.utils.exception.BiokoException;
import org.biokoframework.utils.fields.Fields;

import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
@MultipartConfig
public class BiokoServlet extends HttpServlet {

	private static final long serialVersionUID = -2990444858272343398L;

	private XServerSingleton _xServerSingleton;
	private String _systemName;
	private String _systemVersion;
	private String _systemConfig;

	private Map<String, FieldsFromRequestBuilder> _requestBuilders = FieldsFromRequestBuilderFactory.createRequestBuildersList();
	private List<ResponseFromFieldsBuilder> _responseBuilders = ResponseFromFieldsBuilderFactory.createResponseBuildersList();

	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		_systemName = SystemNames.SYSTEM_A;
		_systemVersion = "1.0";
		_systemConfig = "DEV";

		Injector injector = (Injector) config.getServletContext().getAttribute(Injector.class.getName());
		_xServerSingleton = injector.getInstance(XServerSingleton.class);

		Loggers.engagedInterface.info("System-Config: " + _systemName + " " + _systemVersion + " " + _systemConfig);
		System.out.println("System-Config: " + _systemName + " " + _systemVersion + " " + _systemConfig);

		loadLogProperties(_systemName);

	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		restExposer(req, resp);
	}
	
	@Override
	public void destroy() {
		Loggers.engagedInterface.info("Bioko servlet is going down");
		Loggers.engagedInterface.info("Adios!");
		_xServerSingleton.shutdown();
		super.destroy();
	}

	private void restExposer(HttpServletRequest req, HttpServletResponse response) throws IOException, ServletException {

		RequestWrapper requestWrapper = new RequestWrapper(req);
		
		Loggers.engagedInterface.debug(">>>>>>>>>>>>>>>>>>>> SERVLET >>>>>>>>>>>>>>>>>>>");
		Loggers.engagedInterface.debug(requestWrapper.report());
		Loggers.engagedInterface.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		
		
		
		try {

			Fields input = buildFieldsFromRequest(requestWrapper);

			String pathTranslated = requestWrapper.getPathTranslated();
			Loggers.engagedInterface.info("pathTranslated: " + pathTranslated);

			Fields output = new Fields();

			XSystemIdentityCard xSystemIdentityCard = new XSystemIdentityCard(_systemName, _systemVersion, ConfigurationEnum.valueOf(_systemConfig));
			try {
				_xServerSingleton.getSystem(xSystemIdentityCard, Loggers.engagedServer);
				XSystem serverInstance = _xServerSingleton.getSystem(xSystemIdentityCard, Loggers.engagedServer);
				try {

					output = serverInstance.execute(input);

				} catch (BiokoException exception) {
					response = new RestResponseFromCommandExceptionBuilder()
					.setInputFields(input)
					.setOutputFields(output)
					.setException(exception)
					.build(response);
				}
			} catch (SystemException exception) {
				response = new RestResponseFromSystemExceptionBuilder()
						.setOutputFields(output)
						.setException(exception)
						.build(response);
			}

			String responseType = output.get(GenericFieldNames.RESPONSE_CONTENT_TYPE);
			if (responseType == null || responseType.isEmpty()) {
				responseType = input.get(GenericFieldNames.RESPONSE_CONTENT_TYPE);
			}

			ResponseFromFieldsBuilder responseBuilder = new JSONResponseBuilder();
			for (ResponseFromFieldsBuilder aBuilder : _responseBuilders) {
				if (aBuilder.canHandle(responseType)) {
					responseBuilder = aBuilder;
					break;
				}
			}
			responseBuilder.
			setInput(input).
			setOutput(output).
			build(response);
		} catch (Exception e) {
			StringWriter writer = new StringWriter();
			writer.append(Arrays.toString(e.getStackTrace()).replace(" ", "\n"));
			Loggers.engagedInterface.error(writer);
		}
	}

	private Fields buildFieldsFromRequest(HttpServletRequest request) {
		FieldsFromRequestBuilder requestBuilder = new FieldsFromRestRequestBuilder();
		for (FieldsFromRequestBuilder aBuilder : _requestBuilders.values()) {
			if (aBuilder.canHandle(request)) {
				requestBuilder = aBuilder;
				break;
			}
		}

		Fields input = requestBuilder.setRequest(request).build();
		return input;
	}

	private void loadLogProperties(String systemName) {
		try {
			InputStream log4jPromo = new AutoCloseInputStream(getClass().getClassLoader().getResourceAsStream("log4j-" + systemName + ".properties"));
			if (log4jPromo != null) {
				PropertyConfigurator.configure(log4jPromo);
				log4jPromo.close();		
			}
		} catch (Exception exception) {
			Loggers.engagedInterface.error("Load properties file for logger", exception);
		}
	}

}
