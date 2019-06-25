package Unit_tests;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import nl.utwente.di.SQL.Statistics;

public class testAuth {
	Statistics s;
	@Before
	public void init() {
		s = new Statistics();
		s.connectToDatabase();
		s.execute("DELETE FROM employees");
	}
	@After
	public void shutDown() {
		try {s.connection.close();}catch(Exception e) {}
	}
	
	@Test
	public void testAdd() {
		s.execute("DELETE FROM employees");
		String n1 = "jan";
		String n2 = "piet";
		String m1 = "jan@mail";
		String m2 = "piet@mail";
		assertEquals(s.addEmployee(n1, 1, m1), "success");
		assertEquals(s.addEmployee(n2, 2, m2), "success");
		//id should be unique, mail should be unique, name doesn't matter
		assertFalse(s.addEmployee(n1, 1, "unique").equals("success"));
		assertFalse(s.addEmployee(n1, 2, "unique").equals("success"));
		assertFalse(s.addEmployee(n1, 5, m1).equals("success"));
		assertFalse(s.addEmployee(n1, 5, m2).equals("success"));
		assertTrue(s.addEmployee(n1, 5, "unique").equals("success"));
		s.execute("DELETE FROM employees");
	}
	
	@Test
	public void testDelete() {
		s.execute("DELETE FROM employees");
		String n1 = "jan";
		String n2 = "piet";
		String m1 = "jan@mail";
		String m2 = "piet@mail";
		s.addEmployee(n1, 1, m1);
		s.addEmployee(n2, 2, m2);
		//need name and mail or name and id
		assertFalse(s.deleteEmployee("", 1).equals("success"));
		assertFalse(s.deleteEmployee("", m1).equals("success"));
		assertFalse(s.deleteEmployee(n1, 0).equals("success"));
		assertFalse(s.deleteEmployee(n1, "").equals("success"));
		//needs to be correct
		assertFalse(s.deleteEmployee(n1, m2).equals("success"));
		assertFalse(s.deleteEmployee(n1, 2).equals("success"));
		assertFalse(s.deleteEmployee(n2, m1).equals("success"));
		assertFalse(s.deleteEmployee(n2, 1).equals("success"));
		//then it works
		assertTrue(s.deleteEmployee(n1, m1).equals("success"));
		assertTrue(s.deleteEmployee(n2, 2).equals("success"));
		
		s.execute("DELETE FROM employees");
	}
	
	@Test
	public void testCheck() {
		s.execute("DELETE FROM employees");
		String n1 = "jan";
		String n2 = "piet";
		String m1 = "jan@mail";
		String m2 = "piet@mail";
		s.addEmployee(n1, 1, m1);
		s.addEmployee(n2, 2, m2);
		assertTrue(s.checkEmployee(m1));
		assertTrue(s.checkEmployee(m2));
		assertFalse(s.checkEmployee("wrong"));
	}
}
