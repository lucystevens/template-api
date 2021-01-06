package uk.co.lukestevens.main;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Properties;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.commons.cli.ParseException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.gson.Gson;

import okhttp3.OkHttpClient;
import uk.co.lukestevens.api.TemplateApiService;
import uk.co.lukestevens.api.client.ApiClientException;
import uk.co.lukestevens.api.client.ApiStatus;
import uk.co.lukestevens.api.client.TemplateApiClient;
import uk.co.lukestevens.config.Config;
import uk.co.lukestevens.config.models.PropertiesConfig;
import uk.co.lukestevens.models.Example;

public class TemplateApiIntegrationTest {
	
	@BeforeAll
	public static void runServer() throws ParseException, IOException {
		String[] args = {
				"-c", "src/test/resources/integration.conf"
		};
		TemplateApiMain.main(args);
	}
	
	TemplateApiClient client;
	
	@BeforeEach
	public void setup() throws Exception {
		// Init mock data source
		TemplateApiService.identifiers = new AtomicInteger(1);
		TemplateApiService.data = new HashMap<>();
		
		// Set up client
		Properties props = new Properties();
		props.setProperty("template.api.address", "http://localhost:8000");
		Config config = new PropertiesConfig(props);
		Gson gson = new Gson();
		OkHttpClient httpClient = new OkHttpClient().newBuilder().readTimeout(10, TimeUnit.MINUTES).build();
		client = new TemplateApiClient(config, gson, httpClient);
	}
	
	Example example(String value, boolean enabled) {
		Example example = new Example();
		example.setId(TemplateApiService.identifiers.getAndIncrement());
		example.setValue(value);
		example.setEnabled(enabled);
		TemplateApiService.data.put(example.getId(), example);
		return example;
	}
	
	@Test
	public void testTemplateApi_status() throws IOException {
		ApiStatus status = client.status();
		
		assertNotNull(status);
		assertEquals("template-api-test", status.getName());
		assertEquals("1.0.0-test", status.getVersion());
	}
	
	@Test
	public void testGetExample_exists() throws IOException {
		example("testGetExample_exists", true);
		Example e = client.getExample(1);
		
		assertNotNull(e);
		assertEquals(1, e.getId());
		assertEquals("testGetExample_exists", e.getValue());
		assertTrue(e.isEnabled());
	}
	
	@Test
	public void testGetExample_notExists() throws IOException {
		example("testGetExample_notExists", true);
		ApiClientException e = assertThrows(ApiClientException.class, ()-> client.getExample(2));
		assertEquals("No example found for id 2", e.getServerError("error"));
	}
	
	@Test
	public void testGetExample_unexpectedException() throws IOException {
		TemplateApiService.data = null;
		ApiClientException e = assertThrows(ApiClientException.class, ()-> client.getExample(2));
		assertEquals("Something went wrong, try again later", e.getServerError("error"));
	}
	

}
