package it.bioko.http.requestbuilder;

import it.bioko.http.multipart.MultipartParser;
import it.bioko.http.multipart.RequestPart;
import it.bioko.system.KILL_ME.commons.GenericFieldNames;
import it.bioko.system.KILL_ME.commons.GenericFieldValues;
import it.bioko.system.KILL_ME.commons.HttpMethod;
import it.bioko.system.KILL_ME.commons.logger.Loggers;
import it.bioko.utils.fields.FieldNames;
import it.bioko.utils.fields.Fields;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

public class OnlyGetRequestBuilder extends FieldsFromNonMultipartRequestRequestBuilder {

	private static final String ONLY_GET_REGEXP = "^/(/)?only-get(/)?$";
	private static final String OBJECT_REGEXP = "^\\w+\\..*$";
	private static final String ARRAY_REGEXP = "^\\w+\\[\\d+\\].*$";

	private Map<String, Object> _parameters = new HashMap<String, Object>();
	
	private Map<String, String> _keyRenameMap = new HashMap<String, String>();
	private HttpServletRequest _request;
	
	public OnlyGetRequestBuilder() {
		_keyRenameMap.put(GenericFieldNames.COMMAND, FieldNames.COMMAND_NAME);
		_keyRenameMap.put(GenericFieldNames.TOKEN_HEADER, GenericFieldNames.AUTH_TOKEN);
	}

	@Override
	public boolean canHandle(HttpServletRequest request) {
		
//		String contentLength = request.getHeader(GenericFieldNames.CONTENT_LENGTH_HEADER);
//		if (contentLength != null &&  Long.parseLong(contentLength) != 0) {
//			return false;
//		}
		if (!HttpMethod.GET.toString().equalsIgnoreCase(request.getMethod()) &&
				!HttpMethod.POST.toString().equalsIgnoreCase(request.getMethod())) {
			return false;
		}
		
		String pathInfo = request.getPathInfo();
		return pathInfo.matches(ONLY_GET_REGEXP);
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public Fields build() {
		Fields fields = Fields.empty();
		
		applyRenamingMap(_parameters, _keyRenameMap);
		
		Iterator<String> iterator = _parameters.keySet().iterator();
		while (iterator.hasNext()) {
			String key = iterator.next();
			
			if (key.matches(OBJECT_REGEXP)) {
				String actualKey = key.substring(0, key.indexOf('.'));
				HashMap<String, Object> map = new HashMap<String, Object>();
				if (fields.valueFor(actualKey) != null) {
					map = (HashMap<String, Object>) fields.valueFor(actualKey);
				}
				expandObject(key, map , _parameters.get(key));
				fields.put(actualKey, map);
			} else if (key.matches(ARRAY_REGEXP)) {
				String actualKey = key.substring(0, key.indexOf('['));
				ArrayList<Object> list = new ArrayList<Object>();
				if (fields.valueFor(actualKey) != null) {
					list = (ArrayList<Object>) fields.valueFor(actualKey);
				}
				expandArray(key, list, _parameters.get(key));
				fields.put(actualKey, list);
			} else {
				fields.put(key, _parameters.get(key));
			}			
		}
		
		if (_request.getContentType() != null &&
				_request.getContentType().startsWith(GenericFieldValues.MULTIPART_CONTENT_TYPE)) {
			try {
				MultipartParser multipartParser = new MultipartParser(_request);
				ArrayList<RequestPart> parts = new ArrayList<RequestPart>(multipartParser.getParts());
				BinaryPartBuilder partBuilder = new BinaryPartBuilder();
				fields.putAll(partBuilder.build(parts.get(0), getRequestCharset(_request)));
			} catch (Exception exception) {
				Loggers.engagedInterface.error("Only get extract file", exception);
			}
		}
		
		return fields;
	}

	@SuppressWarnings("unchecked")
	private void expandArray(String key, ArrayList<Object> list, Object value) {
		String remaining = key.substring(key.indexOf(']') + 1);
		int index = Integer.parseInt(key.substring(key.indexOf('[') + 1, key.indexOf(']')));
		increaseArraySize(list, index + 1);
		if (remaining.length() == 0) {
			list.set(index, value);
		} else if (remaining.startsWith(".")) {
			remaining = remaining.substring(1);
			HashMap<String, Object> remainingMap = new HashMap<String, Object>();
			if (list.get(index) != null) {
				remainingMap = (HashMap<String, Object>) list.get(index);
			}
			expandObject(remaining, remainingMap, value);
			list.set(index, remainingMap);
		}
	}

	private <T> void increaseArraySize(ArrayList<T> list, int size) {
		for (int i = list.size(); i < size; i++) {
			list.add(null);
		}
	}

	private void expandObject(String key, HashMap<String, Object> map, Object value) {
		String remaining = key.substring(key.indexOf('.') + 1);
		if (remaining.matches(OBJECT_REGEXP)) {
			HashMap<String, Object> remainingMap = new HashMap<String, Object>();
			expandObject(remaining, remainingMap, value);
			map.put(key, remainingMap);
		} else if (remaining.matches(ARRAY_REGEXP)) {
			
		} else {
			map.put(remaining, value);
		}
	}

	private <V> void applyRenamingMap(Map<String, V> parameters, Map<String, String> keyRenameMap) {
		for (String anOldKeyName : keyRenameMap.keySet()) {
			if (parameters.containsKey(anOldKeyName)) {
				parameters.put(keyRenameMap.get(anOldKeyName), parameters.get(anOldKeyName));
				parameters.remove(anOldKeyName);
			}
		}
	}

	@Override
	protected OnlyGetRequestBuilder setParameters(Map<String, String[]> parameters) {
		_parameters.clear();
		for (String aKey : parameters.keySet()) {
			_parameters.put(aKey, parameters.get(aKey)[0]);
		}
		return this;
	}
	
	@Override
	protected OnlyGetRequestBuilder setMethod(String anHttpMethod) {
		return this;
	}

	@Override
	protected OnlyGetRequestBuilder setPathInfo(String aPathInfo) {
		return this;
	}

	@Override
	protected OnlyGetRequestBuilder setHeaderToken(String headerToken) {
		return this;
	}

	@Override
	protected OnlyGetRequestBuilder setAuthentication(String authentication) {
		return this;
	}

	@Override
	protected OnlyGetRequestBuilder setBody(String body) {
		return this;
	}
	
	@Override
	protected String readBody(HttpServletRequest httpRequest) {
		_request = httpRequest;
		return "";
	}
	
}
