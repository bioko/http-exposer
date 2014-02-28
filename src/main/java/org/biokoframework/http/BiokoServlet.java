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

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.input.AutoCloseInputStream;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;
import org.biokoframework.http.exception.IExceptionResponseBuilder;
import org.biokoframework.http.handler.IHandler;
import org.biokoframework.http.handler.IHandlerLocator;
import org.biokoframework.http.response.IHttpResponseBuilder;
import org.biokoframework.http.routing.IHttpRouteParser;
import org.biokoframework.http.routing.IRoute;
import org.biokoframework.system.KILL_ME.SystemNames;
import org.biokoframework.utils.fields.Fields;

import com.google.inject.Injector;
import com.google.inject.Singleton;

@Singleton
@MultipartConfig
public class BiokoServlet extends HttpServlet {

	private static final long serialVersionUID = -2990444858272343398L;
	private static final Logger LOGGER = Logger.getLogger(BiokoServlet.class);

	private Injector fInjector;

	private IHttpRouteParser fRequestParser;
	private IHandlerLocator fLocator;

	private IHttpResponseBuilder fResponseBuilder;
	private IExceptionResponseBuilder fExceptionResponseBuilder;
	
	private String fSystemName;
	private String fSystemVersion;
	private String fSystemConfig;


	@Override
	public void init(ServletConfig config) throws ServletException {
		super.init(config);
		
		fSystemName = SystemNames.SYSTEM_A;
		fSystemVersion = "1.0";
		fSystemConfig = "DEV";

		fInjector = (Injector) config.getServletContext().getAttribute(Injector.class.getName());
		fRequestParser = fInjector.getInstance(IHttpRouteParser.class);
		fLocator = fInjector.getInstance(IHandlerLocator.class);

		fResponseBuilder = fInjector.getInstance(IHttpResponseBuilder.class);
		fExceptionResponseBuilder = fInjector.getInstance(IExceptionResponseBuilder.class);
		
		LOGGER.info("System-Config: " + fSystemName + " " + fSystemVersion + " " + fSystemConfig);

		loadLogProperties(fSystemName);

	}

	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		restExposer(req, resp);
	}
	
	@Override
	public void destroy() {
		LOGGER.info("Bioko servlet is going down");
		LOGGER.info("Adios!");
		super.destroy();
	}

	private void restExposer(HttpServletRequest req, HttpServletResponse response) throws IOException, ServletException {

		RequestWrapper requestWrapper = new RequestWrapper(req);
		
		LOGGER.debug(">>>>>>>>>>>>>>>>>>>> SERVLET >>>>>>>>>>>>>>>>>>>");
		LOGGER.debug(requestWrapper.report());
		LOGGER.debug(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
		
		Fields input = null;
		Fields output = null;
		
		try {
			String pathTranslated = requestWrapper.getPathTranslated();
			LOGGER.info("pathTranslated: " + pathTranslated);

			IRoute route = fRequestParser.getRoute(requestWrapper);
			input = route.getFields();

			LOGGER.info("Before getHandler");
			IHandler handler = fLocator.getHandler(route);
			LOGGER.info("Before afterHandler");
			
			LOGGER.info("Before execute");
			output = handler.getCommand(fInjector).execute(input);
			LOGGER.info("After execute");
			
			fResponseBuilder.build(requestWrapper, response, input, output);

		} catch (Exception exception) {
			LOGGER.error("Exception", exception);
			response = fExceptionResponseBuilder.build(response, exception, input, output);
		}
	}

	private void loadLogProperties(String systemName) {
		try {
			InputStream log4jPromo = new AutoCloseInputStream(getClass().getClassLoader().getResourceAsStream("log4j-" + systemName + ".properties"));
			if (log4jPromo != null) {
				PropertyConfigurator.configure(log4jPromo);
				log4jPromo.close();		
			}
		} catch (Exception exception) {
			LOGGER.error("Load properties file for logger", exception);
		}
	}

}
