package uk.co.lukestevens.main;

import static org.junit.jupiter.api.Assertions.*;

import java.io.IOException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
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
import uk.co.lukestevens.api.client.template.TemplateApiClient;
import uk.co.lukestevens.api.models.Example;
import uk.co.lukestevens.config.Config;
import uk.co.lukestevens.config.models.PropertiesConfig;
import uk.co.lukestevens.jdbc.result.DatabaseResult;
import uk.co.lukestevens.logging.LoggerLevel;
import uk.co.lukestevens.logging.models.Log;
import uk.co.lukestevens.testing.db.TestDatabase;
import uk.co.lukestevens.testing.mocks.EnvironmentVariableMocker;

// Integration test testing full server ability and client library
public class TemplateApiIntegrationTest {
	
	static TestDatabase db;
	
	@BeforeAll
	public static void runServer() throws ParseException, IOException, SQLException {
		db = new TestDatabase();
		db.executeFile("integration");
		
		EnvironmentVariableMocker.build()
			.with(db.getProperties())
			.with("database.logging.enabled", "true")
			.mock();
		
		// run server
		TemplateApiMain.main(new String[] {});
	}
	
	TemplateApiClient client;
	
	@BeforeEach
	public void setup() throws Exception {
		// Setup sql
		db.executeFile("integration");
		
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
		
		example("setup_example", true);
	}
	
	Example example(String value, boolean enabled) {
		Example example = new Example();
		example.setId(TemplateApiService.identifiers.getAndIncrement());
		example.setValue(value);
		example.setEnabled(enabled);
		TemplateApiService.data.put(example.getId(), example);
		return example;
	}
	
	List<Log> getLogs() throws Exception {
		try(DatabaseResult dbr = db.query("select * from core.logs order by timestamp")){
			return dbr.parseResultSet(rs -> {
				Log log = new Log();
				log.setApplicationName(rs.getString("application_name"));
				log.setApplicationVersion(rs.getString("application_version"));
				log.setName(rs.getString("logger_name"));
				log.setMessage(rs.getString("message"));
				log.setSeverity(LoggerLevel.valueOf(rs.getString("severity")));
				return log;
			});
		}
	}
	
	@Test
	public void testTemplateApi_status() throws Exception {
		ApiStatus status = client.status();
		
		assertNotNull(status);
		assertEquals("template-api-test", status.getName());
		assertEquals("1.0.0-test", status.getVersion());
	}
	
	@Test
	public void testGetExample_exists() throws Exception {
		example("testGetExample_exists", true);
		Example e = client.getExample(2);
		
		assertNotNull(e);
		assertEquals(2, e.getId());
		assertEquals("testGetExample_exists", e.getValue());
		assertTrue(e.isEnabled());
		
		// Check logs
		List<Log> logs = getLogs();
		assertEquals(1, logs.size());
		
		Log log = logs.get(0);
		assertEquals("template-api-test", log.getApplicationName());
		assertEquals("1.0.0-test", log.getApplicationVersion());
		assertEquals("uk.co.lukestevens.server.routes.RouteConfiguration", log.getName());
		assertEquals("Request received from 127.0.0.1: GET http://localhost:8000/api/example/2", log.getMessage());
		assertEquals(LoggerLevel.DEBUG, log.getSeverity());
	}
	
	@Test
	public void testGetExample_notExists() throws Exception {
		ApiClientException e = assertThrows(ApiClientException.class, 
				()-> client.getExample(2));
		assertEquals("No example found for id 2", e.getServerError("error"));
		
		// Check logs
		List<Log> logs = getLogs();
		assertEquals(1, logs.size());
		
		Log log = logs.get(0);
		assertEquals("uk.co.lukestevens.server.routes.RouteConfiguration", log.getName());
		assertEquals("Request received from 127.0.0.1: GET http://localhost:8000/api/example/2", log.getMessage());
		assertEquals(LoggerLevel.DEBUG, log.getSeverity());
	}
	
