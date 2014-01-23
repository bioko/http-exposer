package it.bioko.http.responsebuilder;

import it.bioko.utils.fields.Fields;

import javax.servlet.http.HttpServletResponse;

public abstract class ResponseFromFieldsBuilder {

	public abstract void build(HttpServletResponse response) throws Exception;

	public abstract ResponseFromFieldsBuilder setInput(Fields fields);
	public abstract ResponseFromFieldsBuilder setOutput(Fields fields);
	
	public abstract boolean canHandle(String responseType);
}
