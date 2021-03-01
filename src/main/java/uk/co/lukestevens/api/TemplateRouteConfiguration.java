package uk.co.lukestevens.api;

import java.net.HttpURLConnection;
import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import com.google.gson.Gson;

import spark.Request;
import spark.Response;
import uk.co.lukestevens.api.models.Example;
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
				GET("/api/example/:id", this::getExample),
				POST("/api/example", this::createExample),
				PUT("/api/example/:id", this::updateExample),
				DELETE("/api/example/:id", this::deleteExample)
		);
	}
	
	protected Object getExample(Request req, Response res) throws ServerException {
		String id = req.params("id");
		return api.getExample(id);
	}
	
	protected Object createExample(Request req, Response res) throws ServerException {
		Example example = gson.fromJson(req.body(), Example.class);
		return api.createExample(example);
	}
	
	protected Object updateExample(Request req, Response res) throws ServerException {
		String id = req.params("id");
		Example example = gson.fromJson(req.body(), Example.class);
		return api.updateExample(example, id);
	}
	
	protected Object deleteExample(Request req, Response res) throws ServerException {
		String id = req.params("id");
		api.deleteExample(id);
		res.status(HttpURLConnection.HTTP_NO_CONTENT);
		return EMPTY_RESPONSE;
	}

}