	@Test
	public void testGetExample_unexpectedException() throws Exception {
		TemplateApiService.data = null;
		ApiClientException e = assertThrows(ApiClientException.class, 
				()-> client.getExample(2));
		assertEquals("Something went wrong, try again later", e.getServerError("error"));
		
		// Check logs
		List<Log> logs = getLogs();
		assertEquals(2, logs.size());
		
		{
			Log log = logs.get(0);
			assertEquals("uk.co.lukestevens.server.routes.RouteConfiguration", log.getName());
			assertEquals("Request received from 127.0.0.1: GET http://localhost:8000/api/example/2", log.getMessage());
			assertEquals(LoggerLevel.DEBUG, log.getSeverity());
		}
		
		{
			Log log = logs.get(1);
			assertEquals("uk.co.lukestevens.server.routes.RouteConfiguration", log.getName());
			assertEquals("NullPointerException: null", log.getMessage());
			assertEquals(LoggerLevel.ERROR, log.getSeverity());
		}
	}
	
	@Test
	public void testCreateExample_valid() throws Exception {
		Example example = new Example();
		example.setValue("testCreateExample_valid");
		
		Example e = client.createExample(example);
		
		assertNotNull(e);
		assertEquals(2, e.getId());
		assertEquals("testCreateExample_valid", e.getValue());
		assertFalse(e.isEnabled());
		
		// Check logs
		List<Log> logs = getLogs();
		assertEquals(1, logs.size());
		
		Log log = logs.get(0);
		assertEquals("uk.co.lukestevens.server.routes.RouteConfiguration", log.getName());
		assertEquals("Request received from 127.0.0.1: POST http://localhost:8000/api/example", log.getMessage());
		assertEquals(LoggerLevel.DEBUG, log.getSeverity());
	}
	
	@Test
	public void testCreateExample_idAlreadySet() throws Exception {
		Example example = new Example();
		// id should be ignored
		example.setId(1);
		example.setValue("testCreateExample_idAlreadySet");
		example.setEnabled(true);
		
		Example e = client.createExample(example);
		
		assertNotNull(e);
		assertEquals(2, e.getId());
		assertEquals("testCreateExample_idAlreadySet", e.getValue());
		assertTrue(e.isEnabled());
		
		// Check logs
		List<Log> logs = getLogs();
		assertEquals(1, logs.size());
		
		Log log = logs.get(0);
		assertEquals("uk.co.lukestevens.server.routes.RouteConfiguration", log.getName());
		assertEquals("Request received from 127.0.0.1: POST http://localhost:8000/api/example", log.getMessage());
		assertEquals(LoggerLevel.DEBUG, log.getSeverity());
	}
	
	@Test
	public void testCreateExample_missingValue() throws Exception {
		Example example = new Example();
		example.setEnabled(true);
		ApiClientException e = assertThrows(ApiClientException.class, 
				()-> client.createExample(example));
		
		assertEquals("Value is required", e.getMessage());
		assertEquals("Value is required", e.getServerError("value"));
		
		// Check logs
		List<Log> logs = getLogs();
		assertEquals(1, logs.size());
		
		Log log = logs.get(0);
		assertEquals("uk.co.lukestevens.server.routes.RouteConfiguration", log.getName());
		assertEquals("Request received from 127.0.0.1: POST http://localhost:8000/api/example", log.getMessage());
		assertEquals(LoggerLevel.DEBUG, log.getSeverity());
	}
	
	@Test
	public void testUpdateExample_exists() throws Exception {
		example("testGetExample_exists", true);
		Example example = new Example();
		example.setId(2);
		example.setValue("testUpdateExample_exists");
		example.setEnabled(false);
		
		Example updated = client.updateExample(example);
		
		assertNotNull(updated);
		assertEquals(2, updated.getId());
		assertEquals("testUpdateExample_exists", updated.getValue());
		assertFalse(updated.isEnabled());
		
		Example inDataStore = TemplateApiService.data.get(2);
		assertNotNull(inDataStore);
		assertEquals(2, inDataStore.getId());
		assertEquals("testUpdateExample_exists", inDataStore.getValue());
		assertFalse(inDataStore.isEnabled());
		
		// Check logs
		List<Log> logs = getLogs();
		assertEquals(1, logs.size());
		
		Log log = logs.get(0);
		assertEquals("uk.co.lukestevens.server.routes.RouteConfiguration", log.getName());
		assertEquals("Request received from 127.0.0.1: PUT http://localhost:8000/api/example/2", log.getMessage());
		assertEquals(LoggerLevel.DEBUG, log.getSeverity());
	}
	
