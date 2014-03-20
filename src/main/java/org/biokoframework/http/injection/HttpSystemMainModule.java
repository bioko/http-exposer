/*
 * Copyright (c) $year.
 * 	Mikol Faro		<mikol.faro@gmail.com>
 * 	Simone Mangano	 	<simone.mangano@ieee.org>
 * 	Mattia Tortorelli	<mattia.tortorelli@gmail.com>
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
 */

package org.biokoframework.http.injection;

import com.google.inject.AbstractModule;
import com.google.inject.binder.ConstantBindingBuilder;
import com.google.inject.name.Names;
import org.apache.log4j.Logger;
import org.biokoframework.system.ConfigurationEnum;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014-03-19
 */
public abstract class HttpSystemMainModule extends AbstractModule {

    private static final Logger LOGGER = Logger.getLogger(HttpSystemMainModule.class);

    private Properties fLoadedProperties;
    private final ConfigurationEnum fConfig;

    public HttpSystemMainModule() {
        try {
            fLoadedProperties = new Properties();

            ClassLoader loader = Thread.currentThread().getContextClassLoader();
            // Location based on WAR
            InputStream stream = loader.getResourceAsStream("system.properties");
            fLoadedProperties.load(stream);

            stream.close();
        } catch (IOException exception) {
            LOGGER.fatal("Cannot load 'system.properties", exception);
            throw new RuntimeException("Cannot load 'system.properties", exception);
        }
        fConfig = ConfigurationEnum.valueOf((String) fLoadedProperties.get("config"));
    }

    @Override
    protected final void configure() {
        bind(ConfigurationEnum.class).toInstance(fConfig);

        Names.bindProperties(binder(), fLoadedProperties);
        configureProperties();

        configureMain();

        configureOtherModules();
    }

    protected abstract void configureMain();

    protected abstract void configureProperties();

    protected abstract void configureOtherModules();

    protected ConstantBindingBuilder bindProperty(String propertyName) {
        return bindConstant().annotatedWith(Names.named(propertyName));
    }

    protected ConfigurationEnum getConfig() {
        return fConfig;
    }
}
