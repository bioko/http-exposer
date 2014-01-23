package it.bioko.http.responsebuilder;

import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.KILL_ME.commons.GenericFieldValues;
import it.bioko.system.KILL_ME.commons.logger.Loggers;
import it.bioko.utils.fields.Fields;

import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

import org.json.simple.JSONValue;

public class JSONResponseBuilder extends ResponseFromFieldsBuilder {

	private static final String JSON_CHARSET_UTF_8 = "application/json; charset=UTF-8";
	private Fields _output;
	
	@Override
	public boolean canHandle(String responseType) {
		return GenericFieldValues.JSON_CONTENT_TYPE.equals(responseType);
	}

	@Override
	public void build(HttpServletResponse response) throws Exception {
		Object responseJSON = _output.objectNamed(GenericFieldNames.RESPONSE);
		
		if (_output.contains(GenericFieldNames.TOKEN_HEADER)) {
			response.addHeader(GenericFieldNames.TOKEN_HEADER, _output.stringNamed(GenericFieldNames.TOKEN_HEADER));
		}
		if (_output.contains(GenericFieldNames.TOKEN_EXPIRE_HEADER)) {
			response.addHeader(GenericFieldNames.TOKEN_EXPIRE_HEADER, _output.stringNamed(GenericFieldNames.TOKEN_EXPIRE_HEADER));
		}
	
		response.setContentType(JSON_CHARSET_UTF_8 );
		PrintWriter writer = response.getWriter();
		JSONValue.writeJSONString(responseJSON, writer);
		Loggers.engagedInterface.info("OUT: " + JSONValue.toJSONString(responseJSON));
	}

	@Override
	public ResponseFromFieldsBuilder setInput(Fields input) {
		return this;
	}

	@Override
	public ResponseFromFieldsBuilder setOutput(Fields output) {
		_output = output;
		return this;
	}

}
