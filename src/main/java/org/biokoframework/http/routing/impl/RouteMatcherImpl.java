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

package org.biokoframework.http.routing.impl;

import com.google.common.base.Objects;
import org.apache.commons.lang3.StringUtils;
import org.biokoframework.http.routing.IRoute;
import org.biokoframework.http.routing.IRouteMatcher;
import org.biokoframework.system.KILL_ME.commons.HttpMethod;
import org.biokoframework.utils.fields.Fields;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 19, 2014
 *
 */
public class RouteMatcherImpl implements IRouteMatcher {

	private final HttpMethod fMethod;
	private final Pattern fParameterPattern = Pattern.compile("\\{(?:<.*>)?[a-z]+\\}");
	private final Pattern fRoutePattern;
	
	private final List<String> fParameterNames;

	public RouteMatcherImpl(HttpMethod method, String routePath) {
		fMethod = method;
		fParameterNames = new ArrayList<>();
		
		String plainPath = stripParameters(routePath);
		fRoutePattern = Pattern.compile(fixTrailingSlash(plainPath));
		
	}

	@Override
	public boolean matches(IRoute route) {
		if (fMethod.equals(route.getMethod())) {
			Matcher matcher = fRoutePattern.matcher(route.getPath());
			if (matcher.matches()) {
				Fields parameters = new Fields();
				int i = 1;
				for (String aParameterName : fParameterNames) {
					String paramValue = matcher.group(i);
					if (!paramValue.isEmpty()) {
						parameters.put(aParameterName, paramValue);
					}
					i++;
				}
				
				route.matchedParameters(parameters);
					
				return true;
			}
		}
		return false;
	}
	
	private String fixTrailingSlash(String routePath) {
		if (routePath.endsWith("/")) {
			routePath = routePath + "?";
		} else if (routePath.endsWith("/?")) {
			return routePath;
		}
		return routePath + "/?";
	}
	
	private String stripParameters(String routePath) {
		Matcher matcher = fParameterPattern.matcher(routePath);
		if (matcher.find()) {
			String replacementString = extractParameter(matcher.group(0));
			routePath = routePath.replace(matcher.group(0), replacementString);
		}
		return routePath;
	}

	private String extractParameter(String group) {
		String parameterName = group.replaceAll("\\{(<.*>)?", "").replaceAll("\\}", "");
		fParameterNames.add(parameterName);
		String paramRegexp = group.replaceAll("\\{<?", "").replaceAll(">?" + parameterName + "\\}", "");
		return "(" + StringUtils.defaultIfEmpty(paramRegexp, "[^\\{\\}/\\.]*") + ")";
	}

    @Override
    public String toString() {
        return Objects
                .toStringHelper(RouteMatcherImpl.class)
                .add("method", fMethod)
                .add("routePattern", fRoutePattern)
                .add("parameterPatterns", fParameterPattern)
                .toString();
    }
}
