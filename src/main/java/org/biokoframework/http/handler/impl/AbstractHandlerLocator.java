package org.biokoframework.http.handler.impl;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Map.Entry;

import org.biokoframework.http.handler.IHandler;
import org.biokoframework.http.handler.IHandlerLocator;
import org.biokoframework.http.routing.IRoute;
import org.biokoframework.http.routing.IRouteMatcher;
import org.biokoframework.http.routing.UnknownRouteException;
import org.biokoframework.utils.domain.ErrorEntity;
import org.biokoframework.utils.fields.FieldNames;
import org.biokoframework.utils.fields.Fields;

public abstract class AbstractHandlerLocator implements IHandlerLocator {

	private final ArrayList<Entry<IRouteMatcher, IHandler>> fRoutes = new ArrayList<>();
	
	@Override
	public IHandler getHandler(IRoute route) throws UnknownRouteException {
		for (Entry<IRouteMatcher, IHandler> anEntry : fRoutes) {
			if (anEntry.getKey().matches(route)) {
				return anEntry.getValue();
			}
		}
		
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
