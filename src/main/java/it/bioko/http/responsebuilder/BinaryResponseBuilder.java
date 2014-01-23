package it.bioko.http.responsebuilder;

import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.KILL_ME.commons.GenericFieldValues;
import it.bioko.system.KILL_ME.commons.logger.Loggers;
import it.bioko.system.entity.binary.BinaryEntity;
import it.bioko.utils.fields.Fields;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.IOUtils;

public class BinaryResponseBuilder extends ResponseFromFieldsBuilder {

	private static final ArrayList<String> ACCEPTED_CONTENT_TYPES = new ArrayList<String>(Arrays.asList(
			GenericFieldValues.OCTET_CONTENT_TYPE, 
			GenericFieldValues.JPEG_CONTENT_TYPE, GenericFieldValues.PNG_CONTENT_TYPE, GenericFieldValues.GIF_CONTENT_TYPE));
	
	private Fields _output;
	
	@Override
	public boolean canHandle(String responseType) {
		for (String anAcceptedContentType : ACCEPTED_CONTENT_TYPES) {
			if (anAcceptedContentType.equals(responseType)) {
				return true;
			}
		}
		return false;
	}

	@SuppressWarnings("unchecked")
	@Override
	public void build(HttpServletResponse servletResponse) throws Exception {
		servletResponse.setContentType(_output.stringNamed(GenericFieldNames.RESPONSE_CONTENT_TYPE));
		
		Object response = _output.valueFor(GenericFieldNames.RESPONSE);
		InputStream inputStream = null;
		if (response instanceof InputStream) {
			// Backward compatibility
			inputStream = (InputStream) response;
		} else {
			BinaryEntity blob  = ((ArrayList<BinaryEntity>) response).get(0);
			
			servletResponse.setHeader("Content-Length", blob.get(BinaryEntity.SIZE_BYTES));
			inputStream = blob.getStream();
		}

		if (inputStream != null) {
			ServletOutputStream outputStream = servletResponse.getOutputStream();
			IOUtils.copy(inputStream, outputStream);
			inputStream.close();
			outputStream.close();
		}
		
		Loggers.engagedInterface.info("OUT: binary file - " + 
				_output.stringNamed(GenericFieldNames.RESPONSE_CONTENT_TYPE));
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
