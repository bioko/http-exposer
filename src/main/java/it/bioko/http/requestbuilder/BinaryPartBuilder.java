package it.bioko.http.requestbuilder;

import it.bioko.http.multipart.RequestPart;
import it.bioko.system.KILL_ME.commons.GenericFieldValues;
import it.bioko.system.KILL_ME.commons.logger.Loggers;
import it.bioko.system.entity.binary.BinaryEntity;
import it.bioko.utils.fields.Fields;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class BinaryPartBuilder implements FieldsFromPartBuilder {

	private static final ArrayList<String> ACCEPTED_CONTENT_TYPES = new ArrayList<String>(Arrays.asList(
			GenericFieldValues.OCTET_CONTENT_TYPE, GenericFieldValues.JPEG_CONTENT_TYPE, 
			GenericFieldValues.PNG_CONTENT_TYPE, GenericFieldValues.GIF_CONTENT_TYPE));
	
	@Override
	public Fields build(RequestPart aPart, String charset) {		
		Loggers.engagedInterface.info(new StringBuilder().
				append("REQUEST PART: " + aPart.getName() + "\n").
				append("content type: " + aPart.getContentType() + "\n").
				append("content size: " + aPart.getSize() + "\n").
				toString());
				
		Fields fields = Fields.empty(); 

		try {
			Fields blobFields = Fields.empty();
			//blobFields.put(Blob.STREAM, aPart.getInputStream());			
			blobFields.put(BinaryEntity.MEDIA_TYPE, aPart.getContentType());
			blobFields.put(BinaryEntity.SIZE_BYTES, Long.toString(aPart.getSize()));
			BinaryEntity blob = new BinaryEntity(blobFields);
			blob.setStream(aPart.getInputStream());
			fields.put(aPart.getName(), blob);
		} catch (IOException exception) {
			Loggers.engagedInterface.error("Accessing stream of request content part ("+aPart.getName()+","+aPart.getContentType()+")", exception);
			return Fields.empty();
		}
		
		return fields;
	}
	
	@Override
	public boolean canHandle(RequestPart part) {
		for (String anAcceptedContentType : ACCEPTED_CONTENT_TYPES) {
			if (part.getContentType().startsWith(anAcceptedContentType)) {
				return true;
			}
		}
		return false;
	}

}
