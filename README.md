# Bioko http-exposer

Java Servlet used to expose a Bioko system as Rest web service

## Multipart form data and Tomcat 7

Bioko relies on Guice Servlet. Guice Servlet uses a javax [Filter](http://google.github.io/guice/api-docs/latest/javadoc/index.html?com/google/inject/servlet/GuiceFilter.html)
 to intercept the requests and delegate the servlet object creation to the Injector.

By default Tomcat does not support [HttpServletRequest#getParts](http://docs.oracle.com/javaee/7/api/javax/servlet/http/HttpServletRequest.html#getParts%28%29)
 in filters. To enable it the war should contain the file `WEB-INF/context.xml` file structured like this:
 
```xml
<?xml version='1.0' encoding='utf-8'?>
<Context allowCasualMultipartParsing="true">
</Context>
```
 
## Multipart from data and Jetty
 
A problem similar to the one describe above happens in Jetty. We use Jetty as light server for testing purposes.
To [fix](http://dev.eclipse.org/mhonarc/lists/jetty-users/msg03294.html) the problem we created a [MultipartServletHolder](https://github.com/bioko/http-test/tree/master/src/main/java/org/biokoframework/http/rest/MultipartServletHolder.java),
which can be used as example.

<!-- [![Build Status](https://travis-ci.org/bioko/http-exposer.png?branch=dev)](https://travis-ci.org/bioko/http-exposer) -->
