# Bioko http-exposer

Java Servlet used to expose a Bioko system as Rest web service

# Multipart form data and Tomcat 7

Bioko relies on Guice Servlet. Guice Servlet uses a javax [filter](http://google.github.io/guice/api-docs/latest/javadoc/index.html?com/google/inject/servlet/GuiceFilter.html)
 to intercept the requests and delegate the servlet object creation to the Injector.

By default Tomcat does not support [HttpServletRequest#getParts](http://docs.oracle.com/javaee/7/api/javax/servlet/http/HttpServletRequest.html#getParts%28%29)
 in filters. To enable it the war should contain the file _WEB-INF/context.xml_ file structured like this:
 
 ```xml
<?xml version='1.0' encoding='utf-8'?>
<Context allowCasualMultipartParsing="true">
</Context>
```

<!-- [![Build Status](https://travis-ci.org/bioko/http-exposer.png?branch=dev)](https://travis-ci.org/bioko/http-exposer) -->
