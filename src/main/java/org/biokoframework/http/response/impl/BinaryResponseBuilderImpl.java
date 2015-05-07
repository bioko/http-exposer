package org.biokoframework.http.response.impl;

import com.google.common.net.MediaType;
import org.apache.commons.io.IOUtils;
import org.biokoframework.http.fields.RequestNotSupportedException;
import org.biokoframework.http.response.IResponseContentBuilder;
import org.biokoframework.system.entity.binary.BinaryEntity;
import org.biokoframework.utils.domain.DomainEntity;
import org.biokoframework.utils.domain.ErrorEntity;
import org.biokoframework.utils.fields.FieldNames;
import org.biokoframework.utils.fields.Fields;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * @author Mikol Faro <mikol.faro@gmail.com>
 * @date 2014-09-08
 */
public class BinaryResponseBuilderImpl implements IResponseContentBuilder {

    @Override
    public boolean isCompatibleWith(MediaType mediaType) {
        return !MediaType.JSON_UTF_8.is(mediaType);
    }

    @Override
    public boolean isCompatibleWith(String requestExtension) {
        return !"JSON".equals(requestExtension);
    }

    @Override
    public void build(HttpServletResponse response, Fields output) throws IOException, RequestNotSupportedException {
        MediaType mediaType = output.get("mediaType");
        if (mediaType == null) {
            mediaType = MediaType.OCTET_STREAM;
        }
        response.setContentType(mediaType.toString());

        List<DomainEntity> entities = output.get("RESPONSE");
        for (DomainEntity anEntity : entities) {
            if (anEntity instanceof BinaryEntity) {
                BinaryEntity binaryEntity = (BinaryEntity) anEntity;
                IOUtils.copy(binaryEntity.getStream(), response.getOutputStream());
                return;
            }
        }
        noBinaryEntityFound();
    }

    private void noBinaryEntityFound() throws RequestNotSupportedException {
        ErrorEntity entity = new ErrorEntity();
        entity.setAll(new Fields(
                ErrorEntity.ERROR_CODE, FieldNames.BINARY_ENTITY_NOT_FOUND_CODE,
                ErrorEntity.ERROR_MESSAGE, "No binary entity found"));
        throw new RequestNotSupportedException(entity);
    }

}
