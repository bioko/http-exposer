package org.biokoframework.http.response;

import com.google.common.net.MediaType;
import org.biokoframework.http.fields.RequestNotSupportedException;
import org.biokoframework.utils.fields.Fields;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014-08-13
 */
public interface IResponseContentBuilder {

    boolean isCompatibleWith(MediaType mediaType);
    boolean isCompatibleWith(String requestExtension);

    void build(HttpServletResponse response, Fields output)  throws IOException, RequestNotSupportedException;

}
