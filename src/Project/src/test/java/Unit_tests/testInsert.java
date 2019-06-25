package Unit_tests;

import static org.junit.Assert.*;
import org.junit.Test;

import nl.utwente.di.SQL.Database;

public class testInsert {
	public testInsert() {
		//INIT
	}
	
	@Test
	public void testParserAction() {
		//3 json objects with nested values, 1 with array of json objects
		assertEquals(Database.parseActions("[{{{}}},{{jaja}ja},{{[{},{}]}}]").size(), 3);
		//wrong format for json
		assertEquals(Database.parseActions("[dabadoebidee]"), null);
		assertEquals(Database.parseActions("[}hullo{]"), null);
		//too short for JSON:
		assertEquals(Database.parseActions("[{}]").size(), 0);
		//test some values
		assertEquals(Database.parseActions("[{name=jan}]").get(0), "{name=jan}");
		assertEquals(Database.parseActions("[{name=jan},{name=piet}]").get(1), "{name=piet}");
	}
	
	@Test
	public void testParserBooking() {
		assertEquals(Database.parse("[{\"j\":[{\"k\":[{\"val\":1}]}]},{\"task\":{\"bookingId\":7859326}},{\"one\":{\"arry\":{}}}]").size(), 3);
		//wrong format for json
		assertEquals(Database.parse("[dabadoebidee]"), null);
		assertEquals(Database.parse("[}hullo{]"), null);
		//too short for JSON
		assertEquals(Database.parse("[{}]").size(), 0);
		//test some values
		String JSON1 = "{\"name\":\"jan\"}";
		String JSON2 = JSON1 + "," + "{\"name\":\"piet\"}";
		assertEquals(Database.parse("[" + JSON1 + "]").get(0).get("name"), "jan");
		assertEquals(Database.parse("[" + JSON2 + "]").get(1).get("name"), "piet");
	}
}