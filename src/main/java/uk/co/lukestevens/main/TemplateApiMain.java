package uk.co.lukestevens.main;

import java.io.IOException;

import org.apache.commons.cli.ParseException;

import uk.co.lukestevens.app.App;

public class TemplateApiMain {

	public static void main(String[] args) throws ParseException, IOException {
		App.start(new TemplateApiModule());
	}

}
