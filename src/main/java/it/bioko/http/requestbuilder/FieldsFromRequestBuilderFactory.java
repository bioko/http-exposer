package it.bioko.http.requestbuilder;

import it.bioko.http.FieldsFromRequestBuilder;
import it.bioko.http.rest.FieldsFromRestRequestBuilder;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

public class FieldsFromRequestBuilderFactory {

	public static Map<String, FieldsFromRequestBuilder> createRequestBuildersList() {
		LinkedHashMap<String, FieldsFromRequestBuilder> builderList = new LinkedHashMap<String, FieldsFromRequestBuilder>();
		
		builderList.put(RequestBuilders.ONLY_GET_BUILDER, new OnlyGetRequestBuilder());
		builderList.put(RequestBuilders.MULTIPART_BUILDER,  new FieldsFromMultipartRequestBuilder(createRequestFromPartBuildersList()));
		builderList.put(RequestBuilders.REST_BUILDER, new FieldsFromRestRequestBuilder());
		
		return builderList;
	}

	private static Map<String, FieldsFromPartBuilder> createRequestFromPartBuildersList() {
		HashMap<String, FieldsFromPartBuilder> builderList = new HashMap<String, FieldsFromPartBuilder>();
		
		builderList.put(RequestBuilders.BINARY_PART_BUILDER, new BinaryPartBuilder());
		
		return builderList;
	}

}
