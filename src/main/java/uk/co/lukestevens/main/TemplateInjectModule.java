package uk.co.lukestevens.main;

import uk.co.lukestevens.api.TemplateApiService;
import uk.co.lukestevens.api.TemplateRouteConfiguration;
import uk.co.lukestevens.injection.BaseInjectModule;
import uk.co.lukestevens.server.routes.RouteConfiguration;

public class TemplateInjectModule extends BaseInjectModule {

	@Override
	protected void configure() {
		super.configure();
		bind(TemplateApiService.class);
	}

	@Override
	protected void bindRouteConfiguration() {
		bind(RouteConfiguration.class).to(TemplateRouteConfiguration.class);
	}

}
