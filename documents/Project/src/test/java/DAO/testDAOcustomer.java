package DAO;

import static org.junit.Assert.*;
import org.junit.Test;

public class testDAOcustomer {
	@Test
	public void testID() {
		String wrong = "WRONG";
		String right = "TEST";
		String rightLink = "test.link";
		DAOinsert.insertCustomer(right, rightLink);
		assertEquals(DAOcustomer.getCustId(wrong), -1);
		assertTrue(DAOcustomer.getCustId(right) > 0);
		DAOgeneral.execute("DELETE FROM customers WHERE name='" + right + "';");
	}
	
	@Test
	public void testLink() {
		String wrong = "WRONG";
		String right = "TEST";
		String rightLink = "test.link";
		DAOinsert.insertCustomer(right, rightLink);
		assertEquals(DAOcustomer.getCustLink(DAOcustomer.getCustId(wrong)), "");
		assertEquals(DAOcustomer.getCustLink(DAOcustomer.getCustId(right)), rightLink);
		DAOgeneral.execute("DELETE FROM customers WHERE name='" + right + "';");
	}
}