package uk.co.lukestevens.main;

import java.io.IOException;

import org.apache.commons.cli.ParseException;

import com.google.inject.Guice;
import com.google.inject.Injector;

import uk.co.lukestevens.injection.BaseInjectModule;
import uk.co.lukestevens.server.BaseServer;

public class TemplateApiMain {

	public static void main(String[] args) throws ParseException, IOException {
		BaseInjectModule injectModule = new TemplateInjectModule();
		Injector injector = Guice.createInjector(injectModule);
		
		BaseServer server = injector.getInstance(BaseServer.class);
		server.init();
	}

}
