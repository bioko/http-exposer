package org.biokoframework.http.routing.impl;

import java.util.regex.Pattern;

import org.biokoframework.http.routing.IRoute;
import org.biokoframework.http.routing.IRouteMatcher;
import org.biokoframework.system.KILL_ME.commons.HttpMethod;

public class RouteMatcherImpl implements IRouteMatcher {

	private final HttpMethod fMethod;
	private final Pattern fPattern;

	public RouteMatcherImpl(HttpMethod method, String regexp) {
		fMethod = method;
		fPattern = Pattern.compile(regexp);
	}

	@Override
	public boolean matches(IRoute route) {
		return fMethod.equals(route.getMethod()) && fPattern.matcher(route.getPath()).matches();
	}

}
