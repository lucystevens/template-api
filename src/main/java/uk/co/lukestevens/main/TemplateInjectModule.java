package uk.co.lukestevens.main;

import uk.co.lukestevens.api.TemplateApiService;
import uk.co.lukestevens.api.TemplateRouteConfiguration;
import uk.co.lukestevens.injection.BaseInjectModule;
import uk.co.lukestevens.server.routes.RouteConfiguration;
import uk.co.lukestevens.server.setup.ServerSetup;

public class TemplateInjectModule extends BaseInjectModule {

	public TemplateInjectModule(ServerSetup setup) {
		super(setup);
	}
	
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
