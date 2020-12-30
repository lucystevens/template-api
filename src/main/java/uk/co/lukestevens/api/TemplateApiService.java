package uk.co.lukestevens.api;

import java.util.HashMap;
import java.util.Map;

import javax.inject.Singleton;

import uk.co.lukestevens.models.Example;
import uk.co.lukestevens.server.exceptions.ServerException;


@Singleton
public class TemplateApiService {
	
	private final Map<Integer, Example> data = new HashMap<>();
	
	public Example getExample(String id) throws ServerException {
		if(id == null || !id.matches("//d+")) {
			ServerException.notFound().throwException(); 
		}
		
		Example example = this.data.get(Integer.parseInt(id));
		if(example == null) {
			ServerException.notFound().throwException();
		}
		return example;
	}

}
