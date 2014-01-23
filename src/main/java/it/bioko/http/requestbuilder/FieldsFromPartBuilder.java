package it.bioko.http.requestbuilder;

import it.bioko.http.multipart.RequestPart;
import it.bioko.utils.fields.Fields;

public interface FieldsFromPartBuilder {

	public Fields build(RequestPart part, String charset);

	boolean canHandle(RequestPart part);

}
