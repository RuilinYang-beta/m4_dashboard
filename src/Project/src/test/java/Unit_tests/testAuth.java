package Unit_tests;

import static org.junit.Assert.*;
import org.junit.Test;
import org.junit.Before;
import DAO.*;
import nl.utwente.di.SQL.Statistics;

public class testAuth {
	Statistics s;
	@Before
	public void init() {
		DAOgeneral.execute("DELETE FROM employees WHERE name LIKE '%TEST'");
	}
	
	@Test
	public void testAdd() {
		DAOgeneral.execute("DELETE FROM employees WHERE name LIKE '%TEST'");
		String n1 = "janTEST";
		String n2 = "pietTEST";
		String m1 = "jan@mail";
		String m2 = "piet@mail";
		assertEquals(DAOemployee.addEmployee(n1, 1, m1), "success");
		assertEquals(DAOemployee.addEmployee(n2, 2, m2), "success");
		//id should be unique, mail should be unique, name doesn't matter
		assertFalse(DAOemployee.addEmployee(n1, 1, "unique").equals("success"));
		assertFalse(DAOemployee.addEmployee(n1, 2, "unique").equals("success"));
		assertFalse(DAOemployee.addEmployee(n1, 5, m1).equals("success"));
		assertFalse(DAOemployee.addEmployee(n1, 5, m2).equals("success"));
		assertTrue(DAOemployee.addEmployee(n1, 5, "unique").equals("success"));
		DAOgeneral.execute("DELETE FROM employees");
	}
	
	@Test
	public void testDelete() {
		DAOgeneral.execute("DELETE FROM employees WHERE name LIKE '%TEST'");
		String n1 = "janTEST";
		String n2 = "pietTEST";
		String m1 = "jan@mail";
		String m2 = "piet@mail";
		DAOemployee.addEmployee(n1, 1, m1);
		DAOemployee.addEmployee(n2, 2, m2);
		//need name and mail or name and id
		assertFalse(DAOemployee.deleteEmployee("", 1).equals("success"));
		assertFalse(DAOemployee.deleteEmployee("", m1).equals("success"));
		assertFalse(DAOemployee.deleteEmployee(n1, 0).equals("success"));
		assertFalse(DAOemployee.deleteEmployee(n1, "").equals("success"));
		//needs to be correct
		assertFalse(DAOemployee.deleteEmployee(n1, m2).equals("success"));
		assertFalse(DAOemployee.deleteEmployee(n1, 2).equals("success"));
		assertFalse(DAOemployee.deleteEmployee(n2, m1).equals("success"));
		assertFalse(DAOemployee.deleteEmployee(n2, 1).equals("success"));
		//then it works
		assertTrue(DAOemployee.deleteEmployee(n1, m1).equals("success"));
		assertTrue(DAOemployee.deleteEmployee(n2, 2).equals("success"));
		
		DAOgeneral.execute("DELETE FROM employees");
	}
	
	@Test
	public void testCheck() {
		DAOgeneral.execute("DELETE FROM employees WHERE name LIKE '%TEST'");
		String n1 = "janTEST";
		String n2 = "pietTEST";
		String m1 = "jan@mail";
		String m2 = "piet@mail";
		DAOemployee.addEmployee(n1, 1, m1);
		DAOemployee.addEmployee(n2, 2, m2);
		assertTrue(DAOemployee.checkEmployee(m1));
		assertTrue(DAOemployee.checkEmployee(m2));
		assertFalse(DAOemployee.checkEmployee("wrong"));
		DAOgeneral.execute("DELETE FROM employees WHERE name LIKE '%TEST'");
	}
}
