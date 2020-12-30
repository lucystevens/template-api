package uk.co.lukestevens.api;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import com.google.gson.Gson;

import spark.Request;
import spark.Response;
import uk.co.lukestevens.logging.LoggingProvider;
import uk.co.lukestevens.server.exceptions.ServerException;
import uk.co.lukestevens.server.routes.AbstractRouteConfiguration;
import uk.co.lukestevens.server.routes.DefinedRoute;

public class TemplateRouteConfiguration extends AbstractRouteConfiguration {
	
	private final TemplateApiService api;

	@Inject
	public TemplateRouteConfiguration(LoggingProvider loggingProvider, Gson gson, TemplateApiService api) {
		super(loggingProvider, gson);
		this.api = api;
	}

	@Override
	public List<DefinedRoute> configureRoutes() {
		return Arrays.asList(
				GET("/api/example/:id", this::getExample)
		);
	}
	
	protected Object getExample(Request req, Response res) throws ServerException {
		String id = req.params("id");
		return api.getExample(id);
	}

}