	@Test
	public void testUpdateExample_existsMissingValue() throws Exception {
		example("testGetExample_exists", true);
		Example example = new Example();
		example.setId(2);
		example.setValue("");
		example.setEnabled(false);
		
		ApiClientException e = assertThrows(ApiClientException.class, 
				()-> client.updateExample(example));
		
		assertEquals("Value is required", e.getMessage());
		assertEquals("Value is required", e.getServerError("value"));
		
		// Check logs
		List<Log> logs = getLogs();
		assertEquals(1, logs.size());
		
		Log log = logs.get(0);
		assertEquals("uk.co.lukestevens.server.routes.RouteConfiguration", log.getName());
		assertEquals("Request received from 127.0.0.1: PUT http://localhost:8000/api/example/2", log.getMessage());
		assertEquals(LoggerLevel.DEBUG, log.getSeverity());
	}
	
	@Test
	public void testUpdateExample_notExists() throws Exception {
		Example example = new Example();
		example.setId(2);
		example.setValue("testUpdateExample_notExists");
		
		Example updated = client.updateExample(example);
		
		assertNotNull(updated);
		assertEquals(2, updated.getId());
		assertEquals("testUpdateExample_notExists", updated.getValue());
		assertFalse(updated.isEnabled());
		
		Example inDataStore = TemplateApiService.data.get(2);
		assertNotNull(inDataStore);
		assertEquals(2, inDataStore.getId());
		assertEquals("testUpdateExample_notExists", inDataStore.getValue());
		assertFalse(inDataStore.isEnabled());
		
		// Check logs
		List<Log> logs = getLogs();
		assertEquals(1, logs.size());
		
		Log log = logs.get(0);
		assertEquals("uk.co.lukestevens.server.routes.RouteConfiguration", log.getName());
		assertEquals("Request received from 127.0.0.1: PUT http://localhost:8000/api/example/2", log.getMessage());
		assertEquals(LoggerLevel.DEBUG, log.getSeverity());
	}
	
	@Test
	public void testUpdateExample_noId() throws Exception {
		Example example = new Example();
		example.setValue("testUpdateExample_noId");
		
		ApiClientException e = assertThrows(ApiClientException.class, 
				() -> client.updateExample(example));
		
		assertEquals("Id must be set", e.getMessage());
	}
	
	@Test
	public void testDeleteExample_exists() throws Exception {
		example("testDeleteExample_exists", true);
		
		client.deleteExample(2);
		
		assertNull(TemplateApiService.data.get(2));
		
		// Check logs
		List<Log> logs = getLogs();
		assertEquals(1, logs.size());
		
		Log log = logs.get(0);
		assertEquals("uk.co.lukestevens.server.routes.RouteConfiguration", log.getName());
		assertEquals("Request received from 127.0.0.1: DELETE http://localhost:8000/api/example/2", log.getMessage());
		assertEquals(LoggerLevel.DEBUG, log.getSeverity());
	}
	
	@Test
	public void testDeleteExample_notExists() throws Exception {
		ApiClientException e = assertThrows(ApiClientException.class, () ->
			client.deleteExample(2));
		
		assertEquals("No example found for id 2", e.getMessage());
		assertEquals("No example found for id 2", e.getServerError("error"));
		
		// Check logs
		List<Log> logs = getLogs();
		assertEquals(1, logs.size());
		
		Log log = logs.get(0);
		assertEquals("uk.co.lukestevens.server.routes.RouteConfiguration", log.getName());
		assertEquals("Request received from 127.0.0.1: DELETE http://localhost:8000/api/example/2", log.getMessage());
		assertEquals(LoggerLevel.DEBUG, log.getSeverity());
	}
	

}
