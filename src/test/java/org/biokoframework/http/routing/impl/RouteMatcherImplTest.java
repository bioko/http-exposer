package org.biokoframework.http.routing.impl;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.junit.Assert.assertThat;

import org.biokoframework.http.routing.IRoute;
import org.biokoframework.http.routing.IRouteMatcher;
import org.biokoframework.system.KILL_ME.commons.HttpMethod;
import org.biokoframework.utils.fields.Fields;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

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
		assertThat(route.getFields(), contains(
					"param", "43"
				));
		
		route = new RouteImpl(HttpMethod.GET, "/simple-get-command/theValue", new Fields());
		assertThat(matcher, matches(route));
		assertThat(route.getFields(), is(not(empty())));
		assertThat(route.getFields(), contains(
					"param", "theValue"
				));
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
		assertThat(route.getFields(), contains(
					"id", "43"
				));
		
		route = new RouteImpl(HttpMethod.GET, "/dummy-crud/NaN", new Fields());
		assertThat(matcher, not(matches(route)));
	}

	
	private Matcher<Fields> contains(Object... keysAndValues) {
		return is(equalTo(new Fields(keysAndValues)));
	}
	
	// TODO move to utils
	private Matcher<Fields> empty() {
		return new Empty();
	}

	// TODO move to utils
	private class Empty extends TypeSafeMatcher<Fields> {

		@Override
		public void describeTo(Description description) {
			description.appendText("empty");
		}

		@Override
		protected boolean matchesSafely(Fields item) {
			return item.isEmpty();
		}
		
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
	};
	
}
