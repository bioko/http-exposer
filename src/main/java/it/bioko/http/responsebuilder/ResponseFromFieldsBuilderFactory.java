package it.bioko.http.responsebuilder;

import java.util.ArrayList;
import java.util.List;

public class ResponseFromFieldsBuilderFactory {

	public static List<ResponseFromFieldsBuilder> createResponseBuildersList() {
		ArrayList<ResponseFromFieldsBuilder> builderList = new ArrayList<ResponseFromFieldsBuilder>();
		
		builderList.add(new BinaryResponseBuilder());
		builderList.add(new JSONResponseBuilder());

		return builderList;
	}

}
