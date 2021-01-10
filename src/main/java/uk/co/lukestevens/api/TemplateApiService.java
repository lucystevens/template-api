package uk.co.lukestevens.api;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

import javax.inject.Singleton;

import uk.co.lukestevens.api.models.Example;
import uk.co.lukestevens.server.exceptions.ServerException;


@Singleton
public class TemplateApiService {
	
	public static AtomicInteger identifiers = new AtomicInteger(1);
	public static Map<Integer, Example> data = new HashMap<>();
	
	public Example getExample(String id) throws ServerException {
		validateId(id);
		
		Example example = data.get(Integer.parseInt(id));
		if(example == null) {
			ServerException.notFound().withError("No example found for id " + id).throwException();
		}
		return example;
	}
	
	public Example createExample(Example example) throws ServerException {
		validateValue(example.getValue());
		
		example.setId(identifiers.getAndIncrement());
		data.put(example.getId(), example);
		return example;
	}
	
	public Example updateExample(Example example, String id) throws ServerException {
		validateId(id);
		validateValue(example.getValue());
		
		example.setId(Integer.parseInt(id));
		data.put(example.getId(), example);
		return example;
	}
	
	public void deleteExample(String id) throws ServerException {
		Example toRemove = getExample(id);
		data.remove(toRemove.getId());
	}
	
	void validateId(String id) throws ServerException {
		if(id == null || !id.matches("\\d+")) {
			ServerException.notFound().withError("id must be a number.").throwException(); 
		}
	}
	
	void validateValue(String value) throws ServerException {
		if(value == null || value.isEmpty()) {
			ServerException.invalidRequest().withError("value", "Value is required").throwException();
		}
	}

}
