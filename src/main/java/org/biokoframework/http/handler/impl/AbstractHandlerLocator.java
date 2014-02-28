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

package org.biokoframework.http.handler.impl;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.biokoframework.http.handler.IHandler;
import org.biokoframework.http.handler.IHandlerLocator;
import org.biokoframework.http.routing.IRoute;
import org.biokoframework.http.routing.IRouteMatcher;
import org.biokoframework.http.routing.UnknownRouteException;
import org.biokoframework.utils.domain.ErrorEntity;
import org.biokoframework.utils.fields.FieldNames;
import org.biokoframework.utils.fields.Fields;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 19, 2014
 *
 */
public abstract class AbstractHandlerLocator implements IHandlerLocator {

	private static final Logger LOGGER = Logger.getLogger(AbstractHandlerLocator.class);
	private final ArrayList<Entry<IRouteMatcher, IHandler>> fRoutes = new ArrayList<>();
	
	@Override
	public IHandler getHandler(IRoute route) throws UnknownRouteException {
		LOGGER.info("Start searching");
		for (Entry<IRouteMatcher, IHandler> anEntry : fRoutes) {
			if (anEntry.getKey().matches(route)) {
				return anEntry.getValue();
			}
		}
		LOGGER.info("End searching");
		
		throw createUnknownRoute(route);
	}

	protected void addRoute(IRouteMatcher matcher, IHandler handler) {
		fRoutes.add(new SimpleEntry<IRouteMatcher, IHandler>(matcher, handler));
	}

	protected UnknownRouteException createUnknownRoute(IRoute route) {
		String message = new StringBuilder()
			.append("Route ").append(route.getMethod())
			.append(" ").append(route.getPath()).append(" not found")
			.toString();
		
		ErrorEntity errorEntity = new ErrorEntity(new Fields(
				ErrorEntity.ERROR_CODE, FieldNames.COMMAND_NOT_FOUND_CODE,
				ErrorEntity.ERROR_MESSAGE, message
			));
		return new UnknownRouteException(errorEntity);
	}
	
}
