package uk.co.lukestevens.main;

import com.google.inject.AbstractModule;

import uk.co.lukestevens.api.TemplateApiService;
import uk.co.lukestevens.api.TemplateRouteConfiguration;
import uk.co.lukestevens.server.routes.RouteConfiguration;

public class TemplateApiModule extends AbstractModule {

	@Override
	protected void configure() {
		bind(TemplateApiService.class);
		bind(RouteConfiguration.class).to(TemplateRouteConfiguration.class);
	}

}
