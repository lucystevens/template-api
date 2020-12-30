package uk.co.lukestevens.main;

import org.apache.commons.cli.ParseException;

import com.google.inject.Guice;
import com.google.inject.Injector;

import uk.co.lukestevens.injection.BaseInjectModule;
import uk.co.lukestevens.server.BaseServer;
import uk.co.lukestevens.server.setup.ServerSetup;
import uk.co.lukestevens.server.setup.ServerSetupProvider;

public class TemplateApiMain {

	public static void main(String[] args) throws ParseException {
		ServerSetupProvider serverSetupProvider = new ServerSetupProvider();
		ServerSetup serverSetup = serverSetupProvider.parseCommandLine(args);

		BaseInjectModule injectModule = new TemplateInjectModule(serverSetup);
		Injector injector = Guice.createInjector(injectModule);
		BaseServer server = injector.getInstance(BaseServer.class);
		server.init();
	}

}
