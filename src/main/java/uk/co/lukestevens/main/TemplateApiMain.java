package uk.co.lukestevens.main;

import java.io.IOException;

import org.apache.commons.cli.ParseException;

import com.google.inject.Guice;
import com.google.inject.Injector;

import uk.co.lukestevens.injection.BaseInjectModule;
import uk.co.lukestevens.injection.ConfigLoader;
import uk.co.lukestevens.server.BaseServer;
import uk.co.lukestevens.server.setup.ServerSetup;
import uk.co.lukestevens.server.setup.ServerSetupProvider;

public class TemplateApiMain {

	public static void main(String[] args) throws ParseException, IOException {
		ServerSetupProvider serverSetupProvider = new ServerSetupProvider();
		ServerSetup serverSetup = serverSetupProvider.parseCommandLine(args);
		
		// Exit if help command chosen
		if(serverSetup == null) {
			System.exit(0);
		}

		BaseInjectModule injectModule = new TemplateInjectModule(serverSetup);
		Injector injector = Guice.createInjector(injectModule);
		
		ConfigLoader configLoader = injector.getInstance(ConfigLoader.class);
		configLoader.initialise();
		
		BaseServer server = injector.getInstance(BaseServer.class);
		server.init();
	}

}
