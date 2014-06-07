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

import org.biokoframework.http.routing.IRoute;
import org.biokoframework.http.routing.IRouteMatcher;
import org.biokoframework.system.KILL_ME.commons.HttpMethod;
import org.biokoframework.utils.fields.Fields;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

import static org.biokoframework.utils.matcher.Matchers.contains;
import static org.biokoframework.utils.matcher.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

public class RouteMatcherImplTest {

	private static final String SIMPLE_GET_COMMAND = "/simple-get-command/{param}";
	private static final String SIMPLE_POST_COMMAND = "/simple-post-command";
	private static final String DUMMY_CRUD = "/dummy-crud/{<\\d*>id}";
	
	@Test
	public void test() {
		RouteMatcherImpl matcher = new RouteMatcherImpl(HttpMethod.POST, SIMPLE_POST_COMMAND);
		
		IRoute route = new RouteImpl(HttpMethod.POST, "/simple-post-command", new Fields());
		assertThat(matcher, matches(route));
		assertThat(route.getFields(), is(empty()));
		route = new RouteImpl(HttpMethod.POST, "/simple-post-command/", new Fields());
		assertThat(matcher, matches(route));
		assertThat(route.getFields(), is(empty()));

		route = new RouteImpl(HttpMethod.POST, "/an-other-command", new Fields());
		assertThat(matcher, not(matches(route)));
		
		route = new RouteImpl(HttpMethod.PUT, "/simple-post-command", new Fields());
		assertThat(matcher, not(matches(route)));
	}
	
	@Test
	public void testWithSimpleParameter() {
		RouteMatcherImpl matcher = new RouteMatcherImpl(HttpMethod.GET, SIMPLE_GET_COMMAND);
		
		IRoute route = new RouteImpl(HttpMethod.GET, "/simple-get-command/", new Fields());
		assertThat(matcher, matches(route));
		assertThat(route.getFields(), is(empty()));

		route = new RouteImpl(HttpMethod.GET, "/simple-get-command/43", new Fields());
		assertThat(matcher, matches(route));
		assertThat(route.getFields(), is(not(empty())));
		assertThat(route.getFields(), contains("param", "43"));
		
		route = new RouteImpl(HttpMethod.GET, "/simple-get-command/theValue", new Fields());
		assertThat(matcher, matches(route));
		assertThat(route.getFields(), is(not(empty())));
		assertThat(route.getFields(), contains("param", "theValue"));
	}
	
	@Test
	public void testWithRegexpedParameter() {
		RouteMatcherImpl matcher = new RouteMatcherImpl(HttpMethod.GET, DUMMY_CRUD);
		
		IRoute route = new RouteImpl(HttpMethod.GET, "/dummy-crud/", new Fields());
		assertThat(matcher, matches(route));
		assertThat(route.getFields(), is(empty()));

		route = new RouteImpl(HttpMethod.GET, "/dummy-crud/43", new Fields());
		assertThat(matcher, matches(route));
		assertThat(route.getFields(), is(not(empty())));
		assertThat(route.getFields(), contains("id", "43"));
		
		route = new RouteImpl(HttpMethod.GET, "/dummy-crud/NaN", new Fields());
		assertThat(matcher, not(matches(route)));
	}

    @Test
    public void testWithCatchAllForOptions() {
        RouteMatcherImpl matcher = new RouteMatcherImpl(HttpMethod.OPTIONS, "/{<.*>command}");

        IRoute route = new RouteImpl(HttpMethod.OPTIONS, "/dummy-crud/43", new Fields());
        assertThat(matcher, matches(route));
        assertThat(route.getFields(), is(not(empty())));
        assertThat(route.getFields(), contains("command", "dummy-crud/43"));

        route = new RouteImpl(HttpMethod.OPTIONS, "/dummy-crud/NaN", new Fields());
        assertThat(matcher, matches(route));
        assertThat(route.getFields(), is(not(empty())));
        assertThat(route.getFields(), contains("command", "dummy-crud/NaN"));

        route = new RouteImpl(HttpMethod.GET, "/dummy-crud/", new Fields());
        assertThat(matcher, not(matches(route)));
    }

	private Matcher<IRouteMatcher> matches(IRoute route) {
		return new RouteMatches(route);
	}

	private class RouteMatches extends TypeSafeMatcher<IRouteMatcher> {
		private final IRoute fRoute;
		
		public RouteMatches (IRoute route) {
			fRoute = route;
		}
		
		@Override
		public void describeTo(Description description) {
			description.appendText(" does not match route ").appendValue(fRoute);
			
		}

		@Override
		protected boolean matchesSafely(IRouteMatcher item) {
			return item.matches(fRoute);
		}
	}
	
}
