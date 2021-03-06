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

package org.biokoframework.http.response;

import com.google.inject.AbstractModule;
import com.google.inject.multibindings.Multibinder;
import org.biokoframework.http.response.impl.BinaryResponseBuilderImpl;
import org.biokoframework.http.response.impl.HttpResponseBuilderImpl;
import org.biokoframework.http.response.impl.JsonResponseBuilderImpl;

/**
 * 
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date Feb 26, 2014
 *
 */
public class ResponseBuilderModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(IHttpResponseBuilder.class).to(HttpResponseBuilderImpl.class);

        Multibinder<IResponseContentBuilder> contentBuilderMultibinder = Multibinder.newSetBinder(binder(), IResponseContentBuilder.class);
        contentBuilderMultibinder.addBinding().to(JsonResponseBuilderImpl.class);
        contentBuilderMultibinder.addBinding().to(BinaryResponseBuilderImpl.class);
	}

}
